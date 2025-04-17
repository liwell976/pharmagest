module pkg.tpvente {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens pkg.tpvente to javafx.fxml;
    exports pkg.tpvente;
}