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
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MedecinsController {

    @FXML private VBox medecinListBox;
    @FXML private TextField searchField;
    @FXML private ScrollPane listScrollPane;
    @FXML private Button toggleButton;

    static class Medecin {
        int id;
        int codeSpecialite;
        String nom, prenom, telephone, specialite;

        Medecin(int id, String nom, String prenom,
                int codeSpecialite, String specialite, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.codeSpecialite = codeSpecialite;
            this.specialite = specialite;
            this.telephone = telephone;
        }
    }

    private List<Medecin> medecins = new ArrayList<>();

    @FXML
    public void initialize() {
        // Load from DB instead of fake data
        medecins = MedecinDAO.getAllMedecins();
        refreshList(medecins);
    }
    
    
    private void refreshList(List<Medecin> list) {
        medecinListBox.getChildren().clear();
        for (Medecin m : list) {
            medecinListBox.getChildren().add(createMedecinRow(m));
        }
    }

    private HBox createMedecinRow(Medecin m) {
        HBox row = new HBox(15);
        row.getStyleClass().add("patient-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Icon
        ImageView icon = getIcon("person.png");
        
        
        // Name + speciality VBox
        VBox info = new VBox(3);
        Label name = new Label("Dr. " + m.nom + " " + m.prenom);
        name.getStyleClass().add("patient-name");
        Label specialite = new Label(m.specialite);
        specialite.getStyleClass().add("patient-phone");
        info.getChildren().addAll(name, specialite);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Phone label
        Label phone = new Label(m.telephone);
        phone.getStyleClass().add("patient-doctor");
        
        // Calendar button
        Button calendarBtn = new Button();
        calendarBtn.setGraphic(getIcon("calendar.png")); 
        calendarBtn.getStyleClass().add("edit-btn");
        calendarBtn.setOnAction(e -> openCalendarDialog(m));
       
        // Edit button
        Button editBtn = new Button();
        editBtn.setGraphic(getIcon("edit.png"));
        editBtn.getStyleClass().add("edit-btn");
        editBtn.setOnAction(e -> openEditDialog(m));

        // Delete button
        Button deleteBtn = new Button();
        deleteBtn.setGraphic(getIcon("delete.png"));
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> openDeleteConfirmation(m));

        row.getChildren().addAll(icon, info, phone, calendarBtn, editBtn, deleteBtn);
        return row;
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            refreshList(medecins);
            return;
        }
        List<Medecin> filtered = new ArrayList<>();
        for (Medecin m : medecins) {
            if ((m.nom + " " + m.prenom).toLowerCase().contains(query)
                    || m.specialite.toLowerCase().contains(query)) {
                filtered.add(m);
            }
        }
        refreshList(filtered);
    }

    @FXML
    private void toggleList() {
        boolean visible = listScrollPane.isVisible();
        listScrollPane.setVisible(!visible);
        listScrollPane.setManaged(!visible);
        toggleButton.setText(visible ? "Liste des médecins ∨" : "Liste des médecins ∧");
    }

    @FXML
    private void openAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nouveau médecin");

        VBox form = new VBox(10);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Label title = new Label("Informations du nouveau médecin");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        // Speciality ComboBox
     // Load specialities from DB
        Map<String, Integer> specialites = SpecialiteDAO.getAllSpecialites();

        ComboBox<String> specialiteBox = new ComboBox<>();
        specialiteBox.getItems().addAll(specialites.keySet());
        specialiteBox.setPromptText("Choisir une spécialité");
        specialiteBox.setPrefWidth(300);

    

        TextField telField = new TextField();
        telField.setPromptText("+213XXXXXXXXX");

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-pref-width: 300; -fx-padding: 10;");
        saveBtn.setOnAction(e -> {
            int codeSP = specialites.get(specialiteBox.getValue()); // ← get ID
            Medecin m = new Medecin(
                0,
                nomField.getText(),
                prenomField.getText(),
                codeSP,                    // ← use codeSP
                specialiteBox.getValue(),  // ← specialite name for display
                telField.getText()
            );
            MedecinDAO.addMedecin(m);
            medecins.add(m);
            refreshList(medecins);
            dialog.close();
        });

        form.getChildren().addAll(
            title,
            new Label("Nom"), nomField,
            new Label("Prénom"), prenomField,
            new Label("Spécialité"), specialiteBox,
            new Label("Téléphone"), telField,
            saveBtn
        );

        dialog.setScene(new Scene(form, 350, 420));
        dialog.show();
    }

    private void openEditDialog(Medecin m) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Modifier médecin");

        VBox form = new VBox(10);
        form.setPadding(new Insets(25));
        form.setStyle("-fx-background-color: white;");

        Label title = new Label("Modifier les informations");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField nomField = new TextField(m.nom);
        TextField prenomField = new TextField(m.prenom);

        // Speciality ComboBox pre-selected
        ComboBox<String> specialiteBox = new ComboBox<>();
        Map<String, Integer> specialites = SpecialiteDAO.getAllSpecialites();
        specialiteBox.getItems().addAll(specialites.keySet());
        specialiteBox.setValue(m.specialite);
        specialiteBox.setPrefWidth(300);

        TextField telField = new TextField(m.telephone);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-pref-width: 300; -fx-padding: 10;");
        saveBtn.setOnAction(e -> {
            m.nom = nomField.getText();
            m.prenom = prenomField.getText();
            m.specialite = specialiteBox.getValue();
            m.codeSpecialite = specialites.get(specialiteBox.getValue());
            m.telephone = telField.getText();
            MedecinDAO.updateMedecin(m);
            refreshList(medecins);
            dialog.close();
        });

        form.getChildren().addAll(
            title,
            new Label("Nom"), nomField,
            new Label("Prénom"), prenomField,
            new Label("Spécialité"), specialiteBox,
            new Label("Téléphone"), telField,
            saveBtn
        );

        dialog.setScene(new Scene(form, 350, 420));
        dialog.show();
    }

    private void openDeleteConfirmation(Medecin m) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmation");

        VBox box = new VBox(20);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: white;");

        Label title = new Label("Confirmation");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label msg = new Label("Est-ce que vous êtes sûr de vouloir supprimer ce médecin ?");
        msg.setStyle("-fx-font-size: 13px;");
        msg.setWrapText(true);
        msg.setMaxWidth(280);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button ouiBtn = new Button("OUI");
        ouiBtn.setStyle("-fx-background-color: #d4ece8; -fx-background-radius: 8; -fx-padding: 8 20;");
        ouiBtn.setOnAction(e -> {
        	MedecinDAO.deleteMedecin(m);
            medecins.remove(m);
            refreshList(medecins);
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
    private void openCalendarDialog(Medecin m) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Créneaux - Dr. " + m.nom);

        VBox main = new VBox(15);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: white;");

        // Doctor name header
        Label docName = new Label("Dr. " + m.nom + " " + m.prenom);
        docName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label specialite = new Label(m.specialite);
        specialite.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");

        // All time slots
        String[] allCreneaux = {
            "08:00", "09:00", "10:00",
            "11:00", "13:00", "14:00",
            "15:00", "16:00", "17:00"
        };

        // Generate next 5 days
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        // Day headers
        LocalDate today = LocalDate.now();
        String[] dayNames = {"Lun.", "Mar.", "Mer.", "Jeu.", "Ven.", "Sam.", "Dim."};

        for (int col = 0; col < 5; col++) {
            LocalDate day = today.plusDays(col);
            String dayName = dayNames[day.getDayOfWeek().getValue() - 1];

            VBox dayHeader = new VBox(2);
            dayHeader.setAlignment(Pos.CENTER);
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d5f5a;");
            Label dateLabel = new Label(day.getDayOfMonth() + "/" + 
                                       String.format("%02d", day.getMonthValue()));
            dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
            dayHeader.getChildren().addAll(dayLabel, dateLabel);
            grid.add(dayHeader, col + 1, 0);
        }

        // Time slot rows
        for (int row = 0; row < allCreneaux.length; row++) {
            String creneau = allCreneaux[row];

            // Time label on left
            Label timeLabel = new Label(creneau);
            timeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
            timeLabel.setPrefWidth(50);
            grid.add(timeLabel, 0, row + 1);

            // Slot buttons for each day
            for (int col = 0; col < 5; col++) {
                LocalDate day = today.plusDays(col);
                boolean taken = isSlotTakenForDoctor(m, day, creneau);

                Button slotBtn = new Button(taken ? "✕" : creneau);
                slotBtn.setPrefWidth(75);
                slotBtn.setPrefHeight(32);

                if (taken) {
                    slotBtn.setStyle("-fx-background-color: #f0f0f0; " +
                            "-fx-text-fill: #aaaaaa; -fx-background-radius: 8; " +
                            "-fx-font-size: 12px;");
                    slotBtn.setDisable(true);
                } else {
                    slotBtn.setStyle("-fx-background-color: #d4ece8; " +
                            "-fx-text-fill: #2d5f5a; -fx-background-radius: 8; " +
                            "-fx-font-size: 12px; -fx-cursor: hand;");
                    slotBtn.setOnAction(e -> {
                        // Mark as taken visually
                        slotBtn.setStyle("-fx-background-color: #f0f0f0; " +
                                "-fx-text-fill: #aaaaaa; -fx-background-radius: 8;");
                        slotBtn.setText("✕");
                        slotBtn.setDisable(true);
                        // You can also add to rendezVousList here if needed
                    });
                }
                
                grid.add(slotBtn, col + 1, row + 1);
            }
        }

        // Wrap grid in scroll pane
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        scrollPane.setStyle("-fx-background-color: white;");

        // Close button
        Button closeBtn = new Button("Fermer");
        closeBtn.setStyle("-fx-background-color: #2d5f5a; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-pref-width: 200; -fx-padding: 8;");
        closeBtn.setOnAction(e -> dialog.close());

        main.getChildren().addAll(docName, specialite, 
                                   new Separator(), scrollPane, closeBtn);
        main.setAlignment(Pos.TOP_LEFT);

        dialog.setScene(new Scene(main, 550, 500));
        dialog.show();
    }
    private boolean isSlotTakenForDoctor(Medecin m, LocalDate date, String creneau) {
        // Fake taken slots for demo
        // Replace with real DB check later
        return (m.nom.equals("Meziane") && creneau.equals("09:00") 
                && date.equals(LocalDate.now())) ||
               (m.nom.equals("Hamid") && creneau.equals("11:00") 
                && date.equals(LocalDate.now().plusDays(1)));
    }
    @FXML
    private void goBack() throws Exception {
        Main.loadScene("menu.fxml", "Menu Principal");
    }
}