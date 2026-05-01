package application;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpecialiteDAO {

    // Returns Map of Nom_SP -> code_SP for ComboBox
    public static Map<String, Integer> getAllSpecialites() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT code_SP, Nom_SP FROM Specialite ORDER BY Nom_SP";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("Nom_SP"), rs.getInt("code_SP"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
}