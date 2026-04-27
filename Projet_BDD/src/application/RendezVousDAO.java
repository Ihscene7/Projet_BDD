package application;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    public static List<RendezVousController.RendezVous> getAllRendezVous() {
        List<RendezVousController.RendezVous> list = new ArrayList<>();
        String sql = "SELECT * FROM RENDEZVOUS ORDER BY DATE_RDV";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RendezVousController.RendezVous(
                    rs.getString("PATIENT"),
                    rs.getString("MEDECIN"),
                    rs.getDate("DATE_RDV").toLocalDate(),
                    rs.getString("CRENEAU")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void addRendezVous(RendezVousController.RendezVous rdv) {
        String sql = "INSERT INTO RENDEZVOUS VALUES " +
                     "(SEQ_RENDEZVOUS.NEXTVAL,?,?,?,?)";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, rdv.patient);
            stmt.setString(2, rdv.medecin);
            stmt.setDate(3, Date.valueOf(rdv.date));
            stmt.setString(4, rdv.creneau);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRendezVous(RendezVousController.RendezVous rdv) {
        String sql = "DELETE FROM RENDEZVOUS WHERE PATIENT=? AND MEDECIN=? " +
                     "AND DATE_RDV=? AND CRENEAU=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, rdv.patient);
            stmt.setString(2, rdv.medecin);
            stmt.setDate(3, Date.valueOf(rdv.date));
            stmt.setString(4, rdv.creneau);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isSlotTaken(String medecin, LocalDate date, String creneau) {
        String sql = "SELECT COUNT(*) FROM RENDEZVOUS WHERE MEDECIN=? " +
                     "AND DATE_RDV=? AND CRENEAU=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, medecin);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, creneau);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}