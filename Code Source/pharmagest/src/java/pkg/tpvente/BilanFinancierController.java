package pkg.tpvente;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class BilanFinancierController {

    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TableView<BilanFinancier> ventesTable;
    @FXML private TableColumn<BilanFinancier, Integer> numVenteColumn;
    @FXML private TableColumn<BilanFinancier, LocalDate> dateVenteColumn;
    @FXML private TableColumn<BilanFinancier, Double> montantVenteColumn;

    @FXML private TableView<BilanFinancier> achatsTable;
    @FXML private TableColumn<BilanFinancier, Integer> numCommandeColumn;
    @FXML private TableColumn<BilanFinancier, LocalDate> dateCommandeColumn;
    @FXML private TableColumn<BilanFinancier, Double> montantCommandeColumn;

    @FXML private Label totalVentesLabel;
    @FXML private Label totalAchatsLabel;
    @FXML private Label beneficeNetLabel;

    @FXML private AnchorPane mainPane;
    @FXML private AnchorPane maintenance_form;
    @FXML private AnchorPane bilanFinancier_form;
    @FXML private Button maintenance_btn, bilan_btn;
    @FXML private Button MAJ_btn, maintenanceMedicament_btn, fournisseur_btn;

    private final ObservableList<BilanFinancier> ventesList = FXCollections.observableArrayList();
    private final ObservableList<BilanFinancier> achatsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        ventesTable.setItems(ventesList);
        achatsTable.setItems(achatsList);

        maintenance_btn.setOnAction(event -> showMaintenance());
        bilan_btn.setOnAction(event -> loadBilan());
    }

    private void setupTableColumns() {
        numVenteColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        dateVenteColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));
        montantVenteColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMontant()));

        numCommandeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        dateCommandeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDate()));
        montantCommandeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getMontant()));
    }
    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur ouverture de la fenêtre : " + title);
        }
    }
    @FXML
    private void openUserView() {
        openNewWindow("UserView.fxml", "Gestion des Utilisateurs");
    }

    @FXML
    private void openMiseAJourPrixView() {
        openNewWindow("MiseAJourPrixView.fxml", "Mise à jour des Prix");
    }

    @FXML
    private void openFournisseurView() {
        openNewWindow("FournisseurView.fxml", "Gestion des Fournisseurs");
    }

    @FXML
    private void openStockView() {
        openNewWindow("StockView.fxml", "Gestion du Stock");
    }

    @FXML
    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = (Stage) mainPane.getScene().getWindow(); // Utilisation de mainPane pour obtenir la scène
            stage.getScene().setRoot(root); // Remplacer le root de la scène par le nouvel FXML

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de l'interface : " + fxmlFile);
        }
    }

    private void showMaintenance() {
        mainPane.setVisible(true);
        bilanFinancier_form.setVisible(false);
    }

    @FXML
    private void loadBilan() {
        mainPane.setVisible(false);
        bilanFinancier_form.setVisible(true);
    }


    @FXML
    public void filtrerBilan() {
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();

        if (debut == null || fin == null) {
            return;
        }

        ventesList.clear();
        achatsList.clear();
        double totalVentes = 0;
        double totalAchats = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Charger les ventes
            String queryVentes = "SELECT num_vente, date_vente, montant_total FROM Vente WHERE date_vente BETWEEN ? AND ? AND statut='payée'";
            try (PreparedStatement stmt = conn.prepareStatement(queryVentes)) {
                stmt.setDate(1, Date.valueOf(debut));
                stmt.setDate(2, Date.valueOf(fin));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double montant = rs.getDouble("montant_total");
                        ventesList.add(new BilanFinancier(rs.getInt("num_vente"), rs.getDate("date_vente").toLocalDate(), montant));
                        totalVentes += montant;
                    }
                }
            }

            // Charger les achats fournisseurs
            String queryAchats = "SELECT num_commande, date_commande, montant_total FROM CommandeFournisseur WHERE date_commande BETWEEN ? AND ? AND statut='reçue'";
            try (PreparedStatement stmt = conn.prepareStatement(queryAchats)) {
                stmt.setDate(1, Date.valueOf(debut));
                stmt.setDate(2, Date.valueOf(fin));
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double montant = rs.getDouble("montant_total");
                        achatsList.add(new BilanFinancier(rs.getInt("num_commande"), rs.getDate("date_commande").toLocalDate(), montant));
                        totalAchats += montant;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalVentesLabel.setText(String.format("%.2f €", totalVentes));
        totalAchatsLabel.setText(String.format("%.2f €", totalAchats));
        beneficeNetLabel.setText(String.format("%.2f €", totalVentes - totalAchats));
    }

    @FXML
    public void exporterPDF() {
        // Exporter en PDF
    }

    @FXML
    public void exporterExcel() {
        // Exporter en Excel
    }

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
