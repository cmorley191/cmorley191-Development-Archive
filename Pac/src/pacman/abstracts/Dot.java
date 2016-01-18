package pacman.abstracts;

import pacman.finals.Level;

@SuppressWarnings("serial")
public abstract class Dot extends GameObject {
	public abstract void eaten(Level level);
}
