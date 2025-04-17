package pkg.tpvente;

import java.time.LocalDate;

public class BilanFinancier {
    private int id;
    private LocalDate date;
    private double montant;

    public BilanFinancier(int id, LocalDate date, double montant) {
        this.id = id;
        this.date = date;
        this.montant = montant;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public double getMontant() { return montant; }
}
