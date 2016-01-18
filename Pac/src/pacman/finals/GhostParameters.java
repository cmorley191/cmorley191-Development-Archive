package pacman.finals;

import java.awt.Dimension;

import pacman.finals.ResourceManager.ImageKey;

public class GhostParameters {

	private String name;
	private ImageKey skin;
	private Dimension ghostSize;
	private ImageKey targetImageKey;
	private int personalLeavingDotLimit;
	private int globalLeavingDotLimit;

	public GhostParameters(String name, ImageKey skin, Dimension ghostSize,
			ImageKey targetImageKey, int personalLeavingDotLimit,
			int globalLeavingDotLimit) {
		this.name = name;
		this.skin = skin;
		this.ghostSize = ghostSize;
		this.targetImageKey = targetImageKey;
		this.personalLeavingDotLimit = personalLeavingDotLimit;
		this.globalLeavingDotLimit = globalLeavingDotLimit;
	}

	public String getName() {
		return name;
	}

	public ImageKey getSkin() {
		return skin;
	}

	public Dimension getGhostSize() {
		return ghostSize;
	}

	public ImageKey getTargetImageKey() {
		return targetImageKey;
	}

	public int getPersonalLeavingDotLimit() {
		return personalLeavingDotLimit;
	}

	public int getGlobalLeavingDotLimit() {
		return globalLeavingDotLimit;
	}

}
