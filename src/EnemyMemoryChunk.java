import bwapi.*;

public class EnemyMemoryChunk {
	private Position position;
	private UnitType type;
	private Unit unit;
	
	public EnemyMemoryChunk(Position p, Unit u, UnitType t) {
		this.position = p;
		this.type = t;
		this.unit = u;
	}
	
	public Position getPosition() {
		return position;
	}
	public Unit getUnit() {
		return unit;
	}
	public UnitType getType() {
		return type;
	}
	public void setPosition(Position p) {
		position = p;
	}
	public void setUnit(Unit u) {
		unit = u;
	}
	public void setType(UnitType t) {
		type = t;
	}
	public String toString() {
		String s = new String(type + "\n");
		return s;
	}
}
