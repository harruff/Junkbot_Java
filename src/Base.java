import java.util.ArrayList;
import bwapi.*;

public class Base {
	private Unit cc;	
	private RepairManager rm;
	private MilitiaManager mm;
	
	private ArrayList<MineralChunk> mineralChunks = new ArrayList<MineralChunk>();
	private ArrayList<GeyserChunk> geyserChunks = new ArrayList<GeyserChunk>();
	private ArrayList<Unit> builders = new ArrayList<Unit>();
	private ArrayList<Unit> repairmen = new ArrayList<Unit>();
	
	private ArrayList<Unit> buildings = new ArrayList<Unit>();
	private ArrayList<Unit> incompBuildings = new ArrayList<Unit>();
	private ArrayList<Unit> geysers = new ArrayList<Unit>();
	private ArrayList<Unit> minerals = new ArrayList<Unit>();
	private int maxSCVs = 0;
	
	private ArrayList<Unit> enemies = new ArrayList<Unit>();
	private int militiaNeeded = 0;
	
	private Game game;
	private Player self;
	
	public void setGame(Game g) {
		this.game = g;
		this.self = game.self();
	}
	
	public Base(Unit cc, Game g) {
		this.game = g;
		this.self = game.self();
		this.cc = cc;
		mm = new MilitiaManager(game);
		updateMineralPatches();
		determineSCVSaturation();
	}
	
	public Unit getCC() {return cc;}
	
	public ArrayList<MineralChunk> getMineralChunks() {return mineralChunks;}
	
	public ArrayList<GeyserChunk> getGeyserChunks() {return geyserChunks;}
	
	public RepairManager getRepairManager() {return rm;}
	
	public ArrayList<Unit> getMinerals() {return minerals;}
	
	public ArrayList<Unit> getGeysers() {return geysers;}
	
	public ArrayList<Unit> getBuilders() {return builders;}
	public void addBuilder(Unit s) {builders.add(s);}	
	public void removeBuilder(Unit s) {builders.remove(s);}	
	
	public ArrayList<Unit> getRepairmen() {return repairmen;}
	public void addRepairman(Unit s) {repairmen.add(s);}	
	public void removeRepairman(Unit s) {repairmen.remove(s);}	
	
	public void setRepairManager(RepairManager rm) {
		this.rm = rm;
	}
	
	public int getMaxSCV() {return maxSCVs;}
	
	public void setMinSCVs(ArrayList<Unit> replacements) {
		//Clear all mineral chunks of its workers 
		for (MineralChunk mc : mineralChunks) {
			ArrayList<Unit> temp = new ArrayList<Unit>();
			mc.setMinSCVs(temp);
		}
		
		//Populate the mineral chunks
		for (Unit s : replacements) {
			boolean added = false;
			
			//Add to mineral fields with no scvs first
			if (!added) {
				for (MineralChunk mc : mineralChunks) {			
					if (mc.getMinSCVs().size() < mc.getMaxMinSCVs() - 1) {	
						mc.addMinSCV(s);
						added = true;
						break;
					}
				}
			}
			
			//IF the scv hasn't been added yet, try to add it to any of them with less than max
			if (!added) {
				for (MineralChunk mc : mineralChunks) {
					if (mc.getMinSCVs().size() < mc.getMaxMinSCVs()){
						mc.addMinSCV(s);
						added = true;
						break;
					}		
				}
			}
		}
		
	}
	
	public void removeMineralField(Unit m) {
		minerals.remove(m);
		
		MineralChunk toRemove = null;
		
		for (MineralChunk mc : mineralChunks) {
			if (mc.getMineral().getID() == m.getID()) {
				toRemove = mc;
				break;
			}
		}
		
		if (toRemove != null) {
			mineralChunks.remove(toRemove);
		}
	}

	public void updateMineralPatches() {
		for (Unit n : game.getNeutralUnits()) {
			if (n.getType().isMineralField() && n.getDistance(cc) < 10*32) {
				boolean added = false;
				for (MineralChunk mc : mineralChunks) {
					if (n.getID() == mc.getMineral().getID()) {
						added = true;
					}
				}
				if (!added) {
					mineralChunks.add(new MineralChunk(n, game));
					minerals.add(n);
				}
			} else if (n.getType() == UnitType.Resource_Vespene_Geyser && n.getDistance(cc) < 10*32) {
				boolean added = false;
				for (GeyserChunk gc : geyserChunks) {
					if (n.getID() == gc.getGeyser().getID()) {
						added = true;
					}
				}
				if (!added) {
					geyserChunks.add(new GeyserChunk(n, game));
					geysers.add(n);
				}
			}
		}
	}
	
	public void determineSCVSaturation() {
		maxSCVs = getMinSCVMax() + getGasSCVMax();
	}
	
	public String toString() {
		String s = new String("scvs: " + getAllSCVTotal() + ", minerals: " + minerals.size() + ", geysers: " + geysers.size() + ", maxSCVs: " + maxSCVs+ "\n");
		return s;
	}
	
	@SuppressWarnings("unused")
	public int getMinSCVTotal() {
		int total = 0;
		for (MineralChunk mc : mineralChunks) {
			for (Unit scv : mc.getMinSCVs()) {
				total++;
			}
		}
		return total;
	}
	
	@SuppressWarnings("unused")
	public int getGasSCVTotal() {
		int total = 0;
		for (GeyserChunk gc : geyserChunks) {
			for (Unit scv : gc.getGasSCVs()) {
				total++;
			}
		}
		return total;
	}
	
	@SuppressWarnings("unused")
	public int getMilitiaTotal() {
		int total = 0;
		if (mm != null) {
			for (MilitiaChunk mc : mm.getMilitiaChunks()) {
				for (Unit s : mc.getMySCVs()) {
					total++;
				}
			}
		}
		return total;
	}
	
	public int getBuildersTotal() {
		return builders.size();
	}
	
	public int getRepairmenTotal() {
		return repairmen.size();
	}
	
	public int getAllSCVTotal() {
		return getGasSCVTotal() + getMinSCVTotal() + getBuildersTotal() + getRepairmenTotal() + getMilitiaTotal();
	}
	
	public int getMinSCVMax() {
		return 2*minerals.size();
	}
	
	public int getGasSCVMax() {
		return 3*geysers.size();
	}
	
	public int getAllSCVMax() {
		return maxSCVs;
	}
	
	public void updateEnemies() {
		//Find enemies near our base
		for (Unit u : game.getAllUnits()) {
			//IF there is an enemy within 10 tiles of our base
			if (u.getPlayer() == game.enemy() && cc.getPosition().getDistance(u.getPosition()) < 10*32) {
				boolean alreadyAdded = false;
				for (Unit e : enemies) {
					if (u.getID() == e.getID()) {
						alreadyAdded = true;
						break;
					}
				}
				if (!alreadyAdded) {
					//Add to enemy list
					enemies.add(u);
					//Update militiaNeeded
					determineMilitiaNeeded(u);
				}
			}
		}
		
		//Remove enemies that are too far from the base
		ArrayList<Unit> e_toRemove = new ArrayList<Unit>();
		for (Unit e : enemies) {
			if (cc.getPosition().getDistance(e.getPosition()) >= 10*32) {
				e_toRemove.add(e);
			}
			if (!e.exists()) {
				e_toRemove.add(e);
			}
		}
		for (Unit toRemove : e_toRemove) {
			enemies.remove(toRemove);
		}
	}
	
	public void determineMilitiaNeeded(Unit e) {
		if (e.getType() == UnitType.Zerg_Zergling) {
			militiaNeeded += 2;
		}
		else if (e.getType() == UnitType.Protoss_Zealot){
			militiaNeeded += 3;
		} 
		else if (!e.isFlying()) {
			militiaNeeded += 2;
		}
	}
	
	public void findStraySCVs() {
		for (Unit scv : game.getAllUnits()) {
			if (scv.getType() == UnitType.Terran_SCV && scv.getPlayer() != game.enemy() && cc.getDistance(scv) < 10*32) {
				
				boolean hasRole = false;
				
				//Check MineralChunks
				if (!hasRole) {
					for (MineralChunk mc : mineralChunks) {
						for (Unit mc_scv : mc.getMinSCVs()) {
							if (mc_scv.getID() == scv.getID()) {
								hasRole = true;
								break;
							}
						}
						if (hasRole) {
							break;
						}
					} 
				}
				
				//Check GeyserChunks
				if (!hasRole) {
					for (GeyserChunk gc : geyserChunks) {
						for (Unit gc_scv : gc.getGasSCVs()) {
							if (gc_scv.getID() == scv.getID()) {
								hasRole = true;
								break;
							}
						}
						if (hasRole) {
							break;
						}
					} 
				}
				
				//Check RepairManager
				if (!hasRole && rm != null) {
					for (Unit r : rm.getRepairmen()) {
						if (r.getID() == scv.getID()) {
							hasRole = true;
							break;
						}
					} 
				}
				
				//Check Builders
				if (!hasRole) {
					for (Unit b : builders) {
						if (b.getID() == scv.getID()) {
							hasRole = true;
							break;
						}
					}
				}
				
				//Check Militia
				if (!hasRole && mm != null) {
					for (MilitiaChunk mc : mm.getMilitiaChunks()) {
						for (Unit m : mc.getMySCVs()) {
							if (m.getID() == scv.getID()) {
								hasRole = true;
								break;
							}
						}
					}
				}
				
				//Add to Mineral line by default
				if (!hasRole && scv.isIdle()) {
					boolean added = false;
					for (MineralChunk mc : mineralChunks) {
						if (mc.getMinSCVs().size() + 1 < mc.getMaxMinSCVs()) {
							mc.addMinSCV(scv);
							added = true;
							break;
						}
					}
					if (!added) {
						for (MineralChunk mc : mineralChunks) {
							if (mc.getMinSCVs().size() < mc.getMaxMinSCVs()) {
								mc.addMinSCV(scv);
								break;
							}
						}
					}
				}
				//NOTE: Doesn't account for scouts
			}
		}
	}
	
	public void onFrame() {
		
		findStraySCVs();
		
		//We don't need militia yet
		militiaNeeded = 0;
		
		//Update enemy list and militia required
		updateEnemies();
		game.drawTextScreen(5, 5, "enemies: " + String.valueOf(enemies.size()));
		
		//Send enemies to MilitiaManager
		/*
		if (mm != null) {
			mm.setEnemies(enemies);
		}
		*/
		
		//Based on militiaNeeded, determine how to pull SCVs
		if (militiaNeeded > 0 && mm != null) {		
			ArrayList<Unit> toAddToMilitia = new ArrayList<Unit>();
			for (int i = 0; i < militiaNeeded; i++) {
				Unit toRemove = null;
				
				//Pull from mineral line first
				for (MineralChunk mc : mineralChunks) {
					for (Unit mc_scv : mc.getMinSCVs()) {
						toRemove = mc_scv;
						break;
					}
					if (toRemove != null) {
						mc.removeMinSCV(toRemove);
						break;
					}
				}
				
				//Pull from gas line second if needed
				if (toRemove == null) {
					for (GeyserChunk gc : geyserChunks) {
						for (Unit gc_scv : gc.getGasSCVs()) {
							toRemove = gc_scv;
							break;
						}
						if (toRemove != null) {
							gc.removeGasSCV(toRemove);
							break;
						}
					}
				}
				
				//Pull from repair manager third if needed
				if (toRemove == null && rm != null) {
					for (Unit rm_scv : rm.getRepairmen()) {
						toRemove = rm_scv;
						break;
					}
					if (toRemove != null) {
						rm.removeRepairman(toRemove);
						break;
					}
				}
				
				//IF we found an scv to draft, add it to the militia
				
				if (toRemove != null) {
					toAddToMilitia.add(toRemove);
					//Try to add militiaChunks
					for (Unit enemy : enemies) {
						boolean alreadyAdded = false;
						for (MilitiaChunk mc : mm.getMilitiaChunks()) {
							if (mc.getTarget().getID() == enemy.getID()) {
								alreadyAdded = true;
								break;
							}
						}
						if (!alreadyAdded) {
							mm.addMilitiaChunk(new MilitiaChunk(toAddToMilitia, enemy, game));
						}
					}
				} else {
					System.out.println("Could not find scv to add to militia");
				}
				
			}	
		}
		
		//Do MilitiaManager onFrame()
		if (mm != null) {
			mm.onFrame();
		}
		
		
		//Update building list
		buildings.clear();
		incompBuildings.clear();
		
		//Buildings & SCVs
		for (Unit u : game.getAllUnits()) {
			if (u.getPlayer() == self && (u.getType().isBuilding() || u.getType() == UnitType.Terran_SCV) && u.getDistance(cc) <= 10*32) {
				if (u.isCompleted()) {
					buildings.add(u);
				} else {
					if (u.getType().isBuilding() && !u.getType().isAddon()) {
						incompBuildings.add(u);
					}
				}
			}
		}
		//game.drawTextScreen(10, 10, String.valueOf(incompBuildings.size()));
		buildings.add(cc);
		
		//Send building list to repairManager
		if (rm != null) {
			repairmen = rm.getRepairmen();
			rm.setWatchlist(buildings);
			rm.onFrame();
			
			//Repairmen micro!
			if (rm.getRepairmen().size() > 0) {
				for (RepairChunk rc : rm.getRepairChunks()) {
					Unit r = rc.getRepairman();
					if (!r.isRepairing()){
						if (r.isBeingHealed() && rc.getRepairTarget().getType() != UnitType.Terran_SCV) {
							if (!r.isHoldingPosition()) {
								r.holdPosition();
							}
						} else if (!r.isBeingHealed()) {
							r.repair(rc.getRepairTarget());
						}
					}
				}	
				
				//IF a builder gets killed while building AND there are no repair chunks, attempt to finish the building
				if (rm.getRepairChunks().size() == 0) {
					for (Unit ib : incompBuildings) {
						//Check to see if there is a builder still near this building -> is still being built 
						boolean stillBeingBuilt = false;
						for (Unit builder : builders) {
							if (ib.getDistance(builder) < 2*32) {
								stillBeingBuilt = true;
							}
						}
						if (!stillBeingBuilt) {
							for (Unit r : repairmen) {
								if (r.getOrderTarget().getID() != ib.getID()) {
									r.rightClick(ib);
									break;
								}
							}
						}
					}
					
					//IF a repairman has nothing to repair, start gathering minerals
					for (Unit s : repairmen) {
						if (!s.isRepairing()) {
							for (Unit m : minerals) {
								if (!s.isBeingHealed()) {       
									if (!s.isGatheringMinerals()) {
							            //if a mineral patch was found, send the worker to gather it
							            s.gather(m, false);  
									}
						        } else {
						        	if (!s.isHoldingPosition()) {
						        		s.holdPosition();
						        	}
						        }
								break;
							}	
						}
					}		
				}	
			}	
		}
		

		//Make sure repairmen are repairing
		
		
	}
	
	public void debug() {
		for (GeyserChunk gc : geyserChunks) {
			gc.debug();
		}
		for (MineralChunk mc : mineralChunks) {
			mc.debug();
		}
		for (Unit builder : builders) {
			game.drawCircleMap(builder.getPosition(), 12, Color.Yellow);
			game.drawLineMap(builder.getPosition(), builder.getOrderTargetPosition(), Color.White);
		}
		if (rm != null) {
			rm.debug();
		}
		if (mm != null) {
			mm.debug();
		}
	}
}
