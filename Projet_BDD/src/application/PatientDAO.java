package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    public static List<PatientsController.Patient> getAllPatients() {
        List<PatientsController.Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient ORDER BY Nom_Pat";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new PatientsController.Patient(
                    rs.getInt("Num_Patient"),
                    rs.getString("Nom_Pat"),
                    rs.getString("Prenom_Pat"),
                    rs.getDate("Date_Naissance").toLocalDate(),
                    rs.getString("Telephone"),
                    rs.getString("Adresse")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void addPatient(PatientsController.Patient p) {
        int newId = DatabaseConnection.getNextId("Patient", "Num_Patient");
        String sql = "INSERT INTO Patient VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, newId);
            stmt.setString(2, p.nom);
            stmt.setString(3, p.prenom);
            stmt.setDate(4, Date.valueOf(p.dateNaissance));
            stmt.setString(5, p.telephone);
            stmt.setString(6, p.adresse);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updatePatient(PatientsController.Patient p) {
        String sql = "UPDATE Patient SET Nom_Pat=?, Prenom_Pat=?, " +
                     "Date_Naissance=?, Telephone=?, Adresse=? " +
                     "WHERE Num_Patient=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, p.nom);
            stmt.setString(2, p.prenom);
            stmt.setDate(3, Date.valueOf(p.dateNaissance));
            stmt.setString(4, p.telephone);
            stmt.setString(5, p.adresse);
            stmt.setInt(6, p.id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void deletePatient(PatientsController.Patient p) {
        String sql = "DELETE FROM Patient WHERE Num_Patient=?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, p.id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<PatientsController.Patient> searchPatients(String query) {
        List<PatientsController.Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient WHERE " +
                     "LOWER(Nom_Pat || ' ' || Prenom_Pat) LIKE ? " +
                     "OR Telephone LIKE ?";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, "%" + query.toLowerCase() + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new PatientsController.Patient(
                    rs.getInt("Num_Patient"),
                    rs.getString("Nom_Pat"),
                    rs.getString("Prenom_Pat"),
                    rs.getDate("Date_Naissance").toLocalDate(),
                    rs.getString("Telephone"),
                    rs.getString("Adresse")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}