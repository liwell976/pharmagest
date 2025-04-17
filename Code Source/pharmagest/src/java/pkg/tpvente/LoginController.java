package pkg.tpvente;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private CheckBox login_checkBox, register_checkBox;
    @FXML
    private AnchorPane login_form, register_form;
    @FXML
    private Button login_loginBtn, register_signupBtn;
    @FXML
    private PasswordField login_password, register_password;
    @FXML
    private Hyperlink login_registerHere, register_loginHere;
    @FXML
    private TextField login_showPassword, register_showPassword;
    @FXML
    private ComboBox<String> login_user, register_role;
    @FXML
    private TextField login_username, register_username, register_email;

    private AlertMessage alert = new AlertMessage();
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public void loginAccount() {
        if (login_username.getText().isEmpty() || login_password.getText().isEmpty()) {
            alert.errorMessage("Veuillez remplir tous les champs obligatoires");
            return;
        }

        String sql = "SELECT status FROM users WHERE username = ? AND mdp = ?";

        try {
            connect = DatabaseConnection.getConnection();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, login_username.getText());
            prepare.setString(2, login_password.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                String dbStatus = result.getString("status");
                String selectedStatus = login_user.getValue();

                if (selectedStatus == null || selectedStatus.equals("Sélectionnez un utilisateur")) {
                    alert.errorMessage("Veuillez sélectionner un statut valide.");
                    return;
                }

                try {
                    loadUserInterface(selectedStatus);
                } catch (Exception e) {
                    alert.errorMessage("Erreur lors du chargement de l'interface : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                alert.errorMessage("Nom d'utilisateur ou mot de passe incorrect");
            }

        } catch (SQLException e) {
            alert.errorMessage("Erreur base de données : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseResources();
        }
    }


    private void loadUserInterface(String role) throws Exception {
        String fxmlFile;
        boolean isCaissier = false;

        switch (role) {
            case "Administrateur":
                fxmlFile = "/pkg/tpvente/BilanFinancierView.fxml";
                break;
            case "Pharmacien":
                fxmlFile = "/pkg/tpvente/CommandeFournisseur.fxml";
                break;
            case "Caissier":
                fxmlFile = "/pkg/tpvente/Vente.fxml";
                isCaissier = true;
                break;
            case "Vendeur":
                fxmlFile = "/pkg/tpvente/Vente.fxml";
                break;
            default:
                alert.errorMessage("Rôle non reconnu !");
                return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();

        if (isCaissier || role.equals("Vendeur")) {
            VenteController venteController = loader.getController();
            venteController.setRole(role);  // 👈 on transmet le rôle
        }


        // Si l'utilisateur est un Caissier, afficher uniquement "caisse_form"
        if (isCaissier) {
            VenteController venteController = loader.getController();
            venteController.loadCaisseForm();
        }

        Stage stage = new Stage();
        stage.setTitle(role + " - " + login_username.getText());
        stage.setScene(new Scene(root));
        stage.show();

        // Fermer la fenêtre de connexion
        Stage currentStage = (Stage) login_loginBtn.getScene().getWindow();
        currentStage.close();
    }

    private void closeDatabaseResources() {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loginShowPassword() {
        boolean isChecked = login_checkBox.isSelected();
        login_showPassword.setText(login_password.getText());
        login_showPassword.setVisible(isChecked);
        login_password.setVisible(!isChecked);
    }

    public void registerAccount() {
        if (register_email.getText().isEmpty() || register_username.getText().isEmpty() || register_password.getText().isEmpty() || register_role.getValue() == null) {
            alert.errorMessage("Veuillez remplir tous les champs obligatoires");
            return;
        }

        String checkUsername = "SELECT * FROM users WHERE username = ?";
        String insertData = "INSERT INTO users (username, mdp, email, status) VALUES (?, ?, ?, ?)";

        try {
            connect = DatabaseConnection.getConnection();
            prepare = connect.prepareStatement(checkUsername);
            prepare.setString(1, register_username.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                alert.errorMessage(register_username.getText() + " existe déjà !");
                return;
            }

            prepare.close();
            prepare = connect.prepareStatement(insertData);
            prepare.setString(1, register_username.getText());
            prepare.setString(2, register_password.getText());
            prepare.setString(3, register_email.getText());
            prepare.setString(4, register_role.getValue());
            prepare.executeUpdate();

            alert.successMessage("Inscription réussie !");
            registerClear();
            login_form.setVisible(true);
            register_form.setVisible(false);

        } catch (SQLException e) {
            alert.errorMessage("Erreur base de données : " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseResources();
        }
    }

    public void registerClear() {
        register_email.clear();
        register_username.clear();
        register_password.clear();
        register_showPassword.clear();
    }

    public void registerShowPassword() {
        boolean isChecked = register_checkBox.isSelected();
        register_showPassword.setText(register_password.getText());
        register_showPassword.setVisible(isChecked);
        register_password.setVisible(!isChecked);
    }

    public void initialize() {
        login_user.getItems().addAll("Pharmacien", "Caissier", "Vendeur", "Administrateur");
        login_user.setValue("Sélectionnez un utilisateur");

        register_role.getItems().addAll("Pharmacien", "Caissier", "Vendeur", "Administrateur");
        register_role.setValue("Sélectionnez un rôle");
    }

    public void switchForm(ActionEvent event) {
        boolean isLogin = (event.getSource() == register_loginHere);
        login_form.setVisible(isLogin);
        register_form.setVisible(!isLogin);
    }
}
