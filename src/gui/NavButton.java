package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JButton;

import enums.Constants;

public class NavButton extends JButton {
	public NavButton(String text) {
		this.setText(text);
		this.setBackground(Color.WHITE);
		this.setFont(Constants.BOXFONT);
	}
	public static void evenButtonWidth(NavButton[] buttons){
		ArrayList<NavButton> buList = new ArrayList<NavButton>();
		for(NavButton b : buttons)
			buList.add(b);
		evenButtonWidth(buList);
	}
	
	public static void evenButtonWidth(ArrayList<NavButton> buttons) {
		double width = 0;
		for(NavButton b : buttons){
			if(width< b.getMinimumSize().getWidth()){
				width = b.getMinimumSize().getWidth();
			}
		}
		for(NavButton b : buttons){
			b.setMinimumSize(new Dimension((int) width, (int) b.getPreferredSize().getHeight()));
			b.setPreferredSize(new Dimension((int) width, (int) b.getPreferredSize().getHeight()));		}
	}
	
	public void toggleColor(){
		if(this.getForeground().equals(Color.BLUE))
			this.setForeground(Color.BLACK);
		else
			this.setForeground(Color.BLUE);
	}

}