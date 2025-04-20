package modele.jeu;

import modele.plateau.Direction;
import modele.plateau.Plateau;
import modele.plateau.Case;

import java.util.ArrayList;
import java.util.List;

import modele.jeu.Piece;
import modele.jeu.pieces.*;
import java.awt.Point;

public class Jeu extends Thread{
    private Plateau plateau;
    private Joueur j1, j2;
    protected Coup coupRecu;
    private boolean tourBlanc = true;
    private ArrayList<Coup> historique = new ArrayList<>();


    public Jeu() {
        plateau = new Plateau();
        plateau.placerPieces();

        j1 = new Joueur(this);
        j2 = new Joueur(this);

        start();

    }

    public synchronized boolean isTourBlanc() {
        return tourBlanc;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void placerPieces() {
        plateau.placerPieces();
    }


    public void envoyerCoup(Coup c) {
        coupRecu = c;
        synchronized (this) {
            notify();
        }
    }
        
    public boolean appliquerCoup(Coup coup) {
        Piece piece = coup.dep.getPiece();
        if (piece == null || piece.couleur != tourBlanc) {
            System.out.println("Coup invalide : mauvaise pièce.");
            return false;
        }

        if (!piece.getCasesAccessibles().getCasesAccessibles().contains(coup.arr)) {
            System.out.println("Coup invalide : destination non atteignable.");
            return false;
        }

        // Simuler le coup pour vérifier si le roi reste en sécurité
        Plateau simulation = plateau.clone();
        Point posDep = plateau.getMap().get(coup.dep);
        Point posArr = plateau.getMap().get(coup.arr);

        if (posDep == null || posArr == null) return false;

        Case caseDep = simulation.getCases()[posDep.x][posDep.y];
        Case caseArr = simulation.getCases()[posArr.x][posArr.y];

        if (caseDep == null || caseArr == null) return false;

        Piece simPiece = caseDep.getPiece();
        if (simPiece == null) return false;

        simulation.deplacerPiece(caseDep, caseArr);
        if (simulation.estRoiEnEchec(tourBlanc, simulation.trouverRoi(tourBlanc))) {
            System.out.println("Coup interdit : met le roi en échec.");
            return false;
        }

        // Appliquer réellement le coup
        Piece pieceCapturee = coup.arr.getPiece();
        if (pieceCapturee != null && pieceCapturee.couleur != piece.couleur) {
            plateau.ajouterPieceMorte(pieceCapturee);
            pieceCapturee.quitterCase();
        }

        // Roque ?
        if (piece instanceof Roi && Math.abs(plateau.distancesX(coup.dep, coup.arr)) > 1) {
            int d = plateau.distancesX(coup.dep, coup.arr);
            Case departTour = plateau.positionTour(d, coup.arr);
            Case arriveeTour = plateau.roqueTour(d, coup.arr);
            plateau.deplacerPiece(departTour, arriveeTour);
        }

        plateau.deplacerPiece(coup.dep, coup.arr);

        // Promotion automatique ?
        if (piece instanceof Pion) {
            int y = plateau.getMap().get(coup.arr).y;
            if ((piece.couleur && y == 0) || (!piece.couleur && y == 7)) {
                Piece dame = new Reine(plateau, piece.couleur);
                dame.allerSurCase(coup.arr);
            }
        }

        historique.add(coup);
        return true;
    }


    @Override
    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while(true) {
            Coup c = (tourBlanc) ? j1.getCoup() : j2.getCoup();
            if(appliquerCoup(c))
                tourBlanc = !tourBlanc;
        }
    }

    public List<Coup> getHistorique() {
        return historique;
    }
}
