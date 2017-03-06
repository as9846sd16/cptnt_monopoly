package Monopoly;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.io.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import Monopoly.GameP.*;
public class Client extends JFrame implements Runnable{
	private Client client;// pointer to client itself
	private String IP = "";// server ip
	private SetIPP setIPP = new SetIPP();// set ip and port panel
	
	
	//client socket
	private BufferedReader reader;//reader from server
	private PrintWriter writer;//writer to server
	private Socket server;//server
	public int playerNum;//the player number of this client
	public int turn;
	private String msg;//work mode
	private int numOfPlayers;//number of total players
	//ui
	private IDPanel idP;// panel for ID getting
	private WaitP waitP;// waiting panel
	private Thread waitT;//thread used in waitP
	private ChooseP chooseP;// panel for hero choosing
	public GameP gameP;// the ui of game
	public EndP endP;
	//game data
	public Grid grids[];// grid data
	public Player players[];// player data
	private int curHero;// the hero this client chose
	Client(){
		//setting
		setIconImage(new ImageIcon(getClass().getResource("/image/Logo.png")).getImage());
		setLayout(null);
	    setTitle("Client");
		//initialize
	    idP = new IDPanel();
	    waitP = new WaitP();
	    chooseP = new ChooseP();
	    grids = new Grid[28];
	    for(int i = 0; i<28; i++){
	    	//corner
			if(i == 0){
				grids[i] = new Start(i);
			}//jail
			else if(i == 7){
				grids[i] = new Jail(i);
			}//party
			else if(i == 14){
				grids[i] = new Party(i);
			}//travel
			else if(i == 21){
				grids[i] = new Travel(i);
			}//chance
			else if(i == 4 || i == 11 || i == 18 || i == 25){
				grids[i] = new Chance(i);
			}//land
			else{
				grids[i] = new Land(i);		
			}
		}
	    players = new Player[4];
	    for(int i = 0; i<4; i++){
	    	players[i] = new Player(i);
	    }
	    add(setIPP);
	}
	public static void main(String[] args) {
		Client client = new Client();
		client.client = client;
		client.gameP = new GameP(client);
		client.setBounds(150, 30, 1000+16, 660+34);
		client.setResizable(false);
		client.setVisible(true);
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	class SetIPP extends JPanel{
		private JLabel ipLb = new JLabel("IP:   ex.( 127.0.0.1 )");
		private JTextField ipTa = new JTextField(10);
		private JButton okBt = new JButton("OK");
		SetIPP(){
			setLayout(null);
			setBounds(0, 0, 1000,660);
			this.add(ipLb);
			ipLb.setBounds(350, 300, 150, 20);
			this.add(ipTa);
			ipTa.setBounds(500, 300, 150, 20);
			this.add(okBt);
			okBt.setBounds(450, 450, 100, 50);
			okBt.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					IP = ipTa.getText();
					client.remove(setIPP);
					client.add(waitP);
					client.waitT = new Thread(waitP);
					client.waitT.start();
					client.revalidate();
					client.repaint();
				    Thread thread = new Thread(client);
					thread.start();
				}
				
			});
		}
	}
	//client socket, listening works from server
	@Override
	public void run() {
		try {
			//"140.115.216.61""127.0.0.1"
			server = new Socket(IP,3104);
			reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
			writer = new PrintWriter(server.getOutputStream(), true);
			while(true){
				msg = getString();
				work(msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
	}
	// read integer from server
	public int getInt() throws NumberFormatException, IOException{
		return Integer.parseInt(reader.readLine());
	}
	//read string from server
	public String getString() throws IOException{
		return reader.readLine();
	}
	//send string to server
	public void println(String string){
		writer.println(string);
	}
	//send integer to server
	public void println(int i){
		writer.println(i);
	}
	//handle work
	public void work(String msg){
		int curPlayerNum;
		int toPlayerNum;
		int curGridNum;
		int curLevel;
		int curCash;
		int curTotal;
		Land land;
		try {
			switch(msg){
				//waiting
				case"Player_Number":
					numOfPlayers = getInt();
					waitP.numlb.setText("Number of players : "+ numOfPlayers + "/4");
					writer.println("Wait");
					break;
				//players all arrived change to id setting panel from waiting panel
				case"Ready":
					playerNum = getInt();
					client.setTitle("Client " + playerNum);
					waitP.isWait = false;
					client.remove(waitP);
					client.add(idP);
					break;
				// set id and change to choose hero panel
				case"ID":
					curPlayerNum = getInt();
					String curID = getString();
					players[curPlayerNum].ID = curID;
					if(curPlayerNum == playerNum){
						client.remove(idP);
						client.add(chooseP);
					}
					break;
					
					
				//choose hero and change to game panel
				case"Hero":
					int heroNum;
					curPlayerNum = getInt();
					heroNum = getInt();
					players[curPlayerNum].setHeroNum(heroNum);
					chooseP.herosBt[heroNum].setEnabled(false);
					chooseP.herosBt[heroNum].choosable = false;
					gameP.chessP[curPlayerNum].setHero(curPlayerNum, heroNum);
					if(curPlayerNum == playerNum){
						client.remove(chooseP);
						client.add(gameP);
					}
					break;
				//tell client this is whose turn and add dice button
				case"Turn":
					turn = getInt();
					if(playerNum == turn){//the player in turn
						if(players[playerNum].jailCD == 0){
							gameP.mCP.add(gameP.diceBt);
						}
						else{
							JailP jailP = (JailP)gameP.gridsP[7];
							jailP.addPayJailP();
						}
					}
					else{//else player
						println("Wait");
					}
					break;
				// get the dice result and play the dicing animation
				case "Dice" :
					int dice1 = getInt();
					int dice2 = getInt();
					gameP.mCP.remove(gameP.diceBt);
					gameP.mCP.add(gameP.diceP);
					gameP.diceP.Roll(turn, dice1, dice2);
					Thread diceTh = new Thread(gameP.diceP);
					diceTh.start();
					break;
				// get the destination and play the chess animation
				case "MoveTo" :
					curPlayerNum = getInt();
					int to = getInt();
					gameP.players[curPlayerNum].jailCD = 0;
					gameP.chessP[curPlayerNum].setDes(to);
					Thread chessTh = new Thread(gameP.chessP[curPlayerNum]);
					chessTh.start();
					break;
				// step grids
				case "Step" :
					int des = getInt();
					if (playerNum == turn) {
						//start
						if (des == 0) {
							StartP tmpP = (StartP) gameP.gridsP[des];
							tmpP.step();
						}//jail
						else if(des == 7){
							JailP tmpP = (JailP) gameP.gridsP[des];
							tmpP.step();
						}//party
						else if(des == 14){
							PartyP tmpP = (PartyP) gameP.gridsP[des];
							tmpP.step();
						}//travel
						else if(des == 21){
							TravelP tmpP = (TravelP) gameP.gridsP[des];
							tmpP.step();
						}//chance
						else if ((des - 4) % 7 == 0) {
							ChanceP tmpP = (ChanceP) gameP.gridsP[des];
							tmpP.step();
						}//land
						else {
							land = (Land)grids[des];
							//
							if((land.owner == turn || land.owner == -1) && 
							(players[turn].cash < land.price[land.checkIndex(land.level + 1)] - land.price[land.checkIndex(land.level)] ||
							land.level == 4)){
								println("Nothing");
							}
							else{
								LandP tmpP = (LandP) gameP.gridsP[des];
								tmpP.step();
							}
						}
					}
					break;
				// buy houses on land
				case "Buy" :
					curPlayerNum = getInt();
					curGridNum = getInt();
					curLevel = getInt();
					curCash = getInt();
					curTotal = getInt();
					land = (Land)grids[curGridNum];
					setMoney(curPlayerNum, curCash, curTotal);
					land.owner = curPlayerNum;
					land.level = curLevel;
					gameP.mCP.remove(gameP.curBuyP);
					break;
				// paying the fee of the land and add takeP if the current player can take
				case "Fee" :
					curGridNum = getInt();
					land = (Land)grids[curGridNum];
					//taking player
					curPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(curPlayerNum, curCash, curTotal);
					if( playerNum == curPlayerNum){
						if(land.level != 4 && curCash >= 2 * land.price[land.level]){
							LandP bt = (LandP)gameP.gridsP[curGridNum];
							gameP.mCP.remove(gameP.curTakeP);
							gameP.curTakeP = bt.takeP;
							gameP.mCP.add(gameP.curTakeP, 0);
						}
						else if(curCash >= 0){
							println("Nothing");
						}
					}
					//giving player
					toPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(toPlayerNum, curCash, curTotal);
					break;
				//take the stepped land and call buyP if the current player can buy
				case "Take":
					curGridNum = getInt();
					land = (Land)grids[curGridNum];
					//taking player
					curPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(curPlayerNum, curCash, curTotal);
					//giving player
					toPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(toPlayerNum, curCash, curTotal);
					land.owner = curPlayerNum;
					gameP.mCP.remove(gameP.curTakeP);
					if(playerNum == curPlayerNum){
						LandP bt = (LandP) gameP.gridsP[curGridNum];
						bt.step();
					}
					
					break;
				//get the player who loss
				case "Bankrupt" :
					curPlayerNum = getInt();
					for(int i = 0; i<28; i++){
						if(grids[i].owner == curPlayerNum){
							grids[i] = new Land(i);
							((LandP)gameP.gridsP[i]).setLand((Land)grids[i]);
							
						}
					}
					setMoney(curPlayerNum, 0, 0);
					break;
				// get the player who win
				case "Win":
					curPlayerNum = getInt();
					gameP.detailBP.EnableBt(false);
					endP = new EndP(curPlayerNum);
					client.add(endP, 0);
					break;
				// player pass the start grid earn some money
				case "Earn":
					curPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(curPlayerNum, curCash, curTotal);
					players[curPlayerNum].isFirstRound = false;
					break;
				//player go to jail
				case "Jail":
					curPlayerNum = getInt();
					players[curPlayerNum].jailCD = 2;
					if(curPlayerNum == playerNum){
						println("Nothing");
					}
					break;
				case "JailCD":
					curPlayerNum = getInt();
					int CD = getInt();
					players[curPlayerNum].jailCD = CD;
					break;
				// player pay to bail
				case "Bail":
					curPlayerNum = getInt();
					curCash = getInt();
					curTotal = getInt();
					setMoney(curPlayerNum, curCash, curTotal);
					players[curPlayerNum].jailCD = 0;
					if(curPlayerNum == playerNum){
						println("Dice");
					}
					break;
				// set the land's buff
				case "Party":
					curGridNum = getInt();
					if(curGridNum != -1){
						land = (Land)grids[curGridNum];
						land.buff = 1;
					}
					curGridNum = getInt();
					int buff = getInt();
					Party party = (Party)grids[14];
					party.place = curGridNum;
					land = (Land)grids[curGridNum];
					land.buff = buff;
					break;
			}
			checkRank();
			client.revalidate();
			client.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e){
			e.printStackTrace();
		}
		
	}
	//set player money from server
	public void setMoney(int num, int cash, int total){
		ChangeMoney changer = new ChangeMoney(players[num], cash, total);
		changer.start();
	}
	//check the current rank of players
	public void checkRank(){
		Player[] p = new Player[4];
		for(int i = 0; i<4; i++){
			p[i] = players[i];
		}
		for(int i = 1; i<4; i++){
			int j = i-1;
			Player tmp = p[i];
			while(j >= 0 && tmp.total < p[j].total){
				p[j+1] = p[j];
				j--;
			}
			p[j+1] = tmp;
		}
		for(int i = 0; i<4; i++){
			p[i].rank = 4 - i;
		}
	}
	//panel for waiting
		class WaitP extends JPanel implements Runnable{
			private int imNum;// stick number
			private JLabel waitlb;// wait label
			private JLabel numlb;// number of waiting player label
			private Image background;// background
			private Image stick[];//motive images
			private boolean isWait;
			WaitP(){
				//setting
				setLayout(null);
				setSize(1000,660);
				//initialize
				this.isWait = true;
				background = new ImageIcon(getClass().getResource("/image/Wait/Background.png")).getImage();
				stick = new Image[36];
				for(int i = 0; i < 36; i++)
					stick[i] = new ImageIcon(getClass().getResource("/image/Wait/Load" + i + ".png")).getImage();
				imNum = 0;
				waitlb = new JLabel("Waiting for players");
				numlb = new JLabel("Number of players : "+ numOfPlayers + "/4");
				waitlb.setFont(new Font("Rockwell Extra Bold", 1, 26));
				numlb.setFont(new Font("Rockwell Extra Bold", 1, 16));
				//setting
				waitlb.setBounds(330, 350, 340, 30);
				numlb.setBounds(375, 450, 250, 20);
				//adding
				add(waitlb);
				add(numlb);
			}
			//run a motive graph
			public void run() {
				while(this.isWait) {
					try {
						Thread.sleep(100);
						imNum = (imNum+1)%36;
						repaint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(background, 0, 0, getWidth(),  getHeight(), null);
				g.drawImage(stick[imNum], 670, 360, 200, 20, null);
			}
		}
	//panel for id setting
	class IDPanel extends JPanel{
		private String id;//id
		private JTextField idTf;//text field for id
		private MyButton sendBt;//button to send
		private Image background;//background
		IDPanel(){
			//setting
			setLayout(null);
			setSize(1000, 660);
			//initialize
			background = new ImageIcon(getClass().getResource("/image/ID/Background.png")).getImage();
		    idTf = new JTextField(10);
		    sendBt = new MyButton("Send");
			//setting
		    idTf.setBounds(420, 415, 150, 25);
			sendBt.setBounds(400, 550 ,200, 70);
			sendBt.setContentAreaFilled(false);
		    sendBt.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					id = idTf.getText();
					writer.println("ID");
					writer.println(id);
				}
		    });
			//adding
		    add(idTf);
			add(sendBt);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
	}
	//panel for hero choosing
	class ChooseP extends JPanel{
		private HeroBt[] herosBt;// hero button array
		private MyButton confirmBt;// confirm button
		private JPanel heroP;// panel to put hero button
		private Image BgImg;//background
		ChooseP(){
			//setting
			setSize(1000, 660);
			setLayout(null);
			//initialize
			heroP = new JPanel();
			heroP.setLayout(new GridLayout(1, 4, 5, 5));
			heroP.setBounds(100, 100, 800, 300);
			heroP.setOpaque(false);
			BgImg = new ImageIcon(getClass().getResource("/image/Choose/Background.png")).getImage();
			herosBt = new HeroBt[4];
			for(int i = 0; i<4; i++){
				herosBt[i] = new HeroBt(i);
				heroP.add(herosBt[i]);
			}
			confirmBt = new MyButton("OK");
			//add and set
			confirmBt.setLocation(getWidth()/2 - 100, (int)((double)(getHeight())*3/4.0));
			add(heroP);
			add(confirmBt);
			confirmBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					println("Hero");
					println(curHero);
				}
			});
			
		}
		@Override
		protected void paintComponent(Graphics g){
			g.drawImage( BgImg, 0, 0, getWidth(), getHeight(), this);
		}
	}
	//button for hero
	class HeroBt extends JButton{
		private int heroNum;//number for hero
		public boolean choosable;//whether this hero can be choose or not
		public boolean chosen;//whether this hero is chosen
		private Image heroImg;// hero image
		HeroBt(int num){
			//initialize
			this.setOpaque(true);
			this.heroNum = num;
			setContentAreaFilled(false);
			setBorder(new LineBorder(Color.GREEN, 2));
			//initialize
			chosen = false;
			choosable = true;
			heroImg = new ImageIcon(getClass().getResource("/image/Choose/Hero" + heroNum + ".png")).getImage();
			addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					curHero = heroNum;
					for(int i = 0; i<4; i++){
						if(i == heroNum){
							chooseP.herosBt[i].chosen = true;
						}
						else{
							chooseP.herosBt[i].chosen = false;
						}
					}
					revalidate();
					repaint();
				}
			});
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			if(choosable){
				if(chosen){
					setBorder(new LineBorder(Color.RED, 2));
					this.setBorderPainted(true);
				}
				else{
					setBorder(new LineBorder(Color.GREEN, 2));
					if(getModel().isRollover()){
						this.setBorderPainted(true);
					}
					else{
						this.setBorderPainted(false);
					}
				}
			}
			else{
				heroImg = new ImageIcon(getClass().getResource("/image/Choose/DeadHero" + heroNum + ".png")).getImage();
				this.setBorderPainted(false);
			}
			g.drawImage(heroImg, 0,0,200,300,this);
		}
	}
	// end panel for winner
	class EndP extends JPanel{
		private Image endPBg;// end Background
		private Image winnerImg;// winner image
		public int winner;// winner playerNum
		EndP(int winner){
			setLayout(null);
			setOpaque(false);
			setBounds(0, 0, 1000,660);
			this.winner = winner;
			endPBg = new ImageIcon(getClass().getResource("/image/Game/WinBack.png")).getImage();
			winnerImg = new ImageIcon(getClass().getResource("/image/Game/Win" + players[winner].heroNum + ".png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
			g.setColor(Color.BLACK);
			FontMetrics fm = g.getFontMetrics();
			g.drawImage(endPBg, 0, 0, 1000, 660, this);
			g.drawImage(winnerImg, 0, 0, 1000, 660, this);
			String text = players[winner].ID;
			g.drawString(text, 500 - fm.stringWidth(text)/2, 340);
		}
	}
	//run changing money animation
	class ChangeMoney extends Thread {
		int count, desC, desT;
		Player player;
		ChangeMoney(Player player, int desC, int desT) {
			count = 20;
			this.player = player;
			this.desC = desC;
			this.desT = desT;
		}
		public void run() {
			while(count > 0) {
				try {
					Thread.sleep(50);
					player.cash += 357;
					player.total += 5737;
					count--;
					gameP.repaint();
				} catch (InterruptedException e) {
					break;
				}
			}
			player.cash = desC;
			player.total = desT;
			gameP.repaint();
		}
	}
}
