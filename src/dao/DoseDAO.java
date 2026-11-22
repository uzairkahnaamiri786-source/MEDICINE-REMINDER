package dao;

import model.Alert;
import model.DoseTime;
import java.sql.*;
import java.time.LocalTime;
import java.util.*;

public class DoseDAO {
    public void add(int medicineId, LocalTime time) throws SQLException {
        String sql = "INSERT INTO dose_time (medicine_id, dose_time) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, medicineId);
            ps.setTime(2, Time.valueOf(time));
            ps.executeUpdate();
        }
    }

    public List<DoseTime> getAll() throws SQLException {
        List<DoseTime> list = new ArrayList<>();
        String sql = "SELECT d.dose_id, m.name, u.name, d.dose_time FROM dose_time d " +
                     "JOIN medicine m ON d.medicine_id = m.medicine_id " +
                     "JOIN users u ON m.user_id = u.user_id ORDER BY d.dose_time";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new DoseTime(rs.getInt(1), rs.getString(2), rs.getString(3),
                                      rs.getTime(4).toLocalTime()));
        }
        return list;
    }

    /** Every scheduled dose, as Alert objects ready for the PriorityQueue. */
    public List<Alert> loadAlerts() throws SQLException {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT d.dose_time, u.name, m.name, m.dosage FROM dose_time d " +
                     "JOIN medicine m ON d.medicine_id = m.medicine_id " +
                     "JOIN users u ON m.user_id = u.user_id";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Alert(rs.getTime(1).toLocalTime(), rs.getString(2),
                                   rs.getString(3), rs.getString(4)));
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM dose_time WHERE dose_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
