package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

import enums.Constants;

public class RoundButton extends JButton {
	
	private Color color;
	public RoundButton(String text){
		super(text);
		this.setFont(Constants.BIGFONT.deriveFont(40));
		color = new Color(255,255,255,70);
		setBackground(color);
		this.setOpaque(false);
		this.setBorderPainted(false);
		this.setAlignmentX(CENTER_ALIGNMENT);
		JButton jButton = this;
		
        super.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton.setForeground(Color.RED);
                
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton.setForeground(Color.BLACK);
                jButton.revalidate();
                jButton.repaint();
                
            }
        });
        
        super.addActionListener(e -> {
            jButton.setForeground(Color.BLACK);
        });
        
	}
	
	public void setWidth(double width){
		setMinimumSize(new Dimension((int) width, (int) this.getPreferredSize().getHeight()));
		setPreferredSize(new Dimension((int) width, (int) this.getPreferredSize().getHeight()));
	}
	@Override
    protected void paintComponent(Graphics g) {
		int x = 0;
	    int y = 0;
	    int w = (int) this.getMinimumSize().getWidth();
	    int h = (int) this.getMinimumSize().getHeight();
	    int arc = 20;
	    Graphics2D g2 = (Graphics2D) g.create();
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);

	    g2.setColor(color);
	    g2.fillRoundRect(x, y, w, h, arc, arc);

		super.paintComponent(g);


	    g2.dispose();
        
    }

}
