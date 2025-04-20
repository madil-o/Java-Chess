package modele.jeu.pieces;

import modele.plateau.DecorateurCasesEnDiagonale;
import modele.plateau.Plateau;
import modele.jeu.Piece;

public class Fou extends Piece {

    public Fou(Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesEnDiagonale(null, plateau, this, 7);
    }

    @Override
    public Piece clone(Plateau nouveauPlateau) {
        Fou clone = new Fou(nouveauPlateau, this.couleur);
        clone.setABouge(this.aDejaBouge());
        return clone;
    }
}
