package examples.console.example2.events;

/**
 * All events implementing this interface will be treated as the same "type" of event
 * on the server. If multiple transactions tries to commit the same type of event at the same time
 * then a conflict will occur and no events will be saved.
 */
public interface UserEvent {
}
