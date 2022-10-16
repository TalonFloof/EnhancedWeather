package sh.talonfox.enhancedweather.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sh.talonfox.enhancedweather.client.screens.RadarScreen;

public class RadarBlock extends Block implements BlockEntityProvider {
    public RadarBlock(Settings settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RadarBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if(world.isClient()) {
            MinecraftClient.getInstance().setScreen(new RadarScreen(blockPos));
        }
        return ActionResult.SUCCESS;
    }
}
