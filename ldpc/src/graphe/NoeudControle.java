package graphe;

import java.util.ArrayList;

public class NoeudControle extends Noeud {

	public boolean traite = false;

	public NoeudControle(int l) {
		super(l);
		voisins = new ArrayList<Arc>();
	}



	public void marque(){
		if(this.traite) System.out.print("Noeud d�ja marqu�");
		else this.traite = true;
	}
	public void deMarque(){
		if(! this.traite) System.out.print("Noeud d�ja pas marqu�");
		else this.traite = false;
	}

}