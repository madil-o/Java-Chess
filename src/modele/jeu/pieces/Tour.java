package modele.jeu.pieces;

import modele.plateau.*;
import modele.jeu.Piece;

public class Tour extends Piece {

    public Tour(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesEnLigne(null, _plateau, this);
    }
    
}
