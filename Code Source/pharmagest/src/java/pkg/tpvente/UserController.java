package pkg.tpvente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> colUsername, colEmail, colStatus;
    @FXML
    private TextField txtUsername, txtEmail, txtPassword;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private Button btnAdd, btnUpdate, btnDelete, btnSearch;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void initialize() {
        // Configuration du ComboBox
        cmbStatus.setItems(FXCollections.observableArrayList(
                "Vendeur", "Caissier", "Pharmacien", "Administrateur"
        ));

        // Configuration des colonnes
        colUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        String query = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                userList.add(new User(
                        rs.getString("username"),
                        rs.getString("mdp"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }
            userTable.setItems(userList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs", e.getMessage());
        }
    }

    @FXML
    private void addUser() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String email = txtEmail.getText();
        String status = cmbStatus.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || status == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        String query = "INSERT INTO users (username, mdp, email, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, status);
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès.");
            loadUsers();
            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur", e.getMessage());
        }
    }

    @FXML
    private void updateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à modifier.");
            return;
        }

        String query = "UPDATE users SET username=?, mdp=?, email=?, status=? WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, txtUsername.getText());
            stmt.setString(2, txtPassword.getText());
            stmt.setString(3, txtEmail.getText());
            stmt.setString(4, cmbStatus.getValue());
            stmt.setString(5, selectedUser.getUsername());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès.");
            loadUsers();
            clearFields();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier l'utilisateur", e.getMessage());
        }
    }

    @FXML
    private void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné", "Veuillez sélectionner un utilisateur à supprimer.");
            return;
        }

        String query = "DELETE FROM users WHERE username=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, selectedUser.getUsername());
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès.");
            loadUsers();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'utilisateur", e.getMessage());
        }
    }

    @FXML
    private void searchUser() {
        String username = txtUsername.getText();
        if (username.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ vide", "Veuillez entrer un nom d'utilisateur à rechercher.");
            return;
        }

        userList.clear();
        String query = "SELECT * FROM users WHERE username LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + username + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userList.add(new User(
                        rs.getString("username"),
                        rs.getString("mdp"),
                        rs.getString("email"),
                        rs.getString("status")
                ));
            }
            userTable.setItems(userList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de rechercher l'utilisateur", e.getMessage());
        }
    }

    private void clearFields() {
        txtUsername.clear();
        txtPassword.clear();
        txtEmail.clear();
        cmbStatus.setValue(null);
    }

    // Surcharge de showAlert pour accepter trois paramètres
    private void showAlert(Alert.AlertType type, String title, String content) {
        showAlert(type, title, null, content);
    }

    // Méthode principale showAlert avec quatre paramètres
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
