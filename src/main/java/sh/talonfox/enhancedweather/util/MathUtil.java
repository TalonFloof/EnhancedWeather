package sh.talonfox.enhancedweather.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil {
    public static long wrap(long value, long side) {
        if (side != 0 && (side & side - 1) == 0) {
            return value & (side - 1);
        }
        long result = (value - value / side * side);
        return result < 0 ? result + side : result;
    }
}
