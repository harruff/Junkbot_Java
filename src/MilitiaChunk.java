import java.util.*;
import bwapi.*;

public class MilitiaChunk {
	private ArrayList<Unit> scvs = new ArrayList<Unit>();
	private Unit target;
	
	private Game game;
	private Player self;
	
	public MilitiaChunk(ArrayList<Unit> scvs, Unit target, Game game) {
		this.scvs = scvs;
		this.target = target;
		this.game = game;
	}
	
	public ArrayList<Unit> getMySCVs() {return scvs;}
	public void setMySCVs(ArrayList<Unit> s) {
		scvs = s;
	}
	public void removeSCV(Unit s) {
		scvs.remove(s);
	}
	public Unit getTarget() {return target;}
	
	public void setSCVs(ArrayList<Unit> s) {
		scvs = s;
	}
	
	public void debug() {
		for (Unit s : scvs) {
			if (target != null && s.exists()) {
				Position sPos = s.getPosition();
				game.drawLineMap(sPos, target.getPosition(), Color.White);
				game.drawCircleMap(sPos, 12, Color.Red);
			}
		}
	}
}
