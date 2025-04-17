package pkg.tpvente;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class VenteController {

    @FXML private ComboBox<String> medicamentComboBox;
    @FXML private TextField quantityField;
    @FXML private TableView<TransactionItem> itemsTable;
    @FXML private TableColumn<TransactionItem, String> DCIColumn;
    @FXML private TableColumn<TransactionItem, Double> medicamentPriceColumn;
    @FXML private TableColumn<TransactionItem, Integer> quantityColumn;
    @FXML private TableColumn<TransactionItem, Double> totalColumn;
    @FXML private Label statusLabel;
    @FXML private Button removeButton;
    @FXML private Label grandTotalLabel;
    @FXML private TableView<Vente> venteTableView;
    @FXML private TableColumn<Vente, Integer> numVenteColumn;
    @FXML private TableColumn<Vente, String> medicamentColumn;
    @FXML private TableColumn<Vente, Double> montantTotalColumn;
    @FXML private TableColumn<Vente, String> statutColumn;
    @FXML private TableColumn<Vente, LocalDate> dateVenteColumn;
    @FXML private TableView<Vente> venteTableView1;
    @FXML private TableColumn<Vente, Integer> numVenteColumn1;
    @FXML private TableColumn<Vente, String> medicamentColumn1;
    @FXML private TableColumn<Vente, Double> montantTotalColumn1;
    @FXML private TableColumn<Vente, String> statutColumn1;
    @FXML private TableColumn<Vente, LocalDate> dateVenteColumn1;
    @FXML private Button venteOrdonnanceBtn;
    @FXML private Button caisse_btn, vente_btn;
    @FXML private AnchorPane mainPane;

    // ✅ Ajouts nécessaires
    @FXML private AnchorPane vente_form;
    @FXML private AnchorPane caisse_form;

    private final ObservableList<Vente> venteList = FXCollections.observableArrayList();
    private final ObservableList<TransactionItem> transactionItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        venteTableView.setItems(venteList);
        venteTableView1.setItems(venteList);
        itemsTable.setItems(transactionItems);
        loadVentes();
        refreshMedicamentComboBox();

        // ✅ Formulaires : par défaut on affiche "vente"
        vente_form.setVisible(true);
        caisse_form.setVisible(false);
    }

    @FXML
    private void openVenteOrdonnance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("VenteOrdonnanceView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Vente sur Ordonnance");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors du chargement de la vente sur ordonnance.");
        }
    }


    @FXML
    public void loadVenteForm() {
        vente_form.setVisible(true);
        caisse_form.setVisible(false);
    }

    @FXML
    public void loadCaisseForm() {
        vente_form.setVisible(false);
        caisse_form.setVisible(true);
    }

    public void setRole(String role) {
        if (role.equals("Caissier")) {
            vente_btn.setVisible(false);
            caisse_btn.setVisible(true);
            loadCaisseForm(); // charge la vue caisse automatiquement
        } else if (role.equals("Vendeur")) {
            caisse_btn.setVisible(false);
            vente_btn.setVisible(true);
            loadVenteForm(); // charge la vue vente automatiquement
        } else {
            // rôle mixte, on affiche les deux
            vente_btn.setVisible(true);
            caisse_btn.setVisible(true);
        }
    }

    private void setupTableColumns() {
        DCIColumn.setCellValueFactory(new PropertyValueFactory<>("DCI"));
        medicamentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixVente"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantiteVendue"));
        totalColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTotal()));

        numVenteColumn.setCellValueFactory(cellData -> cellData.getValue().numVenteProperty().asObject());
        medicamentColumn.setCellValueFactory(cellData -> cellData.getValue().medicamentProperty());
        montantTotalColumn.setCellValueFactory(cellData -> cellData.getValue().montant_totalProperty().asObject());
        statutColumn.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
        dateVenteColumn.setCellValueFactory(cellData -> cellData.getValue().date_venteProperty());

        numVenteColumn1.setCellValueFactory(numVenteColumn.getCellValueFactory());
        medicamentColumn1.setCellValueFactory(medicamentColumn.getCellValueFactory());
        montantTotalColumn1.setCellValueFactory(montantTotalColumn.getCellValueFactory());
        statutColumn1.setCellValueFactory(statutColumn.getCellValueFactory());
        dateVenteColumn1.setCellValueFactory(dateVenteColumn.getCellValueFactory());
    }

    private void loadVentes() {
        venteList.clear();
        try (Connection conn = getDatabaseConnection()) {
            String query = "SELECT num_vente, medicament, montant_total, statut, date_vente FROM Vente";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    venteList.add(new Vente(
                            rs.getInt("num_vente"),
                            rs.getString("medicament"),
                            rs.getDouble("montant_total"),
                            rs.getString("statut"),
                            rs.getDate("date_vente").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur de chargement des ventes : " + e.getMessage());
        }
    }

    private void refreshMedicamentComboBox() {
        ObservableList<String> medicamentList = FXCollections.observableArrayList();
        try (Connection conn = getDatabaseConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT DCI FROM Medicament WHERE stock > 0");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                medicamentList.add(rs.getString("DCI"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement médicaments : " + e.getMessage());
        }
        medicamentComboBox.setItems(medicamentList);
    }

    @FXML
    public void addItem() {
        String medicament = medicamentComboBox.getValue();
        String quantityStr = quantityField.getText();
        if (medicament == null || quantityStr.trim().isEmpty()) {
            statusLabel.setText("Veuillez sélectionner un médicament et entrer une quantité.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                statusLabel.setText("Quantité invalide.");
                return;
            }
            try (Connection conn = getDatabaseConnection()) {
                PreparedStatement stmt = conn.prepareStatement("SELECT prix_vente, stock FROM Medicament WHERE DCI = ?");
                stmt.setString(1, medicament);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double prixVente = rs.getDouble("prix_vente");
                        int stock = rs.getInt("stock");
                        if (quantity > stock) {
                            statusLabel.setText("Stock insuffisant.");
                            return;
                        }
                        transactionItems.add(new TransactionItem(medicament, prixVente, quantity));
                        updateGrandTotal();
                        statusLabel.setText("Article ajouté avec succès.");
                    } else {
                        statusLabel.setText("Médicament non trouvé.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Quantité invalide.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur base de données : " + e.getMessage());
        }
    }

    @FXML
    public void removeSelectedItem() {
        TransactionItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            transactionItems.remove(selectedItem);
            updateGrandTotal();
            statusLabel.setText("Article supprimé.");
        } else {
            statusLabel.setText("Aucun article sélectionné.");
        }
    }

    @FXML
    public void finalizeSale() {
        if (transactionItems.isEmpty()) {
            statusLabel.setText("Aucun article à vendre.");
            return;
        }

        try (Connection conn = getDatabaseConnection()) {
            conn.setAutoCommit(false);
            double grandTotal = transactionItems.stream().mapToDouble(TransactionItem::getTotal).sum();

            try (PreparedStatement venteStmt = conn.prepareStatement(
                    "INSERT INTO Vente (date_vente, medicament, montant_total, statut) VALUES (CURRENT_DATE, ?, ?, 'en attente') RETURNING num_vente"
            )) {
                String medicamentListe = transactionItems.stream()
                        .map(TransactionItem::getDCI)
                        .distinct()
                        .reduce((m1, m2) -> m1 + ", " + m2)
                        .orElse("");

                venteStmt.setString(1, medicamentListe);
                venteStmt.setDouble(2, grandTotal);

                ResultSet venteRs = venteStmt.executeQuery();
                venteRs.next();
                int venteId = venteRs.getInt("num_vente");

                try (PreparedStatement ligneStmt = conn.prepareStatement(
                        "INSERT INTO Ligne_vendu (num_vente, medicament_dci, prixunit_vente, qte_vendue) VALUES (?, ?, ?, ?)"
                )) {
                    for (TransactionItem item : transactionItems) {
                        ligneStmt.setInt(1, venteId);
                        ligneStmt.setString(2, item.getDCI());
                        ligneStmt.setDouble(3, item.getPrixVente());
                        ligneStmt.setInt(4, item.getQuantiteVendue());
                        ligneStmt.addBatch();
                    }
                    ligneStmt.executeBatch();
                }
            }

            conn.commit();
            transactionItems.clear();
            statusLabel.setText("Vente enregistrée en attente de paiement. Veuillez passer à la caisse !");
            loadVentes();
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de l'enregistrement de la vente : " + e.getMessage());
        }
    }

    private void updateGrandTotal() {
        double grandTotal = transactionItems.stream().mapToDouble(TransactionItem::getTotal).sum();
        grandTotalLabel.setText(String.format("Total: %.2f €", grandTotal));
    }

    @FXML
    public void validatePayment() {
        Vente selectedVente = venteTableView.getSelectionModel().getSelectedItem();
        if (selectedVente == null) {
            selectedVente = venteTableView1.getSelectionModel().getSelectedItem();
        }

        if (selectedVente == null || !selectedVente.getStatut().equals("en attente")) {
            statusLabel.setText("Sélectionnez une vente en attente de paiement.");
            return;
        }

        try (Connection conn = getDatabaseConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Vente SET statut = 'payée' WHERE num_vente = ?"
            );
            stmt.setInt(1, selectedVente.getNumVente());
            stmt.executeUpdate();
            statusLabel.setText("Paiement validé et vente marquée comme payée.");
            loadVentes();
        } catch (SQLException e) {
            statusLabel.setText("Erreur lors de la validation du paiement : " + e.getMessage());
        }
    }

    private Connection getDatabaseConnection() throws SQLException {
        return new DatabaseConnection().getConnection();
    }

    @FXML
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
