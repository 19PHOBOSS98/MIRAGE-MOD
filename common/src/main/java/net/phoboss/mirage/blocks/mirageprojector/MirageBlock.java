package net.phoboss.mirage.blocks.mirageprojector;

import com.google.gson.JsonObject;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.Mirage;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.utility.BookSettingsUtility;
import net.phoboss.mirage.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

public class MirageBlock extends BlockWithEntity implements BlockEntityProvider, BookSettingsUtility {
    public MirageBlock(Settings settings) {
        super(settings);
    }
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MirageBlockEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.MIRAGE_BLOCK.get(), MirageBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            ItemStack mainHandItemStack = player.getMainHandStack();
            Item mainHandItem = player.getMainHandStack().getItem();
            if (hand == Hand.MAIN_HAND) {
                if(!world.isClient()) {
                    MirageBlockEntity blockEntity = (MirageBlockEntity) world.getBlockEntity(pos);
                    if (mainHandItem == Items.REDSTONE_TORCH) {
                        blockEntity.setActiveLow(!blockEntity.isActiveLow());
                        return ActionResult.SUCCESS;
                    } else if (mainHandItemStack.hasNbt() && mainHandItemStack.getNbt().contains("pages")) {
                        try {
                            executeBookProtocol(mainHandItemStack, blockEntity, blockEntity.getBookSettingsPOJO());
                            loadMirage(blockEntity);
                            return ActionResult.SUCCESS;
                        }catch (Exception e){
                            Mirage.LOGGER.error(e.getMessage(),e);
                            ErrorResponse.onError(world,pos,player,e.getMessage());
                            return ActionResult.FAIL;
                        }
                    }
                }
                return ActionResult.SUCCESS;
            }
        return ActionResult.PASS;
    }

    @Override
    public void implementBookSettings(BlockEntity blockEntity, JsonObject newSettings) throws Exception{
        if(blockEntity instanceof MirageBlockEntity mirageBlockEntity){
            MirageProjectorBook newBook = (MirageProjectorBook) mirageBlockEntity.getBookSettingsPOJO().validateNewBookSettings(newSettings);
            mirageBlockEntity.setBookSettingsPOJO(newBook);
        }
    }

    public static void loadMirage(MirageBlockEntity blockEntity) throws Exception{
        try {
            if (blockEntity != null) {
                blockEntity.startMirage();
            }
        }catch(Exception e){
            throw new Exception(e.getMessage(),e);
        }
    }

}
