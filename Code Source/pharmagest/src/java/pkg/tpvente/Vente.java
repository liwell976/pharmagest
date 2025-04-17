package pkg.tpvente;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Vente {
    private final IntegerProperty num_vente;
    private final StringProperty medicament;
    private final DoubleProperty montant_total;
    private final StringProperty statut;
    private final ObjectProperty<LocalDate> date_vente;


    public Vente(int num_vente, String medicament, double montant_total, String statut, LocalDate date_vente) {
        this.num_vente = new SimpleIntegerProperty(num_vente);
        this.medicament = new SimpleStringProperty(medicament);
        this.montant_total = new SimpleDoubleProperty(montant_total);
        this.statut = new SimpleStringProperty(statut);
        this.date_vente = new SimpleObjectProperty<>(date_vente);

    }

    public int getNumVente() { return num_vente.get(); }
    public IntegerProperty numVenteProperty() { return num_vente; }

    public String getMedicament() { return medicament.get(); }
    public StringProperty medicamentProperty() { return medicament; }

    public double getMontant_total() { return montant_total.get(); }
    public DoubleProperty montant_totalProperty() { return montant_total; }

    public String getStatut() { return statut.get(); }
    public StringProperty statutProperty() { return statut; }

    public LocalDate getDate_vente() { return date_vente.get(); }
    public ObjectProperty<LocalDate> date_venteProperty() { return date_vente; }

}
