package ru.benchmark.functional;

/**
 * @author evans
 *         18.04.14.
 */

public class TicTac implements Runnable {
    private static enum Status {
        TIC {
            public Status next() {
                return TAC;
            }
        }, TAC {
            public Status next() {
                return TIC;
            }
        };
        public abstract Status next();
    }

    private static Status currStatus = Status.TIC;

    private final Status status;

    public TicTac(Status status) {
        this.status = status;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (TicTac.class) {
                while (status != currStatus) {
                    try {
                        TicTac.class.wait();
                    } catch (InterruptedException e) {
                        System.exit(1);
                    }
                }
                System.out.println(status);
                currStatus = status.next();
                TicTac.class.notifyAll();
            }
        }
    }

    public static void main(String[] args) {
        new Thread(new TicTac(Status.TIC)).start();
//        new Thread(new TicTac(Status.TAC)).start();
    }
}
