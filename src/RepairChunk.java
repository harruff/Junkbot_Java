//import java.util.*;
import bwapi.*;

public class RepairChunk {
	private Unit repairman;
	private Unit repairTarget;
	private Game game;
	
	public RepairChunk(Unit scv, Unit target, Game game) {
		repairman = scv;
		this.game = game;
		repairTarget = target;
	}
	
	public Unit getRepairman() {return repairman;}
	
	public Unit getRepairTarget() {return repairTarget;}
	
	public void setRepairman(Unit s) {
		repairman = s;
	}
	
	public void setRepairTarget(Unit s) {
		repairTarget = s;
	}
	
	public void debug() {
		if (repairTarget != null) {
			Position rmPos = repairman.getPosition();
			Position rtPos = repairTarget.getPosition();
			
			game.drawLineMap(rmPos, rtPos, Color.White);
			
			game.drawBoxMap(new Position(rtPos.getX() - 2, rtPos.getY()), new Position(rtPos.getX()+24, rtPos.getY()+14), Color.Black, true);
			float percentage = (repairTarget.getHitPoints() * 100.0f) / repairTarget.getType().maxHitPoints();
			game.drawTextMap(rtPos, String.valueOf(String.format("%.1f", percentage)) + "%");
		}
	}

}
