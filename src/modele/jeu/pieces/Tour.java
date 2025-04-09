package modele.jeu.pieces;

import modele.jeu.Piece;
import modele.plateau.*;

public class Tour extends Piece {

    public Tour(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesEnLigne(null, _plateau, this, 7);
    }
    
}
