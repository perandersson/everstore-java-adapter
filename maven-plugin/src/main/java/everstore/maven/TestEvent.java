package everstore.maven;

public class TestEvent implements EventSerializable {
    public final int id;
    public final String name;
    public final TestEvent event;

    public TestEvent() {
        id = 0;
        name = "";
        event = null;
    }

    public TestEvent(int id, String name, TestEvent event) {
        this.id = id;
        this.name = name;
        this.event = event;
    }

    @Override
    public void serializeEvent(EventWriter writer) {
        writer.put("id", id);
        writer.put("name", name);
        writer.put("event", event);
    }

//    public void serializeEvent(EventWriter s) {
//        s.put("id", id);
//    }
//
//    public static TestEvent deserializeEvent(EventWriter s) {
//        return new TestEvent(
//                s.getInt("id")
//        );
//    }
}
