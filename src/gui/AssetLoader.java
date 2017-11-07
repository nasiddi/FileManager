package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import enums.Constants;

public class AssetLoader {

	public AssetLoader() {
		loadBackGround();
	}

	private static BufferedImage background;

	static JLabel getBackground() {
		loadBackGround();
		return packBufferedImageIntoJLabel(background);
	}

	public static void loadBackGround() {
		if (new File(Constants.SERIESDIR).exists())
			Constants.BACKGROUNDIMAGE = Constants.BACKGROUNDSTD;
		else
			Constants.BACKGROUNDIMAGE = Constants.BACKGROUNDCON;

		background = loadImage(Constants.BACKGROUNDIMAGE);
	}

	private static BufferedImage loadImage(String uri) {
		try {
			return ImageIO.read(new File(uri));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JLabel packBufferedImageIntoJLabel(BufferedImage img) {
		return new JLabel(new ImageIcon(img));
	}
}
