package application;

import javafx.fxml.FXML;

public class MenuController {

    @FXML
    private void openRendezVous() throws Exception {
        Main.loadScene("rendezvous.fxml", "Rendez-vous");
    }

    @FXML
    private void openPatients() throws Exception {
        Main.loadScene("patients.fxml", "Patients");
    }

    @FXML
    private void openMedecins() throws Exception {
        Main.loadScene("medecins.fxml", "Médecins");
    }
}