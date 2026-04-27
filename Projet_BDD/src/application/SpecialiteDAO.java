package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialiteDAO {

    public static List<String> getAllSpecialites() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT NOM FROM SPECIALITES ORDER BY NOM";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("NOM"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getIdByNom(String nom) {
        String sql = "SELECT ID FROM SPECIALITES WHERE NOM=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}