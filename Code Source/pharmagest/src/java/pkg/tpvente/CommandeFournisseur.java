package pkg.tpvente;

import javafx.beans.property.*;

public class CommandeFournisseur {
    private final IntegerProperty numCommande;
    private final StringProperty fournisseur;
    private final StringProperty medicament;
    private final IntegerProperty quantite;
    private final DoubleProperty prixUnitaire;
    private final StringProperty statut;
    private final DoubleProperty montantTotal;

    public CommandeFournisseur(int numCommande, String fournisseur, String medicament, int quantite, double prixUnitaire, String statut) {
        this.numCommande = new SimpleIntegerProperty(numCommande);
        this.fournisseur = new SimpleStringProperty(fournisseur);
        this.medicament = new SimpleStringProperty(medicament);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
        this.statut = new SimpleStringProperty(statut);
        this.montantTotal = new SimpleDoubleProperty(quantite * prixUnitaire); // Initialisation du montant total

        // Mise à jour automatique du montant total lorsque quantité ou prix unitaire change
        this.quantite.addListener((obs, oldVal, newVal) -> updateMontantTotal());
        this.prixUnitaire.addListener((obs, oldVal, newVal) -> updateMontantTotal());
    }

    private void updateMontantTotal() {
        montantTotal.set(quantite.get() * prixUnitaire.get());
    }

    public int getNumCommande() { return numCommande.get(); }
    public IntegerProperty numCommandeProperty() { return numCommande; }

    public String getFournisseur() { return fournisseur.get(); }
    public StringProperty fournisseurProperty() { return fournisseur; }

    public String getMedicament() { return medicament.get(); }
    public StringProperty medicamentProperty() { return medicament; }

    public int getQuantite() { return quantite.get(); }
    public IntegerProperty quantiteProperty() { return quantite; }

    public double getPrixUnitaire() { return prixUnitaire.get(); }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }

    public String getStatut() { return statut.get(); }
    public StringProperty statutProperty() { return statut; }

    public double getMontantTotal() { return montantTotal.get(); }
    public DoubleProperty montantTotalProperty() { return montantTotal; }
}
