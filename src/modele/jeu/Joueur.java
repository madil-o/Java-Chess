package modele.jeu;

import java.util.ArrayList;

import modele.plateau.Case;
import modele.plateau.Plateau;
import java.awt.Point;

public class Joueur {
    private Jeu jeu;

    public Joueur(Jeu _jeu) {
        jeu = _jeu;
    }

    public Coup getCoup() {

        synchronized (jeu) {
            try {
                jeu.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return jeu.coupRecu;
    }

    public boolean aDesCoupsLegaux() {
        Plateau plateau = jeu.getPlateau();
        boolean couleurJoueur = jeu.isTourBlanc();

        ArrayList<Piece> pieces = plateau.getPieces(couleurJoueur);
        for (Piece piece : pieces) {
            ArrayList<Case> destinations = piece.getCasesAccessibles().getMesCasesAccessibles();
            for (Case dest : destinations) {
                Plateau simulation = plateau.clone();

                Point from = plateau.getMap().get(piece.getCase());
                Point to = plateau.getMap().get(dest);

                if (from == null || to == null) continue;

                Case caseFrom = simulation.getCases()[from.x][from.y];
                Case caseTo = simulation.getCases()[to.x][to.y];

                if (caseFrom == null || caseTo == null) continue;
                Piece simPiece = caseFrom.getPiece();
                if (simPiece == null) continue;

                simulation.deplacerPiece(caseFrom, caseTo);

                Case roiPos = simulation.trouverRoi(couleurJoueur);
                if (!simulation.estRoiEnEchec(couleurJoueur, roiPos)) {
                    return true; // au moins un coup légal
                }
            }
        }
        return false;
    }
}
