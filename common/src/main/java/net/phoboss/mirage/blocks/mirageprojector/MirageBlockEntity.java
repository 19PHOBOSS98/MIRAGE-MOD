package net.phoboss.mirage.blocks.mirageprojector;

import dev.architectury.platform.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.Mirage;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.client.rendering.customworld.MirageStructure;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;


public class MirageBlockEntity extends BlockEntity {
    public static Path SCHEMATICS_FOLDER = Platform.getGameFolder().resolve("schematics");
    public NbtCompound schematic = new NbtCompound();
    private MirageWorld mirageWorld;

    public NbtCompound getSchematic(){
        if(this.schematic == null){
            return new NbtCompound();
        }
        return this.schematic;
    }

    public boolean setSchematic(NbtCompound nbtCompound){
        if(nbtCompound == null){
            this.schematic = new NbtCompound();
            markDirty();
            return true;
        }else{
            this.schematic = nbtCompound;
            markDirty();
            return false;
        }
    }

    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
    }

    public void loadScheme() {
        loadScheme(getSchematic());
    }
    public void loadScheme(NbtCompound nbt) {//add BlockRotation, BlockMirror and PosOffset arguments
        if(!world.isClient()) {
            return;
        }
        if(nbt == null){
            return;
        }
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
        this.mirageWorld.clearMirageWorld();
        fakeStructure.place(this.mirageWorld,pos,pos,structurePlacementData,this.mirageWorld.random,Block.NOTIFY_ALL);

        //this.mirageWorld.initVertexBuffers(pos);      //the RenderDispatchers "camera" subojects are null on initialization causing errors
        this.mirageWorld.overideRefreshBuffer = true;   //I couldn't find an Architectury API Event similar to Fabric's "ClientBlockEntityEvents.BLOCK_ENTITY_LOAD" event
                                                        //I could try to use @ExpectPlatform but I couldn't find anything similar for Forge either.
                                                        // So I just let the BER.render(...) method decide when's the best time to refresh the VertexBuffers :)

    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        setMirageWorld(world);
    }

    public void setMirageWorld(World world) {
        if(world.isClient()){
            this.mirageWorld = new MirageWorld(world);
        }
    }


    public static NbtCompound getBuildingNbt(String structureName) {
        try {
            File nbtFile = SCHEMATICS_FOLDER.resolve(structureName+".nbt").toFile();
            NbtCompound nbtc = NbtIo.readCompressed(nbtFile);
            return nbtc;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


    public void setSchematicMirage(String filename) {
        if(filename ==""){
            return;
        }
        if(setSchematic(getBuildingNbt(filename))){
            loadScheme();
        }
        markDirty();
    }


    public MirageWorld getMirageWorld() {
        return this.mirageWorld;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("scheme",getSchematic());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try{
            setSchematic((NbtCompound) nbt.get("scheme"));
            if(this.mirageWorld == null) {
                return;
            }
            loadScheme();
        }catch (Exception e){
            Mirage.LOGGER.error("[MIRAGE MOD]: Error on readNBT: ",e);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = super.toInitialChunkDataNbt();
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void markDirty() {
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        super.markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MirageBlockEntity entity) {
        MirageWorld mirageWorld = entity.mirageWorld;
        if(mirageWorld != null) {
            entity.mirageWorld.tick();
        }
    }

}
