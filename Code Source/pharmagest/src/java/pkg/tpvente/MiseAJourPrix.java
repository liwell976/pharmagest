package pkg.tpvente;

import javafx.beans.property.*;

public class MiseAJourPrix {
    private final StringProperty fournisseur;
    private final DoubleProperty prixAchat;

    public MiseAJourPrix(String fournisseur, double prixAchat) {
        this.fournisseur = new SimpleStringProperty(fournisseur);
        this.prixAchat = new SimpleDoubleProperty(prixAchat);
    }

    public String getFournisseur() { return fournisseur.get(); }
    public StringProperty fournisseurProperty() { return fournisseur; }

    public double getPrixAchat() { return prixAchat.get(); }
    public DoubleProperty prixAchatProperty() { return prixAchat; }
}
