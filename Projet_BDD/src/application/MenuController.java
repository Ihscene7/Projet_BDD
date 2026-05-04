package application;

import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void openRendezVous() throws Exception {		// Charge rendezvous.fxml quand on clique sur le bouton Rendez-vous
        Main.loadScene("rendezvous.fxml", "Rendez-vous");
    }

    @FXML
    private void openPatients() throws Exception {			//Charge patient.fxml quand on clique sur le bouton patients
        Main.loadScene("patients.fxml", "Patients");
    }

    @FXML
    private void openMedecins() throws Exception {			//Charge meddecins.fxml quand on clique sur le bouton medecins
        Main.loadScene("medecins.fxml", "Médecins");
    }
}