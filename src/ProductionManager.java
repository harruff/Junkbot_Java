//import java.util.*;
import bwapi.*;

public class ProductionManager {
	private int reservedMinerals = 0, reservedGas = 0, bIndex = 0;
	private Game game;
	private Player self;
	
	//ARE WE MARINE RUSHING???????
	private boolean bio = false;
	
	//Markers for point in game:
	private boolean midGame = false;
	
	private BuildQueue bq = new BuildQueue();
	private BuildOrder bo = new BuildOrder();
	private BaseManager bm = new BaseManager();

	private int infantryWeapons = 0;
	private int infantryArmor = 0;
	private int mechWeapons = 0;
	private int mechArmor = 0;

	private TilePosition enemyBase = null, ourBase = null, ourNatExpo = null, enemyNatExpo = null;
	//private ArrayList<EnemyMemoryChunk> enemyMemory = new ArrayList<EnemyMemoryChunk>();
	private boolean buildScanner = false; 
	//private boolean baseLanded = true;
	//private boolean ebay = false;
	private boolean academy = false;
	//private boolean refinery = false;
	
	private StringBuilder researchTime = new StringBuilder("Research Display:\n");
	private StringBuilder research = new StringBuilder("\n");
	
	//private int initialMinerals = 0;
	
	private int marineCap = 0;
	
	public void debug() {
		
		game.drawTextScreen(180, 25, bo.display(bIndex));
		game.drawTextScreen(360, 25, researchTime.toString());
		game.drawTextScreen(390, 25, research.toString());
		game.drawTextScreen(390, 10, "reservedMin: " + reservedMinerals);
	}
	
	public void setGame(Game g) {
		this.game = g;
		this.self = game.self();
		bm.setGame(game);
	}
	
	public BaseManager getBaseManager() {
		return bm;
	}
	
	public void setBaseManager(BaseManager fromController) {
		bm = fromController;
	}

    public void setOurInformation(TilePosition ob, TilePosition one) {
		ourBase = ob;
		ourNatExpo = one;
		bm.setOurInformation(ourBase, ourNatExpo);
	}
    
    public void setEnemyInformation(TilePosition eb, TilePosition ene) {
		enemyBase = eb;
    	enemyNatExpo = ene;
    	bm.setEnemyInformation(enemyBase, enemyNatExpo);
    }
    
    /*public void updateEnemyMemory(ArrayList<EnemyMemoryChunk> em) {
    	enemyMemory = em;
    }*/
    
	public int getSurplusMinerals() {
		return self.minerals() - reservedMinerals;
	}
	public int getSurplusGas() {
		return self.gas() - reservedGas;
	}
	public BuildQueue getBuildQueue() {
		return bq;
	}
	public void setBuildOrder(BuildOrder bo) {
		this.bo = bo;
	}	
	public void checkBuildOrder() {
		//If the build chunk at index isn't null AND the index is less than the size AND the supply for that chunk is less than or equal to the supply used...
		//	Then add the build chunk to the queue
		//	Add 1 to the index
		if (bIndex < bo.size()) {
			if (bo.getBuildChunkatIndex(bIndex) != null && bo.getBuildChunkatIndex(bIndex).getSupply() <= self.supplyUsed()/2) {
				//System.out.println("Got Here: " + bIndex);
				addToQueue(bo.getBuildChunkatIndex(bIndex).getBuildingType(), bm.getBaseAtIndex(0).getCC().getTilePosition());
				bIndex++;
			}	
			if (!midGame && bo.getBuildChunkatIndex(bIndex).getSupply() > 32) {
				//midGame = true;
			}
		}
		
	}
	
	public void setBio() {
		bio = true;
	}
	
	public void addSCV(Unit s) {bm.addMinSCV(s);} //add to mineralSCV by default
	public void removeSCV(Unit s) {bm.removeSCV(s);}
	public int sizeSCV() {return bm.sizeAllSCV();}
	
	public void addRefin(Unit r) {bm.addRefin(r);}
	public void removeRefin(Unit r) {bm.removeRefin(r);}
	
	public void addCC(Unit c) {bm.addBase(new Base(c, game));}
	public void removeCC(Unit c) {bm.removeBase(c);}
	public Unit getCC(int i) {return bm.getBaseAtIndex(i).getCC();}
	public int sizeCC() {return bm.sizeBase();}	
	
	public void trainSCVs() {
		for (Base b : bm.getBases()) {
			Unit cc = b.getCC();
			if (!cc.isTraining() && !cc.canLand()) {
				if (buildScanner && academy) {
					cc.buildAddon(UnitType.Terran_Comsat_Station);
					return;
				}	
				else if (getSurplusMinerals() >= 50 && b.getAllSCVTotal() < b.getAllSCVMax()) {
					cc.train(UnitType.Terran_SCV);
				}	
			}			
		}	
	}
	
	//Adds a UnitType to the ~end~ of the queue;
	public void addToQueue(UnitType ut, TilePosition cctp) {
		reservedMinerals += ut.mineralPrice();
		reservedGas += ut.gasPrice();
		
		/*
		 * NOTE TO SELF:
		 * The intended way to get a tile position for the building is to find
		 * the closest scv to the build location, but its set to the closest scv
		 * TO THE COMMAND CENTER. ALSO, it finds a nearby spot based on that scv
		 * and the tile position around the cc
		 */
		
		Unit w = bm.getBuilder(cctp.toPosition()); 
		TilePosition tp = bm.findNearbySpot(w, ut, cctp); //FROM BASE MANAGER
		Unit b = null; //supposed to be null
		boolean s = false; //hasn't been started yet b/c adding to queue
		int f = 0; 
		
		BuildQueueChunk bqc = new BuildQueueChunk(w, b, ut, tp, s, f);			
		bq.addChunk(bqc);
	}
	
	//Adds a UnitType to the ~beginning~ of the queue;
	public void addToQueueBeginning(UnitType ut) {
		reservedMinerals += ut.mineralPrice();
		reservedGas += ut.gasPrice();
		/*
		 * See note above ^
		 */
		TilePosition cctp = bm.getBaseAtIndex(0).getCC().getTilePosition();
		Unit w = bm.getBuilder(cctp.toPosition()); 
		TilePosition tp = bm.findNearbySpot(w, ut, cctp); //FROM BASE MANAGER
		Unit b = null; //supposed to be null
		boolean s = false; //hasn't been started yet b/c adding to queue
		int f = 0;
		
		BuildQueueChunk bqc = new BuildQueueChunk(w, b, ut, tp, s, f);	
		bq.addChunkBeginning(bqc);
	}
	
	//Removes the first instance of a certain building type and then breaks the loop
	public void removeFromQueue(UnitType ut) {
		for (int i = bq.size() - 1; i > -1; i--) {
			if (!bq.getChunk(i).isStarted()) {
				if (bq.getChunk(i).getBuilder() != null) {
					bm.returnBuilder(bq.getChunk(i).getBuilder());
				}
				bq.removeChunkAtIndex(i);
				break;
			}
		}
	}
	
	
	
	//Method that checks to see if an SCV going to build something is following through or not
	public void checkSCVBuild() {   
		/*
		 * If the SCV takes more than 500 frames (around 10 seconds at 50 fps) and
		 * we have enough minerals and gas to make the building, check to see if 
		 * either the building doesn't exist in the building queue or if the building
		 * doesn't exist in the game. If both are false, reassign SCVs and restart
		 * the timer for the new instance of building. 
		 * 
		 * Then, move scv to buildLocation and build the type
		 */
		if (game.getFrameCount() % game.getLatencyFrames() != 0) {
			return;
		}
		
		for (int i = 0; i < bq.size(); i++) {
			BuildQueueChunk bqc = bq.getChunk(i);
		
			if (bqc.getBuilder() == null || !bqc.getBuilder().exists()) {
				Unit temp = bqc.getBuilder();
				temp = bm.getBuilder(bqc.getBuildLocation().toPosition());
				bqc.setBuilder(temp);
				continue;
			}
			
			// set start frame
			if (bqc.getFrameStarted() == 0) {
				bqc.setFrameStarted(game.getFrameCount());
			}
			
			if (self.minerals() >= bqc.getBuildingType().mineralPrice() && self.gas() >= bqc.getBuildingType().gasPrice()) {
				if (game.getFrameCount() - bqc.getFrameStarted() > 600) {
					boolean buildingExists = true;
					if (bqc.getBuilding() == null) {
						buildingExists = false;
					}
					if (buildingExists) {
						if (!bqc.getBuilding().exists()) {
							buildingExists = false;
						}
					}
					if (!buildingExists) {
						//System.out.println("Worker stuck.");
						Unit lostOne = bqc.getBuilder();
						bm.returnBuilder(lostOne);
						//removeSCV(lostOne); // PROBLEM HERE PROBABLY, NEVER TESTED ***************************
						reservedMinerals -= bqc.getBuildingType().mineralPrice();
						reservedGas -= bqc.getBuildingType().gasPrice();
						bm.removeReservedTiles(bqc.getBuildLocation(), bqc.getBuildingType());
						bq.removeChunkAtIndex(i);
						i--;	
						//DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						//tl = bm.getBaseAtIndex(0).getCC().getTilePosition();
						//tr = new TilePosition(tl.getX() + bqc.getBuildingType().tileWidth(), tl.getY());
				    	//bl = new TilePosition(tl.getX(), tl.getY() + bqc.getBuildingType().tileHeight());
				    	//br = new TilePosition(tr.getX(), bl.getY());
				    	//DEBUG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						
						addToQueue(bqc.getBuildingType(), bm.getBaseAtIndex(0).getCC().getTilePosition()); // Added 03/06/2018
						bqc.setFrameStarted(game.getFrameCount());	
						continue; //back to next iteration of for loop
					}				
				}
			}
			
			TilePosition topL = bqc.getBuildLocation();
			TilePosition botL = new TilePosition(topL.getX(), topL.getY() + bqc.getBuildingType().tileHeight());
			TilePosition topR = new TilePosition(topL.getX() + bqc.getBuildingType().tileWidth(), topL.getY());
			TilePosition botR = new TilePosition(topL.getX() + bqc.getBuildingType().tileWidth(), topL.getY() + bqc.getBuildingType().tileHeight());
			
			//build the building;
			if (!game.isVisible(topL) || !game.isVisible(botL) || !game.isVisible(topR) || !game.isVisible(botR)) {
				bqc.getBuilder().move(bqc.getBuildLocation().toPosition());
			} else {
				bqc.getBuilder().build(bqc.getBuildingType(), bqc.getBuildLocation());
			}
			
			// if scv has started construction, set the building to the constructing one		
			if (bqc.getBuilding() == null && bqc.getBuilder().getBuildUnit() != null) {
				bqc.setBuilding(bqc.getBuilder().getBuildUnit());
			}
				
			//Manage the values of a building once it has started construction
			if (bqc.getBuilding() != null) {
				if (!bqc.isStarted()) {
					reservedMinerals -= bqc.getBuildingType().mineralPrice();
					reservedGas -= bqc.getBuildingType().gasPrice();
					bqc.setStarted(true);
				}
				if (bqc.getBuilding().isCompleted()) {
					//System.out.println(bqc.getBuildingType() + " Complete!");
					//return builder to minerals/gas
					bm.returnBuilder(bqc.getBuilder());
					
					//Once done, remove from buildings queue
					bq.removeChunkAtIndex(i);
					i--;
					break;
				}

			}
			//IF REFINERY, WHAT THEN, HUH?
		}	
	}
	
	//Method that checks to see if a researching building is doing something
	public void addResearchToString(Unit a) {
		if (a.isResearching()) {
			researchTime.append(a.getRemainingResearchTime()).append("\n");
			research.append(a.getTech()).append("\n");
		} else if (a.isUpgrading()) {
			researchTime.append(a.getRemainingUpgradeTime()).append("\n");
			research.append(a.getUpgrade()).append(" ").append(a.getUpgrade().mineralPrice()).append("\n");
		}
	}
	
	//Method that plays on start
	public void onStart() {
		bq = new BuildQueue();
		bo = new BuildOrder();
		bm = new BaseManager();

		for (Unit cc : game.getAllUnits()) {
    		if (cc.getType() == UnitType.Terran_Command_Center && cc.getPlayer() == self) {
    			//System.out.println("Found CC");
            	bm.addBase(new Base(cc, game));
    			//System.out.println("Added CC to local baseManager");
            	break;
    		}
    	}
		
		bm.setGame(game);
		research = new StringBuilder("Research Display:\n");
		bm.onStart();
	}
	
	//Method that executes every frame
	public void onFrameBase() { //THIS IS THE PROBLEM*************************************

		bm.onFrame();
	}
	
	public void onFrameMarineRush() {
		//**** Building Priority ****
		//
		//1. If next building supply is met and build queue is empty, add it to the build queue\
		checkBuildOrder();
	
		int numSCVs = 0;
		int numRax = 0;
		
		for (Unit s : game.self().getUnits()) {
			if (s.getType() == UnitType.Terran_SCV) {
				numSCVs++;
			}
		}
		
		boolean buildScanner = false;
		if (numSCVs >= 20) {
			buildScanner = true;
		}
		
		//2. Train Marines if there is a barracks
		for (Unit b : game.self().getUnits()) {
			if (b.getType() == UnitType.Terran_Barracks) {
				numRax++;
				if (getSurplusMinerals() >= 50 && !b.isTraining()) {
					b.train(UnitType.Terran_Marine);
				}
			}
		}	
		
		//3. Train SCVs
		for (Unit u : game.self().getUnits()) {
			if (u.getType() == UnitType.Terran_Command_Center) {
				if (getSurplusMinerals() >= 50 && !u.isTraining() && !buildScanner) {
					u.train(UnitType.Terran_SCV);
				}
				else if (buildScanner) {
					u.buildAddon(UnitType.Terran_Comsat_Station);
				}
			}
		}	
		// Make sure the scv is building from the queue and whatnot
		checkSCVBuild();
		
		//4. Build Supply Depots when surplus minerals are available and supply capped
		boolean supplyInQueue = false;
		for (int i = 0; i < bq.size(); i++) {
			if (bq.getChunk(i).getBuildingType() == UnitType.Terran_Supply_Depot) {
				supplyInQueue = true;
			}
		}
		if (!supplyInQueue && getSurplusMinerals() >= 150 && (self.supplyUsed()/2 + 2*numRax) >= self.supplyTotal()/2 && self.supplyTotal()/2 >= 26) {
			addToQueue(UnitType.Terran_Supply_Depot, bm.getBaseAtIndex(0).getCC().getTilePosition());
		}		
		
		if (getSurplusMinerals() >= (150 + 50*numRax) && numRax <= 8) {
			addToQueue(UnitType.Terran_Barracks, bm.getBaseAtIndex(0).getCC().getTilePosition());
		}
		
		debug();
		
	}
	public void onFrameMacro() {
		
		debug();
		
		 if (self.supplyUsed()/2 > 32 && !bio) {
			 midGame = true;
		 }
		
		//Update unit numbers on every frame
		int numSCVs = 0;
		int numRax = 0;
		int numFact = 0;
		int numShops = 0;
		int numRines = 0;
		int numMedics = 0;
		//int numVults = 0;
		int numTanks = 0;
		int numGols = 0;
		
		for (Unit u : game.self().getUnits()) {
			if (u.getType() == UnitType.Terran_SCV) {numSCVs++;}
			if (u.getType() == UnitType.Terran_Medic) {numMedics++;}
			if (u.getType() == UnitType.Terran_Marine) {numRines++;}	
			//if (u.getType() == UnitType.Terran_Vulture) {numVults++;}
			if (u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode) {numTanks++;}
			if (u.getType() == UnitType.Terran_Goliath) {numGols++;}
			if (u.getType() == UnitType.Terran_Machine_Shop) {numShops++;}
			if (u.getType() == UnitType.Terran_Factory) {numFact++;}
			if (u.getType() == UnitType.Terran_Barracks) {numRax++;}
		}
		
		researchTime = new StringBuilder("Research Display:\n");
		research = new StringBuilder("\n");
		
		
		//**** Building Priority ****
		//
		// 1. BUILD QUEUE
		//If next building supply is met and build queue is empty, add it to the build queue\
		checkBuildOrder();
		
		// 2. SCVs
		trainSCVs();
		checkSCVBuild();
		
		// 3. SUPPLY MANAGEMENT
		/*
		 * If:
		 * 1. There isn't already a supply depot in the build queue
		 * 2. There are enough surplus minerals to build one
		 * 3. The supply used plus 2 supply per barracks is greater than or equal to supply total
		 * 4. The supply total is greater than or equal to 26 (two depots built already)
		 * Then add a supply depot to the queue
		 */
		boolean refineryInQueue = false;
		boolean supplyInQueue = false;
		for (int i = 0; i < bq.size(); i++) {
			if (bq.getChunk(i).getBuildingType() == UnitType.Terran_Supply_Depot) {
				supplyInQueue = true;
			}
			else if (bq.getChunk(i).getBuildingType() == UnitType.Terran_Refinery) {
				refineryInQueue = true;
			}
		}
		
		if (!supplyInQueue /*&& getSurplusMinerals() >= 100*/ && (self.supplyUsed()/2 + 2*(numRax + numFact) >= self.supplyTotal()/2) && self.supplyTotal()/2 >= 18) {
			addToQueue(UnitType.Terran_Supply_Depot, bm.getBaseAtIndex(0).getCC().getTilePosition());
		}	
		

		for (Base base : bm.getBases()) {
			for (Unit g : base.getGeysers()) {
				if (bm.sizeBase() <= 2 && !g.getType().isRefinery() && !refineryInQueue && base != bm.getBaseAtIndex(0) && base.getCC().getRemainingBuildTime() > base.getCC().getType().buildTime()/2) {
					addToQueue(UnitType.Terran_Refinery, g.getTilePosition());
				}
			}
		}
		
		// 4. RESEARCH MANAGEMENT
		//If there are surplus minerals, research Stim Packs
		for (Unit a : game.self().getUnits()) {
			if (a.isCompleted() && !a.isUpgrading() && !a.isResearching()) {
				if (a.getType() == UnitType.Terran_Academy) {
					academy = true;
					if (a.canResearch(TechType.Stim_Packs) && getSurplusMinerals() >= 150 && bio) {
						a.research(TechType.Stim_Packs);
					}
				}
				if (a.getType() == UnitType.Terran_Machine_Shop) {
					/*if (a.canResearch(TechType.Spider_Mines) && getSurplusMinerals() >= 100 && getSurplusGas() >= 100) {
						a.research(TechType.Spider_Mines);
					} else*/ if (a.canUpgrade(UpgradeType.Ion_Thrusters) && getSurplusMinerals() >= 100 && getSurplusGas() >= 100) {
						a.upgrade(UpgradeType.Ion_Thrusters);
					} else if (a.canResearch(TechType.Tank_Siege_Mode) && getSurplusMinerals() >= 150 && getSurplusGas() >= 150) {
						a.research(TechType.Tank_Siege_Mode);
					} else if (a.canUpgrade(UpgradeType.Charon_Boosters) && getSurplusMinerals() >= 100 && getSurplusGas() >= 100) {
						a.upgrade(UpgradeType.Charon_Boosters);
					} 
				}
				//* 1. An ebay has been built
				// * 2. That ebay is able to upgrade
				//* 3. There are enough surplus minerals and gas
				//* 4. We are RUSHING*********
				if (a.getType() == UnitType.Terran_Engineering_Bay) {
					//ebay = true;
					if (a.canUpgrade() && bio) {
						if (getSurplusMinerals() >= 100 && getSurplusGas() >= 100) {
							if (infantryArmor == 0) {
								a.upgrade(UpgradeType.Terran_Infantry_Armor);
								infantryArmor++;
							} else if (infantryWeapons == 0) {
								a.upgrade(UpgradeType.Terran_Infantry_Weapons);
								infantryWeapons++;
							}	
						}	
						else if (getSurplusMinerals() >= 175 && getSurplusGas() >= 175) {
							if (infantryArmor == 1) {
								a.upgrade(UpgradeType.Terran_Infantry_Armor);
								infantryArmor++;
							} else if (infantryWeapons == 1) {
								a.upgrade(UpgradeType.Terran_Infantry_Weapons);
								infantryWeapons++;
							}	
						}	
						else if (getSurplusMinerals() >= 250 && getSurplusGas() >= 250) {
							if (infantryArmor == 2) {
								a.upgrade(UpgradeType.Terran_Infantry_Armor);
								infantryArmor++;
							} else if (infantryWeapons == 2) {
								a.upgrade(UpgradeType.Terran_Infantry_Weapons);
								infantryWeapons++;
							}	
						}
					}
				}
				if (a.getType() == UnitType.Terran_Armory) {
					if (a.canUpgrade()) {
						if (getSurplusMinerals() >= 100 && getSurplusGas() >= 100) {
							if (mechArmor == 0) {
								a.upgrade(UpgradeType.Terran_Vehicle_Plating);
								mechArmor++;
							} else if (mechWeapons == 0) {
								a.upgrade(UpgradeType.Terran_Vehicle_Weapons);
								mechWeapons++;
							}	
						} else if (getSurplusMinerals() >= 175 && getSurplusGas() >= 175) {
							if (mechArmor == 1) {
								a.upgrade(UpgradeType.Terran_Vehicle_Plating);
								mechArmor++;
							} else if (mechWeapons == 1) {
								a.upgrade(UpgradeType.Terran_Vehicle_Weapons);
								mechWeapons++;
							}	
						} else if (getSurplusMinerals() >= 250 && getSurplusGas() >= 250) {
							if (mechArmor == 2) {
								a.upgrade(UpgradeType.Terran_Vehicle_Plating);
								mechArmor++;
							} else if (mechWeapons == 2) {
								a.upgrade(UpgradeType.Terran_Vehicle_Weapons);
								mechWeapons++;
								continue;
							}	
						}
					}
				}
			}	
			if (a.isUpgrading() || a.isResearching()) {
				addResearchToString(a);
			}
		}
		
		// 5. SCANNER
		//If there are at least 14 total SCVs, build a scanner
		if (numSCVs >= 14) {
			buildScanner = true;
		}
		
		// 6. MILITARY
		for (Unit b : game.self().getUnits()) {
			if (b.isCompleted() && !b.isTraining()) {
				if (b.getType() == UnitType.Terran_Factory) {
					//Prioritize Tanks over vultures. Depends on if there is a machine shop
					if (numShops < 2 && b.canBuildAddon(UnitType.Terran_Machine_Shop)) {
						b.buildAddon(UnitType.Terran_Machine_Shop);
					} //Priority: Tank, then Goliath over Vulture
					if (midGame && b.canTrain(UnitType.Terran_Siege_Tank_Tank_Mode) && getSurplusMinerals() >= 150 && getSurplusGas() >= 100) {
						b.train(UnitType.Terran_Siege_Tank_Tank_Mode);
						continue;
					}
					if (b.canTrain(UnitType.Terran_Goliath) && getSurplusMinerals() >= 150 && getSurplusGas() >= 50 && (numGols <= numTanks/2 || numGols < 6)) {
						b.train(UnitType.Terran_Goliath);
					} else if ((!midGame && getSurplusMinerals() >= 75) || (midGame && getSurplusMinerals() >= 150)) {
						b.train(UnitType.Terran_Vulture);
					}
				}
				if (b.getType() == UnitType.Terran_Barracks) {
					//Train Marines if there is a barracks and its early game. Prioritize building 1 medic per 3 marines
					if (!midGame /*&& self.supplyUsed()/2 <= 26*/) {
						if (getSurplusMinerals() >= 50 && getSurplusGas() >= 25 && academy && bio && 3*numMedics < numRines) {
							if (b.canTrain(UnitType.Terran_Medic)) {
								b.train(UnitType.Terran_Medic);
								continue;
							}
						}
						else if (getSurplusMinerals() >= 50 ) {
							if (bio) {
								b.train(UnitType.Terran_Marine);
								continue;
							}
							else if (marineCap < 4) {
								b.train(UnitType.Terran_Marine);
								marineCap++;
								continue;
							}
						}
					}
				}
			}	
		}	
	}
}
