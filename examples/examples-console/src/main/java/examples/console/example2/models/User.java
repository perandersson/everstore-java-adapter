package examples.console.example2.models;

public final class User {
    public final UserId id;
    public final String name;

    public User(UserId id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Set the name and return the result as a new user instance
     *
     * @param newName
     * @return
     */
    public User setName(String newName) {
        return new User(id, newName);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
