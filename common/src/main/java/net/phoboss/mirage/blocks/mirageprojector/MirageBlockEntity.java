package net.phoboss.mirage.blocks.mirageprojector;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageStructure;
import net.phoboss.mirage.blocks.mirageprojector.customworld.MirageWorld;
import org.jetbrains.annotations.Nullable;


public class MirageBlockEntity extends BlockEntity {
    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
    }

    public void loadScheme(NbtCompound nbt) {

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
        this.mirageWorld.initVertexBuffers(pos);
    }


    private MirageWorld mirageWorld = new MirageWorld(MinecraftClient.getInstance());
    public void setSchemeFromNBT(NbtCompound nbt) {
        loadScheme(nbt);
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
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MirageBlockEntity entity) {
        entity.mirageWorld.tick();
    }

}
