import java.util.ArrayList;

public class BuildOrder {
	private ArrayList<BuildChunk> buildChunks = new ArrayList<BuildChunk>();
	
	public BuildChunk getBuildChunkatIndex(int i) {
		return buildChunks.get(i);
	}
	public int size() {
		return buildChunks.size();
	}
	
	public void addChunk(BuildChunk bc) {
		buildChunks.add(bc);
	}
	
	public void clear() {
		buildChunks.clear();
	}
	
	public String display(int bIndex) {
		StringBuilder sb = new StringBuilder();
		sb.append("Build Order:\n");
		if (buildChunks.size() >= bIndex + 4) {
			for (int i = 0; i < 4; i++) {
				if (buildChunks.get(bIndex + i) != null) {
					sb.append(buildChunks.get(bIndex + i).toString());
				}
			}
		} else if ((buildChunks.size() >= bIndex + 1)){
			for (int i = 0; i < (buildChunks.size() - bIndex); i++) {
				if (buildChunks.get(bIndex + i) != null) {
					sb.append(buildChunks.get(bIndex + i).toString());
				}
			}
		} else {
			sb.append("[Completed]");
		}
		return sb.toString();
	}
}

