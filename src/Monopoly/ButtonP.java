package Monopoly;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class ButtonP extends JPanel {
	public JButton bt[];// buttons for grids
	private ImageIcon coverIm;// cover image
	ButtonP() {
		setLayout(null);
		setBounds(10, 10, 640, 640);
		setOpaque(false);
		coverIm = new ImageIcon(getClass().getResource("/image/Grid/Cover.png"));
		bt = new JButton[28];
		for(int i = 0; i < 28; i++) {
			bt[i] = new JButton();
			bt[i].setBorderPainted(false);
			bt[i].setContentAreaFilled(false);
			if(i/7 == 0)
				bt[i].setBounds(80*i, 0, 80, 80);
			else if(i/7 == 1)
				bt[i].setBounds(560, 80*(i-7), 80, 80);
			else if(i/7 == 2)
				bt[i].setBounds(80*(21-i), 560, 80, 80);
			else
				bt[i].setBounds(0, 80*(28-i), 80, 80);
			add(bt[i]);
			
		}
	}
	//cover the button and enable false
	public void Cover(int num) {
		bt[num].setIcon(coverIm);
		bt[num].setEnabled(false);
		repaint();
	}
	//clean the cover image
	public void Clean() {
		for(int i  = 0; i < 28; i++) {
			bt[i].setIcon(null);
		}
	}
	//enable buttons true or false
	public void EnableBt(boolean enable){
		for(int i = 0; i< 28; i++){
			this.bt[i].setEnabled(enable);
		}
		revalidate();
		repaint();
	}
}
