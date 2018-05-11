import java.util.ArrayList;
import bwapi.*;

public class MineralChunk {
	private int maxMinSCVs = 2;
	private ArrayList<Unit> minSCVs = new ArrayList<Unit>();	
	private Unit mineral;
	private Game game;
	
	public MineralChunk(Unit mineral, Game game) {
		this.mineral = mineral;
		this.game = game;
	}
	
	public Unit getMineral() {return mineral;}
	
	public ArrayList<Unit> getMinSCVs() {return minSCVs;}
	
	public int getMaxMinSCVs() {return maxMinSCVs;}
	
	public void setMinSCVs(ArrayList<Unit> s) {
		minSCVs = s;
	}
	
	public void addMinSCV(Unit s) {
		minSCVs.add(s);
	}
	
	public void removeMinSCV(Unit s) {
		minSCVs.remove(s);
	}
	
	public void debug() {
		game.drawTextMap(mineral.getPosition(), String.valueOf(minSCVs.size()));
		for (Unit s : minSCVs) {
			game.drawCircleMap(s.getPosition(), 12, Color.Cyan);
			game.drawLineMap(s.getPosition(), mineral.getPosition(), Color.White);
		}
	}
}
