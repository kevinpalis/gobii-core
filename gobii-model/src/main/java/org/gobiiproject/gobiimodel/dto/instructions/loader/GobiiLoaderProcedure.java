package org.gobiiproject.gobiimodel.dto.instructions.loader;

import java.util.LinkedList;
import java.util.List;

public class GobiiLoaderProcedure {

	private GobiiLoaderMetadata metadata = new GobiiLoaderMetadata();
	private List<GobiiLoaderInstruction> instructions = new LinkedList<>();

	private String jobName;

	public GobiiLoaderMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(GobiiLoaderMetadata metadata) {
		this.metadata = metadata;
	}

	public List<GobiiLoaderInstruction> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<GobiiLoaderInstruction> instructions) {
		this.instructions = instructions;
	}

	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

}