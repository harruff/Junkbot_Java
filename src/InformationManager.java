import java.util.*;
import bwapi.*;
import bwta.*;
import bwta.Region;

public class InformationManager {
	
	//Initialize a whole buncha empty positions, etc.
	private TilePosition ourBase = null, enemyBase = null, ourNatExpo = null, enemyNatExpo = null;
	private Position ourBasePosition = null, enemyBasePosition = null, ourNatExpoPosition = null, enemyNatExpoPosition = null;
	private Region ourBaseRegion = null, enemyBaseRegion = null, ourNatExpoRegion = null, enemyNatExpoRegion = null; 
	private Chokepoint bunkerChoke = null;
	private ArrayList<TilePosition> possibleBases = new ArrayList<TilePosition>();
	private ArrayList<TilePosition> possibleExpos = new ArrayList<TilePosition>();
	private ArrayList<Chokepoint> chokes = new ArrayList<Chokepoint>();
	
	private ArrayList<EnemyMemoryChunk> enemyMemory = new ArrayList<EnemyMemoryChunk>();
	private ArrayList<Unit> enemyUnits = new ArrayList<Unit>();
	
	private StringBuilder info = new StringBuilder("Info:\n"),
			num_info = new StringBuilder("\n");
	
	private int seconds = 0;
			
	private Game game;
	private Player self;
	
	public void setGame(Game g) {
		this.game = g;
		this.self = game.self();
	}
	
	private int enemyUnitsKilled = 0;
	
	//Getters make Java's world go round.
	public TilePosition getOurBase() 				{return ourBase;}
	public Position 	getOurBasePosition() 		{return ourBasePosition;}
	public Region		getOurBaseRegion() 			{return ourBaseRegion;}
	public TilePosition getEnemyBase() 				{return enemyBase;}
	public Position 	getEnemyBasePosition() 		{return enemyBasePosition;}
	public Region		getEnemyBaseRegion() 		{return enemyBaseRegion;}
	public TilePosition getOurNatExpo()				{return ourNatExpo;}
	public Position 	getOurNatExpoPosition()		{return ourNatExpoPosition;}
	public Region		getOurNatExpoRegion() 		{return ourNatExpoRegion;}
	public TilePosition getEnemyNatExpo()			{return enemyNatExpo;}
	public Position 	getEnemyNatExpoPosition()	{return enemyNatExpoPosition;}
	public Region		getEnemyNatExpoRegion() 	{return enemyNatExpoRegion;}
	public Chokepoint 	getBunkerChoke() 			{return bunkerChoke;}
	public int			getSecondsElapsed()			{return seconds;}
	
	public ArrayList<Chokepoint> getChokepoints()	{return chokes;}
	public ArrayList<TilePosition> getPossibleBases()	{return possibleBases;}
	public ArrayList<EnemyMemoryChunk> getEnemyMemory() {return enemyMemory;}
	
	public void setEnemyBase(TilePosition eb) {
		enemyBase = eb;
		enemyBasePosition = enemyBase.toPosition();
		enemyBaseRegion = BWTA.getRegion(enemyBasePosition);
	}
	
	public void setEnemyNatExpo(TilePosition ene) {
		enemyNatExpo = ene;
		enemyNatExpoPosition = enemyNatExpo.toPosition();
		enemyNatExpoRegion = BWTA.getRegion(enemyNatExpoPosition);
	}
	
	private String quadrant = "NA";
	
    // Determines position of player relative to map and opponent
    public void determineQuadrant(TilePosition t) {
		if 		(t.getX() < 64 && t.getY() < 64)	{ quadrant = "tl"; }	
		else if (t.getX() > 64 && t.getY() < 64)	{ quadrant = "tr"; }
		else if (t.getX() < 64 && t.getY() > 64)	{ quadrant = "bl"; }
		else if (t.getX() > 64 && t.getY() > 64)	{ quadrant = "br"; }
    }
    
    public String getQuadrant() {
    	return quadrant;
    }
    
    public void buildingGridOverlay() {
    	
    	 /*       WIP
    	for (Unit u : game.self().getUnits()) {
			// if this unit is in fact a building
			if (u.getType().isBuilding()) {
				// check if we have it's position in memory and add it if we don't
				if (!myBuildings.contains(u.getPosition())) {
					myBuildings.add(u.getPosition());					
				}
				
				int u_w = u.getType().width();
				int u_h = u.getType().height();
				
				
			}
		}
		*/
    	
    	/* Lags the game to a crawl... for some reason
    	for (int x = 0; x < w; x++) {
    		for (int y = 0; y < h; y++) {
    			Position point = new Position(x, y);
    			if (x%4 == 0 && y%4 == 0) {
    				game.drawCircleMap(point, 4, Color.Red);
    			} else {
    				game.drawCircleMap(point, 4, Color.Grey);
    			}
    		}
    	} */
    }
    
    public void debugOverlay() {
        //NUMBERS
        info.append("FPS:\n")
        .append("APM:\n")
        .append("Time:\n")
        .append("\n")
        .append("Map:\n")
        .append("Size:\n");
        
        num_info.append(game.getFPS()).append("\n")
        .append(game.getAPM()).append("\n");
        if (seconds > 60) {
        	if (seconds % 60 < 10) {
        		num_info.append(Math.round(seconds/60)).append(":").append("0").append(seconds % 60).append("\n");
        	} else {
            	num_info.append(Math.round(seconds/60)).append(":").append(seconds % 60).append("\n");
        	}
        } else {
        	num_info.append(seconds).append("\n");
        }
        num_info.append("\n")
        .append(game.mapFileName()).append("\n")
        .append(game.mapWidth() + " by " + game.mapHeight());
        
        
        game.drawTextScreen(540, 25, info.toString());		//NUMBERS
        game.drawTextScreen(570, 25, num_info.toString());	//
    }
    
    public void onUnitDestroy(Unit u) {
		for (EnemyMemoryChunk emc : enemyMemory) {
			if (emc.getUnit().getID() == u.getID()) {
				enemyMemory.remove(emc);
			}
		}
		enemyUnits.remove(u);
    }
	
	public void onStart() {
		seconds = 0;
		//chokes = null;

		ourBase = self.getStartLocation();
		ourBasePosition = ourBase.toPosition();
		ourBaseRegion = BWTA.getRegion(ourBasePosition);
		
		//Find all possible enemy main base locations
		for (TilePosition b : game.getStartLocations()) {
			if (ourBase != null) {
				if (!b.equals(ourBase)) {
					possibleBases.add(b);
				}
			}			
		}
		
		//Find all possible expo locations
		for (BaseLocation b : BWTA.getBaseLocations()) {
			if (!b.isStartLocation()) {
				possibleExpos.add(b.getTilePosition());
			}
		}
		
		//If two player map -> we know where the enemy *should* be
		if (possibleBases.size() == 1) {
			enemyBase = possibleBases.get(0);
			enemyBasePosition = enemyBase.toPosition();
			enemyBaseRegion = BWTA.getRegion(enemyBasePosition);
		}
		
		//Find my natural expansion and enemy expansion
		int ourClosest = 9999999;
		int enemyClosest = 9999999;
		for (BaseLocation bl : BWTA.getBaseLocations()) {
			if (!bl.getTilePosition().equals(ourBase) && bl.getMinerals().size() > 0) {
				if (bl.getGroundDistance(BWTA.getNearestBaseLocation(ourBase)) < ourClosest  
						&& bl.getGeysers().size() > 0) {
					ourClosest = (int) bl.getGroundDistance(BWTA.getNearestBaseLocation(ourBase));
					ourNatExpo = bl.getTilePosition();
					ourNatExpoPosition = ourNatExpo.toPosition();
					ourNatExpoRegion = BWTA.getRegion(ourNatExpoPosition);
					
				}
			}				
			if (enemyBase != null) {
				if (!bl.getTilePosition().equals(enemyBase) && bl.getMinerals().size() > 0) {
					if (bl.getGroundDistance(BWTA.getNearestBaseLocation(enemyBase)) < enemyClosest  
							&& bl.getGeysers().size() > 0) {
						enemyClosest = (int) bl.getGroundDistance(BWTA.getNearestBaseLocation(enemyBase));
						enemyNatExpo = bl.getTilePosition();
						enemyNatExpoPosition = enemyNatExpo.toPosition();
						enemyNatExpoRegion = BWTA.getRegion(enemyNatExpoPosition);
					}
				}
			}			
		}	
		//Find all chokepoints on the map, and determine which one to build a bunker at			
		for (Chokepoint c : BWTA.getChokepoints()) {
			chokes.add(c);
		}
	}
	
	public void onFrame() {		
		int pdiam = 16;
		game.drawCircleMap(ourBasePosition, pdiam, Color.Cyan);
        game.drawTextMap(ourBasePosition, "ourBase");
		game.drawCircleMap(ourNatExpoPosition, pdiam, Color.Cyan);
        game.drawTextMap(ourNatExpoPosition, "ourNatExpo");
        
        if (enemyBasePosition != null) {
            game.drawCircleMap(enemyBasePosition, pdiam, Color.Red);
            game.drawTextMap(enemyBasePosition, "enemyBase");
        }
        if (enemyNatExpoPosition != null) {
    		game.drawCircleMap(enemyNatExpoPosition, pdiam, Color.Red);
            game.drawTextMap(enemyNatExpoPosition, "enemyNatExpo");
        }
        
        for (Chokepoint c : chokes) {
        	game.drawCircleMap(c.getCenter(), 4*32, Color.White);
        }
        
        info = new StringBuilder("Info:\n");
        num_info = new StringBuilder("\n");
        
		seconds = (game.getFrameCount() - game.getLatencyFrames())/50*(5/3);
        
        debugOverlay();
        buildingGridOverlay();
		
		boolean newUnit = true;
		
		enemyUnits.clear();
		
		for (Unit u : game.enemy().getUnits()) {
			enemyUnits.add(u);
		}
		
		//game.drawTextScreen(300, 315, String.valueOf(enemyUnits.size()));
				
		for (Unit u : enemyUnits) {
			Position lastKnownPosition = null;
			
			if (u.isVisible()) {
				lastKnownPosition = u.getPosition();
			}

			//Update every unit that is visible if it matches with our memory
			for (EnemyMemoryChunk emc : enemyMemory) {
				//IF there is an enemy unit that matches with one in our memory
				if (emc.getUnit() == u) {
					//IF VISIBLE
					if (u.isVisible()) {
						//Update it's position to the one that it's in
						if (emc.getPosition() != u.getPosition() && u.getPosition() != null && u.getPosition().getX() != 0)  {
							emc.setPosition(u.getPosition());
						}
						lastKnownPosition = u.getPosition();
					}
					//IF NOT VISIBLE
					else if (!u.isVisible()){
						//Retrieve the location of it from our memory
						lastKnownPosition = emc.getPosition();
					}
				}	
			}
			
			
			// check if we have it's position in memory and add it if we don't
			if (lastKnownPosition != null) {
				if (enemyMemory.size() == 0) {
					enemyMemory.add(new EnemyMemoryChunk (lastKnownPosition, u, u.getType()));
				} else {
					for (int i = 0; i < enemyMemory.size(); i++) {
						if (enemyMemory.get(i).getUnit() == u) {
							newUnit = false;
						}
					}
					if (newUnit) {
						enemyMemory.add(new EnemyMemoryChunk (u.getPosition(), u, u.getType()));
					}
					newUnit = true;
				}	
			}
		}
		
		EnemyMemoryChunk toRemove = null;
		
		for (EnemyMemoryChunk emc : enemyMemory) {
			Position p = emc.getPosition();
			Unit emu = emc.getUnit();
			UnitType t = emc.getType();
			Position tl = new Position(p.getX() - t.width()/2, p.getY() - t.height()/2);
			Position br = new Position(p.getX() + t.width()/2, p.getY() + t.height()/2);
			
			//debug
			if (t.isBuilding()) {
				game.drawBoxMap(tl, br, Color.White);
			} else if (t.isWorker()) {
				game.drawBoxMap(tl, br, Color.Yellow);
			} else {
				game.drawBoxMap(tl, br, Color.Red);
			}
			game.drawTextMap(tl, emc.toString());

			//TilePosition corresponding to our remembered Position
			TilePosition p_tp = new TilePosition(p.getX()/32, p.getY()/32);
			
			//IF that tile is currently visible to us, remove the memory chunk
			if (game.isVisible(p_tp) && !emu.exists()) {
				toRemove = emc;
			}
		}
		//remove the memory chunk if there is one to remove
		if (toRemove != null) {
			enemyMemory.remove(toRemove);
		}
	}
}
