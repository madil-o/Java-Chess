package modele.plateau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import modele.jeu.Piece;

public class DecorateurCasesEnLigne extends DecorateurCasesAccessibles {
    public DecorateurCasesEnLigne(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }


    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        // TODO
        ArrayList<Case> accessible = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(Direction.Haut, Direction.Bas, Direction.Gauche, Direction.Droite);
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
