package modele.plateau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modele.jeu.Piece;

public class DecorateurCasesEnLigne extends DecorateurCasesAccessibles {
    public DecorateurCasesEnLigne(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece,
                                      int _distance_max) {
        super(_baseDecorateur, _plateau, _piece, _distance_max);
    }


    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessible = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(Direction.Haut, Direction.Bas, Direction.Gauche, Direction.Droite);
        for (Direction dir : dirs) {
            Case nextCase = piece.getCase();
            for (int i = 0; i < distance_max; i++){
                nextCase = plateau.appliquerDirection(dir, nextCase);
                if (nextCase != null) {
                    Piece target = nextCase.getPiece();
                    if (target == null || target.couleur != piece.couleur) {
                        accessible.add(nextCase);
                    }
                }
            }
        }
        return accessible;
    }
}
