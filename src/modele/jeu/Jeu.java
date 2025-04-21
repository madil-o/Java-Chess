package modele.jeu;

import modele.plateau.Plateau;
import modele.plateau.Case;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import modele.jeu.pieces.*;
import java.awt.Point;

public class Jeu extends Thread{

    public interface PromotionListener {
        String choisirPromotion(boolean estBlanc);
    }    

    private final Plateau plateau;
    private boolean enReplay = false;

    private final Joueur j1, j2;
    protected Coup coupRecu;
    private boolean tourBlanc = true;
    private ArrayList<Coup> historique = new ArrayList<>();
    private ArrayList<Coup> refaire = new ArrayList<>();
    private Pion dernierPionDoublePas = null;
    private PromotionListener promotionListener;

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
        if (enReplay) {
            Point pDep = plateau.getMap().get(coup.dep);
            Point pArr = plateau.getMap().get(coup.arr);
            if (pDep == null || pArr == null) return false;
            coup.dep = plateau.getCases()[pDep.x][pDep.y];
            coup.arr = plateau.getCases()[pArr.x][pArr.y];
        }
        Piece piece = coup.dep.getPiece();
        if (piece == null || piece.couleur != tourBlanc) {
            System.out.println("Coup invalide : mauvaise pièce.");
            return false;
        }

        if (!piece.getCasesAccessibles().getCasesAccessibles().contains(coup.arr) && !estCoupPriseEnPassant(coup)) {
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

        // Prise en passant effective
        if (estCoupPriseEnPassant(coup)) {
            Point pos = plateau.getMap().get(coup.arr);
            Case caseCapturee = plateau.getCases()[pos.x][pos.y + (tourBlanc ? 1 : -1)];
            plateau.ajouterPieceMorte(caseCapturee.getPiece());
            caseCapturee.quitterLaCase();
        }

        // Roque ?
        if (piece instanceof Roi && Math.abs(plateau.distancesX(coup.dep, coup.arr)) > 1 && !piece.aDejaBouge()) {
            int d = plateau.distancesX(coup.dep, coup.arr);
            Case departTour = plateau.positionTour(d, coup.arr);
            Case arriveeTour = plateau.roqueTour(d, coup.arr);
            if (departTour.getPiece() != null && !departTour.getPiece().aDejaBouge()) {
                plateau.deplacerPiece(departTour, arriveeTour);
            }
        }

        plateau.deplacerPiece(coup.dep, coup.arr);

        // Promotion personnalisée
        if (piece instanceof Pion) {
            int y = plateau.getMap().get(coup.arr).y;
            boolean doitPromouvoir = (piece.couleur && y == 0) || (!piece.couleur && y == 7);

            if (doitPromouvoir) {
                String choix = "Dame";
                if (!enReplay && promotionListener != null) {
                    choix = promotionListener.choisirPromotion(piece.couleur);
                }

                Piece promotion = switch (choix) {
                    case "Tour" -> new Tour(plateau, piece.couleur);
                    case "Fou" -> new Fou(plateau, piece.couleur);
                    case "Cavalier" -> new Cavalier(plateau, piece.couleur);
                    default -> new Reine(plateau, piece.couleur);
                };
                promotion.allerSurCase(coup.arr);
            }
        }

        // Enregistrer double pas
        if (piece instanceof Pion pion) {
            int y1 = plateau.getMap().get(coup.dep).y;
            int y2 = plateau.getMap().get(coup.arr).y;

            if (Math.abs(y2 - y1) == 2) {
                dernierPionDoublePas = pion;
            } else {
                dernierPionDoublePas = null;
            }
        } else {
            dernierPionDoublePas = null;
        }

        if (!enReplay) {
            historique.add(coup);
        }

        plateau.notifierVue();
        return true;
    }

    public void setPromotionListener(PromotionListener listener) {
        this.promotionListener = listener;
    }    

    public boolean estCoupPriseEnPassant(Coup coup) {
        if (!(coup.dep.getPiece() instanceof Pion)) return false;
        if (dernierPionDoublePas == null) return false;
    
        Pion pion = (Pion) coup.dep.getPiece();
        Case casePionAdv = dernierPionDoublePas.getCase();
    
        Point posDep = plateau.getMap().get(coup.dep);
        Point posArr = plateau.getMap().get(coup.arr);
        Point posPionAdv = plateau.getMap().get(casePionAdv);
    
        if (posPionAdv == null || posDep == null || posArr == null) return false;
    
        boolean memeLigne = posDep.y == posPionAdv.y;
        boolean aCote = Math.abs(posDep.x - posPionAdv.x) == 1;
        boolean bonneDestination = posArr.x == posPionAdv.x &&
                                   posArr.y == posDep.y + (pion.couleur ? -1 : 1);
    
        return memeLigne && aCote && bonneDestination;
    }    

    @Override
    public void run() {
        jouerPartie();
    }

    public void jouerPartie() {
        while(true) {
            Coup c = (tourBlanc) ? j1.getCoup() : j2.getCoup();
            if(appliquerCoup(c)) {
                tourBlanc = !tourBlanc;
                refaire.clear();
            }
        }
    }

    public List<Coup> getHistorique() {
        return historique;
    }

    public void annulerDernierCoup() {
        if (!historique.isEmpty()) {
            Coup dernier = historique.remove(historique.size() - 1);
            refaire.add(0, dernier);
    
            plateau.reinitialiser();
            placerPieces();
            tourBlanc = true;
    
            enReplay = true;
            for (Coup c : historique) {
                appliquerCoup(c);
                tourBlanc = !tourBlanc;
            }
            enReplay = false;
        }
    }

    public void refaireCoup() {
        if (!refaire.isEmpty()) {
            Coup prochain = refaire.remove(0);
            enReplay = true;
            appliquerCoup(prochain);
            enReplay = false;
            historique.add(prochain);
            tourBlanc = !tourBlanc;
        }
    }
    
}
