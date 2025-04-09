package modele.jeu;

import modele.jeu.Piece;
import modele.jeu.pieces.Roi;
import modele.plateau.Plateau;

public class Jeu extends Thread{
    private Plateau plateau;
    private Joueur j1;
    private Joueur j2;
    protected Coup coupRecu;
    private boolean tourBlanc = true;

    private Roi roi;

    public Jeu() {
        plateau = new Plateau();
        plateau.placerPieces();

        j1 = new Joueur(this);
        j2 = new Joueur(this);

        start();

    }

    public boolean isTourBlanc() {
        return tourBlanc;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void placerPieces() {

        plateau.placerPieces();
    }


    public void envoyerCoup(Coup c) {
        coupRecu = c;

        synchronized (this) {
            notify();
        }
        System.out.println("hello");
    }


    public void appliquerCoup(Coup coup) {
        Piece piece = coup.dep.getPiece();
        if (piece != null && piece.casesAccessibles.getCasesAccessibles().contains(coup.arr)) {
            plateau.deplacerPiece(coup.dep, coup.arr);
        } else {
            System.out.println("Coup invalide !");
        }
    }

    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while(true) {
            Coup c = (tourBlanc) ? j1.getCoup() : j2.getCoup();
            appliquerCoup(c);
            tourBlanc = !tourBlanc;
        }
    }


}
