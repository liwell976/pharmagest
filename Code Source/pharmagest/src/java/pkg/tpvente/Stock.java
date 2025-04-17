package pkg.tpvente;

import javafx.beans.property.*;

    public class Stock {
        private final StringProperty DCI;
        private final StringProperty nomCommercial;
        private final IntegerProperty quantite;
        private final IntegerProperty seuilMinimum;
        private final DoubleProperty prixUnitaire;
        private final StringProperty fournisseur;

        public Stock(String DCI, String nomCommercial, int quantite, int seuilMinimum, double prixUnitaire, String fournisseur) {
            this.DCI = new SimpleStringProperty(DCI);
            this.nomCommercial = new SimpleStringProperty(nomCommercial);
            this.quantite = new SimpleIntegerProperty(quantite);
            this.seuilMinimum = new SimpleIntegerProperty(seuilMinimum);
            this.prixUnitaire = new SimpleDoubleProperty(prixUnitaire);
            this.fournisseur = new SimpleStringProperty(fournisseur);
        }

        public String getDCI() { return DCI.get(); }
        public StringProperty DCIProperty() { return DCI; }

        public String getNomCommercial() { return nomCommercial.get(); }
        public StringProperty nomCommercialProperty() { return nomCommercial; }

        public int getQuantite() { return quantite.get(); }
        public IntegerProperty quantiteProperty() { return quantite; }

        public int getSeuilMinimum() { return seuilMinimum.get(); }
        public IntegerProperty seuilMinimumProperty() { return seuilMinimum; }

        public double getPrixUnitaire() { return prixUnitaire.get(); }
        public DoubleProperty prixUnitaireProperty() { return prixUnitaire; }

        public String getFournisseur() { return fournisseur.get(); }
        public StringProperty fournisseurProperty() { return fournisseur; }
    }


