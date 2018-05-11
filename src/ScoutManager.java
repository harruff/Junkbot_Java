import java.util.*;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;

public class ScoutManager {
	private ArrayList<ScoutChunk> scoutChunks = new ArrayList<ScoutChunk>();
	private ArrayList<Unit> scouts = new ArrayList<Unit>();
	private ArrayList<Position> startLocations = new ArrayList<Position>();
	private ArrayList<Position> baseLocations = new ArrayList<Position>();
	private int maxScouts;
	private int mainScoutVector = 1;
	
	private Game game;
	
	private TilePosition enemyBase = null, ourBase = null, ourNatExpo = null, enemyNatExpo = null;
	
	public void setGame(Game g) {
		this.game = g;
	}
	
    public void setOurInformation(TilePosition ob, TilePosition one) {
		ourBase = ob;
		ourNatExpo = one;
		findPossibleBases();
	}
    
    public void setEnemyInformation(TilePosition eb, TilePosition ene) {
		enemyBase = eb;
    	enemyNatExpo = ene;
    }
	
	public ScoutManager(ArrayList<Unit> s, Game g, int m) {
		game = g;
		scouts = s;
		maxScouts = m;
	}
	
	public void findPossibleBases() {
		for (TilePosition b : game.getStartLocations()) {
			if (ourBase != null) {
				if (!b.equals(ourBase)) {
					startLocations.add(b.toPosition());
				}
			} else {
				startLocations.add(b.toPosition());
			}
		}
		for (BaseLocation b : BWTA.getBaseLocations()) {
			if (!b.isStartLocation()) {
				baseLocations.add(b.getPosition());
			}
		}
		baseLocations.remove(ourNatExpo.toPosition());
	}
	
	public ArrayList<ScoutChunk> getScoutChunks() {return scoutChunks;}
	public ArrayList<Unit> getScouts() {return scouts;}
	public void addScout(Unit s) {scouts.add(s);}
	public void removeScout(Unit s) {scouts.remove(s);}
	
	public TilePosition getEnemyBase() {return enemyBase;}
	public TilePosition getEnemyNatExpo() {return enemyNatExpo;}
	
	public void explore(ScoutChunk sc) {
		Unit s = sc.getScout();
		Position target = sc.getTargetPosition();
		
		if (!s.isMoving() || (s.isMoving() && !s.getOrderTargetPosition().equals(target))) {
			s.move(target);
		}
	}
	
	public void danceAroundMain(ScoutChunk sc) {
		Unit s = sc.getScout();
		Position target = sc.getTargetPosition();
		
		//IF scout is not in target region, THEN 
		if (BWTA.getRegion(s.getPosition()) != BWTA.getRegion(target)) {
			//IF either isn't moving at all or isn't moving to the target, THEN
			if (!s.isMoving() || (s.isMoving() && !s.getOrderTargetPosition().equals(target))) {
				//Move scout to the target
				s.move(target);
			}
		} 
		//ELSE => scout is in target region
		else {
			int leg = 9*32;
			int radius = (int) Math.sqrt(2*Math.pow(leg, 2));
			int x;
			int y;
			
			//Determine new position to move based on target position
			x = 0;
			y = 0;
			switch (mainScoutVector) {
			case 1:
				//NE
				x = target.getX() + leg;
				y = target.getY() + leg;
				break;
			case 2:
				//E
				x = target.getX() + radius;
				y = target.getY();
				break;
			case 3:
				//SE
				x = target.getX() + leg;
				y = target.getY() - leg;
				break;
			case 4:
				//S
				x = target.getX();
				y = target.getY() - radius;
				break;
			case 5:
				//SW
				x = target.getX() - leg;
				y = target.getY() - leg;
				break;
			case 6:
				//W
				x = target.getX() - radius;
				y = target.getY();
				break;
			case 7:
				//NW
				x = target.getX() - leg;
				y = target.getY() + leg;
				break;
			case 8:
				//N
				x = target.getX();
				y = target.getY() + radius;
				break;
			}
			
			//IF x is out of bounds, correct it
			if (x > game.mapWidth()*32) {
				x = game.mapWidth()*32 - 16;
			} else if (x < 0) {
				x = 16;
			}
			
			//IF y is out of bounds, correct it
			if (y > game.mapHeight()*32) {
				y = game.mapHeight()*32 - 16;
			} else if (y < 0) {
				y = 16;
			}
			
			//Make that new position
			Position mainPos = new Position(x, y);
			game.drawCircleMap(mainPos, 3*32, Color.White);
			game.drawLineMap(mainPos, s.getPosition(), Color.White);
			
			//IF the scout's distance is more than 96 pixels, THEN
			if (s.getDistance(mainPos) > 3*32) {
				//IF BW thinks there is a path for the unit to this spot
				if (s.hasPath(mainPos)) {
					//IF either isn't moving at all or isn't moving to the new target, THEN
					if (!s.isMoving() || (s.isMoving() && !s.getOrderTargetPosition().equals(mainPos))) {
						//Move scout to the new target
						s.move(mainPos);
					}
				}
				//ELSE => scout has no way to get to the target
				else {
					//Increment vector identifier
					mainScoutVector++;
					
					//IF the vector identifier is outside of range, reset to NE
					if (mainScoutVector > 8) {
						sc.setTargetScouted(true);
						mainScoutVector = 1;
					}
				}
			} 
			//ELSE => scout is less than or equal to 64 pixels away
			else {
				//Increment vector identifier
				mainScoutVector++;
				
				//IF the vector identifier is outside of range, reset to NE
				if (mainScoutVector > 8) {
					sc.setTargetScouted(true);
					mainScoutVector = 1;
				}
			}
		}
	}
	
	public void onFrame() {
		
		//Remove scoutChunk where the scout died 
		ArrayList<ScoutChunk> sc_toRemove = new ArrayList<ScoutChunk>();
		ArrayList<Unit> s_toRemove = new ArrayList<Unit>();
		for (ScoutChunk sc : scoutChunks) {
			
			if (Math.abs(sc.getScout().getVelocityX()) < .5 && Math.abs(sc.getScout().getVelocityY()) < .5) {
				sc.setFramesSpentNotMoving(sc.getFramesSpentNotMoving() + 1);
			}
			
			if (enemyBase != null) {
				if (sc.getTargetPosition() == enemyBase.toPosition() && sc.getTargetScouted()) {
					sc_toRemove.add(sc);
				}
			}

			if (!sc.getScout().exists()) {
				sc_toRemove.add(sc);
				s_toRemove.add(sc.getScout());
				
				//IF scout died, set enemy base to its target location at time of death
				if (enemyBase == null) {
					enemyBase = sc.getTargetTilePosition();
					int enemyClosest = 9999999;
					for (Position p : baseLocations) {
						if (p.getDistance(BWTA.getNearestBaseLocation(enemyBase)) < enemyClosest) {
							enemyClosest = (int) p.getDistance(BWTA.getNearestBaseLocation(enemyBase));
							enemyNatExpo = p.toTilePosition();
							baseLocations.remove(enemyNatExpo.toPosition());
						}
					}
				}

				continue;
			} 
			
			//IF a specific scout has spent too much time standing still, remove its chunk
			if (sc.getFramesSpentNotMoving() > 500) {
				
			}
			
			//Remove already found scoutChunks
			//IF we can see a target location OR we're successfully scouted a location
			if (sc.getTargetScouted() || sc.getScout().getDistance(sc.getTargetPosition()) < 6*32) {
				
				boolean isStartLocation = false;
				for (Position p : startLocations) {
					if (sc.getTargetPosition() == p) {
						isStartLocation = true;
						break;
					}
				}
				
				if (isStartLocation) {
					startLocations.remove(sc.getTargetPosition());
				}
				
				//IF we haven't found the enemy base yet, THEN
				if (enemyBase == null) {
					//Iterate over all units
					for (Unit e : game.getAllUnits()) {
						//IF the unit is an enemy building and it's within 8 tiles, THEN
						if ((e.getDistance(sc.getScout()) < 8*32 && e.getPlayer() == game.enemy() && e.getType().isBuilding())) {
							//Enemy base has been found
							enemyBase = sc.getTargetTilePosition();
							//Find the natural expo
							if (enemyBase != null) {
								int enemyClosest = 9999999;
								for (Position p : baseLocations) {
									if (p.getDistance(BWTA.getNearestBaseLocation(enemyBase)) < enemyClosest) {
										enemyClosest = (int) p.getDistance(BWTA.getNearestBaseLocation(enemyBase));
										enemyNatExpo = p.toTilePosition();
									}
								}
							}	
						}	
					}
					//continue;
				}
				//ELSE => we have found base
				else {
					boolean isBaseLocation = false;
					for (Position p : baseLocations) {
						if (sc.getTargetPosition() == p) {
							isBaseLocation = true;
							break;
						}
					}
					if (isBaseLocation) {
						baseLocations.remove(sc.getTargetPosition());
					}
				}
				
				//IF we're looking for the main base, but we haven't found it yet, OR
				//IF we're looking for the main base and it's completely scouted, OR
				//IF we're not looking for the main, THEN remove the chunk
				if ((sc.isScoutingMain() && !sc.getTargetScouted() && enemyBase == null) || (sc.isScoutingMain() && sc.getTargetScouted()) || !sc.isScoutingMain()) {
					sc_toRemove.add(sc);
				} 		
			}
		}
		
		for (ScoutChunk sc : sc_toRemove) {
			scoutChunks.remove(sc);
		}
		for (Unit s : s_toRemove) {
			scouts.remove(s);		
		}
		
		//Try to add new scouting chunks
		if (scoutChunks.size() < maxScouts) {
			//IF ENEMYBASE HAS BEEN FOUND
			if (enemyBase != null) {
				//System.out.println("base found");
				Unit scoutToAdd = null;
				for (Unit s : scouts) {
					boolean repeatScout = false;
					for (ScoutChunk sc : scoutChunks) {
						if (s.getID() == sc.getScout().getID()) {
							repeatScout = true;
						}
					}
					if (!repeatScout) {
						scoutToAdd = s;
					}
				}
				if (scoutToAdd != null) {
					Position positionToAdd = null;
					

					for (Position p : baseLocations) {
						boolean repeatLoc = false;
						for (ScoutChunk sc : scoutChunks) {
							if (p.equals(sc.getTargetPosition())) {
								repeatLoc = true;
							}
						}
						if (!repeatLoc) {
							//FIND CLOSEST
							Position closest = null;
							for (Position rednis : baseLocations) {
								if (closest == null || rednis.getDistance(scoutToAdd) < closest.getDistance(scoutToAdd)) {
									closest = rednis;
								}
							}
							positionToAdd = closest;
						} 
					}
					if (positionToAdd != null) {
						scoutChunks.add(new ScoutChunk(scoutToAdd, positionToAdd, game, false));
					}				
				}

			//ELSE => ENEMYBASE HAS NOT BEEN FOUND YET
			} else {
				if (startLocations.size() == 1) {
					for (Position sl : startLocations) {
						enemyBase = sl.toTilePosition();
						if (enemyBase != null) {
							int enemyClosest = 9999999;
							for (Position p : baseLocations) {
								if (p.getDistance(BWTA.getNearestBaseLocation(enemyBase)) < enemyClosest) {
									enemyClosest = (int) p.getDistance(BWTA.getNearestBaseLocation(enemyBase));
									enemyNatExpo = p.toTilePosition();
								}
							}
						}
					}
				} else {
					Unit scoutToAdd = null;
					for (Unit s : scouts) {
						boolean found = false;
						if (scoutChunks.size() > 0) {
							for (ScoutChunk sc : scoutChunks) {
								if (s.getID() == sc.getScout().getID()) {
									found = true;
								}
							}
						}
						if (!found) {
							scoutToAdd = s;
						}
					}
					if (scoutToAdd != null) {
						Position positionToAdd = null;
						for (Position p : startLocations) {
							boolean repeatLoc = false;
							for (ScoutChunk sc : scoutChunks) {
								if (p.equals(sc.getTargetPosition())) {
									repeatLoc = true;
								}
							}
							if (!repeatLoc) {
								Position closest = null;
								for (Position rednis : startLocations) {
									if (closest == null || rednis.getDistance(scoutToAdd) < closest.getDistance(scoutToAdd)) {
										closest = rednis;
									}
								}
								positionToAdd = closest;
							} 
						}
						if (positionToAdd != null) {
							scoutChunks.add(new ScoutChunk(scoutToAdd, positionToAdd, game, true));
						}
					}	
				}					
			}
		}
		
		//MICRO
		for (ScoutChunk sc : scoutChunks) {
			if (sc.isScoutingMain() && !sc.getTargetScouted() && enemyBase == null) {
				explore(sc);
			}
			else if (sc.isScoutingMain() && !sc.getTargetScouted() && enemyBase != null) {
				danceAroundMain(sc);
			} 
			else if (!sc.isScoutingMain() && enemyBase != null){
				explore(sc);
			}
		}	
		
		debug();
	}
	
	public void debug() {
		for (Unit s : scouts) {
			game.drawCircleMap(s.getPosition(), 12, Color.Purple);
		}
		for (ScoutChunk sc : scoutChunks) {
			sc.debug();
		}
	}
}
