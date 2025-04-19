package modele.plateau;

import java.awt.Point;
import java.util.ArrayList;
import modele.jeu.Piece;

public class DecorateurCasesPion extends DecorateurCasesAccessibles {

    public DecorateurCasesPion(DecorateurCasesAccessibles _baseDecorateur,
                               Plateau _plateau,
                               Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }

    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessible = new ArrayList<>();
        Point currentPos = plateau.getMap().get(piece.getCase());
        if (currentPos == null) return accessible;
        
        Direction dirAvant = piece.couleur ? Direction.Haut : Direction.Bas;
        Case forwardOne = plateau.appliquerDirection(dirAvant, piece.getCase());
    
        if (forwardOne != null && forwardOne.getPiece() == null) {
            accessible.add(forwardOne);
    
            // Premier mouvement (ligne 1 pour blancs, ligne 6 pour noirs)
            if ((piece.couleur && currentPos.y == 6) || (!piece.couleur && currentPos.y == 1)) {
                Case forwardTwo = plateau.appliquerDirection(dirAvant, forwardOne);
                if (forwardTwo != null && forwardTwo.getPiece() == null) {
                    accessible.add(forwardTwo);
                }
            }
        }
        
        // Captures (en diagonale)
        Direction[] captureDirs = piece.couleur
            ? new Direction[]{Direction.HautGauche, Direction.HautDroite}
            : new Direction[]{Direction.BasGauche, Direction.BasDroite};
        
        for (Direction dir : captureDirs) {
            Case captureCase = plateau.appliquerDirection(dir, piece.getCase());
            if (captureCase != null) {
                Piece target = captureCase.getPiece();
                if (target != null && target.couleur != piece.couleur) {
                    accessible.add(captureCase);
                    System.out.println("Capture de la pièce : " + target);
                    plateau.ajouterPieceMorte(target);
                    target.setCase(null);
                }
            }
        }
    
        return accessible;
    }
    
    
    
}
