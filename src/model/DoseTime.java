package model;

import java.time.LocalTime;

public class DoseTime {
    private final int id;
    private final String medicineName, userName;
    private final LocalTime time;

    public DoseTime(int id, String medicineName, String userName, LocalTime time) {
        this.id = id; this.medicineName = medicineName;
        this.userName = userName; this.time = time;
    }
    public int getId() { return id; }
    public String getMedicineName() { return medicineName; }
    public String getUserName() { return userName; }
    public LocalTime getTime() { return time; }
}
