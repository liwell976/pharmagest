package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CommandeFournisseurController {

    @FXML private ComboBox<String> fournisseurComboBox;
    @FXML private ComboBox<String> medicamentComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TableView<CommandeFournisseur> commandeTable;
    @FXML private TableColumn<CommandeFournisseur, Integer> numCommandeColumn;
    @FXML private TableColumn<CommandeFournisseur, String> fournisseurColumn;
    @FXML private TableColumn<CommandeFournisseur, String> medicamentColumn;
    @FXML private TableColumn<CommandeFournisseur, Integer> quantiteColumn;
    @FXML private TableColumn<CommandeFournisseur, Double> prixColumn;
    @FXML private TableColumn<CommandeFournisseur, Double> montantTotalColumn;
    @FXML private TableColumn<CommandeFournisseur, String> statutColumn;
    @FXML private Button annulerCommandeButton;
    @FXML private Label statusLabel;
    @FXML private Button reception_btn, commande_btn;
    @FXML private VBox commande_form;
    @FXML private AnchorPane mainPane;

    private final ObservableList<CommandeFournisseur> commandeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        commandeTable.setItems(commandeList);
        loadCommandes();
        loadFournisseurs();
        loadMedicaments();
        commande_btn.setOnAction(event -> showCommandeForm());
        reception_btn.setOnAction(event -> loadReceptionForm());
        mainPane.getChildren().setAll(commande_form);
    }
    private void setupTableColumns() {
        numCommandeColumn.setCellValueFactory(new PropertyValueFactory<>("numCommande"));
        fournisseurColumn.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));
        medicamentColumn.setCellValueFactory(new PropertyValueFactory<>("medicament"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        montantTotalColumn.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
    }
    @FXML
    private void showCommandeForm() {
        mainPane.getChildren().setAll(commande_form);
    }

    @FXML
    private void loadReceptionForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReceptionCommande.fxml"));
            AnchorPane receptionPane = loader.load();

            mainPane.getChildren().setAll(receptionPane);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement de ReceptionCommande.fxml");
        }
    }
    private void loadFournisseurs() {
        ObservableList<String> fournisseurs = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nom FROM Fournisseur")) {
            while (rs.next()) {
                fournisseurs.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement fournisseurs : " + e.getMessage());
        }
        fournisseurComboBox.setItems(fournisseurs);
    }

    private void loadMedicaments() {
        ObservableList<String> medicaments = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DCI FROM Medicament")) {
            while (rs.next()) {
                medicaments.add(rs.getString("DCI"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement médicaments : " + e.getMessage());
        }
        medicamentComboBox.setItems(medicaments);
    }

    private void loadCommandes() {
        commandeList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT num_commande, fournisseur, medicament, quantite, prix_unitaire, montant_total, statut FROM CommandeFournisseur")) {
            while (rs.next()) {
                commandeList.add(new CommandeFournisseur(
                        rs.getInt("num_commande"),
                        rs.getString("fournisseur"),
                        rs.getString("medicament"),
                        rs.getInt("quantite"),
                        rs.getDouble("prix_unitaire"),
                        rs.getString("statut")
                ));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement commandes : " + e.getMessage());
        }
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'interface : " + fxmlFile);
        }
    }

    @FXML
    public void passerCommande() {
        String fournisseur = fournisseurComboBox.getValue();
        String medicament = medicamentComboBox.getValue();
        String quantiteStr = quantityField.getText();
        String prixStr = priceField.getText();

        if (fournisseur == null || medicament == null || quantiteStr.isEmpty() || prixStr.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            int quantite = Integer.parseInt(quantiteStr);
            double prixUnitaire = Double.parseDouble(prixStr);
            double montantTotal = quantite * prixUnitaire;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO CommandeFournisseur (fournisseur, medicament, quantite, prix_unitaire, montant_total, statut) VALUES (?, ?, ?, ?, ?, 'en attente')")) {
                stmt.setString(1, fournisseur);
                stmt.setString(2, medicament);
                stmt.setInt(3, quantite);
                stmt.setDouble(4, prixUnitaire);
                stmt.setDouble(5, montantTotal);
                stmt.executeUpdate();
            }
            statusLabel.setText("Commande passée avec succès !");
            loadCommandes();
        } catch (NumberFormatException e) {
            statusLabel.setText("Valeurs invalides.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur base de données : " + e.getMessage());
        }
    }
    @FXML
    private void annulerCommande() {
        System.out.println("Commande annulée !");
    }
    @FXML
    public void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de Login.fxml");
        }
    }
}
