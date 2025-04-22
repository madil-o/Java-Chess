package modele.jeu;

import modele.plateau.Case;
import modele.plateau.DecorateurCasesAccessibles;
import modele.plateau.Plateau;

/**
 * Entités amenées à bouger
 */
public abstract class Piece {

    protected Case c;
    protected Plateau plateau;
    protected DecorateurCasesAccessibles casesAccessibles;
    public boolean couleur; // Blanc = true; Noir = false
    private boolean aBouge = false;

    public Piece(Plateau _plateau, boolean _couleur) {
        plateau = _plateau;
        couleur = _couleur;
    }

    public void quitterCase() {
        c.quitterLaCase();
    }
    public void allerSurCase(Case _c) {
        if (c != null) {
            quitterCase();
            setABouge(true);
        }
        c = _c;
        plateau.arriverCase(c, this);
        //setABouge(true);
    }

    public Case getCase() {
        return c;
    }

    public void setCase(Case _c) {
        c = _c;
    }

    public DecorateurCasesAccessibles getCasesAccessibles() {
        return casesAccessibles;
    }

    public boolean aDejaBouge() {
        return aBouge;
    }

    public void setABouge(boolean b) {
        aBouge = b;
    }

    public abstract Piece clone(Plateau nouveauPlateau);
}
