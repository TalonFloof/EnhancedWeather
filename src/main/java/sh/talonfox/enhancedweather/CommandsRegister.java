package sh.talonfox.enhancedweather;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import sh.talonfox.enhancedweather.network.UpdateStorm;
import sh.talonfox.enhancedweather.weather.Ambience;
import sh.talonfox.enhancedweather.weather.weatherevents.Cloud;
import sh.talonfox.enhancedweather.weather.weatherevents.SquallLine;

import java.util.UUID;
import java.util.stream.IntStream;

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
                                Enhancedweather.SERVER_WEATHER.Weathers.put(id,cloud);
                                for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                    UpdateStorm.send(context.getSource().getServer(), id, null, j);
                                }
                                context.getSource().sendMessage(Text.literal("Summoning Rain Storm"));
                                return 1;
                                    })
                            )
                            .then(literal("thunder")
                                    .then(argument("hailIntensity", IntegerArgumentType.integer(0,2))
                                    .then(argument("maxHailIntensity", IntegerArgumentType.integer(-1,2))
                                    .then(argument("windIntensity", IntegerArgumentType.integer(0,3))
                                    .then(argument("maxWindIntensity", IntegerArgumentType.integer(-1,3)).executes(context -> {
                                        Cloud cloud = new Cloud(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                        cloud.Thundering = true;
                                        cloud.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
                                        cloud.Precipitating = true;
                                        cloud.HailIntensity = context.getArgument("hailIntensity",Integer.class);
                                        if(context.getArgument("maxHailIntensity",Integer.class) != -1) {
                                            cloud.MaxHailIntensity = Math.max(cloud.HailIntensity,context.getArgument("maxHailIntensity",Integer.class));
                                        }
                                        cloud.WindIntensity = context.getArgument("windIntensity",Integer.class);
                                        UUID id = UUID.randomUUID();
                                        Enhancedweather.SERVER_WEATHER.Weathers.put(id,cloud);
                                        for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                            UpdateStorm.send(context.getSource().getServer(), id, null, j);
                                        }
                                        context.getSource().sendMessage(Text.literal("Summoning Thunder Storm"));
                                        return 1;
                                    })
                            )))))
                            .then(literal("supercell")
                                    .then(argument("hailIntensity",IntegerArgumentType.integer(0,2))
                                    .then(argument("maxHailIntensity",IntegerArgumentType.integer(-1,2))
                                    .executes(context -> {
                                        Cloud cloud = new Cloud(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                        cloud.Supercell = true;
                                        cloud.Thundering = true;
                                        cloud.Water = Enhancedweather.CONFIG.Weather_MinimumWaterToPrecipitate*2;
                                        cloud.Precipitating = true;
                                        cloud.HailIntensity = context.getArgument("hailIntensity",Integer.class);
                                        if(context.getArgument("maxHailIntensity",Integer.class) != -1) {
                                            cloud.MaxHailIntensity = Math.max(cloud.HailIntensity,context.getArgument("maxHailIntensity",Integer.class));
                                        }
                                        UUID id = UUID.randomUUID();
                                        Enhancedweather.SERVER_WEATHER.Weathers.put(id,cloud);
                                        for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                            UpdateStorm.send(context.getSource().getServer(), id, cloud.generateUpdate(), j);
                                        }
                                        context.getSource().sendMessage(Text.literal("Summoning Supercell"));
                                        return 1;
                                    })
                            )))
                            .then(literal("tornado")
                                .then(argument("intensity",IntegerArgumentType.integer(0,5))
                                .then(argument("maxIntensity",IntegerArgumentType.integer(-1,5))
                                .then(argument("hailIntensity",IntegerArgumentType.integer(0,2))
                                .then(argument("maxHailIntensity",IntegerArgumentType.integer(-1,2))
                                    .executes(context -> {

                                        return 1;
                                    })))))
                            )
                            .then(literal("squallLine")
                                    .then(argument("intensity", IntegerArgumentType.integer(0, 2)).executes(context -> {
                                    SquallLine sl = new SquallLine(Enhancedweather.SERVER_WEATHER,context.getSource().getPosition().multiply(1,0,1).add(0,200,0));
                                    UUID id = UUID.randomUUID();
                                    Enhancedweather.SERVER_WEATHER.Weathers.put(id,sl);
                                    for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                        UpdateStorm.send(context.getSource().getServer(), id, sl.generateUpdate(), j);
                                    }
                                    context.getSource().sendMessage(Text.literal("Summoning Squall Line"));
                                    return 1;
                                }))
                            )
                    )
                    .then(literal("killallOverworld").executes(context -> {
                        for (UUID i : Enhancedweather.SERVER_WEATHER.Weathers.keySet()) {
                            for (ServerPlayerEntity j : PlayerLookup.all(context.getSource().getServer())) {
                                UpdateStorm.send(context.getSource().getServer(), i, null, j);
                            }
                        }
                        Enhancedweather.SERVER_WEATHER.Weathers.clear();
                        Ambience.HighWindExists = false;
                        context.getSource().sendMessage(Text.literal("Clearing all Weather"));
                        return 1;
                    })));
        });
    }
}
