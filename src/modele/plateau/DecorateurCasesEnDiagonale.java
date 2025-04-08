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

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessible = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(
            Direction.HautGauche,
            Direction.HautDroite,
            Direction.BasGauche,
            Direction.BasDroite
        );
        for (Direction dir : dirs) {
            Case nextCase = plateau.appliquerDirection(dir, piece.getCase());
            if (nextCase != null) {
                Piece target = nextCase.getPiece();
                if (target == null || target.couleur != piece.couleur) {
                    accessible.add(nextCase);
                }
            }
        }
        return accessible;   
    }
}
