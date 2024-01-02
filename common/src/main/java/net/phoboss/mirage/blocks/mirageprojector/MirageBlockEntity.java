package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MirageBlockEntity extends BlockEntity {
    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
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
    public boolean isBlockFullySurrounded(BlockPos key,HashMap<BlockPos, Block> fullScheme){
        for(Direction dir: DIRECTIONS){
            if(fullScheme.getOrDefault(key.add(dir.getVector()),null) == null){
                return false;
            }
        }
        return true;
    }

    private Map<BlockPos, Block> scheme = new HashMap<BlockPos, Block>();

    public Map<BlockPos, Block> getScheme() {
        return this.scheme;
    }

    public void setScheme() {
        HashMap<BlockPos, Block> fullScheme = new HashMap<BlockPos, Block>();
        for(int x=0;x<5;++x) {
            for (int y = 0; y < 5; ++y) {
                for (int z = 0; z < 5; ++z) {
                    fullScheme.put(new BlockPos(x,y,z),currentBlock);
                }
            }
        }
        HashMap<BlockPos, Block> culledScheme = new HashMap<BlockPos, Block>();
        fullScheme.forEach((key,value)->{
            if (isBlockFullySurrounded(key,fullScheme)) {
                return;
            }
            culledScheme.put(key, value);
        });
        this.scheme = culledScheme;
        markDirty();
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

    }


}
