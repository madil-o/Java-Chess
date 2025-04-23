package VueControleur;

import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.plateau.Case;
import modele.plateau.Plateau;
import modele.jeu.pieces.*;
import modele.jeu.Piece;

import java.util.Scanner;

public class VueControleurConsole {
    private final Jeu jeu;
    private final Plateau plateau;
    private final Scanner scanner = new Scanner(System.in);

    public VueControleurConsole(Jeu jeu) {
        this.jeu = jeu;
        this.plateau = jeu.getPlateau();
    }

    public void lancer() {
        while (true) {
            afficherPlateau();
            boolean couleurActuelle = jeu.isTourBlanc();
            System.out.println((jeu.isTourBlanc() ? "♚ Blancs" : "♔ Noirs") + " - entrez un coup (ex: e2 e4) :");

            String from = scanner.next();
            String to = scanner.next();

            Case caseDep = coordVersCase(from);
            Case caseArr = coordVersCase(to);

            if (caseDep == null || caseArr == null) {
                System.out.println("Coordonnées invalides.");
                continue;
            }

            Coup coup = new Coup(caseDep, caseArr);
            boolean valide = jeu.appliquerCoup(coup);

            if (!valide) {
                System.out.println("Coup invalide.");
                continue;
            }
    
            // Inverser le tour (puisque pas fait automatiquement ici)
            jeu.setTourBlanc(!couleurActuelle);
    
            boolean couleurAdverse = jeu.isTourBlanc(); // le joueur adverse vient de se faire jouer dessus
            boolean roiEnEchec = plateau.estRoiEnEchec(couleurAdverse, plateau.trouverRoi(couleurAdverse));
            boolean aDesCoups = (couleurAdverse ? jeu.getJ1() : jeu.getJ2()).aDesCoupsLegaux();
    
            if (roiEnEchec && !aDesCoups) {
                afficherPlateau();
                System.out.println("Échec et mat ! Les " + (couleurActuelle ? "blancs" : "noirs") + " gagnent !");
                break;
            } else if (!roiEnEchec && !aDesCoups) {
                afficherPlateau();
                System.out.println("PAT ! Match nul.");
                break;
            } else if (roiEnEchec) {
                System.out.println("ÉCHEC !");
            }
        }
        System.out.println("Fin de la partie.");
        scanner.close();
        System.exit(0);
    }

    private void afficherPlateau() {
        System.out.println("\n  a b c d e f g h");
        for (int y = 0; y < 8; y++) {
            System.out.print((8 - y) + " ");
            for (int x = 0; x < 8; x++) {
                Piece p = plateau.getCases()[x][y].getPiece();
                System.out.print((p != null ? symboleUnicode(p) : "·") + " ");
            }
            System.out.println((8 - y));
        }
        System.out.println("  a b c d e f g h\n");
    }

    private String symboleUnicode(Piece p) {
        if (p instanceof Roi) return p.couleur ? "♚" : "♔";
        if (p instanceof Reine) return p.couleur ? "♛" : "♕";
        if (p instanceof Tour) return p.couleur ? "♜" : "♖";
        if (p instanceof Fou) return p.couleur ? "♝" : "♗";
        if (p instanceof Cavalier) return p.couleur ? "♞" : "♘";
        if (p instanceof Pion) return p.couleur ? "♟" : "♙";
        return "?";
    }

    private Case coordVersCase(String coord) {
        if (coord.length() != 2) return null;
        char file = coord.charAt(0);
        char rang = coord.charAt(1);
        int x = file - 'a';
        int y = 8 - (rang - '0');
        if (x < 0 || x > 7 || y < 0 || y > 7) return null;
        return plateau.getCases()[x][y];
    }
}
