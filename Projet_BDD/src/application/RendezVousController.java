package application;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RendezVousController {

    @FXML private VBox rdvListBox;
    @FXML private ScrollPane listScrollPane;
    @FXML private Button toggleButton;
    @FXML private ComboBox<String> filterMedecin;
    @FXML private DatePicker filterDate;

    
    static class RendezVous {
        int id, numPatient, numMedecin;
        String patient, medecin, creneau, statut;
        LocalDate date;

        RendezVous(int id, int numPatient, int numMedecin,
                   String patient, String medecin,
                   LocalDate date, String creneau, String statut) {
            this.id = id;
            this.numPatient = numPatient;
            this.numMedecin = numMedecin;
            this.patient = patient;
            this.medecin = medecin;
            this.date = date;
            this.creneau = creneau;
            this.statut = statut;
        }
    }

    private List<RendezVous> rendezVousList = new ArrayList<>();

    private List<String> patientsList = new ArrayList<>();
    private List<String> medecinsList = new ArrayList<>();
    private final String[] CRENEAUX = {
    	    "8:00", "8:30", "9:00", "9:30", "10:00", "10:30",
    	    "11:00", "11:30", "13:00", "13:30", "14:00", "14:30",
    	    "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    	};

    @FXML
    public void initialize() {
        // Load patients from DB
        for (PatientsController.Patient p : PatientDAO.getAllPatients()) {
            patientsList.add(p.nom + " " + p.prenom);
        }

        // Load medecins from DB
        for (MedecinsController.Medecin m : MedecinDAO.getAllMedecins()) {
            medecinsList.add("Dr. " + m.nom + " " + m.prenom);
        }

        // Load filter combobox
        filterMedecin.getItems().add("Tous");
        filterMedecin.getItems().addAll(medecinsList);

        // Load rendez-vous
        rendezVousList = RendezVousDAO.getAllRendezVous();
        refreshList(rendezVousList);
    }

   
    private void refreshList(List<RendezVous> list) {
        rdvListBox.getChildren().clear();
        for (RendezVous rdv : list) {
            rdvListBox.getChildren().add(createRdvRow(rdv));
        }
    }

    private HBox createRdvRow(RendezVous rdv) {
        HBox row = new HBox(15);
        row.getStyleClass().add("patient-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Icon
        Label icon = new Label();
        icon.setGraphic(getIcon("calendar.png"));
        icon.setStyle("-fx-font-size: 20px;");

        // Info VBox
        VBox info = new VBox(3);
        Label patient = new Label(rdv.patient);
        patient.getStyleClass().add("patient-name");
        Label medecin = new Label(rdv.medecin);
        medecin.getStyleClass().add("patient-phone");
        info.getChildren().addAll(patient, medecin);
        HBox.setHgrow(info, Priority.ALWAYS);
        
     // Statut label cliquable
        VBox statutBox = new VBox(5);
        statutBox.setAlignment(Pos.CENTER);
        statutBox.setPrefWidth(90);

        Label statutLabel = new Label(rdv.statut);
        statutLabel.setStyle(getStatutStyle(rdv.statut));
        statutLabel.setPadding(new Insets(4, 12, 4, 12));
        statutLabel.setMaxWidth(Double.MAX_VALUE);
        statutLabel.setAlignment(Pos.CENTER);
        statutLabel.setCursor(javafx.scene.Cursor.HAND); // ← curseur main au survol
        statutLabel.setOnMouseClicked(e -> openStatutDialog(rdv, statutLabel)); // ← clic direct

        statutBox.getChildren().add(statutLabel); // ← plus de bouton séparé
        
        // Date + time
        VBox dateBox = new VBox(3);
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        Label date = new Label(rdv.date.toString());
        date.getStyleClass().add("patient-doctor");
        Label creneau = new Label(rdv.creneau);
        creneau.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d5f5a;");
        dateBox.getChildren().addAll(date, creneau);

        // Delete button
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(getIcon("delete.png"));
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> openDeleteConfirmation(rdv));

        row.getChildren().addAll(icon, info, statutBox, dateBox, deleteBtn);
        return row;
    }
    
    //for the statut colour
    private String getStatutStyle(String statut) {
        switch (statut) {
            case "Planifie":
                return "-fx-background-color: #fff3cd; -fx-text-fill: #856404; " +
                       "-fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: bold;";
            case "Effectue":
                return "-fx-background-color: #d4ece8; -fx-text-fill: #2d5f5a; " +
                       "-fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: bold;";
            case "Annule":
                return "-fx-background-color: #f8d7da; -fx-text-fill: #842029; " +
                       "-fx-background-radius: 8; -fx-font-size: 12px; -fx-font-weight: bold;";
            default:
                return "-fx-background-color: #e0e0e0; -fx-background-radius: 8; " +
                       "-fx-font-size: 12px;";
        }
    }
    
    private void openStatutDialog(RendezVous rdv, Label statutLabel) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier le statut");

        VBox box = new VBox(15);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white;");

        Label title = new Label("Modifier le statut");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label current = new Label("Statut actuel : " + rdv.statut);
        current.setStyle("-fx-font-size: 13px; -fx-text-fill: #555555;");

        ComboBox<String> statutBox = new ComboBox<>();
        statutBox.getItems().addAll("Planifie", "Effectue", "Annule");
        statutBox.setValue(rdv.statut);
        statutBox.setPrefWidth(250);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-pref-width: 200; -fx-padding: 8;");
        saveBtn.setOnAction(e -> {
            String newStatut = statutBox.getValue();
            rdv.statut = newStatut;

            // Update in DB
            RendezVousDAO.updateStatut(rdv.id, newStatut);

            // Update label visually
            statutLabel.setText(newStatut);
            statutLabel.setStyle(getStatutStyle(newStatut));

            dialog.close();
        });

        box.getChildren().addAll(title, current, statutBox, saveBtn);
        dialog.setScene(new Scene(box, 300, 220));
        dialog.show();
    }

    @FXML
    private void applyFilter() {
        String medecinFilter = filterMedecin.getValue();
        LocalDate dateFilter = filterDate.getValue();

        // Si aucun filtre sélectionné
        if ((medecinFilter == null || medecinFilter.equals("Tous")) 
                && dateFilter == null) {
            refreshList(rendezVousList);
            return;
        }

        List<RendezVous> filtered = new ArrayList<>();
        for (RendezVous rdv : rendezVousList) {
            
            // Comparaison flexible — avec ou sans "Dr."
            boolean medecinMatch = medecinFilter == null
                    || medecinFilter.equals("Tous")
                    || rdv.medecin.equals(medecinFilter)
                    || ("Dr. " + rdv.medecin).equals(medecinFilter)
                    || rdv.medecin.equals(medecinFilter.replace("Dr. ", ""));

            boolean dateMatch = dateFilter == null
                    || rdv.date.equals(dateFilter);

            if (medecinMatch && dateMatch) {
                filtered.add(rdv);
            }
        }
        refreshList(filtered);
    }

    @FXML
    private void resetFilter() {
        filterMedecin.setValue(null);
        filterDate.setValue(null);
        refreshList(rendezVousList);
    }

    @FXML
    private void toggleList() {
        boolean visible = listScrollPane.isVisible();
        listScrollPane.setVisible(!visible);
        listScrollPane.setManaged(!visible);
        toggleButton.setText(visible
                ? "Liste des rendez-vous ∨"
                : "Liste des rendez-vous ∧");
    }

    @FXML
    private void openAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nouveau rendez-vous");

        VBox form = new VBox(10);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Label title = new Label("Prendre un rendez-vous");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Patient ComboBox
        ComboBox<String> patientBox = new ComboBox<>();
        patientBox.getItems().addAll(patientsList);
        patientBox.setPromptText("Sélectionner un patient");
        patientBox.setPrefWidth(300);

        // Doctor ComboBox
        ComboBox<String> medecinBox = new ComboBox<>();
        medecinBox.getItems().addAll(medecinsList); 
        medecinBox.setPromptText("Sélectionner un médecin");
        medecinBox.setPrefWidth(300);

        // Date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Choisir une date");
        datePicker.setPrefWidth(300);

        // Time slot ComboBox
        ComboBox<String> creneauBox = new ComboBox<>();
        creneauBox.getItems().addAll(CRENEAUX);
        creneauBox.setPromptText("Choisir un créneau horaire");
        creneauBox.setPrefWidth(300);

        // Availability label
        Label availabilityLabel = new Label("");
        availabilityLabel.setStyle("-fx-text-fill: #cc0000; -fx-font-size: 12px;");

        // Check availability when doctor/date/time changes
        creneauBox.setOnAction(e -> checkAvailability(
                medecinBox.getValue(), datePicker.getValue(),
                creneauBox.getValue(), availabilityLabel));
        medecinBox.setOnAction(e -> checkAvailability(
                medecinBox.getValue(), datePicker.getValue(),
                creneauBox.getValue(), availabilityLabel));
        datePicker.setOnAction(e -> checkAvailability(
                medecinBox.getValue(), datePicker.getValue(),
                creneauBox.getValue(), availabilityLabel));

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-pref-width: 300; -fx-padding: 10;");
        
        saveBtn.setOnAction(e -> {
            if (patientBox.getValue() == null || medecinBox.getValue() == null
                    || datePicker.getValue() == null || creneauBox.getValue() == null) {
                availabilityLabel.setText("Veuillez remplir tous les champs.");
                availabilityLabel.setStyle("-fx-text-fill: #cc0000;");
                return;
            }
            if (isSlotTaken(medecinBox.getValue(),
                    datePicker.getValue(), creneauBox.getValue())) {
                availabilityLabel.setText("Ce créneau est déjà pris !");
                availabilityLabel.setStyle("-fx-text-fill: #cc0000;");
                return;
            }

            int numPatient = getPatientId(patientBox.getValue());
            int numMedecin = getMedecinId(medecinBox.getValue());

            RendezVousDAO.addRendezVous(numPatient, numMedecin,
                    datePicker.getValue(), creneauBox.getValue());

            RendezVous rdv = new RendezVous(
                0, numPatient, numMedecin,
                patientBox.getValue(), medecinBox.getValue(),
                datePicker.getValue(), creneauBox.getValue(), "Planifie"
            );
            rendezVousList.add(rdv);
            refreshList(rendezVousList);
            dialog.close();
        });

        form.getChildren().addAll(
                title,
                new Label("Patient"), patientBox,
                new Label("Médecin"), medecinBox,
                new Label("Date"), datePicker,
                new Label("Créneau horaire"), creneauBox,
                availabilityLabel,
                saveBtn
        );

        dialog.setScene(new Scene(form, 360, 480));
        dialog.show();
    }

    private void checkAvailability(String medecin, LocalDate date,
                                    String creneau, Label label) {
        if (medecin == null || date == null || creneau == null) return;
        if (isSlotTaken(medecin, date, creneau)) {
            label.setText("⚠ Ce créneau est déjà pris !");
            label.setStyle("-fx-text-fill: #cc0000;");
        } else {
            label.setText("✓ Créneau disponible");
            label.setStyle("-fx-text-fill: green;");
        }
    }

    private boolean isSlotTaken(String medecinNom, LocalDate date, String creneau) {
        // Get medecin ID from name
        int numMedecin = getMedecinId(medecinNom);
        if (numMedecin == -1) return false;
        return RendezVousDAO.isSlotTaken(numMedecin, date, creneau);
    }
    
    
    private void openDeleteConfirmation(RendezVous rdv) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmation");

        VBox box = new VBox(20);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white;");

        Label title = new Label("Confirmation");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label msg = new Label("Est-ce que vous êtes sûr de vouloir\nsupprimer ce rendez-vous ?");
        msg.setStyle("-fx-font-size: 13px;");
        msg.setWrapText(true);
        msg.setMaxWidth(280);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button ouiBtn = new Button("OUI");
        ouiBtn.setStyle("-fx-background-color: #d4ece8; " +
                "-fx-background-radius: 8; -fx-padding: 8 20;");
        ouiBtn.setOnAction(e -> {
        	RendezVousDAO.deleteRendezVous(rdv.id);
            rendezVousList.remove(rdv);
            refreshList(rendezVousList);
            dialog.close();
        });

        Button nonBtn = new Button("NON");
        nonBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 8 20;");
        nonBtn.setOnAction(e -> dialog.close());

        buttons.getChildren().addAll(ouiBtn, nonBtn);
        box.getChildren().addAll(title, msg, buttons);

        dialog.setScene(new Scene(box, 350, 200));
        dialog.show();
    }
    
    private int getPatientId(String nomPrenom) {
        for (PatientsController.Patient p : PatientDAO.getAllPatients()) {
            if ((p.nom + " " + p.prenom).equals(nomPrenom)) return p.id;
        }
        return -1;
    }

    private int getMedecinId(String nomPrenom) {
        for (MedecinsController.Medecin m : MedecinDAO.getAllMedecins()) {
            if (("Dr. " + m.nom + " " + m.prenom).equals(nomPrenom)) return m.id;
        }
        return -1;
    }
    
    private ImageView getIcon(String filename) {
        javafx.scene.image.Image img = new javafx.scene.image.Image(
            getClass().getResourceAsStream(filename)
        );
        ImageView iv = new ImageView(img);
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        iv.setPreserveRatio(true);
        return iv;
    }

    @FXML
    private void goBack() throws Exception {
        Main.loadScene("menu.fxml", "Menu Principal");
    }
}