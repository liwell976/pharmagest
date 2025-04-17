package pkg.tpvente;

import javafx.beans.property.*;

public class TransactionItem { // <- Ajout de public
    private final StringProperty DCI;
    private final DoubleProperty prixVente;
    private final IntegerProperty quantiteVendue;

    public TransactionItem(String DCI, double prixVente, int quantiteVendue) {
        this.DCI = new SimpleStringProperty(DCI);
        this.prixVente = new SimpleDoubleProperty(prixVente);
        this.quantiteVendue = new SimpleIntegerProperty(quantiteVendue);
    }

    public String getDCI() { return DCI.get(); }
    public StringProperty DCIProperty() { return DCI; }

    public double getPrixVente() { return prixVente.get(); }
    public DoubleProperty prixVenteProperty() { return prixVente; }

    public int getQuantiteVendue() { return quantiteVendue.get(); }
    public IntegerProperty quantiteVendueProperty() { return quantiteVendue; }

    public double getTotal() { return prixVente.get() * quantiteVendue.get(); }
}
