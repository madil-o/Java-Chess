package modele.jeu.pieces;

import modele.jeu.Piece;
import modele.plateau.DecorateurCasesCavalier;
import modele.plateau.Plateau;

public class Cavalier extends Piece {

    public Cavalier(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesCavalier(null, plateau, this);
    }    
}
