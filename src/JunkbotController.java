import java.util.*;
import bwapi.*;
import bwta.BWTA;

/*
 * JUNKBOT
 * 
 * Author	:	Ben Harruff
 * Date		:	05/07/2018
 * Version	:	v1.0.1	
 * 
 * Made with love
 * 
 */
public class JunkbotController extends DefaultBWListener {

    private Mirror mirror = new Mirror();
    private Game game;
    private Player self;
    
    private InformationManager infoManager = new InformationManager();
    private BuildOrderManager buildOrderManager = new BuildOrderManager();
    private ProductionManager prodManager = new ProductionManager(); 
    
    private SquadManager squadManager = new SquadManager();
    private ScannerManager scanManager = new ScannerManager();
 
    private ArrayList<Unit> rax = new ArrayList<Unit>(),  
    						depots = new ArrayList<Unit>(), 
    						cc = new ArrayList<Unit>(), 
    						queue = new ArrayList<Unit>();
    
    private boolean pastFirstFrame = false;
    
    private StringBuilder 	units = new StringBuilder("Units Display:\n"), 		num_units = new StringBuilder("\n"),
    						queue_units = new StringBuilder("\nQueue:\n"), 		queue_time = new StringBuilder("\n\n");
    
	private boolean pastStart;

    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    public Game getGame() {return game;}
    public Player getSelf() {return self;}
    public InformationManager getInformationManager() {return infoManager;}
    public BuildOrderManager getBuildOrderManager() {return buildOrderManager;}
    public ProductionManager getProductionManager() {return prodManager;}
    
    public void debugOverlay() {
    	//UNITS 
        if (prodManager.sizeSCV() != 0) {	num_units.append(prodManager.sizeSCV()).append("\n"); 
											units.append(UnitType.Terran_SCV).append("\n");}
		if (squadManager.sizeRine() != 0) {	num_units.append(squadManager.sizeRine()).append("\n"); 
											units.append(UnitType.Terran_Marine).append("\n");}
		if (squadManager.sizeMedic() != 0) {num_units.append(squadManager.sizeMedic()).append("\n"); 
											units.append(UnitType.Terran_Medic).append("\n");}
		if (squadManager.sizeVult() != 0) {	num_units.append(squadManager.sizeVult()).append("\n"); 
											units.append(UnitType.Terran_Vulture).append("\n");}
		if (squadManager.sizeTank() != 0) {	num_units.append(squadManager.sizeTank()).append("\n"); 
											units.append(UnitType.Terran_Siege_Tank_Tank_Mode).append("\n");}
		if (squadManager.sizeGol() != 0) {	num_units.append(squadManager.sizeGol()).append("\n"); 
											units.append(UnitType.Terran_Goliath).append("\n");}
	
    	//UNIT QUEUE
		for (int i = 0; i < queue.size(); i++) {
        	queue_time.append( queue.get(i).getRemainingBuildTime()).append("\n");
        	queue_units.append(queue.get(i).getType()).append("\n");}
	        
        num_units.append(queue_time);
        units.append(queue_units);
    	
        //draw my units on screen
        game.drawTextScreen(5, 25, num_units.toString());  //UNITS
        game.drawTextScreen(35, 25, units.toString());		//       
    }
    
    public void resetStringBuilders() {
		units = new StringBuilder("Units Display:\n"); 		num_units = new StringBuilder("\n");
		queue_units = new StringBuilder("\nBuild Queue:\n"); 		queue_time = new StringBuilder("\n\n");
    }
    
    @Override
    public void onUnitCreate(Unit u) {     	
    	if (u.getPlayer() == self) {
    		//Building arrays
        	if (u.getType() == UnitType.Terran_Command_Center	) 	{
        		if (pastFirstFrame) {
        			prodManager.addCC(u); 
            		infoManager.determineQuadrant(prodManager.getCC(0).getTilePosition()); 
            		/*findStartLocations(cc.get(0));*/
        		}	
        	}
        	//if (u.getType().isRefinery()						)	{prodManager.addRefin(u);} can't add because its a morph (?)
    		if (u.getType() == UnitType.Terran_Supply_Depot		) 	{depots.add(u);}
    		if (u.getType() == UnitType.Terran_Barracks			) 	{rax.add(u);}
    		if (u.getType() == UnitType.Terran_Comsat_Station	) 	{scanManager.add(u);}
    		
    		//Military arrays
    		if (u.getType() == UnitType.Terran_SCV					) 	{prodManager.addSCV(u);}
    		if (u.getType() == UnitType.Terran_Marine 				)	{squadManager.addRine(u);}
    		if (u.getType() == UnitType.Terran_Medic 				)	{squadManager.addMedic(u);}
    		if (u.getType() == UnitType.Terran_Vulture 				)	{squadManager.addVult(u);}
    		if (u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode	)	{squadManager.addTank(u);}
    		if (u.getType() == UnitType.Terran_Goliath				)	{squadManager.addGol(u);}
        	
    		if (!u.isCompleted()) {
        		queue.add(u);
        	}
    	}  	
    }
    
    @Override
    public void onUnitComplete(Unit u) {
    	if (u.getType() == UnitType.Terran_Refinery) {
    		prodManager.addRefin(u);
    	}
    	if (u.isCompleted()) {            
        	queue.remove(u);   	
    	}    	
    }
    
    @Override
    public void onUnitDestroy(Unit u) {
    	//Building arrays
    	
    	//DOES NOT FIX PROBLEM ???
    	infoManager.onUnitDestroy(u);
    	
    	if (u.getType() == UnitType.Terran_Command_Center	) 	{prodManager.removeCC(u);}
       	if (u.getType().isRefinery()						)	{prodManager.removeRefin(u);}
    	if (u.getType() == UnitType.Terran_Supply_Depot		) 	{depots.remove(u);}
        if (u.getType() == UnitType.Terran_Barracks			) 	{rax.remove(u);}
        if (u.getType() == UnitType.Terran_Comsat_Station	) 	{scanManager.remove(u);}
        
        //Military arrays
        if (u.getType() == UnitType.Terran_SCV				) 	{prodManager.removeSCV(u);}
        if (u.getType() == UnitType.Terran_Marine 			)	{squadManager.removeRine(u);}
        if (u.getType() == UnitType.Terran_Medic 			)	{squadManager.removeMedic(u);}
        if (u.getType() == UnitType.Terran_Vulture 			)	{squadManager.removeVult(u);}
        if (u.getType() == UnitType.Terran_Siege_Tank_Tank_Mode || u.getType() == UnitType.Terran_Siege_Tank_Siege_Mode		)	{squadManager.removeTank(u);}
        if (u.getType() == UnitType.Terran_Goliath 			)	{squadManager.removeGol(u);}
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        game.setLocalSpeed(5);
        self = game.self();
        BWTA.readMap();
        BWTA.analyze();
        game.enableFlag(1);
        
        infoManager = new InformationManager();
    	buildOrderManager = new BuildOrderManager();
    	prodManager = new ProductionManager(); 
    	squadManager = new SquadManager();
    	scanManager = new ScannerManager();
    
        //Clear all unit arrays
        queue.clear();
        
    	prodManager.setGame(game);
    	prodManager.onStart();

    	squadManager.clear();
    	rax.clear();
    	cc.clear();
    	depots.clear();  	
    	buildOrderManager.clear();
    	scanManager.clear();
    	
    	infoManager.setGame(game);
    	infoManager.onStart();
    	squadManager.setOurInformation(infoManager.getOurBase(), infoManager.getOurNatExpo());
    	squadManager.setChokes(infoManager.getChokepoints());
    	scanManager.setOurInformation(infoManager.getOurBase(), infoManager.getOurNatExpo());
    	prodManager.setOurInformation(infoManager.getOurBase(), infoManager.getOurNatExpo());
    	
    	if (infoManager.getEnemyBase() != null && infoManager.getEnemyNatExpo() != null) {
    		squadManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    		scanManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    		prodManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    	}
    	
    	//Set opening buildOrder to ...
    	if (game.enemy().getRace() == Race.Terran) {
        	buildOrderManager.OneFactFE();
    	} else if (game.enemy().getRace() == Race.Protoss) {
    		buildOrderManager.ThreeFactVults();
    	} else if (game.enemy().getRace() == Race.Zerg) {
        	buildOrderManager.MechZerg();
        	//prodManager.setBio();
        	//squadManager.setBio();
    	} else {
        	buildOrderManager.OneFactFE();
    	}

    	squadManager.setGame(game);
    	scanManager.setGame(game);

    	prodManager.setBuildOrder(buildOrderManager.getBuildOrder());
    	
    	pastStart = false;
    	pastFirstFrame = false;
    	
    	game.sendText("glhf");
    }
    
    @Override
    public void onEnd(boolean isWinner) {
    	game.printf("ggwp, " + game.enemy().getName() + "!");
    }
	
    @Override
    public void onFrame() {

    	pastFirstFrame = true;
    	
    	if (self.supplyUsed()/2 >= 13 && pastStart == false) {
    		game.setLocalSpeed(20);
    		pastStart  = true;
    	}
    	
    	resetStringBuilders();
        //game.drawTextScreen(5, 10, "Playing as " + self.getName() + " - " + self.getRace()); 
        
    	infoManager.onFrame();
    	
    	if (prodManager.getBaseManager() != null) {
    		if (prodManager.getBaseManager().getScoutManager() != null) {
    			if (prodManager.getBaseManager().getScoutManager().getEnemyBase() != null 
    			&& prodManager.getBaseManager().getScoutManager().getEnemyNatExpo() != null) {
    				infoManager.setEnemyBase(prodManager.getBaseManager().getScoutManager().getEnemyBase());
    				infoManager.setEnemyNatExpo(prodManager.getBaseManager().getScoutManager().getEnemyNatExpo());
    			}
    		}
    	}
    	
    	if (infoManager.getEnemyBase() != null && infoManager.getEnemyNatExpo() != null) {
    		squadManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    		scanManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    		prodManager.setEnemyInformation(infoManager.getEnemyBase(), infoManager.getEnemyNatExpo());
    	}
    	
    	squadManager.updateEnemyMemory(infoManager.getEnemyMemory());
    	scanManager.updateEnemyMemory(infoManager.getEnemyMemory());
    	prodManager.updateEnemyMemory(infoManager.getEnemyMemory());
    	
        // BUILD ORDER!!!
    	prodManager.onFrameBase(); 
    	prodManager.onFrameMacro();
    	
    	if (prodManager.getBaseManager().getRepairManagerForSquad() != null) {
        	squadManager.setRepairManager(prodManager.getBaseManager().getRepairManagerForSquad());
    	}

    	squadManager.onFrame();
    	scanManager.onFrame();
    	
        debugOverlay();
    }

    public static void main(String[] args) {
        new JunkbotController().run();
    }
}