package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;

public class DecorateurCasesEnLigne extends DecorateurCasesAccessibles {
    public DecorateurCasesEnLigne(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }


    public ArrayList<Case> getMesCasesAccessibles() {
        // TODO
        return null;
    }
}
