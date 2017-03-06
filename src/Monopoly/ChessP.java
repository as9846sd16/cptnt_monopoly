package Monopoly;
import java.awt.Image;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

public class ChessP extends JPanel implements Runnable{
	public int x, y, des;// chess x, y, destination
	private int speed;// chess moving speed
	private Player player;// player
	private Client client;// client
	private JLabel arrowLb;// arrow label
	private Image chessIm;// chess image
	private ImageIcon arrowIm;// arrow image icon
	ChessP(Player player, Client client) {
		setLayout(null);
		setBounds(10, 10, 640, 640);
		setOpaque(false);
		this.client = client;
		this.player = player;
		chessIm = new ImageIcon(getClass().getResource("/image/Grid/Owner" + player.heroNum + ".png")).getImage();
		arrowIm = new ImageIcon(getClass().getResource("/image/Grid/Arrow.png"));
		arrowLb = new JLabel(arrowIm);
		arrowLb.setSize(40, 40);
		x = 20;
		y = 40;
		speed = 20;
	}
	//set hero number
	public void setHero(int num, int heroNum) {
		chessIm = new ImageIcon(getClass().getResource("/image/Grid/Owner" + heroNum + ".png")).getImage();
	}
	//set destination
	public void setDes(int des) {
		this.des = des;
		if(des/7 == 0)
			arrowLb.setLocation(des%7*80+20, 20);
		else if(des/7 == 1)
			arrowLb.setLocation(580, des%7*80+20);
		else if(des/7 == 2)
			arrowLb.setLocation(580-des%7*80, 580);
		else
			arrowLb.setLocation(20, 580-des%7*80);
		add(arrowLb);
		repaint();
	}
	//move
	public void run() {
		try {
			while(player.loc != des) {
				Thread.sleep(50);
				if(player.loc/7 == 0) {
					x += speed;
					if(x%80 == 20)
						player.loc = (player.loc+1)%28; 
				}
				else if(player.loc/7 == 1) {
						y += speed;
						if(y%80 == 40)
							player.loc = (player.loc+1)%28;
				}
				else if(player.loc/7 == 2) {
					x -= speed;
					if(x%80 == 20)
						player.loc = (player.loc+1)%28; 
				}
				else {
						y -= speed;
						if(y%80 == 40)
							player.loc = (player.loc+1)%28; 
				}
				revalidate();
				repaint();
			}
			remove(arrowLb);
			repaint();
			client.println("Arrive");
			client.println(player.loc);
			//remove notice panel of chance
			for(int i = 0; i<4; i++){
				GameP.ChanceP chanceP = (GameP.ChanceP)client.gameP.gridsP[4 + i*7];
				if(chanceP.noticeP != null){
					client.gameP.mCP.remove(chanceP.noticeP);
				}
			}
			client.gameP.mCP.revalidate();
			client.gameP.mCP.repaint();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(int i = 0; i < 4; i++)
			g.drawImage(chessIm, x, y, 40, 40, null);
	}
}
