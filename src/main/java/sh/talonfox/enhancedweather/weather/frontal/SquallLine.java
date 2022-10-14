package sh.talonfox.enhancedweather.weather.frontal;

import net.minecraft.util.math.Vec3d;
import sh.talonfox.enhancedweather.weather.Manager;
import sh.talonfox.enhancedweather.weather.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class SquallLine extends Weather {
    public HashMap<Integer, UUID> Storms = new HashMap<Integer, UUID>();
    /*
    Intensity Legend:
    Intensity 0: Proto-Squall Line (few, largely spaced storms)
    Intensity 1: Squall Line (fully formed, with each cell having a 50% change of having wind)
    Intensity 2: Severe Squall Line (All cells have strong wind, potential of supercell forming tornado)
     */
    public int Intensity = 0;
    public int MaxIntensity = 0;
    public boolean PeakedIntensity = false;
    public float MovementAngle = 0;

    public SquallLine(Manager manager, Vec3d pos) {
        HostManager = manager;
        Position = pos;
        MovementAngle = new Random().nextFloat(360);
    }

    @Override
    public void tickClient() {

    }

    @Override
    public void tickServer() {

    }

    public Vec3d calculateCellPosition(int index) {
        float posAngle = MovementAngle-90;
        Vec3d posVec = new Vec3d(-Math.sin(Math.toRadians(posAngle)),0, Math.cos(Math.toRadians(posAngle))).multiply(150);
        return posVec;
    }
}
