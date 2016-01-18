package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public final class Snake extends JPanel implements Runnable, KeyListener {

	private static JFrame gameWindow;
	private static Snake gamePanel;
	private static final Coordinate ORIGIN = new Coordinate(8, 8);
	private static final int GRID_DIMENSION = 8;
	private final static Coordinate BOARD_SIZE = new Coordinate(50, 50);

	public static void main(String[] args) {
		// Initialize the window, title of "Snake"
		gameWindow = new JFrame("Snake");

		// Set window size
		gameWindow.getContentPane().setPreferredSize(
				new Dimension((BOARD_SIZE.getX() * 2 - 1) * GRID_DIMENSION
						+ (2 * GRID_DIMENSION), (BOARD_SIZE.getY() * 2 - 1)
						* GRID_DIMENSION + (2 * GRID_DIMENSION)));
		gameWindow.setResizable(false);

		// Define that program exits when window is closed
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		gamePanel = new Snake();

		gameWindow.add(gamePanel);
		gameWindow.pack();

		gamePanel.setBackground(Color.BLACK);

		gameWindow.addKeyListener(gamePanel);

		Thread gameThread = new Thread(gamePanel);
		gameThread.start();

		// Show the window
		gameWindow.setVisible(true);
	}

	private enum GameMode {
		Start, Run, End;
	}

	/*
	 * Time measured in milliseconds (1ms = 0.001s)
	 */
	// Time between each step of snake
	private long framePeriod = 70;
	private long startPeriod = 2000;
	private long endPeriod = 4000;
	private GameMode gameMode = GameMode.Start;

	private ArrayList<Coordinate> snake;
	private ArrayList<Coordinate> snake2;
	private ArrayList<Coordinate> empty;
	private Coordinate food;
	private boolean justAte;
	private boolean justAte2;
	private boolean playerOneWins;

	@Override
	public void run() {
		long lastFrameTime;

		while (true) {
			// Start Sequence
			snake = new ArrayList<Coordinate>();
			snake2 = new ArrayList<Coordinate>();
			empty = new ArrayList<Coordinate>();
			justAte = false;
			justAte2 = false;

			for (int x = 11; x > -1; x--) {
				snake.add(new Coordinate(x, 0));
			}
			for (int x = BOARD_SIZE.getX() - 11; x < BOARD_SIZE.getX(); x++) {
				snake2.add(new Coordinate(x, BOARD_SIZE.getY() - 1));
			}
			snake2.add(new Coordinate(BOARD_SIZE.getX() - 3,
					BOARD_SIZE.getY() - 1));
			snake2.add(new Coordinate(BOARD_SIZE.getX() - 2,
					BOARD_SIZE.getY() - 1));
			snake2.add(new Coordinate(BOARD_SIZE.getX() - 1,
					BOARD_SIZE.getY() - 1));
			food = new Coordinate(BOARD_SIZE.getX() / 4 * 3,
					BOARD_SIZE.getY() / 4 * 3);
			for (int x = 0; x < BOARD_SIZE.getX(); x++) {
				for (int y = 0; y < BOARD_SIZE.getY(); y++) {
					Coordinate c = new Coordinate(x, y);
					if (!snake.contains(c) && !snake2.contains(c)
							&& !c.equals(food))
						empty.add(c);
				}
			}
			repaint();
			lastFrameTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - lastFrameTime < startPeriod)
				;
			gameMode = GameMode.Run;
			repaint();
			lastFrameTime = System.currentTimeMillis();

			// Run Sequence
			while (gameMode == GameMode.Run) {
				long currentTime = System.currentTimeMillis();

				if (currentTime - lastFrameTime >= framePeriod) {
					justAte = true;
					justAte2 = true;
					advanceSnake(snake, 1);
					advanceSnake(snake2, 2);
					checkGameMechanics();
					repaint();

					lastFrameTime = currentTime;
				}
			}

			// End sequence
			gameMode = GameMode.End;
			repaint();
			lastFrameTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - lastFrameTime < endPeriod)
				;
			gameMode = GameMode.Start;
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Display the snake
		// For each piece of the snake
		g.setColor(Color.RED);
		for (int i = 0; i < snake.size(); i++) {
			Coordinate space = snake.get(i);

			// Conversion from grid coordinate to actual - origin +
			// dimension*x
			// + dimension*numinbetweenspaces (numinbetweenspaces = x)
			int spaceX = ORIGIN.getX() + (space.getX() * GRID_DIMENSION * 2);
			int spaceY = ORIGIN.getY() + (space.getY() * GRID_DIMENSION * 2);

			// Draw the current space
			g.fillRect(spaceX, spaceY, GRID_DIMENSION, GRID_DIMENSION);

			// If there's another space after this one
			if (i < snake.size() - 1) {
				Coordinate nextSpace = snake.get(i + 1);

				// Find which direction the next space is, draw a space
				// in between
				if (nextSpace.getX() < space.getX()) {
					// Left
					g.fillRect(spaceX - GRID_DIMENSION, spaceY, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getX() > space.getX()) {
					// Right
					g.fillRect(spaceX + GRID_DIMENSION, spaceY, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getY() < space.getY()) {
					// Up
					g.fillRect(spaceX, spaceY - GRID_DIMENSION, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getY() > space.getY()) {
					// Down
					g.fillRect(spaceX, spaceY + GRID_DIMENSION, GRID_DIMENSION,
							GRID_DIMENSION);
				}
			}
		}
		// Display the snake 2
		// For each piece of the snake 2
		g.setColor(Color.BLUE);
		for (int i = 0; i < snake2.size(); i++) {
			Coordinate space = snake2.get(i);

			// Conversion from grid coordinate to actual - origin +
			// dimension*x
			// + dimension*numinbetweenspaces (numinbetweenspaces = x)
			int spaceX = ORIGIN.getX() + (space.getX() * GRID_DIMENSION * 2);
			int spaceY = ORIGIN.getY() + (space.getY() * GRID_DIMENSION * 2);

			// Draw the current space
			g.fillRect(spaceX, spaceY, GRID_DIMENSION, GRID_DIMENSION);

			// If there's another space after this one
			if (i < snake2.size() - 1) {
				Coordinate nextSpace = snake2.get(i + 1);

				// Find which direction the next space is, draw a space
				// in between
				if (nextSpace.getX() < space.getX()) {
					// Left
					g.fillRect(spaceX - GRID_DIMENSION, spaceY, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getX() > space.getX()) {
					// Right
					g.fillRect(spaceX + GRID_DIMENSION, spaceY, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getY() < space.getY()) {
					// Up
					g.fillRect(spaceX, spaceY - GRID_DIMENSION, GRID_DIMENSION,
							GRID_DIMENSION);
				} else if (nextSpace.getY() > space.getY()) {
					// Down
					g.fillRect(spaceX, spaceY + GRID_DIMENSION, GRID_DIMENSION,
							GRID_DIMENSION);
				}
			}
		}
		if (gameMode == GameMode.Start) {
			drawDefaultCenteredString(
					g,
					"Ready?",
					new Coordinate(gameWindow.getWidth() / 2, gameWindow
							.getHeight() / 2));
		} else if (gameMode == GameMode.Run) {

		} else if (gameMode == GameMode.End) {
			if (playerOneWins)
				drawDefaultCenteredString(
						g,
						"Game Over! Player 1 Wins",
						new Coordinate(gameWindow.getWidth() / 2, gameWindow
								.getHeight() / 2));

			else
				drawDefaultCenteredString(
						g,
						"Game Over! Player 2 Wins",
						new Coordinate(gameWindow.getWidth() / 2, gameWindow
								.getHeight() / 2));
		}
	}

	private void drawDefaultCenteredString(Graphics g, String s, Coordinate c) {
		g.setFont(new Font("Arial", Font.PLAIN, 18));
		FontMetrics metrics = g.getFontMetrics();
		int w = metrics.stringWidth(s);
		int h = (metrics.getAscent() + metrics.getDescent());
		int x = c.getX() - (w / 2);
		int y = c.getY() - (h / 2);
		g.setColor(Color.BLACK);
		g.fillRect(x, y - metrics.getAscent(), w, h);
		g.setColor(Color.WHITE);
		g.drawString(s, x, y);
	}

	private enum KeyBinding {

		Up(KeyEvent.VK_W, KeyEvent.VK_UP), Left(KeyEvent.VK_A, KeyEvent.VK_LEFT), Down(
				KeyEvent.VK_S, KeyEvent.VK_DOWN), Right(KeyEvent.VK_D,
				KeyEvent.VK_RIGHT), Jump(KeyEvent.VK_SPACE, KeyEvent.VK_CONTROL);

		private int keycode;
		private int keycode2;

		KeyBinding(int keycode, int keycode2) {
			this.keycode = keycode;
			this.keycode2 = keycode2;
		}

		public int getKeyCode(int player) {
			if (player == 1)
				return keycode;
			return keycode2;
		}
	}

	private boolean[] keyPressed = new boolean[65535];

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keyPressed[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed[e.getKeyCode()] = false;
	}

	private void advanceSnake(ArrayList<Coordinate> snake, int player) {
		Direction nextDirection = getNextDirection(player);
		Coordinate front = snake.get(0);
		Coordinate c;
		int jump = 1;
		if (keyPressed[KeyBinding.Jump.getKeyCode(player)]) {
			jump = 2;
			keyPressed[KeyBinding.Jump.getKeyCode(player)] = false;
		}
		switch (nextDirection) {
		case UP:
			c = new Coordinate(front.getX(), front.getY() - jump);
			snake.add(0, c);
			empty.remove(c);
			break;
		case LEFT:
			c = new Coordinate(front.getX() - jump, front.getY());
			snake.add(0, c);
			empty.remove(c);
			break;
		case DOWN:
			c = new Coordinate(front.getX(), front.getY() + jump);
			snake.add(0, c);
			empty.remove(c);
			break;
		case RIGHT:
			c = new Coordinate(front.getX() + jump, front.getY());
			snake.add(0, c);
			empty.remove(c);
			break;
		default:
			return;
		}
		if (justAte && player == 1) {
			justAte = false;
		} else if (justAte2 && player == 2) {
			justAte2 = false;
		} else {
			empty.add(snake.get(snake.size() - 1));
			snake.remove(snake.size() - 1);
		}
	}

	private enum Direction {
		UP, LEFT, DOWN, RIGHT;
	}

	private Direction getNextDirection(int player) {
		Direction currentDirection = Direction.UP;
		if (player == 1 && snake.size() > 1) {
			Coordinate first = snake.get(0);
			Coordinate second = snake.get(1);
			if (first.getX() < second.getX())
				currentDirection = Direction.LEFT;
			else if (first.getX() > second.getX())
				currentDirection = Direction.RIGHT;
			else if (first.getY() < second.getY())
				currentDirection = Direction.UP;
			else if (first.getY() > second.getY())
				currentDirection = Direction.DOWN;
		} else if (player == 2 && snake2.size() > 1) {
			Coordinate first = snake2.get(0);
			Coordinate second = snake2.get(1);
			if (first.getX() < second.getX())
				currentDirection = Direction.LEFT;
			else if (first.getX() > second.getX())
				currentDirection = Direction.RIGHT;
			else if (first.getY() < second.getY())
				currentDirection = Direction.UP;
			else if (first.getY() > second.getY())
				currentDirection = Direction.DOWN;
		}
		if (keyPressed[KeyBinding.Up.getKeyCode(player)]
				&& currentDirection != Direction.DOWN)
			return Direction.UP;
		else if (keyPressed[KeyBinding.Left.getKeyCode(player)]
				&& currentDirection != Direction.RIGHT)
			return Direction.LEFT;
		else if (keyPressed[KeyBinding.Down.getKeyCode(player)]
				&& currentDirection != Direction.UP)
			return Direction.DOWN;
		else if (keyPressed[KeyBinding.Right.getKeyCode(player)]
				&& currentDirection != Direction.LEFT)
			return Direction.RIGHT;
		return currentDirection;
	}

	private void checkGameMechanics() {

		if (snake.lastIndexOf(snake.get(0)) != 0
				|| snake2.contains(snake.get(0))) {
			gameMode = GameMode.End;
			playerOneWins = false;
		} else if (snake2.lastIndexOf(snake2.get(0)) != 0
				|| snake.contains(snake2.get(0))) {
			gameMode = GameMode.End;
			playerOneWins = true;
		}

		else {
			Coordinate front = snake.get(0);
			if (front.getX() < 0 || front.getX() >= BOARD_SIZE.getX()
					|| front.getY() < 0 || front.getY() >= BOARD_SIZE.getY()) {
				gameMode = GameMode.End;
				playerOneWins = false;
			} else {
				front = snake2.get(0);
				if (front.getX() < 0 || front.getX() >= BOARD_SIZE.getX()
						|| front.getY() < 0
						|| front.getY() >= BOARD_SIZE.getY()) {
					gameMode = GameMode.End;
					playerOneWins = true;
				}
			}
		}
	}
}
