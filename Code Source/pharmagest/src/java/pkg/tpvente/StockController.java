package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class StockController {

    @FXML private TableView<Medicament> stockTable;
    @FXML private TableColumn<Medicament, String> dciColumn;
    @FXML private TableColumn<Medicament, String> familleColumn;
    @FXML private TableColumn<Medicament, Double> prixVenteColumn;
    @FXML private TableColumn<Medicament, Integer> stockColumn;
    @FXML private TableColumn<Medicament, Integer> seuilMinColumn;
    @FXML private TableColumn<Medicament, Integer> stockMaxColumn;
    @FXML private TextField searchField;
    @FXML private TextField dciField;
    @FXML private TextField stockField;
    @FXML private Label statusLabel;

    private final ObservableList<Medicament> stockList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        stockTable.setItems(stockList);
        loadStockData();
        checkStockLevels();
        clearFields();

        // Sélectionner un élément pour l'éditer
        stockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                dciField.setText(newSelection.getDCI());
                stockField.setText(String.valueOf(newSelection.getStock()));
            }
        });
    }

    private void setupTableColumns() {
        dciColumn.setCellValueFactory(cellData -> cellData.getValue().DCIProperty());
        familleColumn.setCellValueFactory(cellData -> cellData.getValue().familleProperty());
        prixVenteColumn.setCellValueFactory(cellData -> cellData.getValue().prixVenteProperty().asObject());
        stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());
        seuilMinColumn.setCellValueFactory(cellData -> cellData.getValue().seuilMinProperty().asObject());
        stockMaxColumn.setCellValueFactory(cellData -> cellData.getValue().stockMaxProperty().asObject());
    }

    private void loadStockData() {
        stockList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dci, famille, prix_vente, stock, seuil_min, stock_max FROM medicament")) {

            while (rs.next()) {
                stockList.add(new Medicament(
                        rs.getString("dci"),
                        rs.getString("famille"),
                        rs.getDouble("prix_vente"),
                        rs.getInt("stock"),
                        rs.getInt("seuil_min"),
                        rs.getInt("stock_max")
                ));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement stock : " + e.getMessage());
        }
    }

    @FXML
    public void searchStock() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStockData(); // Recharge toutes les données si aucun mot-clé n'est entré
            return;
        }

        stockList.clear();
        String query = """
            SELECT dci, famille, prix_vente, stock, seuil_min, stock_max
            FROM medicament
            WHERE dci ILIKE ? OR famille ILIKE ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stockList.add(new Medicament(
                            rs.getString("dci"),
                            rs.getString("famille"),
                            rs.getDouble("prix_vente"),
                            rs.getInt("stock"),
                            rs.getInt("seuil_min"),
                            rs.getInt("stock_max")
                    ));
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de recherche : " + e.getMessage());
        }
    }


    @FXML
    public void updateStock() {
        Medicament selectedMedicament = stockTable.getSelectionModel().getSelectedItem();
        if (selectedMedicament == null) {
            statusLabel.setText("Sélectionnez un médicament.");
            return;
        }

        String newStockStr = stockField.getText();
        if (newStockStr.isEmpty()) {
            statusLabel.setText("Veuillez entrer une valeur.");
            return;
        }

        try {
            int newStock = Integer.parseInt(newStockStr);

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE medicament SET stock = ? WHERE dci = ?")) {

                stmt.setInt(1, newStock);
                stmt.setString(2, selectedMedicament.getDCI());
                stmt.executeUpdate();
            }

            statusLabel.setText("Stock mis à jour.");
            loadStockData();
        } catch (NumberFormatException e) {
            statusLabel.setText("Valeur invalide.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur mise à jour : " + e.getMessage());
        }
    }
@FXML
    public void alertStock() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Stock bas ! Passez une commande fournisseur.", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    public void refreshStock() {
        loadStockData();
    }
    private void checkStockLevels() {
        for (Medicament med : stockList) {
            if (med.getStock() <= med.getSeuilMin()) {
                alertStock();
                break; // Affiche l'alerte une seule fois même si plusieurs médicaments ont un stock bas
            }
        }
    }
    @FXML
    public void clearFields() {
        searchField.clear(); // Efface le champ de recherche
        stockField.clear(); // Efface le champ du stock
    }

}
