package ui;

import dao.*;
import model.*;
import service.AlertScheduler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private final UserDAO userDAO = new UserDAO();
    private final MedicineDAO medDAO = new MedicineDAO();
    private final DoseDAO doseDAO = new DoseDAO();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

    private final DefaultTableModel userModel = model("ID", "Name", "Phone");
    private final DefaultTableModel medModel = model("ID", "User", "Medicine", "Dosage");
    private final DefaultTableModel doseModel = model("ID", "User", "Medicine", "Time");
    private final DefaultTableModel queueModel = model("Time", "User", "Medicine", "Dosage");

    private final JComboBox<User> userCombo = new JComboBox<>();
    private final JComboBox<Medicine> medCombo = new JComboBox<>();
    private final JTable userTable = new JTable(userModel);
    private final JTable medTable = new JTable(medModel);
    private final JTable doseTable = new JTable(doseModel);

    private final JTextArea log = new JTextArea(6, 40);
    private final JLabel nextLabel = new JLabel("Reminders stopped");
    private final AlertScheduler scheduler;

    public MainFrame() {
        super("Medicine Reminder");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 520);
        setLocationRelativeTo(null);

        scheduler = new AlertScheduler(this::fireAlert);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", usersTab());
        tabs.addTab("Medicines", medicinesTab());
        tabs.addTab("Dose Times", dosesTab());
        tabs.addTab("Reminders", remindersTab());
        tabs.addChangeListener(e -> refreshAll());
        add(tabs);

        refreshAll();
    }

    private static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    // ---------- Users ----------
    private JPanel usersTab() {
        JTextField name = new JTextField(12), phone = new JTextField(10);
        JButton add = new JButton("Add"), del = new JButton("Delete selected");

        add.addActionListener(e -> {
            if (name.getText().trim().isEmpty()) { warn("Enter a name."); return; }
            run(() -> userDAO.add(name.getText().trim(), phone.getText().trim()));
            name.setText(""); phone.setText("");
            refreshAll();
        });
        del.addActionListener(e -> {
            int r = userTable.getSelectedRow();
            if (r < 0) { warn("Select a row first."); return; }
            run(() -> userDAO.delete((int) userModel.getValueAt(r, 0)));
            refreshAll();
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Phone:")); form.add(phone);
        form.add(add); form.add(del);
        return panel(form, userTable);
    }

    // ---------- Medicines ----------
    private JPanel medicinesTab() {
        JTextField name = new JTextField(10), dosage = new JTextField(8);
        JButton add = new JButton("Add"), del = new JButton("Delete selected");

        add.addActionListener(e -> {
            User u = (User) userCombo.getSelectedItem();
            if (u == null) { warn("Add a user first."); return; }
            if (name.getText().trim().isEmpty()) { warn("Enter a medicine name."); return; }
            run(() -> medDAO.add(u.getId(), name.getText().trim(), dosage.getText().trim()));
            name.setText(""); dosage.setText("");
            refreshAll();
        });
        del.addActionListener(e -> {
            int r = medTable.getSelectedRow();
            if (r < 0) { warn("Select a row first."); return; }
            run(() -> medDAO.delete((int) medModel.getValueAt(r, 0)));
            refreshAll();
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("User:")); form.add(userCombo);
        form.add(new JLabel("Medicine:")); form.add(name);
        form.add(new JLabel("Dosage:")); form.add(dosage);
        form.add(add); form.add(del);
        return panel(form, medTable);
    }

    // ---------- Dose times ----------
    private JPanel dosesTab() {
        JSpinner time = new JSpinner(new SpinnerDateModel());
        time.setEditor(new JSpinner.DateEditor(time, "HH:mm"));
        JButton add = new JButton("Add"), del = new JButton("Delete selected");

        add.addActionListener(e -> {
            Medicine m = (Medicine) medCombo.getSelectedItem();
            if (m == null) { warn("Add a medicine first."); return; }
            Date d = (Date) time.getValue();
            LocalTime t = LocalTime.parse(new SimpleDateFormat("HH:mm").format(d));
            run(() -> doseDAO.add(m.getId(), t));
            refreshAll();
        });
        del.addActionListener(e -> {
            int r = doseTable.getSelectedRow();
            if (r < 0) { warn("Select a row first."); return; }
            run(() -> doseDAO.delete((int) doseModel.getValueAt(r, 0)));
            refreshAll();
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Medicine:")); form.add(medCombo);
        form.add(new JLabel("Time (HH:mm):")); form.add(time);
        form.add(add); form.add(del);
        return panel(form, doseTable);
    }

    // ---------- Reminders (Priority Queue) ----------
    private JPanel remindersTab() {
        JButton start = new JButton("Start reminders"), stop = new JButton("Stop");
        start.addActionListener(e -> {
            try {
                scheduler.start();
                log.append("Started at " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "\n");
                refreshQueue();
            } catch (Exception ex) { error(ex); }
        });
        stop.addActionListener(e -> { scheduler.stop(); refreshQueue(); log.append("Stopped.\n"); });

        new javax.swing.Timer(1000, e -> refreshQueue()).start();

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(start); top.add(stop); top.add(nextLabel);

        log.setEditable(false);
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(new JTable(queueModel)), BorderLayout.CENTER);
        p.add(new JScrollPane(log), BorderLayout.SOUTH);
        return p;
    }

    private void refreshQueue() {
        queueModel.setRowCount(0);
        List<Alert> upcoming = scheduler.getUpcoming();
        for (Alert a : upcoming)
            queueModel.addRow(new Object[]{a.getTime().format(fmt), a.getUserName(),
                                           a.getMedicineName(), a.getDosage()});
        if (!scheduler.isRunning()) nextLabel.setText("Reminders stopped");
        else if (upcoming.isEmpty()) nextLabel.setText("No more doses today");
        else {
            Alert n = upcoming.get(0);
            nextLabel.setText("Next: " + n.getMedicineName() + " at " + n.getTime().format(fmt));
        }
    }

    private void fireAlert(Alert a) {
        Toolkit.getDefaultToolkit().beep();
        String msg = a.getUserName() + ": take " + a.getMedicineName() + " (" + a.getDosage() + ")";
        log.append("ALERT " + a.getTime().format(fmt) + " - " + msg + "\n");
        refreshQueue();
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(this, msg, "Time for your medicine", JOptionPane.INFORMATION_MESSAGE));
    }

    // ---------- helpers ----------
    private JPanel panel(JPanel form, JTable table) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        p.add(form, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private void refreshAll() {
        try {
            userModel.setRowCount(0); medModel.setRowCount(0); doseModel.setRowCount(0);
            userCombo.removeAllItems(); medCombo.removeAllItems();
            for (User u : userDAO.getAll()) {
                userModel.addRow(new Object[]{u.getId(), u.getName(), u.getPhone()});
                userCombo.addItem(u);
            }
            for (Medicine m : medDAO.getAll()) {
                medModel.addRow(new Object[]{m.getId(), m.getUserName(), m.getName(), m.getDosage()});
                medCombo.addItem(m);
            }
            for (DoseTime d : doseDAO.getAll())
                doseModel.addRow(new Object[]{d.getId(), d.getUserName(), d.getMedicineName(),
                                              d.getTime().format(fmt)});
        } catch (Exception ex) { error(ex); }
    }

    private interface DbAction { void run() throws Exception; }
    private void run(DbAction a) { try { a.run(); } catch (Exception ex) { error(ex); } }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Note", JOptionPane.WARNING_MESSAGE); }
    private void error(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
