package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;

public abstract class DecorateurCasesAccessibles {

    Plateau plateau;
    Piece piece;
    int distance_max;

    private DecorateurCasesAccessibles base;

    public DecorateurCasesAccessibles(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece,
                                      int _distance_max) {
        base = _baseDecorateur;
        plateau = _plateau;
        piece = _piece;
        distance_max = _distance_max;
    }

    public ArrayList<Case> getCasesAccessibles() {
        ArrayList<Case> retour = getMesCasesAccessibles();

        if (base != null) {
            retour.addAll(base.getCasesAccessibles());
        }

        return retour;
    }

    public abstract ArrayList<Case> getMesCasesAccessibles();


}
