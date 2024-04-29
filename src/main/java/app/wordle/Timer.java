package app.wordle;

public class Timer {
    public static long elapsedTime = 0;
    public static long totalTime = 0;
    public static long bestTime = 0;

    private static long startTime = 0;

    public static String format(long ms) {
        long m = ms/60000;
        long s = (ms - m*60000)/1000;
        long cs = (ms - m*60000 - s*1000)/10;
        return ((m > 10)? m : "0" + m) + ":" +
                ((s > 10)? s : "0" + s) + ":" +
                ((cs > 10)? cs : "0" + cs);
    }

    public static void stop() {
        elapsedTime = System.currentTimeMillis() - startTime;
    }

    public static void setBestTime () {
        if (elapsedTime < ((bestTime == 0)? Long.MAX_VALUE : bestTime))
            bestTime = elapsedTime;
    }

    public static void addTotalTime() {
        totalTime += elapsedTime;
    }

    public static void start() {
        startTime = System.currentTimeMillis();
    }
}