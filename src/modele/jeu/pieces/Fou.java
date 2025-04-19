package modele.jeu.pieces;

import modele.plateau.DecorateurCasesEnDiagonale;
import modele.plateau.Plateau;
import modele.jeu.Piece;

public class Fou extends Piece {

    public Fou(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesEnDiagonale(null, plateau, this, 7);
    }
}
