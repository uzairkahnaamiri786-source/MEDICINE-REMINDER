package model;

import java.time.LocalTime;

/** An upcoming dose. Ordered by time so the PriorityQueue keeps the earliest first. */
public class Alert implements Comparable<Alert> {
    private final LocalTime time;
    private final String userName, medicineName, dosage;

    public Alert(LocalTime time, String userName, String medicineName, String dosage) {
        this.time = time; this.userName = userName;
        this.medicineName = medicineName; this.dosage = dosage;
    }
    public LocalTime getTime() { return time; }
    public String getUserName() { return userName; }
    public String getMedicineName() { return medicineName; }
    public String getDosage() { return dosage; }

    @Override public int compareTo(Alert o) { return time.compareTo(o.time); }
}
