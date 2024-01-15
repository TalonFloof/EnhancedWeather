package sh.talonfloof.enhancedweather.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sh.talonfloof.enhancedweather.network.ScreenOpen;

public class RadarBlock extends Block {
    public RadarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        if(!world.isClient()) {
            ScreenOpen.send((ServerPlayerEntity)player,blockPos);
        }
        return ActionResult.SUCCESS;
    }
}