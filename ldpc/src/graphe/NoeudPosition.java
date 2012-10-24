package graphe;

import java.util.ArrayList;

public class NoeudPosition extends Noeud {

	private boolean bit;
	private boolean traite = false;
	private boolean bitCourant;


	public NoeudPosition(int l) {
		super(l);
		voisins = new ArrayList<Arc>();
	}



	public void setBit(boolean bit) {
		this.bit = bit;
	}

	public boolean getBit() {
		return this.bit;
	}
	public boolean getBitCourant(){
		return bitCourant;
	}
	public void setBitCourant(boolean b){
		bitCourant =b;
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