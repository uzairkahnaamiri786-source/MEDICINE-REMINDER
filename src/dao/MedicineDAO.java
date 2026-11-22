package dao;

import model.Medicine;
import java.sql.*;
import java.util.*;

public class MedicineDAO {
    public void add(int userId, String name, String dosage) throws SQLException {
        String sql = "INSERT INTO medicine (user_id, name, dosage) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, dosage);
            ps.executeUpdate();
        }
    }

    public List<Medicine> getAll() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.medicine_id, m.user_id, u.name, m.name, m.dosage " +
                     "FROM medicine m JOIN users u ON m.user_id = u.user_id ORDER BY m.medicine_id";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Medicine(rs.getInt(1), rs.getInt(2), rs.getString(3),
                                      rs.getString(4), rs.getString(5)));
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM medicine WHERE medicine_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
