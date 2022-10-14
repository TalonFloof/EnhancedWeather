package sh.talonfox.enhancedweather;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sh.talonfox.enhancedweather.network.UpdateStorm;
import sh.talonfox.enhancedweather.weather.Ambience;
import sh.talonfox.enhancedweather.weather.Cloud;
import sh.talonfox.enhancedweather.weather.ServersideManager;
import sh.talonfox.enhancedweather.weather.frontal.SquallLine;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.*;

public class CommandsRegister {
    public static void Initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("enhancedweather").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("summonOverworld")
                            .then(literal("rain").executes(context -> {
                                Cloud cloud = new Cloud(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                cloud.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
                                cloud.Precipitating = true;
                                UUID id = UUID.randomUUID();
                                Enhancedweather.SERVER_WEATHER.Clouds.put(id,cloud);
                                for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                    UpdateStorm.send(context.getSource().getServer(), id, null, j);
                                }
                                context.getSource().sendMessage(Text.literal("Summoning Rain Storm"));
                                return 1;
                                    })
                            )
                            .then(literal("thunder").executes(context -> {
                                        Cloud cloud = new Cloud(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                        cloud.Thundering = true;
                                        cloud.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
                                        cloud.Precipitating = true;
                                        UUID id = UUID.randomUUID();
                                        Enhancedweather.SERVER_WEATHER.Clouds.put(id,cloud);
                                        for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                            UpdateStorm.send(context.getSource().getServer(), id, null, j);
                                        }
                                        context.getSource().sendMessage(Text.literal("Summoning Thunder Storm"));
                                        return 1;
                                    })
                            )
                            .then(literal("supercell").executes(context -> {
                                        Cloud cloud = new Cloud(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                        cloud.Supercell = true;
                                        cloud.Thundering = true;
                                        cloud.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
                                        cloud.Precipitating = true;
                                        UUID id = UUID.randomUUID();
                                        Enhancedweather.SERVER_WEATHER.Clouds.put(id,cloud);
                                        for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                            UpdateStorm.send(context.getSource().getServer(), id, null, j);
                                        }
                                        context.getSource().sendMessage(Text.literal("Summoning Supercell"));
                                        return 1;
                                    })
                            )
                            .then(literal("squallLine").executes(context -> {
                                    SquallLine sl = new SquallLine(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                    return 1;
                                })
                            )
                    )
                    .then(literal("killallOverworld").executes(context -> {
                        for (UUID i : Enhancedweather.SERVER_WEATHER.Clouds.keySet()) {
                            for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                UpdateStorm.send(context.getSource().getServer(), i, null, j);
                            }
                        }
                        Enhancedweather.SERVER_WEATHER.Clouds.clear();
                        Ambience.HighWindExists = false;
                        context.getSource().sendMessage(Text.literal("Clearing all Weather"));
                        return 1;
                    })));
        });
    }
}
