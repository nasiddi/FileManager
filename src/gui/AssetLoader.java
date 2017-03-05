package gui;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class AssetLoader {
	
	public AssetLoader(){
		loadBackGround();
	}
	
	private static BufferedImage background;

	public static JLabel getBackground(){
		return packBufferedImageIntoJLabel(background);
	}
	
	
	public void loadBackGround(){
		background = loadImage("assets/background.jpg");
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
