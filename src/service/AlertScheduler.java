package service;

import dao.DoseDAO;
import model.Alert;

import javax.swing.Timer;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

/**
 * Core data-structure logic: a PriorityQueue<Alert> ordered by dose time.
 * peek() = next dose (O(1)), poll()/add() = O(log n).
 * A Swing Timer checks the head of the queue once per second.
 */
public class AlertScheduler {
    private final PriorityQueue<Alert> queue = new PriorityQueue<>();
    private final DoseDAO doseDAO = new DoseDAO();
    private final Consumer<Alert> onAlert;
    private Timer timer;

    public AlertScheduler(Consumer<Alert> onAlert) { this.onAlert = onAlert; }

    public void start() throws SQLException {
        if (timer != null) timer.stop();
        queue.clear();
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        for (Alert a : doseDAO.loadAlerts())
            if (!a.getTime().isBefore(now)) queue.add(a);   // only today's upcoming doses

        timer = new Timer(1000, e -> tick());
        timer.start();
    }

    public void stop() {
        if (timer != null) timer.stop();
        queue.clear();
    }

    public boolean isRunning() { return timer != null && timer.isRunning(); }

    private void tick() {
        LocalTime now = LocalTime.now();
        while (!queue.isEmpty() && !now.isBefore(queue.peek().getTime())) {
            onAlert.accept(queue.poll());
        }
    }

    /** Sorted snapshot of what is still waiting in the queue (for display). */
    public List<Alert> getUpcoming() {
        PriorityQueue<Alert> copy = new PriorityQueue<>(queue);
        List<Alert> sorted = new ArrayList<>();
        while (!copy.isEmpty()) sorted.add(copy.poll());
        return sorted;
    }
}
