package modele.plateau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modele.jeu.Piece;
import modele.jeu.pieces.*;

public class DecorateurCasesRoque extends DecorateurCasesAccessibles {

    public DecorateurCasesRoque(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }


    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessible = new ArrayList<>();
        List<Direction> dirs = Arrays.asList(Direction.Gauche, Direction.Droite);
        for (Direction dir : dirs) {
            Case nextCase = piece.getCase();
            for (int i = 0; i < 2; i++){
                nextCase = plateau.appliquerDirection(dir, nextCase);
                if (nextCase != null) {
                    Piece target = nextCase.getPiece();
                    if (target == null && i == 1) {
                        if (dir == Direction.Gauche) {
                            Case caseCavalier = plateau.appliquerDirection(Direction.Gauche, nextCase);
                            Case caseTourGauche = plateau.appliquerDirection(Direction.Gauche, caseCavalier);
                            if (caseCavalier.getPiece() != null && !(caseTourGauche.getPiece() instanceof Tour)){
                                break;
                            }
                        }
                        else {
                            Case caseTourDroite = plateau.appliquerDirection(Direction.Droite, nextCase);
                            if (!(caseTourDroite.getPiece() instanceof Tour)){
                                break;
                            }
                        }
                        accessible.add(nextCase);
                    }
                    else if (target != null){
                        break;
                    }
                }
            }
        }
        return accessible;
    }
}
