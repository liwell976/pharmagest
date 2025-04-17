package pkg.tpvente;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ReceptionCommande {
    private final IntegerProperty numCommande;
    private final StringProperty medicament;
    private final IntegerProperty quantiteCommandee;
    private final IntegerProperty quantiteRecue;
    private final DoubleProperty prixUnitaire;
    private final DoubleProperty prixFinal;
    private final StringProperty statut;
    private final StringProperty problemeDescription;
    private final ObjectProperty<LocalDate> dateReception;

    public ReceptionCommande(int numCommande, String medicament, int quantiteCommandee, int quantiteRecue,
                             double prixUnitaire, double prixFinal, String statut, String problemeDescription, LocalDate dateReception) {
        this.numCommande = new SimpleIntegerProperty(numCommande);
        this.medicament = new SimpleStringProperty(medicament);
        this.quantiteCommandee = new SimpleIntegerProperty(quantiteCommandee);
        this.quantiteRecue = new SimpleIntegerProperty(quantiteRecue);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
        this.prixFinal = new SimpleDoubleProperty(prixFinal);
        this.statut = new SimpleStringProperty(statut);
        this.problemeDescription = new SimpleStringProperty(problemeDescription);
        this.dateReception = new SimpleObjectProperty<>(dateReception);
    }

    public int getNumCommande() { return numCommande.get(); }
    public IntegerProperty numCommandeProperty() { return numCommande; }

    public String getMedicament() { return medicament.get(); }
    public StringProperty medicamentProperty() { return medicament; }

    public int getQuantiteCommandee() { return quantiteCommandee.get(); }
    public IntegerProperty quantiteCommandeeProperty() { return quantiteCommandee; }

    public int getQuantiteRecue() { return quantiteRecue.get(); }
    public IntegerProperty quantiteRecueProperty() { return quantiteRecue; }

    public double getPrixUnitaire() { return prixUnitaire.get(); }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }

    public double getPrixFinal() { return prixFinal.get(); }
    public DoubleProperty prixFinalProperty() { return prixFinal; }

    public String getStatut() { return statut.get(); }
    public StringProperty statutProperty() { return statut; }

    public String getProblemeDescription() { return problemeDescription.get(); }
    public StringProperty problemeDescriptionProperty() { return problemeDescription; }

    public LocalDate getDateReception() { return dateReception.get(); }
    public ObjectProperty<LocalDate> dateReceptionProperty() { return dateReception; }
}
