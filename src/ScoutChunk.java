//import java.util.*;
import bwapi.*;

public class ScoutChunk {
	private Unit scout;
	private Position targetPos;
	private TilePosition targetTile;
	//private Position ourNat;
	//private Position targetNat;
	boolean targetScouted = false;
	static boolean scoutingMain = false;
	private int framesStill = 0;
	private Game game;
  //private Player self;
	
	public ScoutChunk(Unit scout, Position target, Game game, boolean main) {
		this.scout = scout;
		targetPos = target;
		targetTile = targetPos.toTilePosition();
		this.game = game;
		//self = this.game.self();
		scoutingMain = main;
	}
	
	public Unit getScout() {return scout;}
	public Position getTargetPosition() {return targetPos;}
	public TilePosition getTargetTilePosition() {return targetTile;}
	
	public int getFramesSpentNotMoving() {
		return framesStill;
	}
	
	public void setFramesSpentNotMoving(int f) {
		framesStill = f;
	}
	
	public void setTargetScouted(boolean f) {
		targetScouted = f;
	}
	
	public boolean getTargetScouted() {
		return targetScouted;
	}
	
	public boolean isScoutingMain() {
		return scoutingMain;
	}
	
	public void setScout(Unit s) {
		scout = s;
	}
	
	public void debug() {
		if (targetPos != null && scout.exists()) {
			Position sPos = scout.getPosition();
			game.drawLineMap(sPos, targetPos, Color.White);
			game.drawTextMap(new Position(scout.getPosition().getX() + scout.getType().width(), scout.getPosition().getY() + scout.getType().height()), String.valueOf(framesStill));
		}
	}
}
