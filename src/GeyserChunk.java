import java.util.ArrayList;
import bwapi.*;

public class GeyserChunk {
	private int maxGasSCVs = 0;
	private ArrayList<Unit> gasSCVs = new ArrayList<Unit>();	
	private Unit geyser;
	private boolean isRefinery = false;
	
	private Game game;
	//private Player self;
	
	public GeyserChunk(Unit geyser, Game game) {
		this.geyser = geyser;
		this.game = game;
	}
	
	public Unit getGeyser() {return geyser;}
	
	public ArrayList<Unit> getGasSCVs() {return gasSCVs;}
	
	public int getMaxGasSCVs() {return maxGasSCVs;}
	
	public void setIsRefinery(boolean set) {
		isRefinery = set;
		if (set) {
			maxGasSCVs = 3;
		} else {
			maxGasSCVs = 0;
		}
	}
	
	public boolean isRefinery() {
		return isRefinery;
	}
	
	public void setGasSCVs(ArrayList<Unit> s) {
		gasSCVs = s;
	}
	
	public void addGasSCV(Unit s) {
		gasSCVs.add(s);
	}
	
	public void removeGasSCV(Unit s) {
		gasSCVs.remove(s);
	}
	
	public void debug() {
		game.drawTextMap(geyser.getPosition(), String.valueOf(gasSCVs.size()));
		for (Unit s : gasSCVs) {
			game.drawCircleMap(s.getPosition(), 12, Color.Green);
			game.drawLineMap(s.getPosition(), geyser.getPosition(), Color.White);
		}

	}
}