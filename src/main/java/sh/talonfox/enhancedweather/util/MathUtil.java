package sh.talonfox.enhancedweather.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil {
    public static int wrap(int value, int side) {
        if (MathHelper.isPowerOfTwo(side)) {
            return value & (side - 1);
        }
        int result = (value - value / side * side);
        return result < 0 ? result + side : result;
    }
}
