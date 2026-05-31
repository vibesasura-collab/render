public class SharedSignal {

    private static volatile boolean started = false;

    public static void startSignal() {
        started = true;
    }

    public static boolean isStarted() {
        return started;
    }
}
