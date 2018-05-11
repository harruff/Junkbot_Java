import java.util.*;
import bwapi.*;

public class BaseManager {
	private ArrayList<Base> bases = new ArrayList<Base>();
	private ArrayList<Unit> refineries = new ArrayList<Unit>();
	
	private RepairManager rmForSquad;
	private ScoutManager sm;
	
	//SCVs have 6 roles:
	//private ArrayList<Unit> minSCV = new ArrayList<Unit>();		//1. Gathering minerals default	-- Cyan
	//private ArrayList<Unit> gasSCV = new ArrayList<Unit>();		//2. Gathering gas				-- Green
	private int minSCVTotal = 0;
	private int gasSCVTotal = 0;
	private int repairmenTotal = 0;
	private int buildersTotal = 0;
	//private int militiaTotal = 0;
	
	//private ArrayList<Unit> builders = new ArrayList<Unit>();	//3. Constructing buildings		-- Yellow
	private ArrayList<Unit> scouts = new ArrayList<Unit>();		//4. Scouting					-- Purple
	private ArrayList<Unit> repairmen = new ArrayList<Unit>();	//5. Repairing					-- Orange
	private ArrayList<Unit> militia = new ArrayList<Unit>();	//6. Combat						-- Red
	
	//private ArrayList<Unit> enemies = new ArrayList<Unit>();
	
	ArrayList<Unit> allMinSCVs = new ArrayList<Unit>();
	
	private Game game;
	//private Player self;
	
	private ArrayList<TilePosition> reservedTiles = new ArrayList<TilePosition>();
	private TilePosition enemyBase = null, ourBase = null, ourNatExpo = null, enemyNatExpo = null;
	
	private boolean justAddedBase = false;
	private boolean justFinishedBase = false;
	
	public void setGame(Game g) {
		this.game = g;
		//this.self = game.self();
	}
	
    public void setOurInformation(TilePosition ob, TilePosition one) {
		ourBase = ob;
		ourNatExpo = one;
	}
    
    public void setEnemyInformation(TilePosition eb, TilePosition ene) {
		enemyBase = eb;
    	enemyNatExpo = ene;
    }
    
    public void removeReservedTiles(TilePosition tl, UnitType buildingType) {
    	TilePosition tr = new TilePosition(tl.getX() + buildingType.tileWidth(), tl.getY());
    	TilePosition bl = new TilePosition(tl.getX(), tl.getY() + buildingType.tileHeight());
    	
    	//IF the buildingType can build an addon, THEN
    	if (buildingType == UnitType.Terran_Factory || buildingType == UnitType.Terran_Starport || buildingType == UnitType.Terran_Command_Center) {
    		//Top right and bottom right has two more tiles tacked on to the width
    		tr = new TilePosition(tl.getX() + buildingType.tileWidth() + 2, tl.getY());
    	}
		
		for (int a = tl.getX(); a < tr.getX(); a++) {
    		for (int b = tl.getY(); b < bl.getY(); b++) {
    			TilePosition toRemove = new TilePosition(a, b);
    			boolean foundReserved = false;
    			for (TilePosition reserved : reservedTiles) {
    				if (reserved == toRemove) {
    					foundReserved = true;
    				}
    			}
    			if (foundReserved) {
    				
    			}
    			reservedTiles.remove(toRemove);
    		}
    	}
    }
    
    public void addReservedTiles(TilePosition tl, UnitType buildingType) {
    	TilePosition tr = new TilePosition(tl.getX() + buildingType.tileWidth(), tl.getY());
    	TilePosition bl = new TilePosition(tl.getX(), tl.getY() + buildingType.tileHeight());
    	
    	//IF the buildingType can build an addon, THEN
    	if (buildingType == UnitType.Terran_Factory || buildingType == UnitType.Terran_Starport || buildingType == UnitType.Terran_Command_Center) {
    		//Top right and bottom right has two more tiles tacked on to the width
    		tr = new TilePosition(tl.getX() + buildingType.tileWidth() + 2, tl.getY());
    	}
		
		for (int a = tl.getX(); a < tr.getX(); a++) {
    		for (int b = tl.getY(); b < bl.getY(); b++) {
    			reservedTiles.add(new TilePosition(a, b));
    		}
    	}
    }

	public RepairManager getRepairManagerForSquad() {
		return rmForSquad;
	}
	
	public ScoutManager getScoutManager() {
		return sm;
	}
	
	public void addBase(Base s) {
		bases.add(s);
		if (bases.size() > 1) {
			justAddedBase = true;
		}
	}
	public void removeBase(Unit c) {
		for (Base b : bases) {
			if (b.getCC() == c) {
				bases.remove(b);
			}
		}
	}	
	public ArrayList<Base> getBases() {return bases;}
	public Base getBaseAtIndex(int i) {return bases.get(i);}
	public int sizeBase() {return bases.size();}
	
	public int sizeAllSCV() {return minSCVTotal + gasSCVTotal + buildersTotal + scouts.size() + repairmenTotal + militia.size();}
	
	public void addMinSCV(Unit s) {		
		//ADD TO BASES MinSCV ARRAY
		boolean added = false;
		for (Base b : bases) {
			for (MineralChunk mc : b.getMineralChunks()) {
				for (Unit scv : mc.getMinSCVs()) {
					if (scv.getID() == s.getID()) {
						added = true;
					}
				}
			}
		}
		
		//Add to mineral fields with no scvs first
		if (!added) {
			for (Base b : bases) {
				if (!added) {
					for (MineralChunk mc : b.getMineralChunks()) {			
						if (mc.getMinSCVs().size() < mc.getMaxMinSCVs() - 1) {	
							mc.addMinSCV(s);
							added = true;
							break;
						}
					}
				}	
			}
		}
		
		//IF the scv hasn't been added yet, try to add it to any of them with less than max
		if (!added) {
			for (Base b : bases) {
				if (!added) {
					for (MineralChunk mc : b.getMineralChunks()) {			
						if (mc.getMinSCVs().size() < mc.getMaxMinSCVs()) {	
							mc.addMinSCV(s);
							added = true;
							break;
						}
					}
				}
			}
		}
	}
	
	public void removeMinSCV(Unit s) {	
		//REMOVE FROM BASES MinSCV ARRAY
		for (Base b : bases) {
			for (MineralChunk mc : b.getMineralChunks()) {		
				Unit toRemove = null;
				for (Unit scv : mc.getMinSCVs()) {
					if (scv.getID() == s.getID()) {
						toRemove = scv;
					}
				}
				if (toRemove != null) {
					mc.removeMinSCV(toRemove);
					break;
				}
			}
		}
		
		//REMOVE FROM ALL SCVs
		
		
	}	
	//public Unit getMinSCV(int i) {return minSCV.get(i);}
	//public int sizeMinSCV() {return minSCV.size();}
	
	public void addGasSCV(Unit s) {		
		//ADD TO BASES GasSCV ARRAY
		for (Base b : bases) {
			for (GeyserChunk gc : b.getGeyserChunks()) {			
				if (gc.getGasSCVs().size() < gc.getMaxGasSCVs()) {	
					gc.addGasSCV(s);
					break;
				} 	
			}
		}
	}
	public void removeGasSCV(Unit s) {
		//REMOVE FROM BASES GasSCV ARRAY
		for (Base b : bases) {
			for (GeyserChunk gc : b.getGeyserChunks()) {		
				Unit toRemove = null;
				for (Unit scv : gc.getGasSCVs()) {
					if (scv.getID() == s.getID()) {
						toRemove = scv;
					}
				}
				if (toRemove != null) {
					gc.removeGasSCV(toRemove);
				}
			}
		}
	}	
	//public Unit getGasSCV(int i) {return gasSCV.get(i);}
	//public int sizeGasSCV() {return gasSCV.size();}
	
	public void addBuilder(Unit s) {
		//ADD TO BASES Builders ARRAY
		boolean added = false;
		for (Base b : bases) {
			for (Unit scv : b.getBuilders()) {
				if (scv.getID() == s.getID()) {
					added = true;
				}
			}
		}
		
		//Add to mineral fields with no scvs first
		if (!added) {
			for (Base b : bases) {
				if (!added) {
					b.addBuilder(s);
					added = true;
					break;
				}	
			}
		}
	}
	
	public void removeBuilder(Unit s) {
		for (Base b : bases) {
			Unit toRemove = null;
			for (Unit scv : b.getBuilders()) {
				if (scv.getID() == s.getID()) {
					toRemove = scv;
				}
			}
			if (toRemove != null) {
				b.removeBuilder(toRemove);
				break;
			}
		}
	}	
	
	public void addScout(Unit s) {scouts.add(s);}
	public void removeScout(Unit s) {scouts.remove(s);}	
	public Unit getScout(int i) {return scouts.get(i);}
	public int sizeScout() {return scouts.size();}
	
	public void addRepairman(Unit s) {repairmen.add(s);}
	public void removeRepairman(Unit s) {repairmen.remove(s);}	
	public Unit getRepairman(int i) {return repairmen.get(i);}
	public int sizeRepairman() {return repairmen.size();}
	
	public void addMilitia(Unit s) {militia.add(s);}
	public void removeMilitia(Unit s) {militia.remove(s);}	
	public Unit getMilitia(int i) {return militia.get(i);}
	public int sizeMilitia() {return militia.size();}
	
	public void addRefin(Unit r) {
		refineries.add(r);
		
		for (Base b : bases) {
			for (GeyserChunk g : b.getGeyserChunks()) {
				if (r.getID() == g.getGeyser().getID()) {
					g.setIsRefinery(true);
				}
			}
		}
	}
	public void removeRefin(Unit r) {
		refineries.remove(r);
		
		for (Base b : bases) {
			for (GeyserChunk g : b.getGeyserChunks()) {
				if (r.getID() == g.getGeyser().getID()) {
					g.setIsRefinery(false);
				}
			}
		}
	}	
	public Unit getRefin(int i) {return refineries.get(i);}
	public int sizeRefin() {return refineries.size();}	
	
	public void clear() {
		scouts.clear(); 
		repairmen.clear(); 
		militia.clear();
		bases.clear();
	}
	
	public void removeSCV(Unit s) { 
		allMinSCVs.remove(s);
		removeMinSCV(s);
		removeGasSCV(s);
		removeBuilder(s);
		for (int i = 0; i < scouts.size(); i++) {
			if (s.getID() == scouts.get(i).getID()){
				scouts.remove(s);
				return;
			}
		}
		for (int i = 0; i < repairmen.size(); i++) {
			if (s.getID() == repairmen.get(i).getID()){
				repairmen.remove(s);
				return;
			}
		}
		for (int i = 0; i < militia.size(); i++) {
			if (s.getID() == militia.get(i).getID()){
				militia.remove(s);
				return;
			}
		}
		//System.out.println("COULD NOT FIND SCV TO REMOVE");
	}	
	
	//Method that returns a builder to minerals
	public void returnBuilder(Unit w) {
		if (w == null) {
			return;
		}	
		addMinSCV(w);
		removeBuilder(w);
		
	}
	
	//Method that returns the closest mineral-gathering scv to the build location
	public Unit getBuilder(Position p) {
		Unit closestSCV = null;
		
		for (Base b : bases) {
			for (MineralChunk mc : b.getMineralChunks()) {
				for (Unit minSCV : mc.getMinSCVs()) {
					if ((closestSCV == null || minSCV.getDistance(p) < closestSCV.getDistance(p)) && minSCV.isCompleted()) {
						closestSCV = minSCV;
					}
				}	
			}
		}

		addBuilder(closestSCV);
		removeMinSCV(closestSCV);
		return closestSCV;
	} 
	
	public void onStart() {		
		if (bases.size() > 0) {
			if (bases.get(0).getCC() != null) {
				Unit cc = bases.get(0).getCC();
				TilePosition tl = cc.getTilePosition();
				for (int x = tl.getX(); x < tl.getX() + cc.getType().tileWidth() + 2; x++) {
		    		for (int y = tl.getY(); y < tl.getY() + cc.getType().tileHeight(); y++) {
		    			reservedTiles.add(new TilePosition(x, y));
		    		}
		    	}
			}
			for (Base b : bases) {
				for (Unit min : b.getMinerals()) {
					addReservedTiles(min.getTilePosition(), min.getType());
				}
			}
		}	
	}
	
	public void debug() {
		for (Base b : bases) {
			b.debug();
		}
	}
	
	public void maynardSlide() {
		
	}
	
	public void onFrame() {	
		debug();
		minSCVTotal = 0;
		gasSCVTotal = 0;
		buildersTotal = 0;
		repairmenTotal = 0;
		
		//Count scvs
		for (Base b : bases) {
			for (MineralChunk mc : b.getMineralChunks()) {
				minSCVTotal += mc.getMinSCVs().size();
			}
			for (GeyserChunk mc : b.getGeyserChunks()) {
				gasSCVTotal += mc.getGasSCVs().size();
			}
			buildersTotal += b.getBuildersTotal();
			repairmenTotal += b.getRepairmenTotal();
		}
		
        //System.out.println("Got Here: 1");
		//int basesNum = 0;
		for (Base b : bases) {
			//game.drawTextScreen(170, 330 + 15*basesNum, "Base " + (basesNum+1) + ": " + b.toString());
			//basesNum++;
	        
	        for (Unit m : b.getMinerals()) {
				if (!m.exists()) {
					b.removeMineralField(m);
					continue;
				}
			}	
	        
			//UPDATE MINERAL PATCHES
	        b.updateMineralPatches();
		}

		if (reservedTiles != null) {
			for (TilePosition tp : reservedTiles) {
				game.drawLineMap(tp.toPosition(), new Position(tp.toPosition().getX() + 1*32, tp.toPosition().getY() + 1*32), Color.Grey);
				game.drawLineMap(new Position(tp.toPosition().getX(), tp.toPosition().getY() + 1*32), new Position(tp.toPosition().getX() + 1*32, tp.toPosition().getY()), Color.Grey);
			}
		}
		
		//REASSIGN MINERAL SCVs TO GAS IF THERE ARE NOT ENOUGH GAS SCVs!!!!!
		for (Base b : bases) {
			int numberSCVsToMove = 0;
			for (GeyserChunk gc : b.getGeyserChunks()) {
				if (gc.getGasSCVs().size() < gc.getMaxGasSCVs()) {
					numberSCVsToMove += gc.getMaxGasSCVs() - gc.getGasSCVs().size();
				}
			}
			
			if (numberSCVsToMove > 0 && b.getMinSCVTotal() >= b.getMinerals().size()) {
				
				for (int i = 0; i < numberSCVsToMove; i++) {
					Unit toMove = null;
					
					for (MineralChunk mc : b.getMineralChunks()) {
						if (mc.getMinSCVs().size() > 0) {
							for (Unit scv : mc.getMinSCVs()) {
								toMove = scv;
								break;
							}
							if (toMove != null) {
								removeMinSCV(toMove);
								break;
							}
						}
					}
					
					for (GeyserChunk gc : b.getGeyserChunks()) {
						if (gc.getGasSCVs().size() < gc.getMaxGasSCVs() && toMove != null) {
							addGasSCV(toMove);
							break;
						}
					}
					
				}
			}
		}
		
		//REASSIGN MINERAL SCVs TO SCOUT IF THERE IS NONE!!!!!
		for (Base b : bases) {
			boolean needMore = false;
			if (sm != null) {
				if (sm.getScouts().size() < 1) {
					needMore = true;
				}
			}
			if (sm == null || needMore) {
				if (b.getMinSCVTotal()*2 > b.getMinSCVMax()) {
					Unit toMove = null;
					ArrayList<Unit> scvsToMove;
					if (sm == null) {
						scvsToMove = new ArrayList<Unit>();
					} else {
						scvsToMove = sm.getScouts();
					}
					for (MineralChunk mc : b.getMineralChunks()) {
						if (mc.getMinSCVs().size() > 0) {
							for (Unit scv : mc.getMinSCVs()) {
								toMove = scv;
								scvsToMove.add(scv);
								break;
							}
							if (toMove != null) {
								removeMinSCV(toMove);
								break;
							}
						}
					}		
					sm = new ScoutManager(scvsToMove, game, 1);
					sm.setOurInformation(ourBase, ourNatExpo);
					if (enemyBase != null && enemyNatExpo != null) {
						sm.setEnemyInformation(enemyBase, enemyNatExpo);
					}
					break;
				}
			} 
		}
		
		//REASSIGN MINERAL SCVS TO REPAIR IF THERE IS ENOUGH!!!!
		for (Base b : bases) {
			boolean needMore = false;
			RepairManager rm = b.getRepairManager();
			if (rm != null) {
				if (rm.getRepairmen().size() < 1) {
					needMore = true;
				}
			}
			if (rm == null || needMore) {
				if (b.getMinSCVTotal()*2 > b.getMinSCVMax()) {
					Unit toMove = null;
					ArrayList<Unit> scvsToMove;
					if (rm == null) {
						scvsToMove = new ArrayList<Unit>();
					} else {
						scvsToMove = rm.getRepairmen();
					}
					for (MineralChunk mc : b.getMineralChunks()) {
						if (mc.getMinSCVs().size() > 0) {
							for (Unit scv : mc.getMinSCVs()) {
								toMove = scv;
								scvsToMove.add(scv);
								break;
							}
							if (toMove != null) {
								removeMinSCV(toMove);
								break;
							}
						}
					}		
					b.setRepairManager(new RepairManager(game, 1, scvsToMove));
					break;
				}
			} 
		}
		
		//REASSIGN MINERAL SCVS TO REPAIR SQUAD
		boolean canPull = true;
		for (Base b : bases) {
			if (b.getRepairManager() != null) {
				if (b.getRepairManager().getRepairmen().size() < b.getRepairManager().getMaxRepairGuys()) {
					canPull = false;
				}
			}
		}
		if (canPull) {
			boolean needMore = false;
			if (rmForSquad != null) {
				if (rmForSquad.getRepairmen().size() < 2) {
					needMore = true;
				}
			}
			if (rmForSquad == null || needMore) {
				for (Base b : bases) {
					if (b.getMinSCVTotal()*2 > b.getMinSCVMax()) {
						Unit toMove = null;
						ArrayList<Unit> scvsToMove;
						if (rmForSquad == null) {
							scvsToMove = new ArrayList<Unit>();
						} else {
							scvsToMove = rmForSquad.getRepairmen();
						}
						for (MineralChunk mc : b.getMineralChunks()) {
							if (mc.getMinSCVs().size() > 0) {
								for (Unit scv : mc.getMinSCVs()) {
									toMove = scv;
									scvsToMove.add(scv);
									break;
								}
								if (toMove != null) {
									removeMinSCV(toMove);
									break;
								}
							}
						}		
						rmForSquad = new RepairManager(game, 2, scvsToMove);
						break;
					}
				}
			} 
		}
	
		//Should we Maynard Slide?
		if (justAddedBase) {
			boolean completed = true;
			for (Base b : bases) {
				if (!b.getCC().isCompleted()) {
					completed = false;
				}
			}
			if (completed) {
				justFinishedBase = true;
				justAddedBase = false;
			}
		}
		
		//MAYNARD SLIDE
		if (justFinishedBase) {
			allMinSCVs.clear();
			for (Base b : bases) {
				for (MineralChunk mc : b.getMineralChunks()) {
					for (Unit scv : mc.getMinSCVs()) {
						allMinSCVs.add(scv);
					}
					ArrayList<Unit> temp = new ArrayList<Unit>();
					mc.setMinSCVs(temp);
				}
			}
			for (Unit toAdd : allMinSCVs) {
				//boolean added = false;
				//resetSCV
				toAdd.stop();
				addMinSCV(toAdd);
			}	
			justFinishedBase = false;
		}
		
		//MINERAL AND GAS SCV Manager
		for (Base b : bases) {
			for (MineralChunk mc : b.getMineralChunks()) {
				for (Unit s : mc.getMinSCVs()) {					
					if (mc.getMineral() != null) {
						if (!s.isBeingHealed()) {       
							if (!s.isGatheringMinerals()) {
					            //if a mineral patch was found, send the worker to gather it
					            s.gather(mc.getMineral(), false);  
							}
				        } else {
				        	if (!s.isHoldingPosition()) {
				        		s.holdPosition();
				        	}
				        }
					}			
				}
			}
			
			for (GeyserChunk gc : b.getGeyserChunks()) {
				for (Unit s : gc.getGasSCVs()) {			
					if (!s.isBeingHealed()) {
						if (!s.isGatheringGas() && gc.isRefinery()) {       		        			            			
				            //if a mineral patch was found, send the worker to gather it
				            s.gather(gc.getGeyser(), false);
				        } else if (!gc.isRefinery()){
			            	gc.removeGasSCV(s);
				        }
					} else {
			        	if (!s.isHoldingPosition()) {
			        		s.holdPosition();
			        	}
					}
				}
			}
			
			/*
			//BUILDER Manager
			for (Unit bu : b.getBuilders()) {				
		    	// if it's a idle builder, send it to the closest mineral line by default!***********************************************
			}
			*/	
		}
	
		//do base onFrame()
		for (Base b : bases) {
			b.onFrame();
		}
		
		//do ScoutManager onFrame()
		if (sm != null) {
			sm.onFrame();
		}


	}
	
	//TEMPORARY fix
	public TilePosition findNearbySpot(Unit builder, UnitType buildingType, TilePosition refTile) {
    	TilePosition ret = null;
    	int maxDist = 3;
    	int stopDist = 40;			//Max building distance from reference tile

    	//Refinery, Assimilator, Extractor
    	if (buildingType.isRefinery()) {
    		for (Unit n : game.neutral().getUnits()) {
    			if ((n.getType() == UnitType.Resource_Vespene_Geyser) &&
    					( Math.abs(n.getTilePosition().getX() - refTile.getX()) < stopDist ) &&
    					( Math.abs(n.getTilePosition().getY() - refTile.getY()) < stopDist )
    					) {
    				addReservedTiles(n.getTilePosition(), buildingType);
    				return n.getTilePosition();
    			}
    		}
    	}
    	
    	//NEW BASE
    	if (buildingType == UnitType.Terran_Command_Center) {
    		addReservedTiles(ourNatExpo, buildingType);
    		return ourNatExpo;
    	}

    	while (maxDist < stopDist) {
			for (int x = refTile.getX() - maxDist; x <= refTile.getX() + maxDist; x++) {
    			for (int y = refTile.getY() - maxDist; y <= refTile.getY() + maxDist; y++) {
    				if ((game.canBuildHere(new TilePosition(x + 1, y - 1), buildingType, builder, false)
    						&& game.canBuildHere(new TilePosition(x + 1, y + 1), buildingType, builder, false)
    						&& game.canBuildHere(new TilePosition(x - 1, y - 1), buildingType, builder, false)
    						&& game.canBuildHere(new TilePosition(x - 1, y + 1), buildingType, builder, false))
    						|| ((buildingType == UnitType.Terran_Factory || buildingType == UnitType.Terran_Starport) 
    								&& (game.canBuildHere(new TilePosition(x + 3, y - 1), buildingType, builder, false)
    	    						&& game.canBuildHere(new TilePosition(x + 3, y + 1), buildingType, builder, false)
    	    						&& game.canBuildHere(new TilePosition(x - 1, y - 1), buildingType, builder, false)
    	    						&& game.canBuildHere(new TilePosition(x - 1, y + 1), buildingType, builder, false))))
    				{
    					TilePosition tl = new TilePosition(x, y);
    			    	TilePosition tr = new TilePosition(tl.getX() + buildingType.tileWidth(), tl.getY());
    			    	TilePosition bl = new TilePosition(tl.getX(), tl.getY() + buildingType.tileHeight());
    			    	
    			    	//IF the buildingType can build an addon, THEN
    			    	if (buildingType == UnitType.Terran_Factory || buildingType == UnitType.Terran_Starport || buildingType == UnitType.Terran_Command_Center) {
    			    		//Top right and bottom right has two more tiles tacked on to the width
    			    		tr = new TilePosition(tl.getX() + buildingType.tileWidth() + 2, tl.getY());
    			    	}
    					
    					ArrayList<TilePosition> potentialReservedTiles = new ArrayList<TilePosition>();
    					for (int a = x; a < tr.getX(); a++) {
    			    		for (int b = y; b < bl.getY(); b++) {
    			    			potentialReservedTiles.add(new TilePosition(a, b));
    			    		}
    			    	}
    					
    					boolean unitsInWay = false;			// units that are blocking the tile
    					
    					for (Unit u : game.getAllUnits()) {
    						//If the builder is in the way, continue;
    						if (u.getID() == builder.getID()) {
    							continue;
    						}
    						
    						if (!u.getType().isBuilding()) {
    							if ((Math.abs(u.getTilePosition().getX()-x) < 3) && (Math.abs(u.getTilePosition().getY()-y) < 3)) {
    								unitsInWay = true;
    							}
    						}
    					}
    					

    					if (reservedTiles.size() > 0) {
							for (TilePosition reserved : reservedTiles) {
								for (TilePosition potential : potentialReservedTiles) {
			    					//See if the potential build spot contains tiles that are already reserved
									if (reserved.equals(potential)) {
										unitsInWay = true;
										break;
									}
								}
								if (unitsInWay) {
									break;
								} 
								
							}
							//See if the potential build spot contains tiles that aren't within the map
							if (!unitsInWay) {
								for (TilePosition potential : potentialReservedTiles) {
									int c = potential.getX();
									int d = potential.getY();
									if (c <= 0 || c >= game.mapWidth() || d <= 0 || d >= game.mapHeight()) {
										unitsInWay = true;
										break;
									}
								}
							}
    					}
    					
    					//If there are no units in the way, return the tile position to build
    					if (!unitsInWay) {
    						TilePosition buildSpot = new TilePosition(x, y);
    						for (TilePosition tp : potentialReservedTiles) {
    							reservedTiles.add(tp);
    						}
        					return buildSpot;
    					}
    				}
    			}
    		}
    		maxDist++;
    	}

    	//if (ret == null) System.out.println("Unable to find suitable build position for "+buildingType.toString());
    	return ret;
    }
	
	
}
