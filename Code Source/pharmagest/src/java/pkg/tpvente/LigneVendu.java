package pkg.tpvente;

public class LigneVendu {

    private int ligne_vendu_id;
    private int num_vente;
    private int medicament_dci;
    private int qte_vendue;

    public LigneVendu(int ligne_vendu_id, int num_vente, int medicament_dci, int qte_vendue) {
        this.ligne_vendu_id = ligne_vendu_id;
        this.num_vente = num_vente;
        this.medicament_dci = medicament_dci;
        this.qte_vendue = qte_vendue;
    }

    public int getLigne_vendu_id() {
        return ligne_vendu_id;
    }

    public void setLigne_vendu_id(int ligne_vendu_id) {
        this.ligne_vendu_id = ligne_vendu_id;
    }

    public int getNum_vente() {
        return num_vente;
    }

    public void setNum_vente(int num_vente) {
        this.num_vente = num_vente;
    }

    public int getMedicament_dci() {
        return medicament_dci;
    }

    public void setMedicament_dci(int medicament_dci) {
        this.medicament_dci = medicament_dci;
    }

    public int getQte_vendue() {
        return qte_vendue;
    }

    public void setQte_vendue(int qte_vendue) {
        this.qte_vendue = qte_vendue;
    }
}
