package pkg.tpvente;

import javafx.beans.property.*;

import java.time.LocalDate;

public class VenteOrdonnance {
    private final IntegerProperty idOrdonnance;
    private final StringProperty nomMedecin;
    private final StringProperty nomPatient;
    private final ObjectProperty<java.time.LocalDate> datePrescription;
    private final StringProperty medicament;
    private final IntegerProperty quantite;
    private final DoubleProperty prixUnitaire;

    public VenteOrdonnance(int idOrdonnance, String nomMedecin, String nomPatient, LocalDate datePrescription,
                                                 String medicament, int quantite, double prixUnitaire)
    {
        this.idOrdonnance = new SimpleIntegerProperty(idOrdonnance);
        this.nomMedecin = new SimpleStringProperty(nomMedecin);
        this.nomPatient = new SimpleStringProperty(nomPatient);
        this.datePrescription = new SimpleObjectProperty<>(datePrescription);
        this.medicament = new SimpleStringProperty(medicament);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
    }

    // Getters et setters pour JavaFX TableView
    public int getIdOrdonnance() { return idOrdonnance.get(); }
    public IntegerProperty idOrdonnanceProperty() { return idOrdonnance; }

    public String getNomMedecin() { return nomMedecin.get(); }
    public StringProperty nomMedecinProperty() { return nomMedecin; }

    public String getNomPatient() { return nomPatient.get(); }
    public StringProperty nomPatientProperty() { return nomPatient; }

    public java.time.LocalDate getDatePrescription() { return datePrescription.get(); }
    public ObjectProperty<java.time.LocalDate> datePrescriptionProperty() { return datePrescription; }

    public String getMedicament() { return medicament.get(); }
    public StringProperty medicamentProperty() { return medicament; }

    public int getQuantite() { return quantite.get(); }
    public IntegerProperty quantiteProperty() { return quantite; }

    public double getPrixUnitaire() { return prixUnitaire.get(); }
    public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }

    // ✅ Alias pour éviter l'erreur prixProperty()
    public DoubleProperty prixProperty() {
        return prixUnitaireProperty(); // Retourne la propriété existante
    }
}
