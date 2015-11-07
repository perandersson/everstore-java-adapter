package examples.console.example2;

public final class User {
    public final UserId id;
    public final String name;

    public User(UserId id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
