package dao;

import model.User;
import java.sql.*;
import java.util.*;

public class UserDAO {
    public void add(String name, String phone) throws SQLException {
        String sql = "INSERT INTO users (name, phone) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.executeUpdate();
        }
    }

    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id, name, phone FROM users ORDER BY user_id")) {
            while (rs.next())
                list.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
