package application;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    public static List<RendezVousController.RendezVous> getAllRendezVous() {
        List<RendezVousController.RendezVous> list = new ArrayList<>();
        String sql = "SELECT R.Num_RendezVous, R.Num_Patient, R.Num_Medecin, " +
                     "P.Nom_Pat || ' ' || P.Prenom_Pat AS Patient, " +
                     "M.Nom_Med || ' ' || M.Prenom_Med AS Medecin, " +
                     "R.Date_RendezVous, R.Heure_RendezVous, R.Statut " +
                     "FROM RendezVous R " +
                     "JOIN Patient P ON R.Num_Patient = P.Num_Patient " +
                     "JOIN Medecin M ON R.Num_Medecin = M.Num_Medecin " +
                     "ORDER BY R.Date_RendezVous";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RendezVousController.RendezVous(
                    rs.getInt("Num_RendezVous"),
                    rs.getInt("Num_Patient"),
                    rs.getInt("Num_Medecin"),
                    rs.getString("Patient"),
                    rs.getString("Medecin"),
                    rs.getDate("Date_RendezVous").toLocalDate(),
                    rs.getString("Heure_RendezVous"),
                    rs.getString("Statut")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void addRendezVous(int numPatient, int numMedecin,
                                      LocalDate date, String heure) {
        String sql = "INSERT INTO RendezVous VALUES " +
                     "(SEQ_RendezVous.NEXTVAL,?,?,?,?,DEFAULT)";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, numPatient);
            stmt.setInt(2, numMedecin);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, heure);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deleteRendezVous(int numRendezVous) {
        String sql = "DELETE FROM RendezVous WHERE Num_RendezVous=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, numRendezVous);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean isSlotTaken(int numMedecin,
                                       LocalDate date, String heure) {
        String sql = "SELECT COUNT(*) FROM RendezVous " +
                     "WHERE Num_Medecin=? AND Date_RendezVous=? " +
                     "AND Heure_RendezVous=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, numMedecin);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, heure);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}