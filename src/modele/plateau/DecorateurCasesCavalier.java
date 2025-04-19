package modele.plateau;

import java.util.ArrayList;
import modele.jeu.Piece;
import java.awt.Point;


public class DecorateurCasesCavalier extends DecorateurCasesAccessibles {

    public DecorateurCasesCavalier(DecorateurCasesAccessibles _baseDecorateur,
                                   Plateau _plateau,
                                   Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessible = new ArrayList<>();
        int[][] dirs = {
            {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
            {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
    
        Point currentPos = plateau.getMap().get(piece.getCase());
        if (currentPos == null) return accessible;
    
        for (int[] dir : dirs) {
            Point newPos = new Point(currentPos.x + dir[0], currentPos.y + dir[1]);
            if (plateau.contenuDansGrille(newPos)) {
                Case target = plateau.getCases()[newPos.x][newPos.y];
                Piece p = target.getPiece();
                if (p == null || p.couleur != piece.couleur) {
                    accessible.add(target);
                    // Capturer la pièce et l'ajouter à la liste des pièces mortes
                    if (p != null && p.couleur != piece.couleur) {
                        System.out.println("Capture de la pièce : " + p);
                        plateau.ajouterPieceMorte(p);
                        p.setCase(null); // Retirer la pièce capturée de la case
                    }
                }
            }
        }
        return accessible;
    }
}