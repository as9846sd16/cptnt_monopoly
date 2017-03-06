package Monopoly;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;

//button for many use
	class MyButton extends JButton{
		private String text;//text to show on button
		private Image img;//button background
		MyButton(String text){
			this.text = text;
			img = new ImageIcon(getClass().getResource("/image/Button.png")).getImage();
			setSize(200, 70);
		}
		@Override
		protected void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(img, 0, 0, 200, 70, this);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 24));
			FontMetrics fm = g.getFontMetrics();
			int textWidth = fm.stringWidth(text);
			int textAscent = fm.getAscent();
			g.drawString(text, getWidth()/2 - textWidth/2, getHeight()/2 + textAscent/2);
		}
	}