package modele.plateau;

import modele.jeu.Piece;

public class Case {

    protected Piece p;
    protected Plateau plateau;

    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

    public void quitterLaCase() {
        p = null;
    }

    public Piece getPiece() {
        return p;
    }

    public Plateau getPlateau() {
        return plateau;
    }
   }
