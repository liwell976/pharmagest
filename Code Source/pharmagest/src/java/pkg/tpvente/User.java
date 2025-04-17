package pkg.tpvente;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class User {;
    private final SimpleStringProperty username;
    private final SimpleStringProperty password;
    private final SimpleStringProperty email;
    private final SimpleStringProperty status;

    public User(String username, String password, String email, String status) {
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.email = new SimpleStringProperty(email);
        this.status = new SimpleStringProperty(status);
    }

    public String getUsername() { return username.get(); }
    public SimpleStringProperty usernameProperty() { return username; }

    public String getPassword() { return password.get(); }
    public SimpleStringProperty passwordProperty() { return password; }

    public String getEmail() { return email.get(); }
    public SimpleStringProperty emailProperty() { return email; }

    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
}
