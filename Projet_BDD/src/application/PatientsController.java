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

public class PatientsController {

    @FXML private VBox patientListBox;
    @FXML private TextField searchField;
    @FXML private ScrollPane listScrollPane;
    @FXML private Button toggleButton;
    @FXML private DatePicker ddnField;
 
    
   
    
    @FXML
    private void toggleList() { 		//Affiche/cache la ScrollPane de la liste
        boolean visible = listScrollPane.isVisible();
        listScrollPane.setVisible(!visible);
        listScrollPane.setManaged(!visible);
        toggleButton.setText(visible ? "Liste des patients ∨" : "Liste des patients ∧");
    }

    // Simple Patient model
    static class Patient {
        int id;
        String nom, prenom, telephone, adresse;
        LocalDate dateNaissance;

        Patient(int id, String nom, String prenom, LocalDate ddn,
                String tel, String adresse) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = ddn;
            this.telephone = tel;
            this.adresse = adresse;
        }
    }

    private List<Patient> patients = new ArrayList<>();
    
  

    @FXML
    public void initialize() {		
        // Charge la liste des patients depuis Oracle au démarrage
        patients = PatientDAO.getAllPatients();
        refreshList(patients);
    }

    private void refreshList(List<Patient> list) {
    	// Vide et recrée tous les éléments visuels de la liste
        patientListBox.getChildren().clear();
        for (Patient p : list) {
            patientListBox.getChildren().add(createPatientRow(p));
        }
    }

    private HBox createPatientRow(Patient p) {	//Crée une ligne HBox pour un patient (icône, nom, téléphone,et les boutons edit et delete)
        HBox row = new HBox(15);
        row.getStyleClass().add("patient-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Icon
        ImageView icon = getIcon("person.png");
        
        // Name + phone VBox
        VBox info = new VBox(3);
        Label name = new Label(p.nom + " " + p.prenom);
        name.getStyleClass().add("patient-name");
        Label phone = new Label(p.telephone);
        phone.getStyleClass().add("patient-phone");
        info.getChildren().addAll(name, phone);
        HBox.setHgrow(info, Priority.ALWAYS);

        

        // Edit button
        Button editBtn = new Button();
        editBtn.setGraphic(getIcon("edit.png"));
        editBtn.getStyleClass().add("edit-btn");
        editBtn.setOnAction(e -> openEditDialog(p));

        // Delete button
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(getIcon("delete.png"));
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> openDeleteConfirmation(p));

        row.getChildren().addAll(icon, info, editBtn, deleteBtn);
        return row;
    }

    @FXML
    private void handleSearch() { 		//Filtre la liste selon le texte saisi dans la barre de recherche
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            refreshList(patients);
            return;
        }
        refreshList(PatientDAO.searchPatients(query));
    }

    @FXML				
    private void openAddDialog() {		//Ouvre un dialog pour ajouter un nouveau patient
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nouveau patient");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        Label title = new Label("Informations du nouveau patient");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nomField = new TextField(); nomField.setPromptText("Nom");
        TextField prenomField = new TextField(); prenomField.setPromptText("Prénom");
        TextField telField = new TextField(); telField.setPromptText("+213XXXXXXXXX");
        TextField adresseField = new TextField(); adresseField.setPromptText("Adresse");
        DatePicker ddnField = new DatePicker();
        ddnField.setPromptText("Choisir une date");
        ddnField.setPrefWidth(300);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #296262; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-pref-width: 300; -fx-padding: 10; -fx-cursor: hand;");
        
        Label ObligationChamps = new Label(""); 
        
        saveBtn.setOnAction(e -> {
        	LocalDate limite = LocalDate.of(1900, 1, 1);
        	if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()
                    || ddnField.getValue() == null || telField.getText().isEmpty() || adresseField.getText().isEmpty()) {
        		ObligationChamps.setText("Veuillez remplir tous les champs.");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
                return;
            }else if (telField.getText().length() != 10 || !telField.getText().startsWith("0")) {
            	ObligationChamps.setText("Numéro de téléphone incorrect !");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
        		return;
            } else if ( ddnField.getValue().isAfter(LocalDate.now()) ||  ddnField.getValue().isBefore(limite) ) {
            	ObligationChamps.setText("Date de naissance incorrect !");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
        		return;
            }
        	Patient p = new Patient(
        		    0,
        		    nomField.getText(), prenomField.getText(),
        		    ddnField.getValue(),
        		    telField.getText(),
        		    adresseField.getText()
        		);
            PatientDAO.addPatient(p);
            patients.add(p);
            refreshList(patients);
            dialog.close();
        });
        form.getChildren().addAll(title,
            new Label("Nom"), nomField,
            new Label("Prénom"), prenomField,
            new Label("Téléphone"), telField,
            new Label("Adresse"), adresseField,
            new Label("Date de naissance"), ddnField,
            ObligationChamps,
            saveBtn
        );

        dialog.setScene(new Scene(form, 350, 550));
        dialog.show();
    }

    private void openEditDialog(Patient p) {		// Ouvre un dialog pré-rempli pour modifier un patient existant
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier patient");

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white;");

        Label title = new Label("Modifier les informations");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField nomField = new TextField(p.nom);
        TextField prenomField = new TextField(p.prenom);
        TextField telField = new TextField(p.telephone);
        TextField adresseField = new TextField(p.adresse);
        DatePicker ddnField = new DatePicker();
        ddnField.setValue(p.dateNaissance);
        ddnField.setPrefWidth(300);
        Label ObligationChamps = new Label(""); 

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-pref-width: 300; -fx-padding: 10; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
        	LocalDate limite = LocalDate.of(1900, 1, 1);
        	if (nomField.getText().isEmpty() || prenomField.getText().isEmpty()
                    || ddnField.getValue() == null || telField.getText().isEmpty() || adresseField.getText().isEmpty()) {
        		ObligationChamps.setText("Veuillez remplir tous les champs.");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
                return;
            } else if (telField.getText().length() != 10 || !telField.getText().startsWith("0")) {
            	ObligationChamps.setText("Numéro de téléphone incorrect !");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
        		return;
            } else if ( ddnField.getValue().isAfter(LocalDate.now()) ||  ddnField.getValue().isBefore(limite) ) {
            	ObligationChamps.setText("Date de naissance incorrect !");
        		ObligationChamps.setStyle("-fx-text-fill: #cc0000;");
        		return;
            }
            p.nom = nomField.getText();
            p.prenom = prenomField.getText();
            p.telephone = telField.getText();
            p.adresse = adresseField.getText();
            p.dateNaissance = ddnField.getValue();
            PatientDAO.updatePatient(p);
            refreshList(patients);
            dialog.close();
        });

        form.getChildren().addAll(title,
            new Label("Nom"), nomField,
            new Label("Prénom"), prenomField,
            new Label("Téléphone"), telField,
            new Label("Adresse"), adresseField,
            new Label("Date de naissance"), ddnField,
            ObligationChamps,
            saveBtn
        );

        dialog.setScene(new Scene(form, 350, 550));
        dialog.show();
    }

    private void openDeleteConfirmation(Patient p) {		// Ouvre un dialog de confirmation avant suppression
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmation");

        VBox box = new VBox(20);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white;");

        Label title = new Label("Confirmation");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label msg = new Label("Est-ce que vous êtes sûr de vouloir supprimer ce patient ?");
        msg.setStyle("-fx-font-size: 13px;");
        msg.setWrapText(true);  
        msg.setMaxWidth(280);   

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button ouiBtn = new Button("OUI");
        ouiBtn.setStyle("-fx-background-color: #d4ece8; -fx-background-radius: 8; -fx-padding: 8 20;");
        ouiBtn.setOnAction(e -> {
        	PatientDAO.deletePatient(p);  
            patients.remove(p);
            refreshList(patients);
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

    private ImageView getIcon(String filename) {		// Charge une icône PNG locale et retourne un ImageView 20x20 utilisé par le controller pour les icônes des boutons
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
    private void goBack() throws Exception {		// Retourne au menu principal
        Main.loadScene("menu.fxml", "Menu Principal");
    }
}