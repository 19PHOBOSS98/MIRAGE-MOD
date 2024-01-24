package net.phoboss.mirage.blocks.mirageprojector;

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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import net.phoboss.mirage.utility.BookSettingsUtility;
import net.phoboss.mirage.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

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
                        ActionResult result = executeBookProtocol(mainHandItemStack, state, world, pos, player, blockEntity);
                        if (result == ActionResult.FAIL) {
                            refreshBlockEntityBookSettings(state, blockEntity);
                        }
                /*
                //TEST SCHEMATICS
                fileName = "miragetestingwentities";
                String fileName = "test";
                String fileName = "redstonetest";
                String fileName = "mirage1";
                String fileName = "paintings";
                String fileName = "portal1";
                String fileName = "warp";
                */
                        loadMirage(blockEntity, player);
                        return result;
                    }
                }


                return ActionResult.SUCCESS;
            }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult implementBookSettings(  BlockState state,
                                                World world,
                                                BlockPos pos,
                                                PlayerEntity player,
                                                BlockEntity blockEntity,
                                                Map<String, String> bookSettings) {

        if(blockEntity instanceof MirageBlockEntity mirageBlockEntity){
            String activeLow = bookSettings.get("activeLow");
            String fileName = bookSettings.get("fileName");

            String move = bookSettings.get("move");
            String rotate = bookSettings.get("rotate");
            String mirror = bookSettings.get("mirror");
            try {
                if (!activeLow.isEmpty()) {
                    mirageBlockEntity.setActiveLow(Boolean.parseBoolean(activeLow));
                }
            } catch (Exception e) {
                return ErrorResponse.onErrorActionResult(e, world, pos, player, "Invalid Entry: activeLow:" + activeLow);
            }

            try {
                if (!fileName.isEmpty()) {
                    mirageBlockEntity.setFileName(fileName);
                }
            } catch (Exception e) {
                return ErrorResponse.onErrorActionResult(e, world, pos, player, "Invalid Entry: fileName:" + fileName);
            }

            try {
                if (!move.isEmpty()) {
                    mirageBlockEntity.setMove(Objects.requireNonNullElse(BookSettingsUtility.parseBookVec3i(move), new Vec3i(0,0,0)));
                }
            } catch (Exception e) {
                return ErrorResponse.onErrorActionResult(e, world, pos, player, "Invalid Entry: move:" + move);
            }

            try {
                if (!rotate.isEmpty()) {
                    if(!MirageBlockEntity.ROTATION_STATES_KEYS.contains(rotate)){
                        throw new Exception();
                    }
                    mirageBlockEntity.setRotate(rotate);
                }
            } catch (Exception e) {
                return ErrorResponse.onErrorActionResult(e, world, pos, player, "Invalid Entry: rotate:" + rotate);
            }

            try {
                if (!mirror.isEmpty()) {
                    if(!MirageBlockEntity.MIRROR_STATES_KEYS.contains(mirror)){
                        throw new Exception();
                    }
                    mirageBlockEntity.setMirror(mirror);
                }
            } catch (Exception e) {
                return ErrorResponse.onErrorActionResult(e, world, pos, player, "Invalid Entry: mirror:" + mirror);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void refreshBlockEntityBookSettings(BlockState blockState, BlockEntity blockEntity) {
        if(blockEntity instanceof MirageBlockEntity mirageBlockEntity){
            mirageBlockEntity.bookSettings.put("activeLow",Boolean.toString(mirageBlockEntity.isActiveLow()));
            mirageBlockEntity.bookSettings.put("fileName",mirageBlockEntity.getFileName());

            mirageBlockEntity.bookSettings.put("move",BookSettingsUtility.convertToString(mirageBlockEntity.getMove()));
            mirageBlockEntity.bookSettings.put("rotate",mirageBlockEntity.getRotate());
            mirageBlockEntity.bookSettings.put("mirror",mirageBlockEntity.getMirror());
        }
    }

    public static void loadMirage(MirageBlockEntity blockEntity,PlayerEntity player){
        if(blockEntity != null) {
            blockEntity.setMirage(player);
        }
    }

}
