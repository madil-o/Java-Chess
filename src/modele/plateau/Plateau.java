/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import modele.jeu.Piece;
import modele.jeu.pieces.*;


public class Plateau extends Observable {

    public static final int SIZE_X = 8;
    public static final int SIZE_Y = 8;
    private ArrayList<Piece> piecesMortesBlanches = new ArrayList<>();
    private ArrayList<Piece> piecesMortesNoires = new ArrayList<>();
    private HashMap<Case, Point> map = new  HashMap<>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleCases = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    public Plateau() {
        initPlateauVide();
    }

    public Case[][] getCases() {
        return grilleCases;
    }

    public HashMap<Case, Point> getMap() {
        return map;
    }

    private void initPlateauVide() {

        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y] = new Case(this);
                map.put(grilleCases[x][y], new Point(x, y));
            }

        }

    }

    public void reinitialiser() {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                grilleCases[x][y].quitterLaCase();
            }
        }
        piecesMortesBlanches.clear();
        piecesMortesNoires.clear();
        initPlateauVide();
    }

    public void placerPieces() {
        // Pièces blanches
        new Tour(this, true).allerSurCase(grilleCases[0][7]);
        new Cavalier(this, true).allerSurCase(grilleCases[1][7]);
        new Fou(this, true).allerSurCase(grilleCases[2][7]);
        new Reine(this, true).allerSurCase(grilleCases[3][7]);
        new Roi(this, true).allerSurCase(grilleCases[4][7]);
        new Fou(this, true).allerSurCase(grilleCases[5][7]);
        new Cavalier(this, true).allerSurCase(grilleCases[6][7]);
        new Tour(this, true).allerSurCase(grilleCases[7][7]);
        for (int x = 0; x < SIZE_X; x++) {
            new Pion(this, true).allerSurCase(grilleCases[x][6]);
        }

        // Pièces noires
        new Tour(this, false).allerSurCase(grilleCases[0][0]);
        new Cavalier(this, false).allerSurCase(grilleCases[1][0]);
        new Fou(this, false).allerSurCase(grilleCases[2][0]);
        new Reine(this, false).allerSurCase(grilleCases[3][0]);
        new Roi(this, false).allerSurCase(grilleCases[4][0]);
        new Fou(this, false).allerSurCase(grilleCases[5][0]);
        new Cavalier(this, false).allerSurCase(grilleCases[6][0]);
        new Tour(this, false).allerSurCase(grilleCases[7][0]);
        for (int x = 0; x < SIZE_X; x++) {
            new Pion(this, false).allerSurCase(grilleCases[x][1]);
        }

        setChanged();
        notifyObservers();
    }

    public void arriverCase(Case c, Piece p) {

        c.p = p;

    }

    public void deplacerPiece(Case c1, Case c2) {
        if (c1.p != null) {
            Piece piece = c1.p;
            c1.quitterLaCase();
            if(c2.getPiece() != null) {
                ajouterPieceMorte(c2.getPiece());
            }
            piece.allerSurCase(c2);
        }

        setChanged();
        notifyObservers();
    }

    /** Indique si p est contenu dans la grille
     */
    public boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }

    public int distancesX(Case c1, Case c2){
        return map.get(c2).x - map.get(c1).x;
    }

    public Case appliquerDirection(Direction d, Case c){
        if (c == null) return null;
        Point case_suiv = new Point(map.get(c).x + d.dx, map.get(c).y + d.dy);
        return contenuDansGrille(case_suiv) ? grilleCases[case_suiv.x][case_suiv.y] : null;
    }

    public Case positionTour(int distance, Case arriveeRoi){
        if (distance > 0){
            return appliquerDirection(Direction.Droite, arriveeRoi);
        }
        else {
            Case caseCavalier = appliquerDirection(Direction.Gauche, arriveeRoi);
            return appliquerDirection(Direction.Gauche, caseCavalier);
        }
    }

    public Case roqueTour(int distance, Case arriveeRoi){
        if (distance > 0){
            return appliquerDirection(Direction.Gauche, arriveeRoi);
        }
        else {
            return appliquerDirection(Direction.Droite, arriveeRoi);
        }
    }

    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleCases[p.x][p.y];
        }
        return retour;
    }


    public void ajouterPieceMorte(Piece piece) {
        if (piece.couleur) {
            piecesMortesBlanches.add(piece);
        } else {
            piecesMortesNoires.add(piece);
        }
    }

    public ArrayList<Piece> getPiecesMortesBlanches() {
        return piecesMortesBlanches;
    }

    public ArrayList<Piece> getPiecesMortesNoires() {
        return piecesMortesNoires;
    }

    private Case trouverRoi(boolean couleurRoi) {
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (c.getPiece() != null && c.getPiece() instanceof Roi && c.getPiece().couleur == couleurRoi) {
                    return c;
                }
            }
        }
        return null;
    }

    public boolean estRoiEnEchec(boolean couleurRoi) {
        Case roiCase = trouverRoi(couleurRoi);
        if (roiCase == null) return false;        
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (c.getPiece() != null && c.getPiece().couleur != couleurRoi) {
                    if (c.getPiece().getCasesAccessibles().getMesCasesAccessibles().contains(roiCase)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean estEchecEtMat(boolean couleurRoi) {
        if (!estRoiEnEchec(couleurRoi)) {
            return false;
        }    
        Case roiCase = trouverRoi(couleurRoi);        
        for (Direction d : Direction.values()) {
            Case caseSecuritaire = appliquerDirection(d, roiCase);
            if (caseSecuritaire != null && caseSecuritaire.getPiece() == null) {
                if (!estRoiEnEchec(couleurRoi)) {
                    return false;
                }
            }
        }    
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case c = grilleCases[x][y];
                if (c.getPiece() != null && c.getPiece().couleur == couleurRoi) {
                    if (c.getPiece().getCasesAccessibles().getMesCasesAccessibles().contains(roiCase)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    

    public boolean estPat(boolean couleur) {
        if (estRoiEnEchec(couleur)) {
            return false;
        }
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case caseActuelle = grilleCases[x][y];
                if (caseActuelle.getPiece() != null && caseActuelle.getPiece().couleur) {
                    Piece piece = caseActuelle.getPiece();
                    for (Case caseCible : piece.getCasesAccessibles().getMesCasesAccessibles()) {
                        if (piece.getCasesAccessibles().getMesCasesAccessibles().contains(caseCible)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    

}
