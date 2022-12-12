package cat.util;

public final class MillisTimer {
    public long millis = -1L;
    public static long lastMS = System.currentTimeMillis();
    
    public boolean hasTimeReached(final long ms) {
        return System.currentTimeMillis() >= millis + ms;
    }

    public long getTimeDiff(final long ms) {
        return (ms + millis) - System.currentTimeMillis();
    }
    
    public boolean hasReached(long time) {
		if(System.currentTimeMillis() - lastMS >= time) {
			return true;
		}
		return false;
	}

    public void reset() {
        millis = System.currentTimeMillis();
    }
    
    public static void reset1() {
		lastMS = System.currentTimeMillis();
	}

    public boolean hasTicksPassed(final float ticks){
        return System.currentTimeMillis() - millis >= (ticks * 50);
    }

    public static boolean hasTimeElapsed(long time, boolean reset) {
		if(System.currentTimeMillis()-lastMS > time) {
			if(reset) 
				reset1();
			return true;
		}	
		
		return false;
	}
    
    public long getTime() {
        return System.nanoTime() / 1000000L;
    }
    
    private long prevMS = 0L;

    public boolean delay(float milliSec) {
       return (float)MathUtil.getIncremental((double)(this.getTime() - this.prevMS), 50.0D) >= milliSec;
    }
    
    public static boolean hasTimeElapsed(long time) {
		if(System.currentTimeMillis()-lastMS > time) {
			return true;
		}
		return false;
	}
}
