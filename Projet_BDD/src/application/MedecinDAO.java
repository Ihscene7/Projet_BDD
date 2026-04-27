package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

	public static List<MedecinsController.Medecin> getAllMedecins() {
	    List<MedecinsController.Medecin> list = new ArrayList<>();
	    String sql = "SELECT M.NOM, M.PRENOM, M.TELEPHONE, S.NOM AS SPECIALITE " +
	                 "FROM MEDECINS M JOIN SPECIALITES S ON M.SPECIALITE_ID = S.ID " +
	                 "ORDER BY M.NOM";
	    try (Statement stmt = DatabaseConnection.getConnection().createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {
	        while (rs.next()) {
	            list.add(new MedecinsController.Medecin(
	                rs.getString("NOM"),
	                rs.getString("PRENOM"),
	                rs.getString("SPECIALITE"),
	                rs.getString("TELEPHONE")
	            ));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    public static void addMedecin(MedecinsController.Medecin m) {
        String sql = "INSERT INTO MEDECINS VALUES (SEQ_MEDECINS.NEXTVAL,?,?,?,?)";
        try (PreparedStatement stmt =
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, m.nom);
            stmt.setString(2, m.prenom);
            stmt.setString(3, m.specialite);
            stmt.setString(4, m.telephone);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMedecin(MedecinsController.Medecin m, String oldNom) {
        String sql = "UPDATE MEDECINS SET NOM=?, PRENOM=?, SPECIALITE=?, " +
                     "TELEPHONE=? WHERE NOM=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, m.nom);
            stmt.setString(2, m.prenom);
            stmt.setString(3, m.specialite);
            stmt.setString(4, m.telephone);
            stmt.setString(5, oldNom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMedecin(MedecinsController.Medecin m) {
        String sql = "DELETE FROM MEDECINS WHERE NOM=? AND PRENOM=?";
        try (PreparedStatement stmt = 
                DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, m.nom);
            stmt.setString(2, m.prenom);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}