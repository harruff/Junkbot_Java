import java.util.*;
import bwapi.*;
import bwta.*;

public class SquadManager {
	private Game game;
	private Player self;
	private ArrayList<EnemyMemoryChunk> enemyMemory = new ArrayList<EnemyMemoryChunk>();
	private ArrayList<Chokepoint> chokes = new ArrayList<Chokepoint>();
	private RepairManager rm;

	private TilePosition enemyBase = null, ourBase = null, ourNatExpo = null, enemyNatExpo = null;
	//private ArrayList<Position> enemyUnitPositions = null;
	//private ArrayList<Unit> 	enemyUnits= null;
	private Position attackHere = null, retreatHere = null;
	//private int totalSelfSupply = 0, totalEnemySupply = 0;
	private boolean goHard = false;
	//private boolean rush = false;
	private ArrayList<Unit> rines = new ArrayList<Unit>();
	private ArrayList<Unit> medics = new ArrayList<Unit>();
	private ArrayList<Unit> fbats = new ArrayList<Unit>();
	private ArrayList<Unit> ghosts = new ArrayList<Unit>();
 	private ArrayList<Unit> vults = new ArrayList<Unit>();
	private ArrayList<Unit> tanks = new ArrayList<Unit>();
	private ArrayList<Unit> gols = new ArrayList<Unit>();
	private ArrayList<Unit> wraiths = new ArrayList<Unit>();
	private ArrayList<Unit> valks = new ArrayList<Unit>();
	private ArrayList<Unit> drops = new ArrayList<Unit>();
	private ArrayList<Unit> cruisers = new ArrayList<Unit>();	
	private ArrayList<Unit> scvs = new ArrayList<Unit>();	
	
	private ArrayList<Unit> squad = new ArrayList<Unit>();
	private ArrayList<Unit> mines = new ArrayList<Unit>();
	
	private boolean bio = false;
    
    public void setGame(Game g) {
		this.game = g;
		this.self = game.self();
	}
    
    public void setBio() {
    	bio = true;
    }
    
    public void setOurInformation(TilePosition ob, TilePosition one) {
		ourBase = ob;
		ourNatExpo = one;
	}
    
    public void setEnemyInformation(TilePosition eb, TilePosition ene) {
		enemyBase = eb;
    	enemyNatExpo = ene;
    }
    
    public void setRepairManager(RepairManager rmFromBaseManager) {
    	rm = rmFromBaseManager;
    	if (squad.size() > 0) {
        	rm.setWatchlist(squad);
    	}
    }
    
    public void setChokes(ArrayList<Chokepoint> cp) {
    	chokes = cp;
    }
    
    public void updateEnemyMemory(ArrayList<EnemyMemoryChunk> em) {
    	enemyMemory = em;
    }
    
    public void clear() {
    	rines.clear();
    	medics.clear();
    	fbats.clear();
    	ghosts.clear();
    	vults.clear();
    	tanks.clear();
    	gols.clear();
    	wraiths.clear();
    	valks.clear();
    	drops.clear();
    	cruisers.clear();
    }
    
    public void addSCV(Unit r) {scvs.add(r); squad.add(r);}
	public void removeSCV(Unit r) {scvs.remove(r); squad.remove(r);}	
	public Unit getSCV(int i) {return scvs.get(i);}
	public int sizeSCV() {return scvs.size();}	
    
    public void addRine(Unit r) {rines.add(r); squad.add(r);}
	public void removeRine(Unit r) {rines.remove(r); squad.remove(r);}	
	public Unit getRine(int i) {return rines.get(i);}
	public int sizeRine() {return rines.size();}	
	
	public void addMedic(Unit m) {medics.add(m); squad.add(m);}
	public void removeMedic(Unit m) {medics.remove(m); squad.remove(m);}	
	public Unit getMedic(int i) {return medics.get(i);}
	public int sizeMedic() {return medics.size();}
	
	public void addVult(Unit v) {vults.add(v); squad.add(v);}
	public void removeVult(Unit v) {vults.remove(v); squad.remove(v);}	
	public Unit getVult(int i) {return vults.get(i);}
	public int sizeVult() {return vults.size();}
	
	public void addTank(Unit t) {tanks.add(t); squad.add(t);}
	public void removeTank(Unit t) {tanks.remove(t); squad.remove(t);}	
	public Unit getTank(int i) {return tanks.get(i);}
	public int sizeTank() {return tanks.size();}
	
	public void addGol(Unit t) {gols.add(t); squad.add(t);}
	public void removeGol(Unit t) {gols.remove(t); squad.remove(t);}	
	public Unit getGol(int i) {return gols.get(i);}
	public int sizeGol() {return gols.size();}
	
	public Position getSpiderMinePos(Unit v) {
		//Find the closest chokepoint
		int closestChokeDist = 50*32;
		Position closestChokePos = null;
		for (Chokepoint c : chokes) {
			if (enemyBase != null) {
				if (v.getDistance(c.getCenter()) < closestChokeDist) {
					closestChokePos = c.getCenter();
					closestChokeDist = v.getDistance(c.getCenter());
				}
			} else {
				if (v.getDistance(c.getCenter()) < closestChokeDist) {
					closestChokePos = c.getCenter();
					closestChokeDist = v.getDistance(c.getCenter());
				}
			}
			
		}
		
		//See if there is a mine at specific spots
		Position layHere = null;
		if (closestChokePos != null) {
			//Check the center
			boolean mineHere = false;
			for (Unit mine : mines) {
				if (mine.getPosition().equals(closestChokePos)) {
					mineHere = true;
				}
			} 
			//IF there is no mine in the center
			if (!mineHere) {
				layHere = closestChokePos;
			//ELSE no mine in center
			} else {
				//Check the inner shell
				int numMines = 6;
				int anglePerSector = 360/numMines;
				int radius = (1*32);
				
				for (int i = 0; i < numMines; i++) {
					int angleFromOrigin = anglePerSector*i;
					int x = closestChokePos.getX() + ((int) Math.cos(angleFromOrigin)*radius);
					int y = closestChokePos.getY() + ((int) Math.sin(angleFromOrigin)*radius);
					Position temp = new Position(x, y);
					
					for (Unit mine : mines) {
						if (mine.getPosition().equals(temp)) {
							mineHere = true;
						}
					}
					if (!mineHere) {
						layHere = temp;
						break;
					//ELSE no mine in center
					}
				}
				/*
				if (layHere != null) {
					//Check the inner shell
					numMines = 12;
					anglePerSector = 360/numMines;
					radius = 2*32;
					
					for (int i = 0; i < numMines; i++) {
						int angleFromOrigin = anglePerSector*i;
						int x = closestChokePos.getX() + ((int) Math.cos(angleFromOrigin)*radius);
						int y = closestChokePos.getY() + ((int) Math.sin(angleFromOrigin)*radius);
						Position temp = new Position(x, y);
						
						for (Unit mine : mines) {
							if (mine.getPosition().equals(temp)) {
								mineHere = true;
							}
						}
						if (!mineHere) {
							layHere = temp;
							break;
						//ELSE no mine in center
						}
					}
				}
			*/
			}
		}
		
		if (layHere != null) {
			return layHere;
		} else {
			return layHere;
		}
	}
    
    public void onFrame() {

    	if (rm != null) {
        	rm.onFrame();
        	Color color_rep = Color.Orange;
        	int radius = 12;
        	rm.debug(radius, color_rep, false);
    	}
    	
    	mines.clear();
    	for (Unit s : game.getAllUnits()) {
    		if (s.getType() == UnitType.Terran_Vulture_Spider_Mine) {
    			mines.add(s);
    		}
    	}
    	//game.drawTextScreen(0, 0, String.valueOf(mines.size()));
    	
    	Position closest = null;
    	if (enemyMemory.size() > 0 && !bio) {
    		for (EnemyMemoryChunk emc : enemyMemory) {
    			Position emcPos = emc.getPosition();
    			UnitType emcType = emc.getType();
    			boolean foundThreat = false;
    			boolean foundWorker = false;
    			TilePosition emcPosToTP = emcPos.toTilePosition();
    			
    			//IF the memory position is within bounds of the map, THEN
    			if (emcPos.getX() < game.mapWidth()*32 && emcPos.getY() < game.mapHeight()*32) {
    				//IF the memory tile position is NOT visible to us, THEN
    				if (!game.isVisible(emcPosToTP)) {
    					if (emcType != UnitType.Zerg_Egg 
    							&& emcType != UnitType.Zerg_Larva
    							&& emcType != UnitType.Zerg_Overlord
    							&& emcType != UnitType.Protoss_Observer) {
							if (!emcType.isBuilding() && !emcType.isWorker()) {
								if (closest == null || ourBase.getDistance(emcPos.toTilePosition()) < ourBase.getDistance(closest.toTilePosition())) {
									closest = emcPos;
								}
								foundThreat = true;
							} else if (emcType.isWorker() && !foundThreat) {
								attackHere = emcPos;
								foundWorker = true;
							} else if (emcType.isBuilding() && !foundWorker) {
								attackHere = emcPos;
							}
    					}
    				}
    			} 
    		}
    		if (closest != null) {
    			attackHere = closest;
    		}
    	}
    	
    	if (enemyNatExpo != null) {
    		if (closest != null) {
    			if (ourBase.getDistance(enemyNatExpo) < ourBase.getDistance(closest.toTilePosition())) {
    	        	attackHere = enemyNatExpo.toPosition();
    			}
    		} else {
    			attackHere = enemyNatExpo.toPosition();
    		}
    	} else {
    		attackHere = ourNatExpo.toPosition();
    	}

		retreatHere = ourBase.toPosition();
		
		//goHard = true; //FOR DEBUG PURPOSES
		
		if (squad.size() >= 16 || goHard) {
			goHard = true;
			if (squad.size() < 10) {
				goHard = false;
			}
			if (goHard) {
				attackHere = enemyBase.toPosition();
				retreatHere = ourBase.toPosition();
				
				if (enemyMemory.size() > 0) {
		    		for (EnemyMemoryChunk emc : enemyMemory) {
		    			Position emcPos = emc.getPosition();
		    			UnitType emcType = emc.getType();
		    			boolean foundThreat = false;
		    			boolean foundWorker = false;
		    			TilePosition emcPosToTP = emcPos.toTilePosition();
		    			
		    			//IF the memory position is within bounds of the map, THEN
		    			if (emcPos.getX() < game.mapWidth()*32 && emcPos.getY() < game.mapHeight()*32) {
		    				//IF the memory tile position is NOT visible to us, THEN
		    				if (!game.isVisible(emcPosToTP)) {
		    					//IF the memory type is neither a building nor worker
		    					if (emcType != UnitType.Zerg_Egg 
		    							&& emcType != UnitType.Zerg_Larva
		    							&& emcType != UnitType.Zerg_Overlord
		    							&& emcType != UnitType.Protoss_Observer) {
		    						if (!emcType.isBuilding() && !emcType.isWorker()) {
				    					attackHere = emcPos;
				    					foundThreat = true;
				    				} 
			    					//ELSE IF the memory type is a worker and there is no threat
			    					else if (emcType.isWorker() && !foundThreat) {
				    					attackHere = emcPos;
				    					foundWorker = true;
				    				} 
			    					//ELSE IF the memory type is a building and there is no worker
			    					else if (emcType.isBuilding() && !foundWorker) {
				    					attackHere = emcPos;
				    				}
		    					}
		    				}
		    			} 
		    		}
		    	}
			}	
		}
		
		//SQUAD REPAIR MANAGER!!!!!!!
		//OPTIMIZE
		if (rm != null && rm.getRepairmen().size() > 0) {
			for (Unit r : rm.getRepairmen()) {
				
				ArrayList<Unit> closeUnits = new ArrayList<Unit>(r.getUnitsInRadius(15*32));
				Unit closestThreat = null;
				int closestThreatDist = 50*32;
				
				//find the closest enemy that isn't an egg, larva, or overlord
				for (Unit e : closeUnits) {
					if (e.isVisible() && e.getPlayer() == game.enemy()) {
						//2nd Priority - Military units
						if ((e.getType() != UnitType.Zerg_Egg 
						&& e.getType() != UnitType.Zerg_Larva 
						&& e.getType() != UnitType.Protoss_Observer
						&& !e.getType().isBuilding()
						&& !e.getType().isWorker())
						&& closestThreatDist > r.getDistance(e.getPosition())) {
							closestThreatDist = r.getDistance(e.getPosition());
							closestThreat = e;
						}	
					}
				}
				
				if (rm.getRepairChunks().size() > 0) {
					for (RepairChunk rc : rm.getRepairChunks()) {
						if (rc.getRepairman().getID() == r.getID()) {
							if (rc.getRepairTarget().getType() == UnitType.Terran_Siege_Tank_Siege_Mode && !r.isUnderAttack()) {
								if (!r.isRepairing() && !r.isBeingHealed()) {
									r.repair(rc.getRepairTarget());
								}
							} else {
								if (closestThreat != null && closestThreatDist < 14*32) {
									if ((!r.isMoving() || (!r.getOrderTargetPosition().equals(retreatHere) && r.isMoving()))) {
										r.move(retreatHere);
									}
									
								} else if (!r.isRepairing()){
									if (r.isBeingHealed() && rc.getRepairTarget().getType() != UnitType.Terran_SCV) {
										if (!r.isHoldingPosition()) {
											r.holdPosition();
										}
									} else if (!r.isBeingHealed()) {
										r.repair(rc.getRepairTarget());
									}
								}
							}
						}
					}
				} else {
					boolean squadHasMechanical = false;
					for (Unit m : squad) {
						if (m.getType().isMechanical() && m.isCompleted()) {
							squadHasMechanical = true;
							break;
						}
					}
					if (squadHasMechanical) {
						if (closestThreat != null && closestThreatDist < 14*32) {
							if (!r.isMoving() || (!r.getOrderTargetPosition().equals(retreatHere) && r.isMoving())) {
								r.move(retreatHere);
							}	
						} else {
							if (r.getDistance(attackHere) >= 14*32) {
								if (!r.isMoving() || (!r.getOrderTargetPosition().equals(attackHere) && r.isMoving())) {
									r.move(attackHere);
								}
							} else if (r.getDistance(attackHere) < 12*32) {
								if (!r.isMoving() || (!r.getOrderTargetPosition().equals(retreatHere) && r.isMoving())) {
									r.move(retreatHere);
								}
							} else {
								if (!r.isHoldingPosition()) {
									r.holdPosition();
								}	
							}	
						}
					}
				}	
			}	
		}
		
    	// WHOLE SQUAD
		for (Unit u : squad) {
			
			//game.drawTextMap(u.getPosition(), String.valueOf(df.format(u.getAngle()/6.283)));
			
	    	int maxDist = 15*32; //15 tiles
	    	
	    	Unit closestSunk = null;
			Unit closestAirThreat = null;
			Unit closestGroundThreat = null;
			//Unit closestCloakedUnit = null;
			Unit closestWorker = null;
			Unit closestBuilding = null;
			Unit attackThis = null;
			
			int closestSunkDist = 50*32;
			int closestAirThreatDist = 50*32;
			int closestGroundThreatDist = 50*32;
			int closestWorkerDist = 50*32;
			int closestBuildingDist = 50*32;
			int attackThisDist = 50*32;
			
			boolean kite = true;
			//int sunkenCount = 0;
			boolean retreat = false;
			int marinesClose = 0;
			//int medicsClose = 0;
			//int vultsClose = 0;
			//int golsClose = 0;

			ArrayList<Unit> closeUnits = new ArrayList<Unit>(u.getUnitsInRadius(maxDist));
			Unit closeBunker = null;

			
			for (Unit o : closeUnits) {
				if (o.getPlayer() == self) {
					if (o.getType() == UnitType.Terran_Bunker) {closeBunker = o;}
					//else if (o.getType() == UnitType.Terran_Medic) {medicsClose++;}
					else if (o.getType() == UnitType.Terran_Marine) {marinesClose++;}
					//else if (o.getType() == UnitType.Terran_Vulture) {vultsClose++;}
					//else if (o.getType() == UnitType.Terran_Goliath) {golsClose++;}
				}
				//totalSelfSupply = medicsClose + marinesClose + vultsClose + golsClose;
			}

			
			//find the closest enemy that isn't an egg, larva, or overlord
			for (Unit e : closeUnits) {
				if (e.isVisible() && e.getPlayer() == game.enemy()) {
					
					//1st Priority - Lurkers
					if (e.getType() == UnitType.Terran_Bunker || e.getType() == UnitType.Zerg_Sunken_Colony || e.getType() == UnitType.Protoss_Photon_Cannon || (e.getType() == UnitType.Zerg_Lurker && e.isBurrowed() && !e.isDetected())) {
						//sunkenCount++;
						if (closestSunkDist > u.getDistance(e.getPosition())) {
							closestSunkDist = u.getDistance(e.getPosition());
							closestSunk = e;
						}	

					}	
					//2nd Priority - Military units
					else if (e.getType() != UnitType.Zerg_Egg 
					&& e.getType() != UnitType.Zerg_Larva 
					&& !e.getType().isBuilding()
					&& !e.getType().isWorker()) {
						if ((e.isCloaked() && e.isDetected()) || !e.isCloaked()) {
							if (closestAirThreatDist > u.getDistance(e.getPosition()) && e.isFlying())  {
								closestAirThreatDist = u.getDistance(e.getPosition());
								closestAirThreat = e;
							} else if (closestGroundThreatDist > u.getDistance(e.getPosition()) && !e.isFlying()){
								boolean myUnitInWay = false;
								if (u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
									for (Unit s : squad) {
										if (u.getID() == s.getID()) {
											continue;
										} else {
											if (s.getDistance(e.getPosition()) < 1*32) {
												myUnitInWay = true;
											}
										}
									}
								}
								if (!myUnitInWay) {
									closestGroundThreatDist = u.getDistance(e.getPosition());
									closestGroundThreat = e;
								}
							}		
						}				
					}	
					//4th Priority - Workers
					else if (e.getType().isWorker() || closestWorkerDist > u.getDistance(e.getPosition())) {
						closestWorkerDist = u.getDistance(e.getPosition());
						closestWorker = e;
					}
					// the rest
					else if (closestBuildingDist > u.getDistance(e.getPosition())){
						closestBuildingDist = u.getDistance(e.getPosition());
						closestBuilding = e;
					}
				}
			}
			
			//IS THIS UNIT BEING HEALED 
			boolean isBeingRepaired = false;
			if (closestGroundThreat == null && closestAirThreat == null) {
				for (RepairChunk rc : rm.getRepairChunks()) {
					if (u.getID() == rc.getRepairTarget().getID()) {
						if (u.getDistance(rc.getRepairman()) > 4*32) {
							u.move(rc.getRepairman().getPosition());
						} else if (!u.isHoldingPosition()) {
							u.holdPosition();
						}
						isBeingRepaired = true;
						break;
					}
				}
			}
			if (isBeingRepaired) {
				continue;
			}
			
			//DECIDE WHERE TO ATTACK/WAIT
			game.drawCircleMap(attackHere, 32, Color.White);
			game.drawCircleMap(attackHere, 31, Color.Red);
			game.drawCircleMap(attackHere, 30, Color.White);
			
			game.drawCircleMap(retreatHere, 32, Color.White);
			game.drawCircleMap(retreatHere, 31, Color.Cyan);
			game.drawCircleMap(retreatHere, 30, Color.White);
			
			//Determine attack target based on unit type (Done to reduce confusion of focus between units)
			
			//***************************SEIGED_TANK**************************************
			if (u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
				if (closestSunk != null) {
					attackThis = closestSunk;
				}
				if (closestGroundThreat != null && closestGroundThreatDist < 12*32) {
					attackThis = closestGroundThreat;
				}
			} 
			//***************************GOLIATH*****************************************
			else if (u.getType() == UnitType.Terran_Goliath) {
				if (closestAirThreat != null) {
					attackThis = closestAirThreat;
				}
				if (closestGroundThreat != null && closestGroundThreatDist <= u.getType().groundWeapon().maxRange() && closestAirThreatDist > u.getType().airWeapon().maxRange()) {
					//IF goliaths do not have charon boosters, attack ground units if they are closer
					attackThis = closestGroundThreat;
				}
			}
			//***************************VULTURE*****************************************
			else if (u.getType() == UnitType.Terran_Vulture) {
				if (closestGroundThreat != null) {
					if (closestWorker != null && closestGroundThreat.getDistance(u) > 7*32) {
						attackThis = closestWorker;
						kite = false;
					} else {
						attackThis = closestGroundThreat;
					}
				}
			}
			////***************************ALL_ELSE*****************************************
			else {
				if (closestGroundThreat != null) {
					attackThis = closestGroundThreat;
				}
			}
			
			//IF attackThis is still null, then there must not be a threat
			
			boolean findTarget = true;
			if (attackThis != null) {
				findTarget = false;
			}
			if (findTarget) {
				if (closestWorker != null) {
					attackThis = closestWorker;
					kite = false;
				} else if (closestBuilding != null) {
					attackThis = closestBuilding;
					kite = false;
				}
			}
			
			
			//SIEGE TANK TECH DECISION-MAKING
			//TilePosition corresponding to our remembered Position
			TilePosition attackHere_tp = new TilePosition(attackHere.getX()/32, attackHere.getY()/32);
			
			//Is there a choke that is too close?
			boolean chokeTooClose = false;
			for (Chokepoint c : chokes) {
				if (u.getDistance(c.getCenter()) < 2*32) {
					chokeTooClose = true;
				}
			}
			//FUUUUUUUUUCK
			//Should we siege up?
			if (u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {
				if (attackThis != null && attackThis.isVisible()) {
					//IF sunken is in range - 1 => try to siege and destroy it
					if (u.isSieged()) {
						if (closestGroundThreat != null ) {
							if (closestGroundThreat.getType().groundWeapon().maxRange() <= 2*32 && closestGroundThreatDist < 3*32) { 
								u.useTech(TechType.Tank_Siege_Mode); 
							} else if (u.getDistance(attackThis) > 12*32){
								u.useTech(TechType.Tank_Siege_Mode); 
							}
						}
					}
					else if (!u.isSieged()) {
						if (closestSunkDist <= 12*32 - 16 && !chokeTooClose && closestSunkDist <= closestGroundThreatDist) {
							u.useTech(TechType.Tank_Siege_Mode);
						} 
					} 
					
					//ELSE sunken is not in range => try to kite and play it safe
					else {
						if (u.isSieged()) {
							if (closestGroundThreat != null ) {
								if (closestGroundThreat.getType().groundWeapon().maxRange() <= 2*32 && closestGroundThreatDist < 12*32) { 
									u.useTech(TechType.Tank_Siege_Mode); 
								}
							}
							else if (attackThisDist > 12*32) {
								u.useTech(TechType.Tank_Siege_Mode); 
							} 
						}
					}
				}
				else {
					//IF we have an unsieged tank AND there is not a choke nearby AND attackHere is less than siege max range - 2 tiles AND attackHere is visible, THEN siege
					if (!u.isSieged() && !chokeTooClose && u.getDistance(attackHere) < 2*32 && game.isVisible(attackHere_tp)) {
						u.useTech(TechType.Tank_Siege_Mode); 
					}
					//IF we have a sieged tank and attackHere is greater than siege max range, THEN unsiege.
					else if (u.isSieged() && (u.getDistance(attackHere) > 10*32) ) {
						u.useTech(TechType.Tank_Siege_Mode);
					}
				}
			}
			
			/*
			if (attackThis != null) {
				game.drawTextMap(new Position(u.getPosition().getX() + u.getType().width(),  u.getPosition().getY() + u.getType().height()), attackThis.getType().toString());
				game.drawLineMap(u.getPosition(), attackThis.getPosition(), Color.Red);
			}
			*/
			
			//GENERAL KITE BEHAVIOR
			if (kite) {
				if (u.getType() == UnitType.Terran_Marine || u.getType() == UnitType.Terran_Vulture || u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || u.getType() == UnitType.Terran_Goliath) {
					//KITE: if enemy is within max weapon range and the unit has shot, run away
					//		OR if marine health is less and there is an enemy, run away
					//		OR we should run from sunken
					if (closeBunker != null && u.getType() == UnitType.Terran_Marine) {
						if (u.getOrder() == null) {
							u.rightClick(closeBunker);
						}
						else if (!u.getOrderTargetPosition().equals(closeBunker.getPosition())) {
							u.rightClick(closeBunker);
						}
						continue;
					} else {
						//SOMETHING HERE IS VERY VERY WRONG, *32 SHOULD NOT BE THERE 
						if ((/*attackThisDist < u.getType().groundWeapon().maxRange()*32 && */u.getGroundWeaponCooldown() != 0)
						|| (u.getType() == UnitType.Terran_Marine && u.getHitPoints() < (u.getType().maxHitPoints()*3/8) && attackThis != null)) {
							if (u.getOrder() != Order.Move) {
								u.move(retreatHere);
							} else if (!u.getOrderTargetPosition().equals(attackHere) && attackThis == null) {
								u.attack(attackHere);
							} else if (!u.getOrderTargetPosition().equals(retreatHere) && attackThis != null) {
								u.move(retreatHere);
							}
							continue;
						}
					}
				}
				
				if (u.getType() == UnitType.Terran_Medic) {
					//KITE: OR if medics health is less that 25 and there is an enemy, run away
					//		OR we should run from sunken
					//		OR if there are no marines close and there is an enemy
					if ((u.getHitPoints() < 25 && attackThis != null)
					|| (retreat == true)
					|| (marinesClose == 0 && attackThis != null)) {
						if (u.getOrder() != Order.Move) {
							u.move(retreatHere);
						}
						continue;
					}
				}
			}
			
			//ATTACK?
			//If there is an enemy and this unit can attack
			if (attackThis != null && attackThis.isVisible()) {
				if (closestSunkDist <= 9*32 && !bio) {
					if (u.getOrder() != Order.Move) {
						u.move(retreatHere);
					}
					else if (!u.getOrderTargetPosition().equals(retreatHere)) {
						u.move(retreatHere);
					}
				} else if (closestSunkDist > 10*32) {
					//Use stim-pack
					if (!u.isStimmed() && u.getHitPoints() == u.getType().maxHitPoints() && u.canUseTech(TechType.Stim_Packs)) {
						u.useTech(TechType.Stim_Packs);
					}
					
					//FOR MEDICS ONLY
					//DOES A UNIT NEED TO BE HEALED? *******GOT MOVED, MIGHT NOT WORK
					if (u.getType() == UnitType.Terran_Medic) {
						Unit needsHeal = null;
						for (Unit nh : closeUnits) {
							if (nh.getPlayer() == self && !nh.getType().isBuilding()) {
								if (attackThisDist > u.getDistance(nh.getPosition())
								&& nh.getHitPoints() != nh.getType().maxHitPoints()
								&& !nh.isBeingHealed()) {
									needsHeal = nh;
								}
							}
						}
						if (needsHeal != null) {
							if (u.getOrderTarget() == null) {
								u.useTech(TechType.Healing, needsHeal);
							}
							continue;
						}
					}		
						
					/*
					 * JUST FOR REFERENCE
					//Continue attacking if already attacking a unit
					if (u.getOrderTarget() == null) {
						u.attack(attackThis);
						continue;
					}
					else if (u.getOrderTarget().getID() != attackThis.getID()) {
						u.attack(attackThis);
						continue;
					}
					
					//Attack the nearest enemy
					if (u.getOrder() != Order.AttackMove 
							&& u.getOrder() != Order.AttackTile
							&& u.getOrder() != Order.AttackUnit) {
						u.attack(attackThis.getPosition());
					}
					*/
					
					
					//Lay mines
					if (u.getType() == UnitType.Terran_Vulture /*&& u.getSpiderMineCount() > 0 && u.canUseTech(TechType.Spider_Mines)*/) {
						/*
						Position layHere = getSpiderMinePos(u);
						if (layHere != null) {
							if (u.canUseTech(TechType.Spider_Mines, layHere) && !u.isAttacking()) {
								u.useTech(TechType.Spider_Mines, layHere);
							}
						} else */
						
						if (closestGroundThreatDist >= 4*32 && closestGroundThreatDist < 5*32) {
							if (u.getOrder() != Order.Patrol) {
								//int dist = u.getDistance(attackThis);		//distance in pixels
								double fift = 2*Math.PI*(15/360);			//15 degrees in radians
								double myAngle = u.getAngle();
								
								double patrolHereAngle = myAngle + fift;
								if (myAngle + fift > 2*Math.PI) {
									patrolHereAngle -= 2*Math.PI;
								}
								
								int abs_x = Math.abs(u.getPosition().getX() - attackThis.getX());
								int abs_y = Math.abs(u.getPosition().getY() - attackThis.getY());
								
								int ang_x = ((int)Math.cos(patrolHereAngle))*abs_x;
								int ang_y = ((int)Math.sin(patrolHereAngle))*abs_y;
								
								int atk_x = u.getX() + ang_x;
								int atk_y = u.getY() + ang_y;
								
								Position patrolHere = new Position(atk_x, atk_y);
								game.drawLineMap(u.getPosition(), patrolHere, Color.Red);
								u.patrol(patrolHere);
							}
						} else {
							
							//Continue attacking if already attacking a unit
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
					
					//ONLY ATTACK IF target is greater or equal to the unit's max range
					else {
						if (closestGroundThreatDist > u.getType().groundWeapon().maxRange() - 16  || !u.isMoving()) {
							if (u.getOrderTarget() == null) {
								u.attack(attackThis);
								continue;
							}
							else if (u.getOrderTarget().getID() != attackThis.getID()) {
								u.attack(attackThis);
								continue;
							}
							
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
			else { //no enemy in vision
				//IF ground weapon cooldown == 0
				if (u.getGroundWeaponCooldown() == 0) {
					//If not moving and not at the place it should be, move towards enemy base
					if (u.getPosition().getDistance(attackHere) > (4*32)) {
						if (u.getOrder() == null) {
							u.move(retreatHere);
						} else if (attackThis == null && !u.getOrderTargetPosition().equals(attackHere)) {
							u.move(attackHere);
						} else if (attackThis != null && !u.getOrderTargetPosition().equals(retreatHere)) {
							u.move(retreatHere);
						}
					}
				}
			}
			continue;	
		}
    }
}
