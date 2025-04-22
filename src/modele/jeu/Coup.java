package modele.jeu;

import modele.plateau.Case;
import modele.plateau.Plateau;
import java.awt.Point;

public class Coup {
    protected Case dep;
    protected Case arr;
    public Coup(Case _dep, Case _arr) {
        dep = _dep;
        arr = _arr;
    }
    public Case getCaseDep() {
        return dep;
    }
    public Case getCaseArr() {
        return arr;
    }

    @Override
    public String toString() {
        String colonne = "abcdefgh";
        Point pDep = dep.getPlateau().getMap().get(dep);
        Point pArr = arr.getPlateau().getMap().get(arr);

        if (pDep == null || pArr == null) return "? → ?";

        String depStr = colonne.charAt(pDep.x) + String.valueOf(8 - pDep.y);
        String arrStr = colonne.charAt(pArr.x) + String.valueOf(8 - pArr.y);

        return depStr + " → " + arrStr;
    }

}
