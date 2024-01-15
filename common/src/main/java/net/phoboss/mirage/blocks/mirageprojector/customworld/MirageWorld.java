package net.phoboss.mirage.blocks.mirageprojector.customworld;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
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
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.tick.QueryableTickScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MirageWorld extends World implements ServerWorldAccess {

    public class StateNEntity {
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
        public void addEntity(Entity entity){
            this.entity = entity;
        }
    }

    protected World world;

    protected Map<BlockPos, FluidState> mirageFluidStates;
    protected Map<BlockPos, BlockState> mirageBlockStates;
    protected Map<BlockPos, BlockEntity> mirageBlockEntities;
    protected List<Entity> mirageEntities;

    protected Long2ObjectOpenHashMap<StateNEntity> mirageStateNEntities;

    protected List<BlockTicker> mirageBlockEntityTickers;
    protected ChunkManager chunkManager;
    public final TextureManager mirageTextureManager;

    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static BlockRenderManager blockRenderManager = mc.getBlockRenderManager();
    public static BlockEntityRenderDispatcher blockEntityRenderDispatcher = mc.getBlockEntityRenderDispatcher();
    public static EntityRenderDispatcher entityRenderDispatcher = mc.getEntityRenderDispatcher();
    public static WorldRenderer worldRenderer = mc.worldRenderer;
    public static GameRenderer gameRenderer = mc.gameRenderer;

    public MirageWorld(MinecraftClient MC) {
        this(MC.world,MC.getTextureManager());
    }
    public MirageWorld(World world,TextureManager textureManager) {
        super((MutableWorldProperties) world.getLevelProperties(),
                world.getRegistryKey(),
                world.method_40134(),
                world::getProfiler,
                world.isClient(),
                world.isDebugWorld(),
                0);
        this.world = world;
        this.mirageBlockStates = new HashMap();
        this.mirageFluidStates = new HashMap();
        this.mirageBlockEntities = new HashMap();
        this.mirageBlockEntityTickers = new ArrayList();
        this.mirageEntities = new ArrayList();

        this.mirageStateNEntities = new Long2ObjectOpenHashMap();

        this.mirageTextureManager = textureManager;
        setChunkManager(new MirageChunkManager(this));
    }

    public void render(BlockPos projectorPos,float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        this.mirageStateNEntities.forEach((fakeBlockPosKey,fakeStateNEntity)->{
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

            if (fakeBlockState != null) {
                renderMirageBlock(fakeBlockState, fakeBlockPos, this, matrices, vertexConsumers, true, getRandom(), fakeBlockEntity);
            }
            if (fakeBlockEntity != null) {
                renderMirageBlockEntity(fakeBlockEntity, tickDelta, matrices, vertexConsumers);
            }
            matrices.pop();
        });
    }

    @ExpectPlatform
    public static void renderMirageBlock(BlockState state, BlockPos referencePos, BlockRenderView world, MatrixStack matrices, VertexConsumerProvider vertexConsumers, boolean cull, Random random, BlockEntity blockEntity){
        RenderLayer rl = RenderLayers.getEntityBlockLayer(state,true);
        blockRenderManager.renderBlock(state,referencePos,world,matrices,
                vertexConsumers.getBuffer(rl),cull,random);
    }
    public void renderMirageBlockEntity(BlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        blockEntityRenderDispatcher.render(blockEntity,tickDelta,matrices,vertexConsumers);
    }
    public void renderMirageEntity(Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        this.entityRenderDispatcher.render(entity, 0, 0, 0, entity.getYaw(), tickDelta, matrices, vertexConsumers, this.entityRenderDispatcher.getLight(entity, tickDelta));
    }

    static final Direction[] DIRECTIONS = Direction.values();
    public boolean isBlockFullySurrounded(long key,Long2ObjectOpenHashMap<StateNEntity> fullScheme){
        for(Direction dir: DIRECTIONS) {
            BlockPos pos = BlockPos.fromLong(key).add(dir.getVector());
            long keyPos = pos.asLong();
            StateNEntity neighbor = fullScheme.getOrDefault(keyPos, null);
            if (neighbor == null) {
                return false;
            }
            BlockState blockState = neighbor.blockState;
            if (blockState == null) {
                return false;
            }
            boolean isNeighborTransparent = !(blockState.isOpaqueFullCube(this, pos) || blockState.isSolidBlock(this, pos));
            if (isNeighborTransparent) {
                return false;
            }
        }
        return true;
    }

    public void cullBlocks(){
        ArrayList<Long> removeKeys = new ArrayList<>();
        this.mirageStateNEntities.forEach((key,entry)->{
            if (isBlockFullySurrounded(key,this.mirageStateNEntities)) {
                removeKeys.add(key);
            }
        });
        for (long key:removeKeys) {
            this.mirageStateNEntities.remove(key);
        }
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
        //this.mirageBlockEntities.put(pos,blockEntity);
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
        /*if(!fluidState.isEmpty()){
            this.mirageFluidStates.put(pos,fluidState);
        }*/
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

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
        return setBlockState(pos, state);
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
    public boolean spawnEntity(Entity entity) {
        //this.mirageEntities.add(entity);
        spawnEntity(entity.getBlockPos(), entity);
        return true;
    }

    @Override
    public void addBlockEntity(BlockEntity blockEntity) {
        BlockPos pos = blockEntity.getPos();
        blockEntity.setWorld(this);//needs to be done AFTER setBlockState here to properly initialize FramedBlockEntity ModelData
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
        //entry.blockEntity.setWorld(this);
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

    public Long2ObjectOpenHashMap<StateNEntity> getMirageStateNEntities(){
        return this.mirageStateNEntities;
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
        synchronized (this.mirageBlockEntityTickers){
            this.mirageBlockEntityTickers.forEach((blockTicker)->{
                blockTicker.blockEntityTicker.tick(this,blockTicker.blockPos,blockTicker.blockState,blockTicker.blockEntity);
            });
        }
    }

/*
    //I didn't look into it any further
    public void tickFluids(){
        synchronized (this.mirageFluidStates) {
            this.mirageFluidStates.forEach((pos, fluidState) -> {
                fluidState.onScheduledTick(this, pos);
            });
        }
    }

 */



    public void tick(){
        //tickFluids();
        tickBlockEntities();
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    public World getWorld(){
        return this.world;
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
    public QueryableTickScheduler<Block> getBlockTickScheduler() {
        return world.getBlockTickScheduler();
    }

    @Override
    public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
        return world.getFluidTickScheduler();
    }



    @Override
    public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {

    }

    @Override
    public void emitGameEvent(@Nullable Entity entity, GameEvent event, BlockPos pos) {

    }








}
