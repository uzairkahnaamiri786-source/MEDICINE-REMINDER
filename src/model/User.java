package model;

public class User {
    private final int id;
    private final String name, phone;

    public User(int id, String name, String phone) {
        this.id = id; this.name = name; this.phone = phone;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    @Override public String toString() { return name; }
}
