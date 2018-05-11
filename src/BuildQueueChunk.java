import bwapi.*;

class BuildQueueChunk {
	private Unit worker, building;
	private UnitType type;
	private TilePosition buildLocation;
	private boolean started;
	private int frame;
	
	public BuildQueueChunk(Unit w, Unit b, UnitType ut, TilePosition tp, boolean st, int f) {
		this.worker = w;
		this.building = b;
		this.type = ut;
		this.buildLocation = tp;
		this.started = st;
		this.frame = f;
	}
	
	public Unit getBuilder() {
		return worker;
	}
	public Unit getBuilding() {
		return building;
	}
	public UnitType getBuildingType() {
		return type;
	}
	public TilePosition getBuildLocation() {
		return buildLocation;
	}
	public boolean isStarted() {
		return started;
	}
	public int getFrameStarted() {
		return frame;
	}
	
	public void setBuilder(Unit w) {
		worker = w;
	}
	public void setBuilding(Unit b) {
		building = b;
	}
	public void setType(UnitType ut) {
		type = ut;
	}
	public void setBuildLocation(TilePosition bl) {
		buildLocation = bl;
	}
	public void setStarted(boolean s) {
		started = s;
	}
	public void setFrameStarted(int f) {
		frame = f;
	}
	
	public String toString() {
		String s = new String(type + "\n");
		return s;
	}
}
