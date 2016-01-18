package pacman.finals;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public final class ResourceManager {
	private ResourceManager() {
	}

	private static final String DEFAULT_IMAGE_SUFFIX = ".png";

	private static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	private static ArrayList<ImageKey> imageKeys = new ArrayList<ImageKey>();

	public enum ImageKey {
		Pacman, Maze1, Maze1Grid, Maze1GridLegal, Blinky, Pinky, Inky, Clyde, BlinkyTarget, PinkyTarget, InkyTarget, ClydeTarget, PointDot, EnergizerDot;
	}

	public static void loadAllKeys() {
		ImageKey[] values = ImageKey.values();
		for (ImageKey key : values) {
			loadImage(key);
		}
	}

	public static void loadImages(ImageKey[] keys) {
		for (ImageKey key : keys) {
			loadImage(key);
		}
	}

	public static void loadImage(ImageKey key) {
		try {
			images.add(ImageIO.read(ResourceManager.class
					.getResourceAsStream("/" + key.name()
							+ DEFAULT_IMAGE_SUFFIX)));
			imageKeys.add(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage get(ImageKey key) {
		return images.get(imageKeys.indexOf(key));
	}
}
