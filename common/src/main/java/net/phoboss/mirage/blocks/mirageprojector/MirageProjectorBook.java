package net.phoboss.mirage.blocks.mirageprojector;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.math.Vec3i;
import net.phoboss.mirage.client.rendering.customworld.StructureStates;
import net.phoboss.mirage.utility.Book;

//this class is used to handle Book&Quill interface
public class MirageProjectorBook implements Book {
    @SerializedName("type")
    private String typeName = getClass().getName();
    int[] move = {0,0,0};
    String mirror = "NONE";
    int rotate = 0;
    boolean activeLow = false;
    String file = "";


    public int[] getMove() {
        return move;
    }

    public Vec3i getMoveVec3i() {
        return new Vec3i(move[0],move[1],move[2]);
    }

    public void setMove(int[] move) {
        this.move = move;
    }
    public void setMove(Vec3i move) {
        setMove(new int[]{move.getX(), move.getY(), move.getZ()});
    }
    public String getMirror() {
        return mirror;
    }

    public void setMirror(String mirror) {
        this.mirror = mirror;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public boolean isActiveLow() {
        return activeLow;
    }

    public void setActiveLow(boolean activeLow) {
        this.activeLow = activeLow;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


    public static MirageProjectorBook validateNewBookSettings(JsonObject newSettings) throws Exception {
        JsonArray moveArray = newSettings.get("move").getAsJsonArray();
        if(moveArray.size()>3){
            throw new Exception("Invalid Move Value: "+ newSettings.get("move"));
        }
        try {
            moveArray.forEach((mv)->{
                mv.getAsInt();
            });
        }catch (Exception e){
            throw new Exception("Invalid Move Value: "+ newSettings.get("move"));
        }

        MirageProjectorBook newBook = new Gson().fromJson(newSettings, MirageProjectorBook.class);

        if(!StructureStates.ROTATION_STATES_KEYS.contains(newBook.getRotate())){
            throw new Exception("Invalid Rotation Value: "+ newBook.getRotate() +"\nSupported Values: 0,90,180,270");
        }
        if(!StructureStates.MIRROR_STATES_KEYS.contains(newBook.getMirror())){
            throw new Exception("Invalid Mirror Value: "+ newBook.getMirror() +"\nSupported Values: NONE,FRONT_BACK,LEFT_RIGHT");
        }

        return newBook;
    }
}
