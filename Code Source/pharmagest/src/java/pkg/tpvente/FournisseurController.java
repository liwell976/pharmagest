package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class FournisseurController {

    @FXML private TableView<Fournisseur> fournisseurTable;
    @FXML private TableColumn<Fournisseur, Integer> idColumn;
    @FXML private TableColumn<Fournisseur, String> nomColumn;
    @FXML private TableColumn<Fournisseur, String> contactColumn;
    @FXML private TableColumn<Fournisseur, String> adresseColumn;
    @FXML private TableColumn<Fournisseur, String> telephoneColumn;

    @FXML private TextField searchField;
    @FXML private TextField nomField;
    @FXML private TextField contactField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;

    @FXML private Label statusLabel;

    private final ObservableList<Fournisseur> fournisseurList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        fournisseurTable.setItems(fournisseurList);
        loadFournisseurs();
        searchFournisseur();
        ajouterFournisseur();
        modifierFournisseur();
        supprimerFournisseur();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idFournisseurProperty().asObject());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        adresseColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty());
        telephoneColumn.setCellValueFactory(cellData -> cellData.getValue().telephoneProperty());
    }
    @FXML
    private void loadFournisseurs() {
        fournisseurList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Fournisseur")) {
            while (rs.next()) {
                fournisseurList.add(new Fournisseur(
                        rs.getInt("id_fournisseur"),
                        rs.getString("nom"),
                        rs.getString("contact"),
                        rs.getString("adresse"),
                        rs.getString("telephone")
                ));
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur chargement fournisseurs : " + e.getMessage());
        }
    }

    @FXML
    public void searchFournisseur() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadFournisseurs();
            return;
        }

        fournisseurList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Fournisseur WHERE nom ILIKE ? OR contact ILIKE ?")) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fournisseurList.add(new Fournisseur(
                            rs.getInt("id_fournisseur"),
                            rs.getString("nom"),
                            rs.getString("contact"),
                            rs.getString("adresse"),
                            rs.getString("telephone")
                    ));
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur recherche : " + e.getMessage());
        }
    }

    @FXML
    public void ajouterFournisseur() {
        String nom = nomField.getText();
        String contact = contactField.getText();
        String adresse = adresseField.getText();
        String telephone = telephoneField.getText();

        if (nom.isEmpty() || contact.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Fournisseur (nom, contact, adresse, telephone) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, nom);
            stmt.setString(2, contact);
            stmt.setString(3, adresse);
            stmt.setString(4, telephone);
            stmt.executeUpdate();
            statusLabel.setText("Fournisseur ajouté avec succès !");
            loadFournisseurs();
        } catch (SQLException e) {
            statusLabel.setText("Erreur ajout : " + e.getMessage());
        }
    }

    @FXML
    public void modifierFournisseur() {
        Fournisseur selected = fournisseurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un fournisseur.");
            return;
        }

        String nom = nomField.getText().trim();
        String contact = contactField.getText().trim();
        String adresse = adresseField.getText().trim();
        String telephone = telephoneField.getText().trim();

        if (nom.isEmpty() || contact.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        String query = "UPDATE Fournisseur SET nom = ?, contact = ?, adresse = ?, telephone = ? WHERE id_fournisseur = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            stmt.setString(2, contact);
            stmt.setString(3, adresse);
            stmt.setString(4, telephone);
            stmt.setInt(5, selected.getIdFournisseur());
            stmt.executeUpdate();

            statusLabel.setText("Fournisseur modifié avec succès !");
            loadFournisseurs();
        } catch (SQLException e) {
            statusLabel.setText("Erreur modification : " + e.getMessage());
        }
    }

    @FXML
    public void supprimerFournisseur() {
        Fournisseur selected = fournisseurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un fournisseur.");
            return;
        }

        String query = "DELETE FROM Fournisseur WHERE id_fournisseur = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selected.getIdFournisseur());
            stmt.executeUpdate();

            statusLabel.setText("Fournisseur supprimé avec succès !");
            loadFournisseurs();
        } catch (SQLException e) {
            statusLabel.setText("Erreur suppression : " + e.getMessage());
        }
    }

}
