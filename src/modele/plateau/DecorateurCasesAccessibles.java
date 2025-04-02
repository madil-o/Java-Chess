package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;

public abstract class DecorateurCasesAccessibles {

    Plateau plateau; // TODO
    Piece piece; // TODO

    private DecorateurCasesAccessibles base;

    public DecorateurCasesAccessibles(DecorateurCasesAccessibles _baseDecorateur) {
        base = _baseDecorateur;
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
