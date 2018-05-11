import bwapi.*;

public class BuildOrderManager {
	private BuildOrder bo = new BuildOrder();	
	private BuildChunk nextChunk = new BuildChunk(0, null);
	
	public void BBS() {
		nextChunk = new BuildChunk(7, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(8, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(9, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(14, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
	}
	
	public void OneFactFE() {
		nextChunk = new BuildChunk(8, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(12, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(12, UnitType.Terran_Refinery);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(15, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(16, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(23, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(28, UnitType.Terran_Command_Center);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(28, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(32, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(34, UnitType.Terran_Academy);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(40, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(48, UnitType.Terran_Armory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(56, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		//nextChunk = new BuildChunk(64, UnitType.Terran_Command_Center);
		//bo.addChunk(nextChunk);
	}
	
	public void MechZerg() {
		nextChunk = new BuildChunk(8, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(11, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(12, UnitType.Terran_Bunker);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(14, UnitType.Terran_Refinery);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(15, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(18, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(21, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(26, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(34, UnitType.Terran_Academy);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(40, UnitType.Terran_Armory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(44, UnitType.Terran_Command_Center);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(60, UnitType.Terran_Starport);
		bo.addChunk(nextChunk);
	}
	
	public void ThreeFactVults() {
		nextChunk = new BuildChunk(8, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(11, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(11, UnitType.Terran_Refinery);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(15, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(18, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(20, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(22, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(32, UnitType.Terran_Factory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(40, UnitType.Terran_Armory);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(44, UnitType.Terran_Command_Center);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(48, UnitType.Terran_Armory);
		bo.addChunk(nextChunk);
		
	}
	
	public void BionicVsZerg() {
		//Opening: TwoRaxPressure
		nextChunk = new BuildChunk(9, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(10, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(11, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(14, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(15, UnitType.Terran_Refinery);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(18, UnitType.Terran_Academy);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(20, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(22, UnitType.Terran_Engineering_Bay);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(25, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(26, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(30, UnitType.Terran_Supply_Depot);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(34, UnitType.Terran_Command_Center);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(36, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
		nextChunk = new BuildChunk(38, UnitType.Terran_Barracks);
		bo.addChunk(nextChunk);
	}
	
	public void clear() {
		bo.clear();
	}
	
	public BuildOrder getBuildOrder() {
		return bo;
	}
	
	public String toString() {
		return bo.toString();
	}
	
}