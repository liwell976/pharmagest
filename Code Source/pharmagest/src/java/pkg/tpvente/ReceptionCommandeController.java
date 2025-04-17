package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

public class ReceptionCommandeController {

    @FXML private TableView<ReceptionCommande> receptionTable;
    @FXML private TableColumn<ReceptionCommande, Integer> numCommandeColumn;
    @FXML private TableColumn<ReceptionCommande, String> medicamentColumn;
    @FXML private TableColumn<ReceptionCommande, Integer> quantiteCommandeeColumn;
    @FXML private TableColumn<ReceptionCommande, Integer> quantiteRecueColumn;
    @FXML private TableColumn<ReceptionCommande, Double> prixColumn;
    @FXML private TableColumn<ReceptionCommande, String> statutColumn;
    @FXML private TextField quantiteRecueField;
    @FXML private TextField prixFinalField;
    @FXML private Label statusLabel;
    @FXML private AnchorPane receptionCommande_form;

    private final ObservableList<ReceptionCommande> receptionList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        receptionTable.setItems(receptionList);
        loadCommandes();
        getReceptionCommandeForm();
    }
    public AnchorPane getReceptionCommandeForm() {
        return receptionCommande_form;
    }

    private void setupTableColumns() {
        numCommandeColumn.setCellValueFactory(cellData -> cellData.getValue().numCommandeProperty().asObject());
        medicamentColumn.setCellValueFactory(cellData -> cellData.getValue().medicamentProperty());
        quantiteCommandeeColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteCommandeeProperty().asObject());
        quantiteRecueColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteRecueProperty().asObject());
        prixColumn.setCellValueFactory(cellData -> cellData.getValue().prixUnitaireProperty().asObject());
        statutColumn.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
    }

    private void loadCommandes() {
        receptionList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Commandefournisseur WHERE statut = 'en attente'")) {
            while (rs.next()) {
                receptionList.add(new ReceptionCommande(
                        rs.getInt("num_commande"),
                        rs.getString("medicament"),
                        rs.getInt("quantite"),
                        0, // Quantité reçue par défaut à 0
                        rs.getDouble("prix_unitaire"),
                        0.0, // Prix final par défaut
                        rs.getString("statut"),
                        "", // Pas de problème signalé par défaut
                        null // Date de réception inconnue au départ
                ));

            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement commandes : " + e.getMessage());
        }
    }
    @FXML
    public void signalerProbleme() {
        ReceptionCommande selectedCommande = receptionTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            statusLabel.setText("Sélectionnez une commande à signaler.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO ProblemesReception (num_commande, medicament, description) VALUES (?, ?, ?)")) {
            stmt.setInt(1, selectedCommande.getNumCommande());
            stmt.setString(2, selectedCommande.getMedicament());
            stmt.setString(3, "Problème signalé sur cette réception.");
            stmt.executeUpdate();

            statusLabel.setText("Problème signalé avec succès.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur signalement : " + e.getMessage());
        }
    }

    @FXML
    public void confirmerReception() {
        ReceptionCommande selectedCommande = receptionTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            statusLabel.setText("Sélectionnez une commande.");
            return;
        }

        // Vérifier que la quantité reçue et le prix final sont bien remplis
        String quantiteStr = quantiteRecueField.getText();
        String prixStr = prixFinalField.getText();
        if (quantiteStr.isEmpty() || prixStr.isEmpty()) {
            statusLabel.setText("Veuillez entrer la quantité reçue et le prix final.");
            return;
        }

        try {
            int quantiteRecue = Integer.parseInt(quantiteStr);
            double prixFinal = Double.parseDouble(prixStr);

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                // Mettre à jour le stock du médicament
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Medicament SET stock = stock + ? WHERE DCI = ?")) {
                    stmt.setInt(1, quantiteRecue);
                    stmt.setString(2, selectedCommande.getMedicament());
                    stmt.executeUpdate();
                }

                // Mettre à jour la commande fournisseur (statut et prix final)
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE CommandeFournisseur SET quantite_recue = ?, prix_unitaire = ?, statut = 'reçue' WHERE num_commande = ?")) {
                    stmt.setInt(1, quantiteRecue);
                    stmt.setDouble(2, prixFinal);
                    stmt.setInt(3, selectedCommande.getNumCommande());
                    stmt.executeUpdate();
                }

                conn.commit();
                statusLabel.setText("Réception confirmée et stock mis à jour.");
                loadCommandes();
                clearFields();
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Valeurs invalides.");
        } catch (SQLException e) {
            statusLabel.setText("Erreur mise à jour stock : " + e.getMessage());
        }
    }
    private void clearFields() {
        quantiteRecueField.clear();
        prixFinalField.clear();
    }

}
