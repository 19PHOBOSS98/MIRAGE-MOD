package net.phoboss.decobeacon.blocks.decobeacon;

import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;

import java.util.Arrays;
import java.util.List;

public class DecoBeaconBlockEntity extends BlockEntity {

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state,Boolean isGhost) {
        this(pos, state);
        this.isGhost = isGhost;
    }

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECO_BEACON.get(), pos, state);
        //world.setBlockState(pos,state.with(DecoBeaconBlock.COLOR,this.prevColorID),Block.NOTIFY_ALL);
    }

    @ExpectPlatform
    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        throw new AssertionError();
    }

    private int prevY;
    List<DecoBeamSegment> decoBeamSegments = Lists.<DecoBeamSegment>newArrayList();
    private List<DecoBeamSegment> segmentsBuffer = Lists.<DecoBeamSegment>newArrayList();

    private boolean wasPowered = false;

    private int prevColorID = 0;

    private boolean isGhost = false;

    public List<DecoBeamSegment> getDecoBeamSegments() {
        return decoBeamSegments;
    }

    public int getDecoBeamSegmentsTotalHeight(){
        int totalHeight = 0;
        for (DecoBeamSegment segment:getDecoBeamSegments()) {
            totalHeight += segment.getHeight();
        }
        return totalHeight;
    }

    public boolean wasPowered() {
        return wasPowered;
    }

    public void setWasPowered(boolean wasPowered) {
        this.wasPowered = wasPowered;
    }

    public boolean isGhost() {
        return this.isGhost;
    }

    public boolean isPowered() {
        World world = this.getWorld();
        BlockPos pos = this.getPos();
        boolean active = world.isReceivingRedstonePower(pos);
        active = this.getCachedState().get(DecoBeaconBlock.ACTIVE_LOW) != active;
        return active;
    }

    public static void tick(World world, BlockPos pos, BlockState state, DecoBeaconBlockEntity blockEntity) {
        boolean isPowered = blockEntity.isPowered();
        boolean isGhost = blockEntity.isGhost();

        if(!world.isClient()){// note to self only update state properties in server-side
            world.setBlockState(pos,state.with(Properties.LIT,isPowered),Block.NOTIFY_ALL);
        }

        int curColorID = state.get(DecoBeaconBlock.COLOR);
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos blockPos;

        if (blockEntity.prevColorID != curColorID) {
            blockEntity.prevY = world.getBottomY() - 1;
            blockEntity.prevColorID = curColorID;
        }


        if (blockEntity.prevY < j) {
            blockPos = pos;
            blockEntity.segmentsBuffer = Lists.<DecoBeamSegment>newArrayList();
            blockEntity.prevY = pos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, blockEntity.prevY + 1, k);
        }

        DecoBeamSegment decoBeamSegment = blockEntity.segmentsBuffer.isEmpty()
                ? null
                : blockEntity.segmentsBuffer.get(blockEntity.segmentsBuffer.size() - 1);

        int worldSurface = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        boolean opaqueBlockDetected = false;
        for (int m = 0; m < 10 && blockPos.getY() <= worldSurface; ++m) {
            BlockState blockState = world.getBlockState(blockPos);
            float[] colorMultiplier = getColorMultiplier(blockState);
            if (colorMultiplier != null) {
                if (blockEntity.segmentsBuffer.size() <= 1) {
                    decoBeamSegment = new DecoBeamSegment(colorMultiplier);
                    blockEntity.segmentsBuffer.add(decoBeamSegment);
                } else if (decoBeamSegment != null) {
                    if (Arrays.equals(colorMultiplier, decoBeamSegment.color)) {
                        decoBeamSegment.increaseHeight();
                    } else {
                        decoBeamSegment = new DecoBeamSegment(new float[]{
                                (decoBeamSegment.color[0] + colorMultiplier[0]) / 2.0F,
                                (decoBeamSegment.color[1] + colorMultiplier[1]) / 2.0F,
                                (decoBeamSegment.color[2] + colorMultiplier[2]) / 2.0F});
                        blockEntity.segmentsBuffer.add(decoBeamSegment);
                    }
                }
            } else {
                if (decoBeamSegment == null) {
                    blockEntity.segmentsBuffer.clear();
                    blockEntity.prevY = worldSurface;
                    break;
                }

                if (blockState.getOpacity(world, blockPos) >= 15 && !isGhost) {
                    blockEntity.prevY = worldSurface;
                    decoBeamSegment.increaseHeight();
                    opaqueBlockDetected = true;
                    break;
                }

                decoBeamSegment.increaseHeight();
            }

            blockPos = blockPos.up();
            ++blockEntity.prevY;
        }

        if (world.getTime() % 80L == 0L) {
            if (isPowered) {
                playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }

        opaqueBlockDetected = !isGhost && opaqueBlockDetected;

        if (blockEntity.prevY >= worldSurface || opaqueBlockDetected) {
            blockEntity.prevY = world.getBottomY() - 1;
            if (!opaqueBlockDetected && !blockEntity.segmentsBuffer.isEmpty()){
                blockEntity.segmentsBuffer.get(blockEntity.segmentsBuffer.size()-1).overrideHeight(1024);
            }

            blockEntity.decoBeamSegments = blockEntity.segmentsBuffer;

            if (!world.isClient) {
                if (!isPowered && blockEntity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                } else if (isPowered && !blockEntity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                }
                blockEntity.setWasPowered(isPowered);
            }
        }


    }
    public static float[] getColorMultiplier(BlockState blockState)
    {
        Block block = blockState.getBlock();

        if (block instanceof Stainable) {
            return block instanceof DecoBeaconBlock ? DyeColor.byId(blockState.get(DecoBeaconBlock.COLOR)).getColorComponents() : ((Stainable) block).getColor().getColorComponents();
        }
        return null;
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.prevY = world.getBottomY() - 1;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
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
