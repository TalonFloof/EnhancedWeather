package sh.talonfox.enhancedweather.events;

public class Tornado extends WeatherEvent {
    public int intensity = 0;
    public Tornado(double x, double y, double z) {
        super(x,y,z);
    }
}
