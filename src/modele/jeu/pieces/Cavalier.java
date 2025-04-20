package modele.jeu.pieces;

import modele.jeu.Piece;
import modele.plateau.DecorateurCasesCavalier;
import modele.plateau.Plateau;

public class Cavalier extends Piece {

    public Cavalier(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesCavalier(null, plateau, this);
    }    

    @Override
    public Piece clone(Plateau nouveauPlateau) {
        Cavalier clone = new Cavalier(nouveauPlateau, this.couleur);
        clone.setABouge(this.aDejaBouge());
        return clone;
    }
}
