import bwapi.*;

public class BuildChunk {
	private int supply;
	private UnitType building;
	
	public BuildChunk(int s, UnitType b) {
		this.supply = s;
		this.building = b;
	}
	
	public int getSupply() {
		return supply;
	}
	public UnitType getBuildingType() {
		return building;
	}
	
	public String toString() {
		String s = new String(supply + "\t- " + building + "\n");
		return s;
	}
	
}
