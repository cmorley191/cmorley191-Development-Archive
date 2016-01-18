package pacman.children;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import pacman.abstracts.Sprite;
import pacman.abstracts.TriggerSpace;
import pacman.finals.Coordinate;
import pacman.finals.Pac.KeyBinding;
import pacman.finals.ResourceManager;
import pacman.finals.ResourceManager.ImageKey;
import pacman.finals.Direction;

@SuppressWarnings("serial")
public final class Pacman extends Sprite implements KeyListener {
	private Direction selectedDirection = this.direction;
	private boolean[] directionKeys = new boolean[4];

	private FlagInterceptMovementFlag corneringInterceptFlag = new FlagInterceptMovementFlag();
	private EnteredNewTileMovementFlag keyUpdateNewTileFlag = new EnteredNewTileMovementFlag();
	private EndOfFrameMovementFlag corneringFrameFinishFlag = new EndOfFrameMovementFlag();
	private ImmediateMovementFlag keyUpdateFlag = new ImmediateMovementFlag();

	public Pacman(boolean[][] legalSpaces, ArrayList<TriggerSpace> triggerSpaces) {
		super(legalSpaces, triggerSpaces);
		this.setSize(12, 13);
		this.movementFlags.add(corneringInterceptFlag);
		this.movementFlags.add(keyUpdateNewTileFlag);
		this.movementFlags.add(corneringFrameFinishFlag);
		direction = Direction.LEFT;
		selectedDirection = Direction.LEFT;
	}

	public class FlagInterceptMovementFlag extends MovementFlag {

		// Executes along with the next flag.
		@Override
		public double findDistanceToFlag(double travelDistance) {
			double minDistance = Double.POSITIVE_INFINITY;
			for (int a = 0; a < movementFlags.size(); a++) {
				if (movementFlags.get(a) instanceof FlagInterceptMovementFlag)
					continue;
				double distance = movementFlags.get(a).findDistanceToFlag(
						travelDistance);
				if (distance > travelDistance
						|| distance < 0
						|| movementFlags.get(a) instanceof ImmediateMovementFlag)
					continue;
				if (distance < minDistance)
					minDistance = distance;
			}
			return minDistance;
		}

	}

	public class EndOfFrameMovementFlag extends MovementFlag {

		// Executes after all the traveldistance is done.
		@Override
		public double findDistanceToFlag(double travelDistance) {
			return travelDistance;
		}

	}

	private double getCorneringOffset() {
		// Returns the distance from the center line of the tile currently in
		if (direction == Direction.UP || direction == Direction.DOWN)
			return (getPosition().getX() - ((int) getPosition().getX() + 0.5));
		else if (direction == Direction.LEFT || direction == Direction.RIGHT)
			return (getPosition().getY() - ((int) getPosition().getY() + 0.5));
		else
			return 0;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(ResourceManager.get(ImageKey.Pacman), 0, 0, null);
	}

	@Override
	protected void handleMovementFlag(MovementFlag flag,
			double travelledDistance) {
		super.handleMovementFlag(flag, travelledDistance);
		if (flag == keyUpdateNewTileFlag || flag == keyUpdateFlag) {
			if (flag == keyUpdateFlag)
				movementFlags.remove(keyUpdateFlag);
			if (direction != selectedDirection) {
				switch (selectedDirection) {
				case UP:
					if (!positionInLegalBounds(getPosition()
							.subtractCoordinate(new Coordinate(0, 1)))
							|| legalSpaces[(int) getPosition().getX()][(int) getPosition()
									.getY() - 1])
						direction = selectedDirection;
					break;
				case LEFT:
					if (!positionInLegalBounds(getPosition()
							.subtractCoordinate(new Coordinate(1, 0)))
							|| legalSpaces[(int) getPosition().getX() - 1][(int) getPosition()
									.getY()])
						direction = selectedDirection;
					break;
				case DOWN:
					if (!positionInLegalBounds(getPosition().combineCoordinate(
							new Coordinate(0, 1)))
							|| legalSpaces[(int) getPosition().getX()][(int) getPosition()
									.getY() + 1])
						direction = selectedDirection;
					break;
				case RIGHT:
					if (!positionInLegalBounds(getPosition().combineCoordinate(
							new Coordinate(1, 0)))
							|| legalSpaces[(int) getPosition().getX() + 1][(int) getPosition()
									.getY()])
						direction = selectedDirection;
					break;
				}
			}
		} else if (flag == corneringInterceptFlag) {
			processCorneringMovement(travelledDistance);
		} else if (flag == corneringFrameFinishFlag) {
			processCorneringMovement(travelledDistance);
		}
	}

	private void processCorneringMovement(double travelledDistance) {
		double offset = getCorneringOffset();
		if (direction == Direction.UP || direction == Direction.DOWN) {
			if (offset < 0) {
				setPosition(

				getPosition().combineCoordinate(
						new Coordinate(Math.min(travelledDistance,
								Math.abs(offset)), 0)));
			} else if (offset > 0) {
				setPosition(

				getPosition().subtractCoordinate(
						new Coordinate(Math.min(travelledDistance,
								Math.abs(offset)), 0)));
			}
		} else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
			if (offset < 0) {
				setPosition(

				getPosition().combineCoordinate(
						new Coordinate(0, Math.min(travelledDistance,
								Math.abs(offset)))));
			} else if (offset > 0) {
				setPosition(

				getPosition().subtractCoordinate(
						new Coordinate(0, Math.min(travelledDistance,
								Math.abs(offset)))));
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyBinding.PacMoveUp.getKeycode()) {
			selectedDirection = Direction.UP;
			movementFlags.add(keyUpdateFlag);
			directionKeys[0] = true;
		} else if (keycode == KeyBinding.PacMoveLeft.getKeycode()) {
			selectedDirection = Direction.LEFT;
			movementFlags.add(keyUpdateFlag);
			directionKeys[1] = true;
		} else if (keycode == KeyBinding.PacMoveDown.getKeycode()) {
			selectedDirection = Direction.DOWN;
			movementFlags.add(keyUpdateFlag);
			directionKeys[2] = true;
		} else if (keycode == KeyBinding.PacMoveRight.getKeycode()) {
			selectedDirection = Direction.RIGHT;
			movementFlags.add(keyUpdateFlag);
			directionKeys[3] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Make it so that when no keys are being pressed, there is no change in
		// direction
		int keycode = e.getKeyCode();
		if (keycode == KeyBinding.PacMoveUp.getKeycode()) {
			directionKeys[0] = false;
		} else if (keycode == KeyBinding.PacMoveLeft.getKeycode()) {
			directionKeys[1] = false;
		} else if (keycode == KeyBinding.PacMoveDown.getKeycode()) {
			directionKeys[2] = false;
		} else if (keycode == KeyBinding.PacMoveRight.getKeycode()) {
			directionKeys[3] = false;
		}
		for (int a = 0; a < 4; a++) {
			if (directionKeys[a])
				return;
		}
		selectedDirection = direction;
	}

	@Override
	protected double getSubjectiveSpeed() {
		return SPRITE_MAX_SPEED * 0.8;
	}
}
