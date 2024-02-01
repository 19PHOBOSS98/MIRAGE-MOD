package net.phoboss.mirage.blocks.miragezoetrope;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.blocks.mirageprojector.MirageBlock;
import org.jetbrains.annotations.Nullable;

public class MirageZoetropeBlock extends MirageBlock {
    public MirageZoetropeBlock(Settings settings) {
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MirageZoetropeBlockEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.MIRAGE_ZOETROPE_BLOCK.get(), MirageZoetropeBlockEntity::tick);
    }

    /*
    @Override
    public void implementBookSettings(BlockEntity blockEntity, JsonObject newSettings) throws Exception{
        if(blockEntity instanceof MirageZoetropeBlockEntity mirageZoetropeBlockEntity){
            MirageZoetropeBook newBook = MirageZoetropeBook.validateNewBookSettings(newSettings);
            mirageZoetropeBlockEntity.setBookSettingsPOJO(newBook);
        }
    }

    /*public static void loadMirage(MirageZoetropeBlockEntity blockEntity) throws Exception{
        try {
            if (blockEntity != null) {
                blockEntity.startMirages();
            }
        }catch(Exception e){
            throw new Exception(e.getMessage(),e);
        }
    }*/

}
