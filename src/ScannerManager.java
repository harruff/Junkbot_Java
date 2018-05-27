import java.util.ArrayList;

import bwapi.*;

public class ScannerManager {
	private ArrayList<Unit> comsats = new ArrayList<Unit>();
	private Game game;
	//private Player self;
	
	//private TilePosition enemyBase = null, ourBase = null, ourNatExpo = null, enemyNatExpo = null;
	//private ArrayList<EnemyMemoryChunk> enemyMemory = new ArrayList<EnemyMemoryChunk>();
	//private int lastScanFrame = 0;
	
	public void setGame(Game g) {
		this.game = g;
		//this.self = game.self();
	}

	/*
    public void setOurInformation(TilePosition ob, TilePosition one) {
		ourBase = ob;
		ourNatExpo = one;
	}
    
    public void setEnemyInformation(TilePosition eb, TilePosition ene) {
		enemyBase = eb;
    	enemyNatExpo = ene;
    }
    
    public void updateEnemyMemory(ArrayList<EnemyMemoryChunk> em) {
    	enemyMemory = em;
    }
    */
   
	public void add(Unit m) {comsats.add(m);}
	public void remove(Unit m) {comsats.remove(m);}	
	public void clear() {comsats.clear();}	
	public Unit get(int i) {return comsats.get(i);}
	public int size() {return comsats.size();}	
	/*
	public void checkBaseScan() {
		if (comsats.size() > 0 && enemyBase == null) {
			if (comsats.get(0).getEnergy() >= 50) {
				comsats.get(0).useTech(TechType.Scanner_Sweep, possiblebases)
			}
		}
	}
	*/
	public void scanCloaked() {	
		boolean unitIsClose = false;
		for (Unit cloaked : game.enemy().getUnits()) {
			if (!cloaked.isDetected() && cloaked.exists()) {
				for (Unit close : cloaked.getUnitsInRadius(4*32)) {
					if (!close.getType().isWorker() && !close.getType().isBuilding()) {
						unitIsClose = true;
					}				
				}
				if (unitIsClose) {
					 for ( int i = 0; i < comsats.size(); i++) {
						 if (comsats.get(i).getEnergy() > 50) {
							 comsats.get(i).useTech(TechType.Scanner_Sweep, cloaked.getPosition());
							 //lastScanFrame = game.getFrameCount();
							 break; //don't scan more than once;
						 }
					 }
					 break;
				}
			}
		}
	}
	
	public void onFrame() {
		scanCloaked();
	}
	
}