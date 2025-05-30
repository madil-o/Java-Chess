package modele.jeu.pieces;

import modele.jeu.Piece;
import modele.plateau.DecorateurCasesPion;
import modele.plateau.Plateau;

public class Pion extends Piece {

    public Pion(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesPion(null, plateau, this);
    }    

    @Override
    public Piece clone(Plateau nouveauPlateau) {
        Pion clone = new Pion(nouveauPlateau, this.couleur);
        clone.setABouge(this.aDejaBouge());
        return clone;
    }
}
