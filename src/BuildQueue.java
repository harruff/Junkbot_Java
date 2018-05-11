import java.util.ArrayList;
import bwapi.*;

public class BuildQueue {
	private ArrayList<BuildQueueChunk> buildQueueChunks = new ArrayList<BuildQueueChunk>();
	
	public BuildQueueChunk getChunk(int i) {
		return buildQueueChunks.get(i);
	}
	public void setBuilderAtIndex(Unit w, int i) {
		buildQueueChunks.get(i).setBuilder(w);
	}
	public void setBuildingAtIndex(Unit b, int i) {
		buildQueueChunks.get(i).setBuilding(b);
	}
	public void setBuildingTypeAtIndex(UnitType ut, int i) {
		buildQueueChunks.get(i).setType(ut);
	}
	public void setBuildLocationAtIndex(TilePosition bl, int i) {
		buildQueueChunks.get(i).setBuildLocation(bl);
	}
	public void setStartedAtIndex(boolean s, int i) {
		buildQueueChunks.get(i).setStarted(s);
	}
	
	public int size() {
		return buildQueueChunks.size();
	}
	public void addChunk(BuildQueueChunk bqc) {
		buildQueueChunks.add(bqc);
	}
	public void addChunkBeginning(BuildQueueChunk bqc) {
		ArrayList<BuildQueueChunk> current = new ArrayList<BuildQueueChunk>();
		current.add(bqc);
		for (int i = 0; i < buildQueueChunks.size(); i++) {
			current.add(buildQueueChunks.get(i));
		}
		buildQueueChunks = current;
	}
	public void removeChunkAtIndex(int i) {
		buildQueueChunks.remove(i);		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Build Queue:\n");
		for (int i = 0; i < buildQueueChunks.size(); i++) {
			sb.append(getChunk(i).toString());
		}
		return sb.toString();
	}

	
}
