package Monopoly;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

class DiceP extends JPanel  implements Runnable{
	private DiceP dicep;
	private GameP gamep;
	private Image dice[];
	private MyButton roll;
	private Thread chessTh[];
	private int num, timer, dNum1, dNum2, dice1, dice2;
	DiceP(GameP gamep) {
		this.gamep = gamep;
		dicep = this;
		setLayout(null);
		setOpaque(false);
		setBorder(new LineBorder(Color.PINK, 2));
		setBounds(0, 0, 480, 480);
		timer = 40;
		dNum1 = 0;
		dNum2 = 5;
		dice = new Image[6];
		for(int i = 0; i < 6; i++)
			dice[i] = new ImageIcon(getClass().getResource("/image/Game/Dice" + (i+1) + ".png")).getImage();
		chessTh = new Thread[4];
		for(int i = 0; i < 4; i++)
			chessTh[i] = new Thread(gamep.chessP[i]);
	}
	public void Roll(int num, int a, int b) {
		this.num = num;
		dice1 = a;
		dice2 = b;
	}
	public void run() {
		try {
			while(true) {
				Thread.sleep(50);
				if(timer > 0) {
					dNum1 = (dNum1+1)%6;
					dNum2 = (dNum2+5)%6;
					timer--;
				}
				else {
					if(dNum1 != dice1 - 1)
						dNum1 = (dNum1+1)%6;
					if(dNum2 != dice2 - 1)
						dNum2 = (dNum2+5)%6;
					else if(dNum1 == dice1 - 1)
						break;
				}
				repaint();
			}
			repaint();
			Thread.sleep(1500);
			timer = 40;
			gamep.mCP.remove(dicep);
			gamep.client.println("Throw");
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(dice[dNum1], 160, 100, 60, 60, null);
		g.drawImage(dice[dNum2], 260, 100, 60, 60, null);
	}
}
