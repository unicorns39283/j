package cat.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

public class MathUtil {
	
	public final static SecureRandom RANDOM = new SecureRandom();
	
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static double round(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }
    public static int inRange(int value, int min, int max){
        return Math.max(Math.min(value, max), min);
    }
    public static float inRange(float value, float min, float max){
        return Math.max(Math.min(value, max), min);
    }
    
    public static double getIncremental(double val, double inc) {
        double one = 1.0D / inc;
        return (double)Math.round(val * one) / one;
     }
    
    public static double roundToDecimal(double number, double places) {
        return Math.round(number * Math.pow(10, places)) / Math.pow(10, places);
    }
    
    public static double getDifference(double base, double yaw) {
		final double bigger;
		if (base >= yaw)
			bigger = base - yaw;
		else
			bigger = yaw - base;
		return bigger;
	}

	public static float getDifference(float base, float yaw) {
		float bigger;
		if (base >= yaw)
			bigger = base - yaw;
		else
			bigger = yaw - base;
		return bigger;
	}

	public static long getDifference(long base, long yaw) {
		long bigger;
		if (base >= yaw)
			bigger = base - yaw;
		else
			bigger = yaw - base;

		return bigger;
	}
	
	public static double getRandom_double(double min, double max) {
		if (min > max) return min;
		Random RANDOM = new Random();
		return RANDOM.nextDouble() * (max - min) + min;
	}
}
