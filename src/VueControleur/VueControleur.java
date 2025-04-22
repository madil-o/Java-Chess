package VueControleur;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

    private JLabel labelTour;
    private JTextArea historiqueCoups;
    private JPanel panelCapturesBlancs;
    private JPanel panelCapturesNoirs;

    public VueControleur(Jeu _jeu) {
        jeu = _jeu;
        jeu.setPromotionListener(estBlanc -> {
            Object[] options = {"Dame", "Tour", "Fou", "Cavalier"};
            return (String) JOptionPane.showInputDialog(
                this,
                "Choisissez une pièce pour la promotion :",
                "Promotion",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                "Dame"
            );
        });        
        plateau = jeu.getPlateau();
        sizeX = plateau.SIZE_X;
        sizeY = plateau.SIZE_Y;

        chargerLesIcones();
        placerLesComposantsGraphiques();

        plateau.addObserver(this);
        mettreAJourAffichage();
        addKeyListener(getKeyListener());
        setFocusable(true);
        requestFocusInWindow();

        setVisible(true);

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
    
        // Création du panneau principal
        JPanel contenu = new JPanel(new BorderLayout());
    
        // Plateau d'échecs 
        JPanel grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX));
        grilleJLabels.setPreferredSize(new Dimension(sizeX * pxCase, sizeY * pxCase));
        tabJLabel = new JLabel[sizeX][sizeY];
    
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                jlab.setOpaque(true);
                jlab.setHorizontalAlignment(SwingConstants.CENTER);
                tabJLabel[x][y] = jlab;
    
                final int xx = x, yy = y;
                jlab.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        gererClic(xx, yy);
                    }
                });
    
                Color base = ((x + y) % 2 == 0) ? new Color(150, 150, 210) : new Color(50, 50, 110);
                jlab.setBackground(base);
                grilleJLabels.add(jlab);
            }
        }
    
        // Panneau latéral
        JPanel panneauDroite = new JPanel();
        panneauDroite.setLayout(new BoxLayout(panneauDroite, BoxLayout.Y_AXIS));
        panneauDroite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        labelTour = new JLabel("Tour des Blancs");
        labelTour.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelTour.setFont(new Font("Arial", Font.BOLD, 16));
        panneauDroite.add(labelTour);
        panneauDroite.add(Box.createVerticalStrut(10));
    
        historiqueCoups = new JTextArea(15, 12);
        historiqueCoups.setEditable(false);
        JScrollPane scrollHistorique = new JScrollPane(historiqueCoups);
        scrollHistorique.setAlignmentX(Component.CENTER_ALIGNMENT);
        panneauDroite.add(new JLabel("Historique des coups :"));
        panneauDroite.add(scrollHistorique);
        panneauDroite.add(Box.createVerticalStrut(10));
    
        panneauDroite.add(new JLabel("Pièces capturées :"));
        panelCapturesBlancs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCapturesNoirs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panneauDroite.add(new JLabel("Par les Noirs :"));
        JScrollPane scrollBlancs = new JScrollPane(panelCapturesBlancs);
        scrollBlancs.setPreferredSize(new Dimension(150, 40));
        scrollBlancs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollBlancs.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        JScrollPane scrollNoirs = new JScrollPane(panelCapturesNoirs);
        scrollNoirs.setPreferredSize(new Dimension(150, 40));
        scrollNoirs.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollNoirs.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        
        panneauDroite.add(scrollBlancs);
        panneauDroite.add(new JLabel("Par les Blancs :"));
        panneauDroite.add(scrollNoirs);
        
        panneauDroite.add(Box.createVerticalStrut(10));
    
        JButton boutonRejouer = new JButton("Rejouer la partie");
        boutonRejouer.setAlignmentX(Component.CENTER_ALIGNMENT);
        boutonRejouer.addActionListener(e -> recommencerPartie());
        panneauDroite.add(boutonRejouer);
    
        // Ajout dans le layout principal
        contenu.add(grilleJLabels, BorderLayout.CENTER);
        contenu.add(panneauDroite, BorderLayout.EAST);
    
        // Finalisation
        setContentPane(contenu);
        pack();
        setLocationRelativeTo(null); // centre la fenêtre
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
                Color base = ((x + y) % 2 == 0) ? new Color(150, 150, 210) : new Color(50, 50, 110);
                tabJLabel[x][y].setBackground(base);
            }
        }
    }

    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        setTitle("Jeu d'Échecs - Tour des " + (jeu.isTourBlanc() ? "Blancs" : "Noirs"));
        if (labelTour != null) {
            labelTour.setText("Tour des " + (jeu.isTourBlanc() ? "Blancs" : "Noirs"));
        }
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                tabJLabel[x][y].setIcon(null);
                Piece p = plateau.getCases()[x][y].getPiece();
                if (p != null) tabJLabel[x][y].setIcon(getIcone(p));
            }
        }

        if (historiqueCoups != null) {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (Coup c : jeu.getHistorique()) {
                sb.append(i++).append(". ").append(c).append("\n");
            }
            historiqueCoups.setText(sb.toString());
        }
        
        // Affichage des pièces capturées
        if (panelCapturesBlancs != null && panelCapturesNoirs != null) {
            panelCapturesBlancs.removeAll();
            panelCapturesNoirs.removeAll();
        
            for (Piece p : plateau.getPiecesMortesBlanches()) {
                JLabel label = new JLabel(redimensionnerIcone(getIcone(p), 30));
                panelCapturesBlancs.add(label);
            }
        
            for (Piece p : plateau.getPiecesMortesNoires()) {
                JLabel label = new JLabel(redimensionnerIcone(getIcone(p), 30));
                panelCapturesNoirs.add(label);
            }
        
            panelCapturesBlancs.revalidate();
            panelCapturesBlancs.repaint();
            panelCapturesNoirs.revalidate();
            panelCapturesNoirs.repaint();
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
        jeu.setTourBlanc(true);
        jeu.resetHistorique();
        mettreAJourAffichage();
    }

    private KeyListener getKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    jeu.annulerDernierCoup();
                    caseClic1 = null;
                    reinitialiserCouleurs();
                    mettreAJourAffichage();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    jeu.refaireCoup();
                    caseClic1 = null;
                    reinitialiserCouleurs();
                    mettreAJourAffichage();
                }
            }
        };
    }

    private ImageIcon redimensionnerIcone(ImageIcon icon, int taille) {
        Image img = icon.getImage().getScaledInstance(taille, taille, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }    
}
