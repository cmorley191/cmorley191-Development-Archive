package stennet.app.photoapp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PhotoCover {

	public static void main(String[] args) {
		PhotoApp.main(null);
		JFrame frame = new JFrame("StenNet PhotoCover");
		boolean[] recollection = PhotoApp.getPattern();
		
		Dimension photoDimension = PhotoApp.photoDimension;
		int[] display = new int[recollection.length];
		for (int i = 0; i < recollection.length; i++)
			display[i] = (recollection[i]) ? 16777215 : 0;
		BufferedImage image = new BufferedImage(photoDimension.width,
				photoDimension.height, BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, photoDimension.width,
				photoDimension.height, display);
		JLabel recallPhotoLabel = new JLabel();
		recallPhotoLabel.setIcon(new ImageIcon(image));
		JPanel panel = new JPanel();
		frame.add(panel);
		panel.setLayout(new BorderLayout());
		panel.add(recallPhotoLabel, BorderLayout.CENTER);
		frame.setSize(1800, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
