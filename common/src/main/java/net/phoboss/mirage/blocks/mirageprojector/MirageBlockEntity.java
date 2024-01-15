package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageStructure;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MirageBlockEntity extends BlockEntity {
    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
        scheme = new HashMap();
    }

    private Block currentBlock = Blocks.ENCHANTING_TABLE;

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Block currentBlock) {
        this.currentBlock = currentBlock;
        markDirty();
    }

    static final Direction[] DIRECTIONS = Direction.values();
    public boolean isBlockFullySurrounded(BlockPos key,Map<BlockPos, StateNEntity> fullScheme){
        for(Direction dir: DIRECTIONS) {
            BlockPos pos = key.add(dir.getVector());
            StateNEntity neighbor = fullScheme.getOrDefault(pos, null);
            if (neighbor == null) {
                return false;
            }
            BlockState blockState = neighbor.blockState;
            if (blockState == null) {
                return false;
            }
            boolean isNeighborTransparent = !(blockState.isOpaqueFullCube(mirageWorld, pos) || blockState.isSolidBlock(mirageWorld, pos));
            if (isNeighborTransparent) {
                return false;
            }
        }
        return true;
    }

    private Map<BlockPos, StateNEntity> scheme;

    public Map<BlockPos, StateNEntity> getScheme() {
        return this.scheme;
    }

    public class StateNEntity {
        BlockState blockState;
        BlockEntity blockEntity;
        Entity entity;
        public StateNEntity(BlockState blockState,BlockEntity blockEntity) {
            this.blockState = blockState;
            this.blockEntity = blockEntity;
        }
        public StateNEntity(Entity entity) {
            this.entity = entity;
        }
        public void addEntity(Entity entity){
            this.entity = entity;
        }
    }

    public ArrayList<BlockState> getBlockPalette(NbtCompound nbt){
        NbtList paletteNbt = nbt.getList("palette", 10);
        ArrayList<BlockState> palette = new ArrayList<>();;
        for(int i = 0; i < paletteNbt.size(); i++) {
            palette.add(NbtHelper.toBlockState(paletteNbt.getCompound(i)));
        }
        return palette;
    }

    public Map<BlockPos, StateNEntity> createScheme(NbtCompound nbt) {

        BlockPos pos = getPos();
        MirageStructure fakeStructure = new MirageStructure();
        fakeStructure.readNbt(nbt);

        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setIgnoreEntities(false);
        //structurePlacementData.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
        //structurePlacementData.setRotation(BlockRotation.CLOCKWISE_90);
        //structurePlacementData.setRotation(BlockRotation.CLOCKWISE_180);

        //structurePlacementData.setMirror(BlockMirror.FRONT_BACK);
        //structurePlacementData.setMirror(BlockMirror.LEFT_RIGHT);
        fakeStructure.place(this.mirageWorld,pos,pos,structurePlacementData,this.mirageWorld.random,Block.NOTIFY_ALL);


        /*ArrayList<BlockState> palette = getBlockPalette(nbt);
        NbtList blocksNbt = nbt.getList("blocks", 10);
        for(int i = 0; i < blocksNbt.size(); i++) {
            NbtCompound blockNbt = blocksNbt.getCompound(i);
            BlockState fakeState = palette.get(blockNbt.getInt("state"));
            if(fakeState.isAir()){
                continue;
            }
            NbtList blockPosNbt = blockNbt.getList("pos", 3);
            BlockPos posNbt = getPos().add(new BlockPos(
                    blockPosNbt.getInt(0),
                    blockPosNbt.getInt(1),
                    blockPosNbt.getInt(2)
            ));
            BlockEntity fakeBlockEntity = BlockEntity.createFromNbt(posNbt,fakeState,blockNbt.getCompound("nbt"));
            mirageWorld.setBlockState(pos, fakeState, Block.NOTIFY_LISTENERS);
            if (fakeBlockEntity != null) {
                //fakeBlockEntity.setWorld(mirageWorld);//needs to be done AFTER setBlockState here to properly initialize FramedBlockEntity ModelData
                this.mirageWorld.addBlockEntity(fakeBlockEntity);
            }
        }*/

        /*Map<BlockPos, StateNEntity> scheme = new HashMap();
        ArrayList<BlockState> palette = getBlockPalette(nbt);
        NbtList blocksNbt = nbt.getList("blocks", 10);

        for(int i = 0; i < blocksNbt.size(); i++) {
            NbtCompound blockNbt = blocksNbt.getCompound(i);
            BlockState fakeState = palette.get(blockNbt.getInt("state"));
            if(fakeState.isAir()){
                continue;
            }
            NbtList blockPosNbt = blockNbt.getList("pos", 3);
            BlockPos pos = getPos().add(new BlockPos(
                                            blockPosNbt.getInt(0),
                                            blockPosNbt.getInt(1),
                                            blockPosNbt.getInt(2)
                                        ));


            BlockEntity fakeBlockEntity = BlockEntity.createFromNbt(pos,fakeState,blockNbt.getCompound("nbt"));

            mirageWorld.setBlockState(pos, fakeState, Block.NOTIFY_LISTENERS);
            if (fakeBlockEntity != null) {
                fakeBlockEntity.setWorld(mirageWorld);//needs to be done AFTER setBlockState here to properly initialize FramedBlockEntity ModelData
                mirageWorld.addBlockEntity(fakeBlockEntity);
            }


            scheme.put( pos,
                        new StateNEntity(
                                            fakeState,
                                            fakeBlockEntity
                                        )
            );
        }

        NbtList entitiesNbt = nbt.getList("entities", 10);
        for(int i = 0; i < entitiesNbt.size(); i++) {
            NbtCompound entityNbt = entitiesNbt.getCompound(i);
            NbtList entityPosNbt = entityNbt.getList("pos", 6);
            NbtList entityBlockPosNbt = entityNbt.getList("blockPos", 3);

            Vec3d entityPos = new Vec3d(entityPosNbt.getDouble(0)+getPos().getX(),
                                        entityPosNbt.getDouble(1)+getPos().getY(),
                                        entityPosNbt.getDouble(2)+getPos().getZ());



            BlockPos entityBlockPos = getPos().add(new BlockPos(
                    entityBlockPosNbt.getInt(0),
                    entityBlockPosNbt.getInt(1),
                    entityBlockPosNbt.getInt(2)
            ));


            entityPosNbt.set(0, NbtDouble.of(entityBlockPos.getX()));
            entityPosNbt.set(1, NbtDouble.of(entityBlockPos.getY()));
            entityPosNbt.set(2, NbtDouble.of(entityBlockPos.getZ()));


            if (!entityNbt.contains("nbt")) {
                continue;
            }
            NbtCompound entityInfoNbt = entityNbt.getCompound("nbt");
            entityInfoNbt.put("Pos", entityPosNbt);
            entityInfoNbt.remove("UUID");
            EntityType.getEntityFromNbt(entityInfoNbt, this.mirageWorld).ifPresent((entity) -> {
                //entity.setPos(entityPos.x,entityPos.y,entityPos.z);
                if (scheme.containsKey(entityBlockPos)) {
                    scheme.get(entityBlockPos).addEntity(entity);
                }
                scheme.put(entityBlockPos,new StateNEntity(entity));
            });
        }

        Map<BlockPos, StateNEntity> culledScheme = new HashMap();
        scheme.forEach((pos,stateNEntity)->{
            if (isBlockFullySurrounded(pos,scheme)) {
                return;
            }
            culledScheme.put(pos, stateNEntity);
        });*/

        Map<BlockPos, StateNEntity> culledScheme = new HashMap();
        return culledScheme;
    }



    private MirageWorld mirageWorld = new MirageWorld(MinecraftClient.getInstance());
    public void setSchemeFromNBT(NbtCompound nbt) {
        this.scheme = createScheme(nbt);
        markDirty();
    }

    public MirageWorld getMirageWorld() {
        return this.mirageWorld;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MirageBlockEntity entity) {
        entity.mirageWorld.tick();
    }

}
