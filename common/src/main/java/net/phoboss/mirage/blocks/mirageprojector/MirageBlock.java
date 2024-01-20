package net.phoboss.mirage.blocks.mirageprojector;

import dev.architectury.platform.Platform;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.mirage.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

public class MirageBlock extends BlockWithEntity implements BlockEntityProvider {
    public MirageBlock(Settings settings) {
        super(settings);
    }

    public static Path SCHEMATICS_FOLDER = Platform.getGameFolder().resolve("schematics");
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

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = player.getMainHandStack().getItem();
        if(hand == Hand.MAIN_HAND){
            //NbtCompound nbt = getBuildingNbt("test", resourceManager);
            //NbtCompound nbt = getBuildingNbt("redstonetest", resourceManager);
            //NbtCompound nbt = getBuildingNbt("mirage1", resourceManager);
            NbtCompound nbt = getBuildingNbt("miragetestingwentities");
            //NbtCompound nbt = getBuildingNbt("paintings", resourceManager);
            //NbtCompound nbt = getBuildingNbt("portal1", resourceManager);
            //NbtCompound nbt = getBuildingNbt("warp", resourceManager);
            MirageBlockEntity entity = (MirageBlockEntity) world.getBlockEntity(pos);
            if(world.isClient()) {
                    entity.setSchemeFromNBT(nbt);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
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

}
