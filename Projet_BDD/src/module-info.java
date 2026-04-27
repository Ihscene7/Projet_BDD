module Projet_BDD {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;          // ← ajoute cette ligne
  
	opens application to javafx.graphics, javafx.fxml;
}
