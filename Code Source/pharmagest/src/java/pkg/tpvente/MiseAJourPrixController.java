package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class MiseAJourPrixController {

    @FXML private TableView<Medicament> medicamentTable;
    @FXML private TableColumn<Medicament, String> dciColumn;
    @FXML private TableColumn<Medicament, Double> prixVenteColumn;

    @FXML private ComboBox<String> fournisseurComboBox;
    @FXML private TableView<MiseAJourPrix> prixFournisseurTable;
    @FXML private TableColumn<MiseAJourPrix, String> fournisseurColumn;
    @FXML private TableColumn<MiseAJourPrix, Double> prixAchatColumn;

    @FXML private TextField nouveauPrixField;
    @FXML private Label statusLabel;

    private final ObservableList<Medicament> medicamentList = FXCollections.observableArrayList();
    private final ObservableList<MiseAJourPrix> miseAJourPrixList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        medicamentTable.setItems(medicamentList);
        prixFournisseurTable.setItems(miseAJourPrixList);
        loadMedicaments();
        loadFournisseurs();

        // Ajouter un listener pour charger les prix des fournisseurs lorsque l'on sélectionne un médicament
        medicamentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadPrixFournisseurs(newSelection.getDCI());
            }
        });
    }

    private void setupTableColumns() {
        dciColumn.setCellValueFactory(cellData -> cellData.getValue().DCIProperty());
        prixVenteColumn.setCellValueFactory(cellData -> cellData.getValue().prixVenteProperty().asObject());

        fournisseurColumn.setCellValueFactory(cellData -> cellData.getValue().fournisseurProperty());
        prixAchatColumn.setCellValueFactory(cellData -> cellData.getValue().prixAchatProperty().asObject());
    }

    private void loadMedicaments() {
        medicamentList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dci, famille, prix_vente, stock, seuil_min, stock_max FROM Medicament")) {
            while (rs.next()) {
                medicamentList.add(new Medicament(
                        rs.getString("dci"),
                        rs.getString("famille"),
                        rs.getDouble("prix_vente"),
                        rs.getInt("stock"),
                        rs.getInt("seuil_min"),
                        rs.getInt("stock_max")
                ));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement médicaments : " + e.getMessage());
        }
    }


    private void loadFournisseurs() {
        fournisseurComboBox.getItems().clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT fournisseur FROM Commandefournisseur")) {
            while (rs.next()) {
                fournisseurComboBox.getItems().add(rs.getString("fournisseur"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement fournisseurs : " + e.getMessage());
        }
    }

    private void loadPrixFournisseurs(String dci) {
        System.out.println("Recherche des prix fournisseurs pour : " + dci); // 🔹 Ajout pour vérifier

        miseAJourPrixList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT fournisseur, prix_unitaire FROM CommandeFournisseur WHERE LOWER(medicament) = LOWER(?)"
             )) {
            stmt.setString(1, dci);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean hasData = false;
                while (rs.next()) {
                    System.out.println("Fournisseur trouvé : " + rs.getString("fournisseur") + ", Prix : " + rs.getDouble("prix_unitaire"));
                    miseAJourPrixList.add(new MiseAJourPrix(
                            rs.getString("fournisseur"),
                            rs.getDouble("prix_unitaire")
                    ));
                    hasData = true;
                }
                if (!hasData) {
                    System.out.println("Aucune donnée trouvée pour " + dci);
                    statusLabel.setText("Aucun prix fournisseur trouvé pour ce médicament.");
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement prix fournisseurs : " + e.getMessage());
        }
        prixFournisseurTable.refresh();
    }


    @FXML
    public void mettreAJourPrix() {
        Medicament selectedMedicament = medicamentTable.getSelectionModel().getSelectedItem();
        if (selectedMedicament == null) {
            statusLabel.setText("Sélectionnez un médicament.");
            return;
        }

        String nouveauPrixStr = nouveauPrixField.getText().trim();
        if (nouveauPrixStr.isEmpty()) {
            statusLabel.setText("Entrez un prix valide.");
            return;
        }

        try {
            double nouveauPrix = Double.parseDouble(nouveauPrixStr);
            if (nouveauPrix <= 0) {
                statusLabel.setText("Le prix doit être positif.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE Medicament SET prix_vente = ? WHERE dci = ?")) {
                stmt.setDouble(1, nouveauPrix);
                stmt.setString(2, selectedMedicament.getDCI());
                stmt.executeUpdate();
            }

            selectedMedicament.setPrixVente(nouveauPrix);
            medicamentTable.refresh();
            nouveauPrixField.clear();
            statusLabel.setText("Prix mis à jour avec succès !");
        } catch (NumberFormatException | SQLException e) {
            statusLabel.setText("Erreur mise à jour : " + e.getMessage());
        }
    }
}
