import VueControleur.VueControleur;
import VueControleur.VueControleurConsole;
import modele.jeu.Jeu;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Jeu jeu = new Jeu();
        boolean vueChoisie = false;

        while (!vueChoisie) {
            System.out.println("Choisissez votre vue :");
            System.out.println("1 - Vue graphique (Swing)");
            System.out.println("2 - Vue console (terminal)");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    new VueControleur(jeu);
                    vueChoisie = true;
                    break;
                case "2":
                    VueControleurConsole vueConsole = new VueControleurConsole(jeu);
                    vueConsole.lancer();
                    vueChoisie = true;
                    break;
                default:
                    System.out.println("Choix invalide. Veuillez entrer 1 ou 2.\n");
            }
        }
    }
}
