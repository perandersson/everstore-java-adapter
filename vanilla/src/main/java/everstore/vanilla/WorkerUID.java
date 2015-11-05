package everstore.vanilla;

public final class WorkerUID {
    public final int value;

    public WorkerUID(int value) {
        this.value = value;
    }

    public static final WorkerUID ZERO = new WorkerUID(0);
}
