package model;

public class Medicine {
    private final int id, userId;
    private final String userName, name, dosage;

    public Medicine(int id, int userId, String userName, String name, String dosage) {
        this.id = id; this.userId = userId; this.userName = userName;
        this.name = name; this.dosage = dosage;
    }
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    @Override public String toString() { return name + " (" + userName + ")"; }
}
