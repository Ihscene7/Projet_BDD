package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // Get all patients
    public static List<PatientsController.Patient> getAllPatients() {
        List<PatientsController.Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM PATIENTS ORDER BY NOM";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PatientsController.Patient(
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("TELEPHONE"),
                    rs.getString("MEDECIN"),
                    rs.getString("ADRESSE"),
                    rs.getDate("DATE_NAISSANCE").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Add patient
    public static void addPatient(PatientsController.Patient p) {
        String sql = "INSERT INTO PATIENTS VALUES (SEQ_PATIENTS.NEXTVAL,?,?,?,?,?,?)";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.nom);
            stmt.setString(2, p.prenom);
            stmt.setString(3, p.telephone);
            stmt.setString(4, p.medecin);
            stmt.setString(5, p.adresse);
            stmt.setDate(6, Date.valueOf(p.dateNaissance));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update patient
    public static void updatePatient(PatientsController.Patient p, String oldNom) {
        String sql = "UPDATE PATIENTS SET NOM=?, PRENOM=?, TELEPHONE=?, " +
                     "MEDECIN=?, ADRESSE=?, DATE_NAISSANCE=? WHERE NOM=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.nom);
            stmt.setString(2, p.prenom);
            stmt.setString(3, p.telephone);
            stmt.setString(4, p.medecin);
            stmt.setString(5, p.adresse);
            stmt.setDate(6, Date.valueOf(p.dateNaissance));
            stmt.setString(7, oldNom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete patient
    public static void deletePatient(PatientsController.Patient p) {
        String sql = "DELETE FROM PATIENTS WHERE NOM=? AND PRENOM=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.nom);
            stmt.setString(2, p.prenom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search patients
    public static List<PatientsController.Patient> searchPatients(String query) {
        List<PatientsController.Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM PATIENTS WHERE " +
                     "LOWER(NOM || ' ' || PRENOM) LIKE ? OR TELEPHONE LIKE ?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + query.toLowerCase() + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new PatientsController.Patient(
                    rs.getString("NOM"),
                    rs.getString("PRENOM"),
                    rs.getString("TELEPHONE"),
                    rs.getString("MEDECIN"),
                    rs.getString("ADRESSE"),
                    rs.getDate("DATE_NAISSANCE").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}