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
    protected boolean couleur; /* Blanc = 1; Noir = 0 */

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
        }
        c = _c;
        plateau.arriverCase(c, this);
    }

    public Case getCase() {
        return c;
    }






}
