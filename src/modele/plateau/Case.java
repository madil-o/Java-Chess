/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele.plateau;

import modele.jeu.Piece;

public class Case {

    protected Piece p;
    protected Plateau plateau;



    public void quitterLaCase() {
        p = null;
    }



    public Case(Plateau _plateau) {

        plateau = _plateau;
    }

    public Piece getPiece() {
        return p;
    }
/*
    public void setEntite(Piece _e) {

        p = _e;

    }*/

   }
