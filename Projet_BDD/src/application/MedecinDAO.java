package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

    public static List<MedecinsController.Medecin> getAllMedecins() {
        List<MedecinsController.Medecin> list = new ArrayList<>();
        String sql = "SELECT M.Num_Medecin, M.Nom_Med, M.Prenom_Med, " +
                     "M.code_SP, S.Nom_SP, M.Telephone " +
                     "FROM Medecin M JOIN Specialite S " +
                     "ON M.code_SP = S.code_SP ORDER BY M.Nom_Med";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new MedecinsController.Medecin(
                    rs.getInt("Num_Medecin"),
                    rs.getString("Nom_Med"),
                    rs.getString("Prenom_Med"),
                    rs.getInt("code_SP"),
                    rs.getString("Nom_SP"),
                    rs.getString("Telephone")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void addMedecin(MedecinsController.Medecin m) {
        String sql = "INSERT INTO Medecin VALUES " +
                     "(SEQ_Medecin.NEXTVAL,?,?,?,?)";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, m.nom);
            stmt.setString(2, m.prenom);
            stmt.setInt(3, m.codeSpecialite);
            stmt.setString(4, m.telephone);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateMedecin(MedecinsController.Medecin m) {
        String sql = "UPDATE Medecin SET Nom_Med=?, Prenom_Med=?, " +
                     "code_SP=?, Telephone=? WHERE Num_Medecin=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, m.nom);
            stmt.setString(2, m.prenom);
            stmt.setInt(3, m.codeSpecialite);
            stmt.setString(4, m.telephone);
            stmt.setInt(5, m.id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteMedecin(MedecinsController.Medecin m) {
        String sql = "DELETE FROM Medecin WHERE Num_Medecin=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, m.id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}