package modele.plateau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modele.jeu.Piece;

public class DecorateurCasesEnDiagonale extends DecorateurCasesAccessibles {

    public DecorateurCasesEnDiagonale(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }

    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> tab = new ArrayList<>();
        System.out.print("jedoiaezajejaozejao");
        List<Direction> dirs = Arrays.asList(Direction.HautGauche,
                                             Direction.HautDroite,
                                             Direction.BasGauche,
                                             Direction.BasDroite);
        return null;
    }
}
