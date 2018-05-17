import java.util.*;
import bwapi.*;

public class RepairManager {
	private ArrayList<RepairChunk> repairChunks = new ArrayList<RepairChunk>();
	private ArrayList<Unit> repairmen = new ArrayList<Unit>();
	private ArrayList<Unit> watchlist = new ArrayList<Unit>();
	private ArrayList<Unit> queue = new ArrayList<Unit>();
	private int maxRepairGuys;
	
	private Game game;
	
	public void setGame(Game g) {
		this.game = g;
	}
	
	public RepairManager(Game g, int m, ArrayList<Unit> rr) {
		this.game = g;
		this.maxRepairGuys = m;
		this.repairmen = rr;
	}
	
	public int getMaxRepairGuys() {
		return maxRepairGuys;
	}
	
	public ArrayList<RepairChunk> getRepairChunks() {return repairChunks;}
	
	public ArrayList<Unit> getRepairmen() {return repairmen;}
	public void addRepairman(Unit s) {repairmen.add(s);}
	public void removeRepairman(Unit s) {repairmen.remove(s);}
	
	public ArrayList<Unit> getWatchlist() {return watchlist;}
	
	public void setWatchlist(ArrayList<Unit> units) {
		watchlist = units;
		queue = sortLowHPFirst(watchlist);
	}
	
	public ArrayList<Unit> sortLowHPFirst(ArrayList<Unit> units) {
		
		ArrayList<Unit> damagedUnits = new ArrayList<Unit>();
		ArrayList<Unit> damagedTanks = new ArrayList<Unit>();
		ArrayList<Unit> toReturn = new ArrayList<Unit>();
		
		//If there is a repairman that needs repaired, add it to the front of the queue
		for (Unit r : repairmen) {
			if (r.getHitPoints() < r.getType().maxHitPoints() && r.isCompleted() && r.exists()) {
				toReturn.add(r);
			}
		}
		
		//Find units with less than max HP
		if (units.size() > 0) {
			for (Unit u : units) {
				if (u.getHitPoints() < u.getType().maxHitPoints() && u.isCompleted() && u.getType().isMechanical()) {
					if (u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode && u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode) {
						damagedTanks.add(u);
					} else {
						damagedUnits.add(u);
					}
				} 
			}
		}
		
		//Bubble sort enemies based on percent health remaining
		if (damagedTanks.size() > 1) {
			int i, j;
			for (i = 0; i < damagedTanks.size()-1; i++) {
				for (j = 0; j < damagedTanks.size() - i - 1; j++) {
					double curPercentage = (damagedTanks.get(j).getHitPoints() * 100.0f) / damagedTanks.get(j).getType().maxHitPoints();
					double nxtPercentage = (damagedTanks.get(j+1).getHitPoints() * 100.0f) / damagedTanks.get(j+1).getType().maxHitPoints();
					
					if (curPercentage > nxtPercentage) {
						Unit temp = damagedTanks.get(j);
						damagedTanks.set(j, damagedTanks.get(j + 1));
						damagedTanks.set(j + 1, temp);
					}
				}
			}
		}
		
		//Bubble sort enemies based on percent health remaining
		if (damagedUnits.size() > 1) {
			int i, j;
			for (i = 0; i < damagedUnits.size()-1; i++) {
				for (j = 0; j < damagedUnits.size() - i - 1; j++) {
					double curPercentage = (damagedUnits.get(j).getHitPoints() * 100.0f) / damagedUnits.get(j).getType().maxHitPoints();
					double nxtPercentage = (damagedUnits.get(j+1).getHitPoints() * 100.0f) / damagedUnits.get(j+1).getType().maxHitPoints();
					
					if (curPercentage > nxtPercentage) {
						Unit temp = damagedUnits.get(j);
						damagedUnits.set(j, damagedUnits.get(j + 1));
						damagedUnits.set(j + 1, temp);
					}
				}
			}
		}
		
		//Add damagedunits AFTER the damaged repairmen
		toReturn.addAll(damagedTanks);
		toReturn.addAll(damagedUnits);

		//Return toReturn
		return toReturn;
	}
	
	public void onFrame() {
		//NOTE: Will have needed an updated list of units beforehand to work properly
		//	ie. setWatchlist
		
		//temp debug:
		/*
		game.drawTextScreen(500, 215, "repair chunks size : " + String.valueOf(repairChunks.size()));
		game.drawTextScreen(500, 230, "queue size : " + String.valueOf(queue.size()));
		game.drawTextScreen(500, 245, "repairmen size : " + String.valueOf(repairmen.size()));
		game.drawTextScreen(500, 260, "watchlist size : " + String.valueOf(watchlist.size()));
		*/
		
		//Remove already repaired repair chunks or repair chunks where either the target or repairman is dead
		ArrayList<RepairChunk> rc_toRemove = new ArrayList<RepairChunk>();
		for (RepairChunk rc : repairChunks) {
			if (!rc.getRepairTarget().exists() || !rc.getRepairman().exists()) {
				rc_toRemove.add(rc);
				continue;
			} 
			if (rc.getRepairTarget().getHitPoints() == rc.getRepairTarget().getType().maxHitPoints()) {
				rc_toRemove.add(rc);
				continue;
			}
		}
		for (RepairChunk r : rc_toRemove) {
			repairChunks.remove(r);
		}
		
		//Remove repairmen if they die
		ArrayList<Unit> rm_toRemove = new ArrayList<Unit>();
		for (Unit rm : repairmen) {
			if (!rm.exists()) {
				rm_toRemove.add(rm);
			}
		}
		for (Unit r : rm_toRemove) {
			repairmen.remove(r);
		}
		
		//Remove from queue
		ArrayList<Unit> queue_toRemove = new ArrayList<Unit>();
		for (Unit u : queue) {
			if (!u.exists()) {
				queue_toRemove.add(u);
				System.out.println("Removed : " + u.getType());
			}
		}
		for (Unit r : queue_toRemove) {
			queue.remove(r);
		}
		
		//Try to add repair chunks based on queue
		for (Unit toRepair : queue) {
			if (repairChunks.size() >= maxRepairGuys) {
				break;
			} else {
				//Find unoccupied worker
				//getRepairMan
				Unit toAdd = null;
				for (Unit r : repairmen) {
					if (r.getID() == toRepair.getID()) {
						continue;
					} else {
						boolean found = false;
						if (repairChunks.size() > 0) {
							for (RepairChunk rc : repairChunks) {
								if (r.getID() == rc.getRepairman().getID()) {
									found = true;
								}
							}
						}
						if (!found) {
							toAdd = r;
						}
					}
				}
				if (toAdd != null) {
					repairChunks.add(new RepairChunk(toAdd, toRepair, game));
				}
			}
		}
	}
	
	public void debug(int rad, Color c, boolean fill) {
		for (Unit r : repairmen) {
			game.drawCircleMap(r.getPosition(), rad, Color.Orange, fill);
		}
		for (RepairChunk rc : repairChunks) {
			rc.debug();
		}
	}
	
}
