package net.phoboss.decobeacon.blocks.decobeacon;

import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DecoBeaconBlockEntity extends BlockEntity {

    private boolean isGhost = false;

    public boolean isGhost() {
        return this.isGhost;
    }

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECO_BEACON.get(), pos, state);
    }

    public DecoBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state,Boolean isGhost) {
        this(pos, state);
        this.isGhost = isGhost;
    }

    public DecoBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,Boolean isGhost) {
        super(type, pos, state);
        this.isGhost = isGhost;
    }





    @ExpectPlatform
    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        throw new AssertionError();
    }

    private boolean activeLow = false;

    private boolean isTransparent = true;

    public boolean isActiveLow(){
        return this.activeLow;
    }

    public void setActiveLow(boolean activeLow){
        this.activeLow = activeLow;
        markDirty();
    }

    public boolean isTransparent() {
        return this.isTransparent;
    }

    public void setTransparent(boolean transparent) {
        this.isTransparent = transparent;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("activeLow",isActiveLow());
        nbt.putBoolean("isTransparent",isTransparent());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.activeLow = nbt.getBoolean("activeLow");
        this.isTransparent = nbt.getBoolean("isTransparent");
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
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        super.markDirty();
    }

    public int prevY;

    public int getPrevY() {
        return prevY;
    }

    public void setPrevY(int prevY) {
        this.prevY = prevY;
    }

    public List<DecoBeamSegment> segmentsBuffer = Lists.<DecoBeamSegment>newArrayList();

    public List<DecoBeamSegment> getSegmentsBuffer() {
        return segmentsBuffer;
    }

    public void setSegmentsBuffer(List<DecoBeamSegment> segmentsBuffer) {
        this.segmentsBuffer = segmentsBuffer;
    }

    public int prevColorID = 0;

    public int getPrevColorID() {
        return prevColorID;
    }

    public void setPrevColorID(int prevColorID) {
        this.prevColorID = prevColorID;
    }

    public List<DecoBeamSegment> decoBeamSegments = Lists.<DecoBeamSegment>newArrayList();

    public List<DecoBeamSegment> getDecoBeamSegments() {
        return decoBeamSegments;
    }

    public void setDecoBeamSegments(List<DecoBeamSegment> decoBeamSegments) {
        this.decoBeamSegments = decoBeamSegments;
    }

    public int getDecoBeamSegmentsTotalHeight(){
        int totalHeight = 0;
        for (DecoBeamSegment segment:getDecoBeamSegments()) {
            totalHeight += segment.getHeight();
        }
        return totalHeight;
    }

    public boolean wasPowered = false;

    public boolean wasPowered() {
        return wasPowered;
    }

    public void setWasPowered(boolean wasPowered) {
        this.wasPowered = wasPowered;
    }



    public boolean isPowered() {
        World world = this.getWorld();
        BlockPos pos = this.getPos();
        boolean active = world.isReceivingRedstonePower(pos);
        //active = this.getCachedState().get(OmniBeaconBlock.ACTIVE_LOW) != active;
        active = this.isActiveLow() != active;
        return active;
    }

    public static void tick(World world, BlockPos pos, BlockState state, DecoBeaconBlockEntity entity) {
        boolean isPowered = entity.isPowered();
        boolean passThruSolid = entity.isGhost();

        if(!world.isClient()){// note to self only update state properties in server-side
            world.setBlockState(pos,state.with(Properties.LIT,isPowered),Block.NOTIFY_ALL);
        }

        int curColorID = state.get(DecoBeaconBlock.COLOR);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos blockPos;

        if (entity.prevColorID != curColorID) {
            entity.prevY = world.getBottomY() - 1;
            entity.prevColorID = curColorID;
        }


        if (entity.prevY < j) {
            blockPos = pos;
            entity.segmentsBuffer = Lists.<DecoBeamSegment>newArrayList();
            entity.prevY = pos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, entity.prevY + 1, k);
        }

        DecoBeamSegment decoBeamSegment = entity.segmentsBuffer.isEmpty()
                ? null
                : entity.segmentsBuffer.get(entity.segmentsBuffer.size() - 1);

        int worldSurface = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        boolean opaqueBlockDetected = false;

        if (entity.segmentsBuffer.size() < 1) {
            decoBeamSegment = new DecoBeamSegment(DyeColor.byId(state.get(DecoBeaconBlock.COLOR)).getColorComponents());
            entity.segmentsBuffer.add(decoBeamSegment);
            blockPos = blockPos.up();
            ++entity.prevY;
        }

        for (int m = 0; m < 10 && blockPos.getY() <= worldSurface; ++m) {
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);

            float[] colorMultiplier = getColorMultiplier(blockState);
            if (colorMultiplier != null) {

                if (blockState.getBlock() instanceof DecoBeaconBlock) {
                    if(blockEntity instanceof DecoBeaconBlockEntity be){
                        if(!be.isTransparent()){
                            if(!be.isGhost()) {
                                entity.prevY = worldSurface;
                                //decoBeamSegment.increaseHeight();
                                opaqueBlockDetected = true;
                                break;
                            }else{
                                colorMultiplier = decoBeamSegment.color;
                            }
                        }
                    }
                }

                if (decoBeamSegment != null) {
                    if (Arrays.equals(colorMultiplier, decoBeamSegment.color)) {
                        decoBeamSegment.increaseHeight();
                    } else {
                        decoBeamSegment = new DecoBeamSegment(new float[]{
                                (decoBeamSegment.color[0] + colorMultiplier[0]) / 2.0F,
                                (decoBeamSegment.color[1] + colorMultiplier[1]) / 2.0F,
                                (decoBeamSegment.color[2] + colorMultiplier[2]) / 2.0F});
                        entity.segmentsBuffer.add(decoBeamSegment);
                    }
                }
            } else {
                if (decoBeamSegment == null) {
                    entity.segmentsBuffer.clear();
                    entity.prevY = worldSurface;
                    break;
                }

                if (blockState.getOpacity(world, blockPos) >= 15 && !passThruSolid) {
                    entity.prevY = worldSurface;
                    //decoBeamSegment.increaseHeight();
                    opaqueBlockDetected = true;
                    break;
                }

                decoBeamSegment.increaseHeight();
            }

            blockPos = blockPos.up();
            ++entity.prevY;
        }

        if (world.getTime() % 80L == 0L) {
            if (isPowered) {
                playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }

        opaqueBlockDetected = !passThruSolid && opaqueBlockDetected;

        if (entity.prevY >= worldSurface || opaqueBlockDetected) {
            entity.prevY = world.getBottomY() - 1;
            if (!opaqueBlockDetected && !entity.segmentsBuffer.isEmpty()){
                entity.segmentsBuffer.get(entity.segmentsBuffer.size()-1).overrideHeight(1024);
            }

            entity.decoBeamSegments = entity.segmentsBuffer;

            if (!world.isClient) {
                if (!isPowered && entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                } else if (isPowered && !entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                }
                entity.setWasPowered(isPowered);
            }
        }


    }

    public static float[] getColorMultiplier(BlockState blockState)
    {
        Block block = blockState.getBlock();

        if (!(block instanceof Stainable)) {
            return null;
        }

        return block instanceof DecoBeaconBlock ?
                DyeColor.byId(blockState.get(DecoBeaconBlock.COLOR)).getColorComponents()
                : ((Stainable) block).getColor().getColorComponents();
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.prevY = world.getBottomY() - 1;
    }





    public static class DecoBeamSegment {
        final float[] color;
        private int height;

        public DecoBeamSegment(float[] color) {
            this.color = color;
            this.height = 1;
        }

        public void increaseHeight() {
            ++this.height;
        }

        public void overrideHeight(int newHeight) {
            this.height = newHeight;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
