/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

/** Type énuméré des directions : les directions correspondent à un ensemble borné de valeurs, connu à l'avance
 *
 *
 */
public enum Direction {
    Haut(0, -1), Bas (0, 1),Gauche(-1, 0), Droite(1, 0),
    HautGauche(-1, -1), HautDroite(1, -1), BasGauche(-1, 1), BasDroite(1, 1);
    public final int dx;
    public final int dy;


    private Direction(int _dx, int _dy) {
        dx = _dx;
        dy = _dy;
    }

    
}
