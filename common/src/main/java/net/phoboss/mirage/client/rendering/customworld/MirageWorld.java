package net.phoboss.mirage.client.rendering.customworld;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class MirageWorld extends World implements ServerWorldAccess {

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
    public static EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
    protected ChunkManager chunkManager;


    public static class StateNEntity {
        public FluidState fluidState;
        public BlockState blockState;
        public BlockEntity blockEntity;
        public Entity entity;
        public StateNEntity(BlockState blockState,BlockEntity blockEntity) {
            this.blockState = blockState;
            this.blockEntity = blockEntity;
        }
        public StateNEntity(BlockState blockState) {
            this.blockState = blockState;
        }
        public StateNEntity(FluidState fluidState) {
            this.fluidState = fluidState;
        }
        public StateNEntity(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }
        public StateNEntity(Entity entity) {
            this.entity = entity;
        }
    }

    public static class BlockWEntity {
        public FluidState fluidState;
        public BlockState blockState;
        public BlockEntity blockEntity;
        public BlockWEntity(BlockState blockState,BlockEntity blockEntity) {
            this.blockState = blockState;
            this.blockEntity = blockEntity;
        }
        public BlockWEntity(BlockState blockState) {
            this.blockState = blockState;
        }
        public BlockWEntity(FluidState fluidState) {
            this.fluidState = fluidState;
        }
        public BlockWEntity(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }
    }

    protected World world;
    public ObjectArrayList<BlockTicker> mirageBlockEntityTickers;
    protected Long2ObjectOpenHashMap<StateNEntity> mirageStateNEntities;
    protected Long2ObjectOpenHashMap<StateNEntity> manualBlocksList;
    protected Long2ObjectOpenHashMap<StateNEntity> VertexBufferBlocksList;
    protected Long2ObjectOpenHashMap<BlockWEntity> BERBlocksList;
    private MirageBufferStorage mirageBufferStorage;

    public MirageWorld(World world) {
        super((MutableWorldProperties) world.getLevelProperties(),
                world.getRegistryKey(),
                world.method_40134(),
                world::getProfiler,
                world.isClient(),
                world.isDebugWorld(),
                0);
        this.world = world;
        this.mirageBlockEntityTickers = new ObjectArrayList();
        this.mirageStateNEntities = new Long2ObjectOpenHashMap();
        this.BERBlocksList = new Long2ObjectOpenHashMap();
        this.VertexBufferBlocksList = new Long2ObjectOpenHashMap();
        this.manualBlocksList = new Long2ObjectOpenHashMap();

        setChunkManager(new MirageChunkManager(this));

        this.mirageBufferStorage = new MirageBufferStorage();
    }

    public boolean newlyRefreshedBuffers = true;
    public boolean overideRefreshBuffer = true;
    @ExpectPlatform
    public static void refreshVertexBuffersIfNeeded(BlockPos projectorPos,MirageWorld mirageWorld){
        throw new AssertionError();
    }
    public static RenderLayer TRANSLUCENT_RENDER_LAYER = RenderLayer.getTranslucent();

    public void render(BlockPos projectorPos,float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        refreshVertexBuffersIfNeeded(projectorPos,this);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        this.mirageBufferStorage.mirageVertexBuffers.forEach((renderLayer,vertexBuffer)->{
            renderLayer.startDrawing();
            vertexBuffer.setShader(matrixStack.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix(),RenderSystem.getShader());
            renderLayer.endDrawing();
        });
        matrixStack.pop();

        this.manualBlocksList.forEach((key, block)->{//need to render multi-model-layered translucent blocks (i.e. slime, honey, DecoBeacons etc) manually :(
            matrices.push();
            BlockPos fakeBlockPos = BlockPos.fromLong(key);
            BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
            matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());
            renderMirageBlock(block.blockState, fakeBlockPos, this, matrices, vertexConsumers, true, getRandom());
            matrices.pop();
        });

        this.BERBlocksList.forEach((key,block)->{//animated blocks (enchanting table...)
            matrices.push();
            BlockPos fakeBlockPos = BlockPos.fromLong(key);
            BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
            matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());
            renderMirageBlockEntity(block.blockEntity, tickDelta, matrices, vertexConsumers);
            matrices.pop();
        });
    }

    public void initVertexBuffers(BlockPos projectorPos) {
        this.mirageBufferStorage.reset();
        MatrixStack matrices = new MatrixStack();
        VertexConsumerProvider.Immediate vertexConsumers = this.mirageBufferStorage.tempImmediate;

        this.VertexBufferBlocksList.forEach((fakeBlockPosKey,fakeStateNEntity)->{
            BlockPos fakeBlockPos = BlockPos.fromLong(fakeBlockPosKey);
            BlockState fakeBlockState = fakeStateNEntity.blockState;
            BlockEntity fakeBlockEntity = fakeStateNEntity.blockEntity;
            Entity fakeEntity = fakeStateNEntity.entity;


            matrices.push();

            if (fakeEntity != null) {
                Vec3d entityPos = fakeEntity.getPos().subtract(new Vec3d(projectorPos.getX(),projectorPos.getY(),projectorPos.getZ()));
                matrices.translate(entityPos.getX(),entityPos.getY(),entityPos.getZ());
                renderMirageEntity(fakeEntity, 0, matrices, vertexConsumers);
            }
            matrices.pop();

            matrices.push();
            BlockPos relativePos = fakeBlockPos.subtract(projectorPos);
            matrices.translate(relativePos.getX(),relativePos.getY(),relativePos.getZ());

            markAnimatedSprite(fakeBlockState,this.getRandom());

            if (fakeBlockEntity != null) {
                renderMirageModelData(fakeBlockState, fakeBlockPos, this, true, getRandom(), fakeBlockEntity, matrices, vertexConsumers);
                matrices.pop();
                return;
            }

            if (fakeBlockState != null) {
                renderMirageBlock(fakeBlockState, fakeBlockPos, this, matrices, vertexConsumers, true, getRandom());
            }
            matrices.pop();
        });

        this.mirageBufferStorage.copyBufferBuilders(this.mirageBufferStorage.tempImmediate);
        this.mirageBufferStorage.uploadBufferBuildersToVertexBuffers();
    }

    @ExpectPlatform
    public static void markAnimatedSprite(BlockState blockState,Random random){
        throw new AssertionError();
    }//WIP Embeddium compat
    @ExpectPlatform
    public static boolean isOnTranslucentRenderLayer(BlockState blockState){
        return RenderLayers.getEntityBlockLayer(blockState,true) == RenderLayer.getTranslucent();
    }
    @ExpectPlatform
    public static boolean addToManualRenderList(long blockPosKey,StateNEntity stateNEntity,Long2ObjectOpenHashMap<StateNEntity> manualRenderBlocks){
        return false;
    }

    public void clearMirageWorld(){
        synchronized (this.mirageStateNEntities){
            this.mirageStateNEntities.clear();
        }
        synchronized (this.BERBlocksList){
            this.BERBlocksList.clear();
        }
        synchronized (this.VertexBufferBlocksList){
            this.VertexBufferBlocksList.clear();
        }
        synchronized (this.manualBlocksList){
            this.manualBlocksList.clear();
        }
        synchronized (this.mirageBufferStorage){
            this.mirageBufferStorage = new MirageBufferStorage();
        }
        synchronized (this.mirageBlockEntityTickers){
            this.mirageBlockEntityTickers.clear();
        }
    }
    public void initBlockRenderLists() {
        this.mirageStateNEntities.forEach((blockPosKey,stateNEntity)->{
            BlockState blockState = stateNEntity.blockState;
            BlockEntity blockEntity = stateNEntity.blockEntity;

            if(blockEntity != null) {
                if (blockEntityRenderDispatcher.get(blockEntity)!=null) {
                    this.BERBlocksList.put(blockPosKey,new BlockWEntity(blockState,blockEntity));
                }
                if (isOnTranslucentRenderLayer(blockState)) {
                    if(addToManualRenderList(blockPosKey,new StateNEntity(blockState,blockEntity), this.manualBlocksList)){//isDecoBeaconBlock
                        return;
                    }
                }
                this.VertexBufferBlocksList.put(blockPosKey,stateNEntity);
                return;
            }

            if(blockState != null) {
                if (isOnTranslucentRenderLayer(blockState)) {
                    this.manualBlocksList.put(blockPosKey, new StateNEntity(blockState));
                    return;
                }
            }

            this.VertexBufferBlocksList.put(blockPosKey,stateNEntity);
        });
    }


    public static void renderMirageBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        blockEntityRenderDispatcher.render(blockEntity,tickDelta,matrices,vertexConsumers);
    }
    public static void renderMirageEntity(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        entityRenderDispatcher.render(entity, 0, 0, 0, entity.getYaw(), tickDelta, matrices, vertexConsumers, entityRenderDispatcher.getLight(entity, tickDelta));
    }

    public static void renderMirageBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, boolean cull, Random random){
        RenderLayer rl = RenderLayers.getEntityBlockLayer(state,true);
        blockRenderManager.renderBlock(state,referencePos,world,matrices,
                vertexConsumerProvider.getBuffer(rl),cull,random);
    }
    @ExpectPlatform
    public static void renderMirageModelData(BlockState state, BlockPos referencePos, BlockRenderView world, boolean cull, Random random, BlockEntity blockEntity, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider){
        throw new AssertionError();
    }

    public void resetWorldForBlockEntities(){
        this.mirageStateNEntities.forEach((key,entry)->{
           if(entry.blockEntity != null){
               entry.blockEntity.setWorld(this);//framed block ModelData is set on `FramedBlockEntity.setWorld(...)`
           }
        });
    }

    public void setMirageBlockEntity(BlockPos pos,BlockEntity blockEntity) {
        long key = pos.asLong();
        if (this.mirageStateNEntities.containsKey(key)) {
            StateNEntity mirageStateNEntity = this.mirageStateNEntities.get(key);
            mirageStateNEntity.blockEntity = blockEntity;
        }else{
            this.mirageStateNEntities.put(key,new StateNEntity(blockEntity));
        }
    }
    public void setFluidState(BlockPos pos,BlockState state){
        long key = pos.asLong();
        FluidState fluidState = state.getFluidState();
        if (this.mirageStateNEntities.containsKey(key)) {
            StateNEntity mirageStateNEntity = this.mirageStateNEntities.get(key);
            mirageStateNEntity.fluidState = fluidState;
        }else{
            this.mirageStateNEntities.put(key,new StateNEntity(fluidState));
        }
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state) {
        if(state.isAir()){
            return true;
        }
        long key = pos.asLong();
        if (this.mirageStateNEntities.containsKey(key)) {
            StateNEntity mirageStateNEntity = this.mirageStateNEntities.get(key);
            mirageStateNEntity.blockState = state;
        }else{
            this.mirageStateNEntities.put(key,new StateNEntity(state));
        }
        setFluidState(pos,state);
        if (state.getBlock() instanceof BlockEntityProvider bep) {
            addBlockEntity(bep.createBlockEntity(pos,state));
        }
        return true;
    }

    public boolean spawnEntity(BlockPos pos, Entity entity) {
        long key = pos.asLong();
        if (this.mirageStateNEntities.containsKey(key)) {
            StateNEntity mirageStateNEntity = this.mirageStateNEntities.get(key);
            mirageStateNEntity.entity = entity;
        }else{
            this.mirageStateNEntities.put(key,new StateNEntity(entity));
        }
        return true;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return setBlockState(pos, state);
    }

    @Override
    public boolean spawnEntity(Entity entity) {
        spawnEntity(entity.getBlockPos(), entity);
        return true;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getPos();
        blockEntity.setWorld(this);//needs to be done AFTER setBlockState(...) here to properly initialize FramedBlockEntity ModelData
        setMirageBlockEntity(pos,blockEntity);
        setMirageBlockEntityTicker(pos,blockEntity);
    }



    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        long key = pos.asLong();
        StateNEntity entry = this.mirageStateNEntities.get(key);
        if(entry == null) {
            return null;
        }
        if(entry.blockEntity == null) {
            return null;
        }
        return entry.blockEntity;
    }
    @Override
    public BlockState getBlockState(BlockPos pos) {
        long key = pos.asLong();
        if(this.mirageStateNEntities.containsKey(key)) {
            BlockState blockState = this.mirageStateNEntities.get(key).blockState;
            if ( blockState != null) {
                return blockState;
            }
        }
        return Blocks.AIR.getDefaultState();
    }
    @Override
    public FluidState getFluidState(BlockPos pos) {
        long key = pos.asLong();
        if(this.mirageStateNEntities.containsKey(key)) {
            FluidState fluidState = this.mirageStateNEntities.get(key).fluidState;
            if (fluidState != null) {
                return fluidState;
            }
        }
        return Blocks.AIR.getDefaultState().getFluidState();
    }

    @Override
    public ServerWorld toServerWorld() {
        if (this.world instanceof ServerWorld) {
            return (ServerWorld) this.world;
        }
        throw new IllegalStateException("Cannot use IServerWorld#getWorld in a client environment");
    }

    public void spawnMirageEntityAndPassengers(Entity entity) {
        entity.streamSelfAndPassengers().forEach(this::spawnEntity);
    }



    public class BlockTicker {
        public BlockPos blockPos;
        public BlockState blockState;
        public BlockEntity blockEntity;
        public BlockEntityTicker blockEntityTicker;

        public BlockTicker(BlockPos blockPos, BlockState blockState, BlockEntity blockEntity, BlockEntityTicker blockEntityTicker) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.blockEntity = blockEntity;
            this.blockEntityTicker = blockEntityTicker;
        }
    }
    public void setMirageBlockEntityTicker(BlockPos pos,BlockEntity blockEntity) {
        if(!this.world.isClient()){//world doesn't save when adding entityTickers in server side
            return;
        }
        if(blockEntity instanceof BeaconBlockEntity){//Don't want to have players having a portable beacon buff :)
            return;
        }
        BlockState blockstate = blockEntity.getCachedState();
        BlockEntityTicker blockEntityTicker = blockstate.getBlockEntityTicker(this, blockEntity.getType());
        if (blockEntityTicker != null) {
            synchronized (this.mirageBlockEntityTickers) {
                this.mirageBlockEntityTickers.add(new BlockTicker(pos, blockstate, blockEntity, blockEntityTicker));
            }
        }
    }

    public void tickBlockEntities(){
        this.mirageBlockEntityTickers.forEach((blockTicker)->{
            blockTicker.blockEntityTicker.tick(this,blockTicker.blockPos,blockTicker.blockState,blockTicker.blockEntity);
        });
    }



    public void tick(){
        tickBlockEntities();
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public BiomeAccess getBiomeAccess() {
        return world.getBiomeAccess();
    }

    @Override
    public int getColor(BlockPos pos, ColorResolver colorResolver) {
        return world.getColor(pos, colorResolver);
    }

    @Override
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return world.getGeneratorStoredBiome(biomeX,biomeY,biomeZ);
    }

    @Override
    public long getTime() {
        return this.world.getTime();
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return this.world.getRegistryManager();
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.world.getLightingProvider();
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return this.world.getPlayers();
    }

    @Override
    public float getBrightness(Direction direction, boolean shaded) {
        return this.world.getBrightness(direction, shaded);
    }

    @Override
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        this.world.updateListeners(pos,oldState,newState,flags);
    }

    @Override
    public void playSound(@Nullable PlayerEntity except, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        this.world.playSound(except, x, y, z, sound, category, volume, pitch);
    }

    @Override
    public String asString() {
        return this.world.asString();
    }

    @Override
    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    @Override
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return world.getBlockTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return world.getFluidTickScheduler();
    }

    @Override
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return 512;
    }

    @Override
    public void playSoundFromEntity(@Nullable PlayerEntity except, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
    }

    @Nullable
    @Override
    public Entity getEntityById(int id) {
        return null;
    }

    @Nullable
    @Override
    public MapState getMapState(String id) {
        return null;
    }

    @Override
    public void putMapState(String id, MapState state) {

    }

    @Override
    public int getNextMapId() {
        return 0;
    }

    @Override
    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {

    }

    @Override
    public RecipeManager getRecipeManager() {
        return null;
    }

    @Override
    protected EntityLookup<Entity> getEntityLookup() {
        return null;
    }

    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void emitGameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos) {

    }

}
