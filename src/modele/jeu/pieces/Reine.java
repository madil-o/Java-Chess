package modele.jeu.pieces;

import modele.plateau.DecorateurCasesEnDiagonale;
import modele.plateau.DecorateurCasesEnLigne;
import modele.plateau.Plateau;
import modele.jeu.Piece;

public class Reine extends Piece {

    public Reine(modele.plateau.Plateau _plateau, boolean _couleur) {
        super(_plateau, _couleur);
        casesAccessibles = new DecorateurCasesEnDiagonale(new DecorateurCasesEnLigne(null, plateau, this, 7),
                plateau, this, 7);
    }   
    
    @Override
    public Piece clone(Plateau nouveauPlateau) {
        Reine clone = new Reine(nouveauPlateau, this.couleur);
        clone.setABouge(this.aDejaBouge());
        return clone;
    }
}
