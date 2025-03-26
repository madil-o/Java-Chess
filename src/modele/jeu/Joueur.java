package modele.jeu;

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
}
