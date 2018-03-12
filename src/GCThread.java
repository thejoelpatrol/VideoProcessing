public class GCThread extends Thread {
    private static final int SLEEP_MILLIS = 2000;
    private boolean finish = false;

    @Override
    public void run() {
        while (!finish) {
            try {
                sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) { }
            System.gc();
            System.runFinalization();
        }
    }

    public void done() {
        finish = true;
    }
}
