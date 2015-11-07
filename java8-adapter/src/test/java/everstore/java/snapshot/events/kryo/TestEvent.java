package everstore.java.snapshot.events.kryo;

public class TestEvent {
    public final int value;

    protected TestEvent() {
        value = 0;
    }

    public TestEvent(int value) {
        this.value = value;
    }
}
