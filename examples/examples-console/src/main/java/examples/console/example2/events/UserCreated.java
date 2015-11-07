package examples.console.example2.events;

public final class UserCreated implements UserEvent {
    public final long userId;
    public final String firstName;
    public final String lastName;

    protected UserCreated() {
        userId = 0;
        firstName = "John";
        lastName = "Doe";
    }

    public UserCreated(long userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCreated that = (UserCreated) o;

        if (userId != that.userId) return false;
        if (!firstName.equals(that.firstName)) return false;
        return lastName.equals(that.lastName);

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        return result;
    }
}
