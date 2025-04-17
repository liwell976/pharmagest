package pkg.tpvente;

import javafx.beans.property.*;

public class Fournisseur {
    private final IntegerProperty idFournisseur;
    private final StringProperty nom;
    private final StringProperty contact;
    private final StringProperty adresse;
    private final StringProperty telephone;

    public Fournisseur(int idFournisseur, String nom, String contact, String adresse, String telephone) {
        this.idFournisseur = new SimpleIntegerProperty(idFournisseur);
        this.nom = new SimpleStringProperty(nom);
        this.contact = new SimpleStringProperty(contact);
        this.adresse = new SimpleStringProperty(adresse);
        this.telephone = new SimpleStringProperty(telephone);
    }

    public int getIdFournisseur() { return idFournisseur.get(); }
    public IntegerProperty idFournisseurProperty() { return idFournisseur; }

    public String getNom() { return nom.get(); }
    public StringProperty nomProperty() { return nom; }

    public String getContact() { return contact.get(); }
    public StringProperty contactProperty() { return contact; }

    public String getAdresse() { return adresse.get(); }
    public StringProperty adresseProperty() { return adresse; }

    public String getTelephone() { return telephone.get(); }
    public StringProperty telephoneProperty() { return telephone; }
}
