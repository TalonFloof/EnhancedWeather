package sh.talonfloof.enhancedweather.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.events.WeatherEvent;

import java.util.UUID;

import static sh.talonfloof.enhancedweather.block.RadarBlock.LIGHT;

public class RadarBlockEntity extends BlockEntity {
    public int count = 0;
    public RadarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.RADAR_BLOCK_ENTITY, pos, state);
    }
    public static void tick(World world, BlockPos pos, BlockState state, RadarBlockEntity blockEntity) {
        if(world.isClient())
            return;
        int newCount = 0;
        for(UUID id : EnhancedWeather.events.keySet()) {
            WeatherEvent w = EnhancedWeather.events.get(id);
            Vec3d ourPos = new Vec3d(pos.getX(),w.position.y,pos.getZ());
            if(w.position.distanceTo(ourPos) < 2048) {
                newCount++;
            }
        }
        if(newCount > blockEntity.count && !state.get(LIGHT)) {
            world.setBlockState(pos, blockEntity.getCachedState().with(LIGHT, true));
            world.playSound(null, pos, SoundEvent.of(new Identifier("enhancedweather:radar.beep")), SoundCategory.BLOCKS,1F,1.0F);
        }
        blockEntity.count = newCount;
    }
}
