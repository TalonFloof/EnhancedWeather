package sh.talonfloof.enhancedweather.commands;

package sh.talonfloof.enhancedweather.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import sh.talonfloof.enhancedweather.EnhancedWeather;
import sh.talonfloof.enhancedweather.events.Tornado;

import java.util.UUID;

public class SpawnTornadoCommand { // Example: /wxtornadoevent -42 785.75 500 - =D
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("wxtornadoevent")
                    .then(CommandManager.argument("x", IntegerArgumentType.integer())
                            .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                    .then(CommandManager.argument("intensityMax", IntegerArgumentType.integer())
                                            .executes(context -> execute(context.getSource(),
                                                    FloatArgumentType.getFloat(context, "x"),
                                                    FloatArgumentType.getFloat(context, "z"),
                                                    IntegerArgumentType.getInteger(context, "intensityMax")
                                            ))))));
        });
    }

    private static int execute(ServerCommandSource source, float x, float z, int intensityMax) {
        Tornado t = new Tornado(x,192,z,intensityMax);
        EnhancedWeather.events.put(UUID.randomUUID(),t);
        return 1;
    }
}
