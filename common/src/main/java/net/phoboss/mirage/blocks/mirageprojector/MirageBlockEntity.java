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
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

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
