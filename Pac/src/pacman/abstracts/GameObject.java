package pacman.abstracts;

import javax.swing.JComponent;

import pacman.finals.Coordinate;
import pacman.finals.Ghost;
import pacman.finals.Ghost.HouseStatus;

@SuppressWarnings("serial")
public abstract class GameObject extends JComponent {
	private Coordinate realPosition = new Coordinate(0, 0);
	private Coordinate gridPosition = new Coordinate(0, 0);
	private CoordinateSystem gridSystem = CoordinateSystem.Grid8;

	private enum CoordinateSystem {
		Real(1), Grid8(8);

		private int gridScale;

		CoordinateSystem(int gridScale) {
			this.gridScale = gridScale;
		}

		public int getGridScale() {
			return gridScale;
		}
	}

	public void setPosition(Coordinate location) {
		setLocation(gridSystem, location);
	}

	private void setLocation(CoordinateSystem system, Coordinate location) {
		/*
		 * Real Location refers to the top left corner of sprite in the window
		 * system (1px:1)
		 * 
		 * Grid Location refers to the middle position (round down) of the
		 * sprite in the grid system (8px:1)
		 */
		switch (system) {
		case Real: {
			realPosition = location;
			gridPosition = new Coordinate(realPosition.getX()
					/ gridSystem.getGridScale() + (getSize().getWidth() / 2.0),
					realPosition.getY() / gridSystem.getGridScale()
							+ (getSize().getHeight() / 2.0));
			break;
		}
		default: {
			gridPosition = new Coordinate(location.getX()
					* (system.getGridScale()) / (gridSystem.getGridScale()),
					location.getY() * (system.getGridScale())
							/ (gridSystem.getGridScale()));
			realPosition = new Coordinate(gridPosition.getX()
					* (gridSystem.getGridScale())
					- (getSize().getWidth() / 2.0), gridPosition.getY()
					* (gridSystem.getGridScale())
					- (getSize().getHeight() / 2.0));
			break;
		}
		}
		if (this instanceof Ghost
				&& (((Ghost) this).getHouseStatus() == HouseStatus.Idling || ((Ghost) this)
						.getHouseStatus() == HouseStatus.Leaving))
			realPosition = realPosition.combineCoordinate(new Coordinate(
					0.5 * gridSystem.getGridScale(), 0.5 * gridSystem
							.getGridScale()));
		setLocation((int) realPosition.getX(), (int) realPosition.getY());

	}

	public Coordinate getPosition() {
		return gridPosition;
	}
}
