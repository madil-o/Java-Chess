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
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (clic position départ -> position arrivée pièce))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Plateau plateau; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)
    private Jeu jeu;
    private final int sizeX; // taille de la grille affichée
    private final int sizeY;
    private static final int pxCase = 50; // nombre de pixel par case
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
    private Case caseClic2;


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
        BufferedImage image = null;

        ImageIcon icon = new ImageIcon(urlIcone);

        // Redimensionner l'icône
        Image img = icon.getImage().getScaledInstance(pxCase, pxCase, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(img);

        return resizedIcon;
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Jeu d'Échecs");
        setResizable(false);
        setSize(sizeX * pxCase, sizeX * pxCase);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille


        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();

                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )

                final int xx = x; // permet de compiler la classe anonyme ci-dessous
                final int yy = y;
                // écouteur de clics
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        boolean couleurJoueurActuel = jeu.isTourBlanc();
                        Case caseCliquee = plateau.getCases()[xx][yy];
                
                        // Bloque les clics sur pièces adverses
                        if (caseClic1.getPiece() != null && caseClic1.getPiece().couleur != couleurJoueurActuel) {
                            System.out.println("Coup invalide !");
                            return;
                        }
                
                        if (caseClic1 != null && caseClic1.equals(caseCliquee)) {
                            // Désélection si clic sur la même case
                            caseClic1 = null;
                            mettreAJourAffichage();
                        } 
                        else if (caseClic1 != null && caseCliquee.getPiece() != null) {
                            // Changement de sélection si nouvelle pièce alliée
                            caseClic1 = caseCliquee;
                            mettreAJourAffichage();  // Réinitialise toutes les couleurs
                            tabJLabel[xx][yy].setBackground(Color.YELLOW); // Met en surbrillance la nouvelle
                        } 
                        else if (caseClic1 == null) {
                            // Sélection initiale
                            caseClic1 = caseCliquee;
                            tabJLabel[xx][yy].setBackground(Color.YELLOW);
                        } 
                        else {
                            // Tentative de déplacement
                            caseClic2 = caseCliquee;
                            jeu.envoyerCoup(new Coup(caseClic1, caseClic2));
                            
                            if (jeu.isTourBlanc() != couleurJoueurActuel) { // Coup valide
                                caseClic1 = null;
                                caseClic2 = null;
                            }
                            mettreAJourAffichage();
                        }
                    }
                });


                jlab.setOpaque(true);

                if ((y%2 == 0 && x%2 == 0) || (y%2 != 0 && x%2 != 0)) {
                    tabJLabel[x][y].setBackground(new Color(50, 50, 110));
                } else {
                    tabJLabel[x][y].setBackground(new Color(150, 150, 210));
                }

                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                tabJLabel[x][y].setIcon(null); // <-- Reset
            }
        }
        setTitle("Jeu d'Échecs - Tour des " + (jeu.isTourBlanc() ? "Blancs" : "Noirs"));
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if ((x + y) % 2 == 0) {
                    tabJLabel[x][y].setBackground(new Color(50, 50, 110));
                } else {
                    tabJLabel[x][y].setBackground(new Color(150, 150, 210));
                }
                Case c = plateau.getCases()[x][y];

                if (c != null) {

                    Piece e = c.getPiece();

                    if (e!= null) {
                        if (e instanceof Roi) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoRoiB : icoRoiN);
                        } else if (e instanceof Tour) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoTourB : icoTourN);
                        } else if (e instanceof Fou) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoFouB : icoFouN);
                        } else if (e instanceof Cavalier) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoCavalierB : icoCavalierN);
                        } else if (e instanceof Reine) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoReineB : icoReineN);
                        } else if (e instanceof Pion) {
                            tabJLabel[x][y].setIcon(e.couleur ? icoPionB : icoPionN);
                        }
                    }
                    
                }

            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
        /*

        // récupérer le processus graphique pour rafraichir
        // (normalement, à l'inverse, a l'appel du modèle depuis le contrôleur, utiliser un autre processus, voir classe Executor)


        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                }); 
        */

    }
}
