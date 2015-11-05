package everstore.vanilla;

import java.util.Optional;

public final class Event {
    public final String data;
    public final Optional<String> timestamp;

    public Event(String data, Optional<String> timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }
}
