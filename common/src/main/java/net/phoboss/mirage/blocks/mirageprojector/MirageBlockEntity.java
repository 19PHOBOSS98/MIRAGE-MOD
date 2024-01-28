package net.phoboss.mirage.blocks.mirageprojector;

import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.phoboss.mirage.Mirage;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.client.rendering.customworld.MirageStructure;
import net.phoboss.mirage.client.rendering.customworld.MirageWorld;
import net.phoboss.mirage.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MirageBlockEntity extends BlockEntity {
    public static Path SCHEMATICS_FOLDER = Platform.getGameFolder().resolve("schematics");
    public static List<String> MIRROR_STATES_KEYS = Util.make(new ArrayList<>(), (map) -> {
        map.add("NONE");
        map.add("FRONT_BACK");
        map.add("LEFT_RIGHT");
    });
    public static List<String> ROTATION_STATES_KEYS = Util.make(new ArrayList<>(), (map) -> {
        map.add("0");
        map.add("90");
        map.add("180");
        map.add("270");
    });
    public static Object2ObjectLinkedOpenHashMap<String, BlockMirror> MIRROR_STATES = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
        map.put(MIRROR_STATES_KEYS.get(0),BlockMirror.NONE);
        map.put(MIRROR_STATES_KEYS.get(1),BlockMirror.FRONT_BACK);
        map.put(MIRROR_STATES_KEYS.get(2),BlockMirror.LEFT_RIGHT);
    });
    public static Object2ObjectLinkedOpenHashMap<String, BlockRotation> ROTATION_STATES = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
        map.put(ROTATION_STATES_KEYS.get(0),BlockRotation.NONE);
        map.put(ROTATION_STATES_KEYS.get(1),BlockRotation.CLOCKWISE_90);
        map.put(ROTATION_STATES_KEYS.get(2),BlockRotation.CLOCKWISE_180);
        map.put(ROTATION_STATES_KEYS.get(3),BlockRotation.COUNTERCLOCKWISE_90);
    });
    public NbtCompound schematic = new NbtCompound();
    public Object2ObjectLinkedOpenHashMap<String,String> bookSettings;
    public Vec3i move = new Vec3i(0,0,0);
    public boolean activeLow = false;
    public String rotate = ROTATION_STATES_KEYS.get(0);
    public String mirror = MIRROR_STATES_KEYS.get(0);
    public String fileName = "";//filename(s) here

    public void setActiveLow(boolean activeLow) {
        this.activeLow = activeLow;
        bookSettings.put("activeLow",Boolean.toString(activeLow));//ServerSide doesn't update without it...
        markDirty();
    }
    public void setMove(Vec3i move) {
        this.move = move;
        bookSettings.put("move",move.getX()+","+move.getY()+","+move.getZ());
        markDirty();
    }
    public void setRotate(String rotate) {
        this.rotate = rotate;
        bookSettings.put("rotate",rotate);
        markDirty();
    }
    public void setMirror(String mirror) {
        this.mirror = mirror;
        bookSettings.put("mirror",mirror);
        markDirty();
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
        bookSettings.put("fileName",fileName);
        markDirty();
    }
    public boolean isActiveLow() {
        return activeLow;
    }
    public Vec3i getMove() {
        return move;
    }
    public String getRotate() {
        return rotate;
    }
    public String getMirror() {
        return mirror;
    }
    public String getFileName() {
        return fileName;
    }

    public Object2ObjectLinkedOpenHashMap<String,String> setupBookSettings(){
        Object2ObjectLinkedOpenHashMap<String,String> map = new Object2ObjectLinkedOpenHashMap<>();
        map.put("activeLow","false");
        map.put("move","0,0,0");//X,Y,Z
        map.put("rotate",ROTATION_STATES_KEYS.get(0));
        map.put("mirror",MIRROR_STATES_KEYS.get(0));
        map.put("fileName","");
        /*
        map.put("autoPlay","false");// stepPlay if false
        map.put("speed","1.0");// animation speed
        map.put("loop","false");// animation loop
         */
        return map;
    }
    private MirageWorld mirageWorld;

    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
        bookSettings = this.setupBookSettings();
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

        structurePlacementData.setRotation(ROTATION_STATES.get(getRotate()));
        structurePlacementData.setMirror(MIRROR_STATES.get(getMirror()));

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


    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("scheme",getSchematic());
        int[] move = {getMove().getX(),getMove().getY(),getMove().getZ()};
        nbt.putIntArray("move",move);
        nbt.putBoolean("activeLow",isActiveLow());
        nbt.putString("rotate",getRotate());
        nbt.putString("mirror",getMirror());
        nbt.putString("fileName",getFileName());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try{
            this.schematic = Objects.requireNonNullElse((NbtCompound) nbt.get("scheme"), new NbtCompound());

            int[] moveArray = nbt.getIntArray("move");
            this.move = new Vec3i(moveArray[0],moveArray[1],moveArray[2]);
            this.activeLow = nbt.getBoolean("activeLow");
            this.rotate = nbt.getString("rotate");
            this.mirror = nbt.getString("mirror");
            this.fileName = nbt.getString("fileName");

            this.bookSettings.put("activeLow",Boolean.toString(this.activeLow));
            this.bookSettings.put("move",this.move.getX()+","+this.move.getY()+","+this.move.getZ());
            this.bookSettings.put("rotate",this.rotate);
            this.bookSettings.put("mirror",this.mirror);
            this.bookSettings.put("fileName",this.fileName);

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
