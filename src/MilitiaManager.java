import java.util.*;
import bwapi.*;
//import bwta.*;

public class MilitiaManager {
	private ArrayList<MilitiaChunk> militiaChunks = new ArrayList<MilitiaChunk>();
	//private ArrayList<Unit> militia = new ArrayList<Unit>();
	//private ArrayList<Unit> enemies = new ArrayList<Unit>();
	//private TilePosition ourBase = null, ourNatExpo = null;
	
	//private int militiaNeeded = 0;
	
	//private Game game;
	//private Player self;
	
	public void setGame(Game g) {
		//this.game = g;
		//this.self = game.self();
	}
	
	public MilitiaManager(Game g) {
		//this.game = g;
	}
	/*
  public void setOurInformation(TilePosition ob, TilePosition one) {


  /*public void setOurInformation(TilePosition ob, TilePosition one) {
    ourBase = ob;
    ourNatExpo = one;
	}
    
    public void setEnemies(ArrayList<Unit> e) {
    	enemies = e;
    }
    }*/
    
  public ArrayList<MilitiaChunk> getMilitiaChunks() {return militiaChunks;}
  public void addMilitiaChunk(MilitiaChunk mc) {militiaChunks.add(mc);}
	public void removeMilitiaChunk(MilitiaChunk mc) {militiaChunks.remove(mc);}
    
	//public ArrayList<Unit> getMilitia() {return militia;}
	//public void addMilitia(Unit s) {militia.add(s);}
	//public void removeMilitia(Unit s) {militia.remove(s);}
	
	/*
	public void setMilitiaNeeded(int n) {
		//militiaNeeded = n;
	}
	*/
	
	public void onFrame() {
		ArrayList<MilitiaChunk> mc_toRemove = new ArrayList<MilitiaChunk>();
		ArrayList<Unit> m_toRemove = new ArrayList<Unit>();
		
		//Remove militiaChunks
		for (MilitiaChunk mc : militiaChunks) {
			//IF a militiaChunk's scv has been killed, THEN remove it
			for (Unit m : mc.getMySCVs()) {
				if (!m.exists()) {
					m_toRemove.add(m);
				}
			}
			
			//IF a militiaChunk's target doesn't exist, remove the chunk
			if (!mc.getTarget().exists()) {
				mc_toRemove.add(mc);
			}
			
			/*
			//IF an enemy doesn't exist, but there's a militiaChunk for it, remove the chunk
			boolean foundIt = false;
			for (Unit e : enemies) {
				if (mc.getTarget().getID() == e.getID()) {
					foundIt = true;
					break;
				}
			}
			if (!foundIt) {
				mc_toRemove.add(mc);
			}
			*/
			
			/*
			//IF the target is outside the bases radius, remove the chunk
			if (ourBase.toPosition().getDistance(mc.getTarget()) >= 10*32){
				mc_toRemove.add(mc);
			}
			*/
		}
		
		for (MilitiaChunk mc : mc_toRemove) {
			militiaChunks.remove(mc);
		}
		
		for (Unit m : m_toRemove) {
			for (MilitiaChunk mc : militiaChunks) {
				mc.removeSCV(m);
			}
		}

		//MILITIA MICRO
		for (MilitiaChunk mc : militiaChunks) {
			for (Unit u : mc.getMySCVs()) {
				Unit attackThis = mc.getTarget();
				if (attackThis != null) {
					if (u.getOrderTarget() == null) {
						u.attack(attackThis);
						continue;
					}
					else if (u.getOrderTarget().getID() != attackThis.getID()) {
						u.attack(attackThis);
						continue;
					}
					
					//IF unit is not attacking, and thus it's order is to attack the correct target
					//Attack the nearest enemy
					if (u.getOrder() != Order.AttackMove 
							&& u.getOrder() != Order.AttackTile
							&& u.getOrder() != Order.AttackUnit) {
						u.attack(attackThis.getPosition());
					}
				}
			}
		}
	}
	
	public void debug(int r, Color c, boolean f) {
		for (MilitiaChunk mc : militiaChunks) {
			mc.debug(r, c, f);
		}
	}
}
