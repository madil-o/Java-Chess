package VueControleur;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import modele.jeu.Coup;
import modele.jeu.Jeu;
import modele.jeu.Piece;
import modele.jeu.pieces.*;
import modele.plateau.Case;
import modele.plateau.Plateau;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'ap plication (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (clic position départ -> position arrivée pièce))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 60; // nombre de pixel par case
    // icones affichées dans la grille
    private ImageIcon icoRoiB, 
                      icoRoiN, 
                      icoTourB, 
                      icoTourN,
                      icoFouB, 
                      icoFouN, 
                      icoCavalierB, 
                      icoCavalierN, 
                      icoReineB, 
                      icoReineN, 
                      icoPionB, 
                      icoPionN;

    private Case caseClic1; // mémorisation des cases cliquées
    //private Case caseClic2;

    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);
        mettreAJourAffichage();
    }


    private void chargerLesIcones() {
        icoRoiB = chargerIcone("Images/wK.png");
        icoRoiN = chargerIcone("Images/bK.png");
        icoTourB = chargerIcone("Images/wR.png");
        icoTourN = chargerIcone("Images/bR.png");
        icoFouB = chargerIcone("Images/wB.png");
        icoFouN = chargerIcone("Images/bB.png");
        icoCavalierB = chargerIcone("Images/wN.png");
        icoCavalierN = chargerIcone("Images/bN.png");
        icoReineB = chargerIcone("Images/wQ.png");
        icoReineN = chargerIcone("Images/bQ.png");
        icoPionB = chargerIcone("Images/wP.png");
        icoPionN = chargerIcone("Images/bP.png");
    }

    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image;
        ImageIcon icon = new ImageIcon(urlIcone);
        // Redimensionner l'icône
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeY * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille
        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                jlab.setOpaque(true);
                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x, yy = y; // permet de compiler la classe anonyme ci-dessous
                // écouteur de clics
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        gererClic(xx, yy);
                    }
                });

                Color base = ((x + y) % 2 == 0) ? new Color(50, 50, 110) : new Color(150, 150, 210);
                jlab.setBackground(base);
                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }

    private void gererClic(int x, int y) {
        Case clic = plateau.getCases()[x][y];

        if (caseClic1 == null) {
            if (clic.getPiece() != null && clic.getPiece().couleur == jeu.isTourBlanc()) {
                caseClic1 = clic;
                tabJLabel[x][y].setBackground(Color.YELLOW);
            }
        } else {
            Case caseClic2 = clic;
            jeu.envoyerCoup(new Coup(caseClic1, caseClic2));
            caseClic1 = null;
            reinitialiserCouleurs();
        }
    }

    private void reinitialiserCouleurs() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                Color base = ((x + y) % 2 == 0) ? new Color(50, 50, 110) : new Color(150, 150, 210);
                tabJLabel[x][y].setBackground(base);
            }
        }
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        setTitle("Jeu d'Échecs - Tour des " + (jeu.isTourBlanc() ? "Blancs" : "Noirs"));
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                tabJLabel[x][y].setIcon(null);
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p != null) tabJLabel[x][y].setIcon(getIcone(p));
            }
        }

        // Fin de partie ?
        if (plateau.estRoiEnEchec(true, plateau.trouverRoi(true)) && plateau.estEchecEtMat(true)) {
            afficherVictoire(false);
        } else if (plateau.estRoiEnEchec(false, plateau.trouverRoi(false)) && plateau.estEchecEtMat(false)) {
            afficherVictoire(true);
        } else if (plateau.estPat(true) || plateau.estPat(false)) {
            afficherPat();
        }
    }

    private ImageIcon getIcone(Piece e) {
        if (e instanceof Roi) return e.couleur ? icoRoiB : icoRoiN;
        if (e instanceof Reine) return e.couleur ? icoReineB : icoReineN;
        if (e instanceof Fou) return e.couleur ? icoFouB : icoFouN;
        if (e instanceof Cavalier) return e.couleur ? icoCavalierB : icoCavalierN;
        if (e instanceof Tour) return e.couleur ? icoTourB : icoTourN;
        if (e instanceof Pion) return e.couleur ? icoPionB : icoPionN;
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(this::mettreAJourAffichage);
    }

    public void afficherVictoire(boolean couleurGagnante) {
        String message = couleurGagnante ? "Les Blancs ont gagné !" : "Les Noires ont gagné !";
        int reponse = JOptionPane.showConfirmDialog(this, message + "\nVoulez-vous recommencer une nouvelle partie ?", 
                                                    "Victoire", JOptionPane.YES_NO_OPTION);
        if (reponse == JOptionPane.YES_OPTION) {
            recommencerPartie();
        } else {
            System.exit(0);
        }
    }

    public void afficherPat() {
        int rep = JOptionPane.showConfirmDialog(this, "Match nul par pat !\nVoulez-vous recommencer une nouvelle partie ?",
                                                "Pat", JOptionPane.YES_NO_OPTION);
        if (rep == JOptionPane.YES_OPTION) {
            recommencerPartie();
        } else {
            System.exit(0);
        }
    }

    public void recommencerPartie() {
        plateau.reinitialiser();
        jeu.placerPieces();
        mettreAJourAffichage();
    }
    
    
}
