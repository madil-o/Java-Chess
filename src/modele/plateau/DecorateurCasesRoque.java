package modele.plateau;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import modele.jeu.Piece;
import modele.jeu.pieces.*;

import java.awt.Point;

public class DecorateurCasesRoque extends DecorateurCasesAccessibles {

    public DecorateurCasesRoque(DecorateurCasesAccessibles _baseDecorateur,
                                      Plateau _plateau,
                                      Piece _piece) {
        super(_baseDecorateur, _plateau, _piece);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles() {
        ArrayList<Case> accessibles = base != null
            ? base.getMesCasesAccessibles()
            : new ArrayList<>();

        if (!(piece instanceof Roi roi)) return accessibles;

        Point position = plateau.getMap().get(piece.getCase());
        if (position == null) return accessibles;

        int ligne = position.y;

        // ROQUE À DROITE
        Case caseF1 = plateau.getCases()[5][ligne];
        Case caseG1 = plateau.getCases()[6][ligne];
        Case tourDroite = plateau.getCases()[7][ligne];

        if (estRoquePossible(roi, caseF1, caseG1, tourDroite)) {
            accessibles.add(caseG1); // arrivée du roi à droite
        }

        // ROQUE À GAUCHE
        Case caseD1 = plateau.getCases()[3][ligne];
        Case caseC1 = plateau.getCases()[2][ligne];
        Case caseB1 = plateau.getCases()[1][ligne];
        Case tourGauche = plateau.getCases()[0][ligne];

        if (estRoquePossible(roi, caseD1, caseC1, tourGauche, caseB1)) {
            accessibles.add(caseC1); // arrivée du roi à gauche
        }

        return accessibles;
    }

    private boolean estRoquePossible(Roi roi, Case case1, Case case2, Case tour) {
        if (case1 == null || case2 == null || tour == null) return false;
        if (case1.getPiece() != null || case2.getPiece() != null) return false;
        if (!(tour.getPiece() instanceof Tour)) return false;
        if (roi.aDejaBouge() || tour.getPiece().aDejaBouge()) return false;

        return verifierCheminSansEchec(roi, case1, case2);
    }

    private boolean estRoquePossible(Roi roi, Case case1, Case case2, Case tour, Case case3) {
        if (case1 == null || case2 == null || tour == null || case3 == null) return false;
        if (case1.getPiece() != null || case2.getPiece() != null || case3.getPiece() != null) return false;
        if (!(tour.getPiece() instanceof Tour)) return false;
        if (roi.aDejaBouge() || tour.getPiece().aDejaBouge()) return false;

        return verifierCheminSansEchec(roi, case1, case2);
    }

    private boolean verifierCheminSansEchec(Roi roi, Case... chemin) {
        Case roiCase = roi.getCase();
        Point pRoi = plateau.getMap().get(roiCase);
        if (pRoi == null) return false;
        if(plateau.estRoiEnEchec(roi.couleur, roiCase)) return false;
        for (Case c : chemin) {
            Plateau simulation = plateau.clone();
            Point pC = plateau.getMap().get(c);
            if (pC == null) return false;
            Case simRoi = simulation.getCases()[pRoi.x][pRoi.y];
            Case simC = simulation.getCases()[pC.x][pC.y];
            simulation.deplacerPiece(simRoi, simC);
            if (simulation.estRoiEnEchec(roi.couleur, simC)) return false;
            pRoi = pC;
        }

        return true;
    }
}