package net.phoboss.mirage.blocks.mirageprojector;

import com.google.gson.Gson;
import dev.architectury.platform.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.phoboss.mirage.Mirage;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.client.rendering.customworld.MirageStructure;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;
import net.phoboss.mirage.client.rendering.customworld.StructureStates;
import net.phoboss.mirage.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;


public class MirageBlockEntity extends BlockEntity {
    public static Path SCHEMATICS_FOLDER = Platform.getGameFolder().resolve("schematics");

    public NbtCompound schematic = new NbtCompound();

    public void setActiveLow(boolean activeLow) {
        this.bookSettingsPOJO.setActiveLow(activeLow);
        markDirty();
    }
    public void setMove(Vec3i move) {
        this.bookSettingsPOJO.setMove(move);
        markDirty();
    }
    public void setRotate(String rotate) {
        this.bookSettingsPOJO.setRotate(Integer.parseInt(rotate));
        markDirty();
    }
    public void setMirror(String mirror) {
        this.bookSettingsPOJO.setMirror(mirror);
        markDirty();
    }
    public void setFileName(String fileName) {
        this.bookSettingsPOJO.setFile(fileName);
        markDirty();
    }
    public boolean isActiveLow() {
        return this.bookSettingsPOJO.isActiveLow();
    }
    public Vec3i getMove() {
        return this.bookSettingsPOJO.getMoveVec3i();
    }
    public int getRotate() {
        return this.bookSettingsPOJO.getRotate();
    }
    public String getMirror() {
        return this.bookSettingsPOJO.getMirror();
    }
    public String getFileName() {
        return this.bookSettingsPOJO.getFile();
    }

    private MirageWorld mirageWorld;

    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
        bookSettingsPOJO = new MirageProjectorBook();
    }

    public NbtCompound getSchematic(){
        if(schematic == null){
            return new NbtCompound();
        }
        return schematic;
    }

    public boolean setSchematic(NbtCompound nbtCompound){
        schematic = nbtCompound;
        markDirty();
        return !nbtCompound.isEmpty();
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
        BlockPos pos = getPos().add(getMove());
        MirageStructure fakeStructure = new MirageStructure();
        fakeStructure.readNbt(nbt);

        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setIgnoreEntities(false);

        structurePlacementData.setRotation(StructureStates.ROTATION_STATES.get(getRotate()));
        structurePlacementData.setMirror(StructureStates.MIRROR_STATES.get(getMirror()));

        mirageWorld.clearMirageWorld();
        fakeStructure.place(mirageWorld,pos,pos,structurePlacementData,mirageWorld.random,Block.NOTIFY_ALL);

        //this.mirageWorld.initVertexBuffers(pos);      //the RenderDispatchers "camera" subojects are null on initialization causing errors
        mirageWorld.overideRefreshBuffer = true;   //I couldn't find an Architectury API Event similar to Fabric's "ClientBlockEntityEvents.BLOCK_ENTITY_LOAD" event
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
            mirageWorld = new MirageWorld(world);
        }
    }


    public static NbtCompound getBuildingNbt(String structureName, World world, BlockPos pos, PlayerEntity player) {
        File nbtFile = SCHEMATICS_FOLDER.resolve(structureName+".nbt").toFile();
        try {
            return NbtIo.readCompressed(nbtFile);
        }
        catch (Exception e) {
            Mirage.LOGGER.error("Couldn't load file: "+nbtFile, e);
            ErrorResponse.onError(world,pos,player,"Couldn't load file: "+nbtFile);
            return null;
        }
    }

    public void setMirage(PlayerEntity player) {
        setSchematicMirage(getFileName(),player);
    }
    public void setSchematicMirage(String filename,PlayerEntity player) {
        if(filename.isEmpty()){
            return;
        }
        if(setSchematic(Objects.requireNonNullElse(getBuildingNbt(filename,world,pos,player),getSchematic()))){
            loadScheme();
        }
    }


    public MirageWorld getMirageWorld() {
        return mirageWorld;
    }

    public MirageProjectorBook bookSettingsPOJO;

    public MirageProjectorBook getBookSettingsPOJO() {
        return bookSettingsPOJO;
    }

    public void setBookSettingsPOJO(MirageProjectorBook bookSettingsPOJO) {
        this.bookSettingsPOJO = bookSettingsPOJO;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {

        nbt.putString("bookJSON",new Gson().toJson(this.bookSettingsPOJO));

        nbt.put("scheme",getSchematic());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try{
            String bookString = nbt.getString("bookJSON");

            this.bookSettingsPOJO = bookString.isEmpty() ? new MirageProjectorBook() : new Gson().fromJson(bookString, MirageProjectorBook.class);


            this.schematic = Objects.requireNonNullElse((NbtCompound) nbt.get("scheme"), new NbtCompound());

            if(this.mirageWorld != null) {
                loadScheme();
            }

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

    public boolean isPowered() {
        boolean active = false;
        try {
            active = getWorld().getEmittedRedstonePower(getPos().down(), Direction.DOWN)>0;
            active = isActiveLow() != active;
        }catch(Exception e){
            Mirage.LOGGER.error("Error on isPowered() method: ",e);
        }
        return active;
    }

    public static void tick(World world, BlockPos pos, BlockState state, MirageBlockEntity entity) {
        if(entity.isPowered()) {
            MirageWorld mirageWorld = entity.mirageWorld;
            if (mirageWorld != null) {
                entity.mirageWorld.tick();
            }
        }
    }

}
