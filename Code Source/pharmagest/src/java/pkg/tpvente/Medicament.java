package pkg.tpvente;

import javafx.beans.property.*;



class Medicament {
    private final StringProperty DCI;
    private final StringProperty famille;
    private final DoubleProperty prixVente;
    private final IntegerProperty stock;
    private final IntegerProperty seuilMin;
    private final IntegerProperty stockMax;


    public Medicament(String DCI, String famille, double prixVente, int stock, int seuilMin, int stockMax) {
        this.DCI = new SimpleStringProperty(DCI);
        this.famille = new SimpleStringProperty(famille);
        this.prixVente = new SimpleDoubleProperty(prixVente);
        this.stock = new SimpleIntegerProperty(stock);
        this.seuilMin = new SimpleIntegerProperty(seuilMin);
        this.stockMax = new SimpleIntegerProperty(stockMax);
    }

    public String getDCI() { return DCI.get(); }
    public StringProperty DCIProperty() { return DCI; }

    public String getFamille() { return famille.get(); }
    public StringProperty familleProperty() { return famille; }

    public double getPrixVente() { return prixVente.get(); }
    public DoubleProperty prixVenteProperty() { return prixVente; }

    public int getStock() { return stock.get(); }
    public IntegerProperty stockProperty() { return stock; }

    public int getSeuilMin() { return seuilMin.get(); }
    public IntegerProperty seuilMinProperty() { return seuilMin; }

    public int getStockMax() { return stockMax.get(); }
    public IntegerProperty stockMaxProperty() { return stockMax; }

    public void setPrixVente(double prix) { this.prixVente.set(prix); }
}
