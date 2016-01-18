package stennet.app.photoapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import stennet.neural.HopfieldNetwork;

/**
 * Swing interface for training and recalling grayscale data from photos in a
 * Hopfield neural network.
 * 
 * @author Charlie Morley
 *
 */
public final class PhotoApp implements ActionListener {

	private static JFrame frame;
	private static JLabel responseLabel;
	private static JLabel photoLabel;
	private static JLabel recallPhotoLabel;
	private static JButton trainButton;
	private static JButton recallButton;

	static Dimension photoDimension = null;
	private static HopfieldNetwork network = null;

	public static void main(String[] args) {
		frame = new JFrame("StenNet PhotoApp");
		frame.setSize(1800, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel mainPanel = new JPanel();
		frame.add(mainPanel);
		mainPanel.setLayout(new BorderLayout());

		responseLabel = new JLabel();
		mainPanel.add(responseLabel, BorderLayout.NORTH);

		JPanel photoPanel = new JPanel();
		mainPanel.add(photoPanel, BorderLayout.CENTER);
		photoLabel = new JLabel();
		photoPanel.add(photoLabel);
		recallPhotoLabel = new JLabel();
		photoPanel.add(recallPhotoLabel);

		PhotoApp instance = new PhotoApp();
		JPanel buttonPanel = new JPanel();
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		trainButton = new JButton("Train");
		buttonPanel.add(trainButton);
		trainButton.addActionListener(instance);
		recallButton = new JButton("Recall");
		buttonPanel.add(recallButton);
		recallButton.addActionListener(instance);

		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == (trainButton)) {
			boolean[] pattern = getPattern();
			if (pattern == null)
				return;

			responseLabel.setText("Training...");
			if (network == null)
				network = new HopfieldNetwork(photoDimension.width * photoDimension.height);
			network.trainPattern(pattern);
			recallPhotoLabel.setIcon(null);

			responseLabel.setText("Trained!");
		} else if (e.getSource() == (recallButton)) {
			boolean[] pattern = getPattern();
			if (pattern == null)
				return;

			responseLabel.setText("Recalling...");
			boolean[] recollection = network.getNearestPattern(pattern);

			responseLabel.setText("Displaying...");
			int[] display = new int[recollection.length];
			for (int i = 0; i < recollection.length; i++)
				display[i] = (recollection[i]) ? 16777215 : 0;
			BufferedImage image = new BufferedImage(photoDimension.width,
					photoDimension.height, BufferedImage.TYPE_INT_RGB);
			image.getRaster().setDataElements(0, 0, photoDimension.width,
					photoDimension.height, display);
			recallPhotoLabel.setIcon(new ImageIcon(image));

			responseLabel.setText("Recalled!");
		}
	}

	static boolean[] getPattern() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilter() {

			private String[] extensions = { "jpg", "png", "gif" };

			@Override
			public boolean accept(File f) {
				for (String extension : extensions)
					if (f.getName().toLowerCase().endsWith(extension))
						return true;
				return false;
			}

			@Override
			public String getDescription() {
				return "";
			}

		});
		int command = fc.showOpenDialog(frame);
		if (command == JFileChooser.APPROVE_OPTION) {
			responseLabel.setText("Receiving...");
			File file = fc.getSelectedFile();
			BufferedImage image;
			try {
				image = ImageIO.read(file);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				responseLabel.setText("Error receiving image.");
				e1.printStackTrace();
				return null;
			}
			photoLabel.setIcon(new ImageIcon(image));

			responseLabel.setText("Analyzing...");
			if (photoDimension == null) {
				photoDimension = new Dimension(image.getWidth(),
						image.getHeight());
			} else if (image.getWidth() != photoDimension.width
					|| image.getHeight() != photoDimension.height) {
				responseLabel.setText("Error: wrong photo size.");
				return null;
			}

			responseLabel.setText("Scanning...");
			boolean[] pattern = new boolean[photoDimension.width
					* photoDimension.height];
			for (int x = 0; x < photoDimension.width; x++) {
				for (int y = 0; y < photoDimension.height; y++) {
					int intensity = image.getRGB(x, y);
					intensity = (((intensity >> 16) & 0xFF)
							+ ((intensity >> 8) & 0xFF) + (intensity & 0xFF)) / 3;
					pattern[(y * photoDimension.height) + x] = intensity > 100;
				}
			}
			return pattern;
		} else {
			return null;
		}
	}
}
