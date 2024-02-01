package net.phoboss.mirage.blocks.mirageprojector;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MirageBlockEntity extends BlockEntity {
    public MirageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRAGE_BLOCK.get(), pos, state);
        setBookSettingsPOJO(new MirageProjectorBook());
    }
    public MirageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public void setActiveLow(boolean activeLow) {
        getBookSettingsPOJO().setActiveLow(activeLow);
        markDirty();
    }
    public void setMove(Vec3i move) {
        getBookSettingsPOJO().setMove(move);
        markDirty();
    }
    public void setRotate(String rotate) {
        getBookSettingsPOJO().setRotate(Integer.parseInt(rotate));
        markDirty();
    }
    public void setMirror(String mirror) {
        getBookSettingsPOJO().setMirror(mirror);
        markDirty();
    }

    public boolean isActiveLow() {
        return getBookSettingsPOJO().isActiveLow();
    }
    public Vec3i getMove() {
        return getBookSettingsPOJO().getMoveVec3i();
    }
    public int getRotate() {
        return getBookSettingsPOJO().getRotate();
    }
    public String getMirror() {
        return getBookSettingsPOJO().getMirror();
    }
    public List<String> getFileNames() {
        return getBookSettingsPOJO().getFiles();
    }
    private List<MirageWorld> mirageWorlds;

    public void resetMirageWorlds() {
        if(mirageWorlds != null){
            mirageWorlds.clear();
        }
    }
    public void resetMirageWorlds(World world, int count){
        resetMirageWorlds();
        if(mirageWorlds != null) {
            for (int i = 0; i < count; ++i) {
                mirageWorlds.add(new MirageWorld(world));
            }
        }
    }public void addMirageWorld(){
        mirageWorlds.add(new MirageWorld(world));
    }

    public void loadMirage() throws Exception{
        if(this.mirageWorlds == null){
            return;
        }
        String fileName = "";
        try {
            List<String> files = getFileNames();
            int fileCount = files.size();
            resetMirageWorlds(world,fileCount);

            HashMap<Integer,Frame> frames = getBookSettingsPOJO().getFrames();

            for(int i=0;i<fileCount;++i){
                fileName = files.get(i);
                NbtCompound buildingNBT = getBuildingNbt(fileName);
                MirageWorld mirageWorld = this.mirageWorlds.get(i);
                Vec3i actualMove = getMove();
                int actualRotate = getRotate();
                String actualMirror = getMirror();

                Frame frame;
                if(frames.containsKey(i)){
                    frame = frames.get(i);
                }else{
                    loadMirage(mirageWorld,buildingNBT,actualMove,actualRotate,actualMirror);
                    continue;
                }

                String mainMirror = getMirror();
                String subMirror = frame.getMirror();

                actualMove = actualMove.add(frame.getMoveVec3i());
                actualRotate += frame.getRotate();


                if(mainMirror.equals(subMirror)){
                    actualMirror = "NONE";
                }else if(mainMirror.equals("NONE")){
                    actualMirror = subMirror;
                }else if(subMirror.equals("NONE")){
                    actualMirror = mainMirror;
                }else {
                    actualMirror = "NONE";
                    actualRotate += 180;
                }

                actualRotate %= 360;
                loadMirage(mirageWorld,buildingNBT,actualMove,actualRotate,actualMirror);
            }
        }catch (Exception e) {
            throw new Exception("Couldn't read nbt file: "+fileName,e);
        }
    }
    public void loadMirage(MirageWorld mirageWorld, NbtCompound nbt, Vec3i move, int rotate, String mirror) {
        if(!world.isClient()) {
            return;
        }
        if(nbt == null){
            return;
        }
        BlockPos pos = getPos().add(move);
        MirageStructure fakeStructure = new MirageStructure();
        fakeStructure.readNbt(nbt);

        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setIgnoreEntities(false);

        structurePlacementData.setRotation(StructureStates.ROTATION_STATES.get(rotate));
        structurePlacementData.setMirror(StructureStates.MIRROR_STATES.get(mirror));

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
        this.mirageWorlds = new ArrayList<>();
    }


    public static NbtCompound getBuildingNbt(String structureName) throws Exception{
        File nbtFile = getBuildingNbtFile(structureName);
        try {
            return NbtIo.readCompressed(nbtFile);
        }
        catch (Exception e) {
            throw new Exception("Couldn't read nbt file: "+nbtFile,e);
        }
    }
    public static File getBuildingNbtFile(String structureName) throws Exception{
        File nbtFile = null;
        try {
            nbtFile = Mirage.SCHEMATICS_FOLDER.resolve(structureName+".nbt").toFile();
            if(nbtFile.exists()){
                return nbtFile;
            }
        }
        catch (Exception e) {
            throw new Exception("Couldn't open file: \n"+nbtFile.getName(),e);
        }
        throw new Exception("Couldn't find: "+nbtFile.getName()+"\nin schematics folder: "+Mirage.SCHEMATICS_FOLDER.getFileName());
    }
    public void startMirage() throws Exception{
        validateNBTFiles(getFileNames());
        markDirty();//load schematic to mirageWorld in "readNBT(...)"
    }

    public void validateNBTFiles(List<String> fileNames) throws Exception{
        try{
            for(String fileName : fileNames){
                if(fileName.isEmpty()){
                    throw new Exception("Blank File Name");
                }
                getBuildingNbtFile(fileName);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage(),e);
        }
    }


    public List<MirageWorld> getMirageWorlds() {
        return mirageWorlds;
    }

    public MirageProjectorBook bookSettingsPOJO;

    public MirageProjectorBook getBookSettingsPOJO() {
        return this.bookSettingsPOJO;
    }

    public void setBookSettingsPOJO(MirageProjectorBook bookSettingsPOJO) {
        this.bookSettingsPOJO = bookSettingsPOJO;
    }

    public String serializeBook() throws Exception{
        return new Gson().toJson(getBookSettingsPOJO());
    }

    public void deserializeBook(String bookString) throws Exception{
        setBookSettingsPOJO(bookString.isEmpty() ? new MirageProjectorBook() : new Gson().fromJson(bookString, MirageProjectorBook.class));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        try {
            nbt.putString("bookJSON",serializeBook());
            nbt.putInt("mirageWorldIndex",getMirageWorldIndex());
        } catch (Exception e) {
            Mirage.LOGGER.error("Error on writeNBT: ",e);
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        try{
            deserializeBook(nbt.getString("bookJSON"));
            this.mirageWorldIndex = nbt.getInt("mirageWorldIndex");
            loadMirage();
        }catch (Exception e){
            Mirage.LOGGER.error("Error on readNBT: ",e);
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

    public int mirageWorldIndex = 0;

    public int getMirageWorldIndex() {
        return this.mirageWorldIndex;
    }

    public void setMirageWorldIndex(int mirageWorldIndex) {
        if(getBookSettingsPOJO().isLoop()){
            this.mirageWorldIndex = Math.abs(mirageWorldIndex % getMirageWorlds().size());
        }else{
            this.mirageWorldIndex = Math.abs(Math.max(0,Math.min(mirageWorldIndex,getMirageWorlds().size()-1)));
        }
        markDirty();
    }
    public long previousTime = System.currentTimeMillis();

    public void incrementMirageWorldIndex(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.previousTime >= getBookSettingsPOJO().getDelay()*1000) {

            int index = getMirageWorldIndex() + 1;

            setMirageWorldIndex(index);
            this.previousTime = currentTime;
        }
    }


    public static void tick(World world, BlockPos pos, BlockState state, MirageBlockEntity blockEntity) {

        if(blockEntity.isPowered()) {
            blockEntity.getMirageWorlds().forEach((mirageWorld)->{
                mirageWorld.tick();
            });
        }

    }

}
