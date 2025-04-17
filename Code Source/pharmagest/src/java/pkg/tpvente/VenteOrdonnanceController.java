package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;

public class VenteOrdonnanceController {

    @FXML private ComboBox<String> medicamentComboBox;
    @FXML private TextField quantiteField;
    @FXML private TableView<VenteOrdonnance> tableVente;
    @FXML private TableColumn<VenteOrdonnance, String> medicamentColumn;
    @FXML private TableColumn<VenteOrdonnance, Integer> quantiteColumn;
    @FXML private TableColumn<VenteOrdonnance, Double> prixColumn;
    @FXML private TextField nomMedecinField;
    @FXML private TextField nomPatientField;
    @FXML private DatePicker datePrescriptionPicker;
    @FXML private Label statusLabel;

    private final ObservableList<VenteOrdonnance> venteList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        tableVente.setItems(venteList);
        loadMedicaments();
    }

    private void setupTableColumns() {
        medicamentColumn.setCellValueFactory(cellData -> cellData.getValue().medicamentProperty());
        quantiteColumn.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());
        prixColumn.setCellValueFactory(cellData -> cellData.getValue().prixProperty().asObject());
    }

    private void loadMedicaments() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT dci FROM Medicament WHERE ordonnance_requise = true")) {
            while (rs.next()) {
                medicamentComboBox.getItems().add(rs.getString("dci"));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement médicaments : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterMedicament() {
        String medicament = medicamentComboBox.getValue();
        String quantiteStr = quantiteField.getText();
        if (medicament == null || quantiteStr.isEmpty()) {
            statusLabel.setText("Sélectionnez un médicament et entrez une quantité.");
            return;
        }

        try {
            int quantite = Integer.parseInt(quantiteStr);
            if (quantite <= 0) {
                statusLabel.setText("Quantité invalide.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT prix_vente FROM Medicament WHERE dci = ?")) {
                stmt.setString(1, medicament);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double prix = rs.getDouble("prix_vente");
                        venteList.add(new VenteOrdonnance(
                                0,                        // idOrdonnance (met 0 par défaut si pas encore défini)
                                "",                       // nomMedecin (vide pour l'instant)
                                "",                       // nomPatient (vide pour l'instant)
                                LocalDate.now(),          // datePrescription (ou une valeur correcte si disponible)
                                medicament,               // médicament (OK)
                                quantite,                 // quantité (OK)
                                prix                      // prix unitaire (OK)
                        ));
                        statusLabel.setText("Médicament ajouté.");
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
    public void finaliserVente() {
        if (venteList.isEmpty()) {
            statusLabel.setText("Aucun médicament sélectionné.");
            return;
        }

        String nomMedecin = nomMedecinField.getText().trim();
        String nomPatient = nomPatientField.getText().trim();
        LocalDate datePrescription = datePrescriptionPicker.getValue();

        if (nomMedecin.isEmpty() || nomPatient.isEmpty() || datePrescription == null) {
            statusLabel.setText("Veuillez remplir les informations de l'ordonnance.");
            return;
        }

        for (VenteOrdonnance vente : venteList) {
            vente.nomMedecinProperty().set(nomMedecin);
            vente.nomPatientProperty().set(nomPatient);
            vente.datePrescriptionProperty().set(datePrescription);
        }

        statusLabel.setText("Vente sous ordonnance finalisée !");
        venteList.clear();
    }

    @FXML
    public void annulerVente() {
        venteList.clear();
        statusLabel.setText("Vente annulée.");
    }
}
