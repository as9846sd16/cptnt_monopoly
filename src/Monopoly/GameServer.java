package Monopoly;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class GameServer extends JFrame {
	private Server server; // server
	// item for UI
	private JPanel myP;
	private JLabel textShow[];
	private  JTextPane workShow; // show each work
	private JTextPane msgShow; // show  msg between server and each client
	private Document doc;
	private SimpleAttributeSet workFont;
	private SimpleAttributeSet msgFont;
	private JScrollPane workSP;
	private JScrollPane msgSP;
	private JScrollBar workBar;
	private JScrollBar msgBar;
	// item for game setting
	private final int playNum; // set how many player can play together
	private int playcount; // number of player
	private ClientListener listeners[]; // listener to each client
	private boolean isLink[]; // is player still linking to server
	private boolean isChose[]; // has hero been chosen
	private boolean isSet[]; // does player chose a hero
	private boolean isPlay; // is the game playing
	// item for game
	private Player players[]; // player
	private int turn, round, salary;
	private int dice1, dice2, dbcount; // how many times dice1 = dice2 
	private int partyBuff, partyPiv; // party's buff and where is it
	private boolean isBankrupt[]; // is player bankrupts
	private boolean isdb; // is last time dice1 = dice2 
	private Grid grids[]; // grids on table
	private Land land; // a pointer for land
	
	GameServer(Server server, int gameNum, final int pNum) {
		// set frame status
		setIconImage(new ImageIcon(getClass().getResource("/image/ServerLogo.png")).getImage());
		setTitle("Server" + gameNum);
		setLayout(null);
		setBounds(150, 70, 700+8, 500+34);
		setVisible(true);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// add frame's item and set their font
		myP = new JPanel();
		myP.setBounds(0, 0, getWidth(), getHeight());
		myP.setLayout(null);
		textShow = new JLabel[7];
		for(int i = 0; i < 7; i++) {
			textShow[i] = new JLabel("          Player " + i);
			textShow[i].setBounds(0, i*getHeight()/30, getWidth()*3/7, getHeight()/30);
			textShow[i].setFont(new Font("Rockwell Extra Bold", Font.BOLD, 12));
			textShow[i].setForeground(Color.BLACK);
			myP.add(textShow[i]);
		}
		textShow[4].setText("          To All Player");
		textShow[5].setText("Client >>");
		textShow[6].setBounds(getWidth()*2/7, getHeight()*5/30, getWidth()/7, getHeight()/30);
		textShow[6].setText("     	<< Server");
		msgShow = new JTextPane();
		workShow = new JTextPane();
		msgShow.setEditable(false);
		workShow.setEditable(false);
		workFont = new SimpleAttributeSet();
		msgFont = new SimpleAttributeSet();
		StyleConstants.setFontFamily(workFont, "Meiryo");
		StyleConstants.setFontFamily(msgFont, "Meiryo");
		StyleConstants.setFontSize(workFont,12);
		StyleConstants.setFontSize(msgFont,12);
		msgSP = new JScrollPane();
		workSP = new JScrollPane();
		msgSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		workSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		msgSP.setViewportView(msgShow);
		workSP.setViewportView(workShow);
		msgSP.setBounds(0, getHeight()/5, getWidth()*3/7, getHeight()*4/5-28);
		workSP.setBounds(getWidth()*3/7, 0, getWidth()*4/7-5, getHeight()-28);
		workBar = workSP.getVerticalScrollBar();
		msgBar = msgSP.getVerticalScrollBar();
		myP.add(msgSP);
		myP.add(workSP);
		add(myP);
		revalidate();
		repaint();
		ShowWork("Open Game " + gameNum, Color.black, true);
		ShowWork("Waiting for players ...", Color.black, true);
		ShowMsg("------Waiting------", Color.black, 2, true);
		
		// initialize game's item for setting
		this.server = server;
		playNum = pNum;
		playcount = 0;
		players = new Player[playNum];
		listeners = new ClientListener[playNum];
		isLink = new boolean[playNum];
		isBankrupt = new boolean[playNum];
		isSet = new boolean[playNum];
		isChose = new boolean[4];
		for(int i = 0; i < playNum; i++) {
			players[i]  = new Player(i);
			isLink[i] = false;
			isBankrupt[i] = false;
			isSet[i] = false;
		}
		for(int j = 0; j < 4; j++) {
			isChose[j] = false;
		}
		grids = new Grid[28];
		for(int i = 0; i < 28; i++) {
			if(i == 0)
				grids[i] = new Start(i);
			else if(i == 7)
				grids[i] = new Jail(i);
			else if(i == 14)
				grids[i] = new Party(i);
			else if(i == 21)
				grids[i] = new Travel(i);
			else if(i%7 == 4)
				grids[i] = new Chance(i);
			else
				grids[i] = new Land(i);
		}
		
		// initialize game's data
		isPlay = false;
		isdb = false;
		turn = -1;
		round = 30;
		salary = 300000;
		dbcount = 0;
		partyBuff = 2;
		partyPiv = -1;
	}
	// client join in
	public synchronized void JoinClient(Socket client) {
		// find empty player, and set it as the new client
		 for(int i = 0; i < playNum; i++) {
			 if(!isLink[i]) {
				 listeners[i] = new ClientListener(this, i, client);
				 listeners[i].start();
				 isLink[i] = true;
				 playcount++;
				 ShowWork("New Player " + i + " has join", Color.BLACK, true);
				 Sends("Player_Number", false);
				 Sends(playcount + "", true);
				 break;
			 }
		 }
		 // start game when get enough  players
		 if(playcount == playNum) {
			 isPlay = true;
			 ShowWork("Player Start Setting!!", Color.black, true);
			 ShowMsg("----Start setting----", Color.black, 2, true);
			 Sends("Ready", true);
			 // tell players their number
			 for(int i = 0; i < playNum; i++) {
				 if(isLink[i])
					 listeners[i].Send(i + "");
			 }
		 }
	}
	// client leaves
	public synchronized void ClientLeave(int num) {
		// let the player bankrupt when the game has already started
		isLink[num] = false;
		if(isPlay) 
			Bankrupt(num);
		// ask server for one more client to join this game
		else {
			server.ClientLeave();
			playcount--;
			ShowWork("Player " + num, listeners[num].textC, false);
			ShowWork(" stop linking", Color.black, true);
			Sends("Player_Number", false);
			Sends(playcount + "", true);
		}
	}
	// player set ID
	public synchronized void setID(int num , String id) {
		// set player's ID
		players[num].ID = id;
		ShowWork("Player " + num + "'s ID is " + id, Color.black, true);
		Sends("ID", false);
		Sends(num + "", false);
		Sends(id, true);
	}
	// player set hero
	public synchronized void setHero(int num , int hero) {
		// if the hero is still not be choose let player select it
		if(!isChose[hero]) {
			isChose[hero] = true;
			isSet[num] = true;
			switch(hero) {
			case 0 : listeners[num].textC = new Color(50, 100, 150);break;
			case 1 : listeners[num].textC = new Color(160, 100, 30);break;
			case 2 : listeners[num].textC = new Color(40, 100, 30);break;
			case 3 : listeners[num].textC = new Color(100, 15, 30);break;
			}
			textShow[num].setForeground(listeners[num].textC);
			repaint();
			ShowWork("Player " + num + "'s ", listeners[num].textC, false);
			ShowWork("hero is " + hero, Color.black, true);
			Sends("Hero", false);
			Sends(num + "", false);
			Sends(hero + "", true);
			// start the game if every player choose their heros
			if(isAllSet()) {
				ShowWork("Game start play!", Color.black, true);
				ShowMsg("----Game Start----", Color.black, 2, true);
				ShowMsg("----Round "+ round + "----", Color.black, 2, true);
				Turn();
			}
		}
	}
	// next turn
	public void Turn() {
		if(!isdb) {
			do {
				turn++;
				if(turn == playNum) {
					// next round
					round--;
					ShowWork("Round " + round, Color.black, true);
					ShowMsg("-----Round " + round + " -----", Color.black, 2, true);
					Sends("Round", false);
					Sends(round + "", true);
					turn = 0;
				}
			} while(isBankrupt[turn]);
		}
		// game over when finishing 30 round
		if(round > 0) {
			ShowWork("Player" + turn + "'s turn", listeners[turn].textC, true);
			ShowMsg("----" + turn + "'s Turn----", listeners[turn].textC, 2, true);
			Sends("Turn", false);
			Sends(turn + "", true);
		}
		else {
			int piv = -1;
			for(int i = 0; i < playNum; i++) {
				if(players[i].rank == 1) {
					piv = i;
					break;
				}
			}
			ShowWork("Game ends ~!!", Color.black, true);
			ShowWork("Player " + piv + " win ~~!!", listeners[piv].textC, true);
			Sends("Win", false);
			Sends(piv + "", true);
		}
	}
	// throw dices
	public void Dice(int num) {
		// random 2 dice
		dice1 = (int)(Math.random()*6+1);
		dice2 = (int)(Math.random()*6+1);
		if(dice1 == dice2 && players[num].jailCD == 0) {
			dbcount++;
			isdb = true;
		}
		else {
			dbcount = 0;
			isdb = false;
		}
		ShowWork("Roll dice : " + dice1 + ", " + dice2, Color.black, true);
		Sends("Dice", false);
		Sends(dice1 + "", false);
		Sends(dice2 + "", true);
	}
	// move depend on dices
	public synchronized void Throw(int num) {
		isSet[num] = true;
		if(isAllSet()) {
			// still stay in jail
			if(dice1 != dice2 && players[turn].jailCD > 0) {
				players[turn].jailCD--;
				ShowWork("Player " + turn, listeners[turn].textC, false);
				ShowWork(" can't get out", Color.black, true);
				Sends("JailCD", false);
				Sends(turn + "", false);
				Sends(players[turn].jailCD + "", true);
				Turn();
			}
			// sent to jail when throw double 3 times
			else if(dbcount == 3) {
				dbcount = 0;
				isdb = false;
				ShowWork("3 double throw!!", Color.black, true);
				ShowWork("Player " + turn, listeners[turn].textC, false);
				ShowWork(" sent to Jail", Color.black, true);
				Sends("MoveTo", false);
				Sends(turn + "", false);
				Sends(7 + "", true);
			}
			// move with number of dices
			else {
				players[turn].loc += dice1+dice2;
				if(players[turn].loc > 27) {
					players[turn].loc %= 28;
					giveSalary(turn);
				}
				Sends("MoveTo", false);
				Sends(turn + "", false);
				Sends(players[turn].loc + "", true);			
			}
		}
	}
	// tell player do action on that grid
	public void Step(int num, int des) {
		isSet[num] = true;
		if(isAllSet()) {
			ShowWork("Player " + turn, listeners[turn].textC, false);
			ShowWork( " step "+ des, Color.black, true);
			players[turn].loc = des;
			ShowWork("Player " + turn, listeners[turn].textC, false);
			ShowWork(" arrive grid" + players[turn].loc, Color.black, true);
			listeners[turn].Send("Step");
			ShowMsg("Step", listeners[turn].textC, 2, false);
			listeners[turn].Send(players[turn].loc + "");
			ShowMsg(players[turn].loc + "", listeners[turn].textC, 1, true);
		}
	}
	// player buy or upgrade a land
	public void Buy(int num, int gridNum, int level) {
		land = (Land)grids[gridNum];
		int price = land.price[level] - land.price[land.checkIndex(land.level)];
		players[num].cash -= price;
		land.owner = num;
		land.level = level;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork(" buy grid" + gridNum + " with level " + level, Color.black, true);
		Sends("Buy", false);
		Sends(num + "", false);
		Sends(gridNum + "", false);
		Sends(level + "", false);
		Sends(players[num].cash+ "", false);
		Sends(players[num].total + "", true);
		Turn();
	}
	// player pay fee to other player
	public void Fee(int num, int gridNum) {
		land = (Land)grids[gridNum];
		int owner = land.owner;
		int price = land.buff*land.fee[land.level];
		// paying
		players[num].cash -= price;
		players[num].total -= price;
		players[land.owner].cash += price;
		players[land.owner].total += price;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork( " pay " + price + " to ", Color.black, false);
		ShowWork( "player" + land.owner, listeners[land.owner].textC, true);
		Sends("Fee", false);
		Sends(gridNum + "", true);
		Sends(num + "", false);
		Sends(players[num].cash+ "", false);
		Sends(players[num].total + "", true);
		Sends(owner + "", false);
		Sends(players[owner].cash  + "", false);
		Sends(players[owner].total + "", true);
		// is player bankrupt
		if(players[num].cash < 0) {
			Bankrupt(num);
		}
	}
	// player buy building from other player
	public void Take(int num, int gridNum) {
		land = (Land)grids[gridNum];
		int owner = land.owner;
		int price = land.price[land.level];
		players[num].cash -= 2*price;
		players[num].total -= price;
		players[owner].cash += 2*price;
		players[owner].total += price;
		land.owner = num;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork(" take grid" + gridNum + " from ", Color.black, false);
		ShowWork("player" + owner, listeners[owner].textC, true);
		Sends("Take", false);
		Sends(gridNum + "", true);
		Sends(num + "", false);
		Sends(players[num].cash + "", false);
		Sends(players[num].total + "", true);
		Sends(owner + "", false);
		Sends(players[owner].cash + "", false);
		Sends(players[owner].total + "", true);
	}
	
	// get in jail
	public void Jail(int num) {
		players[num].jailCD = 2;
		isdb = false;
		dbcount = 0;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork(" is in Jail", Color.black, true);
		Sends("Jail", false);
		Sends(num + "", true);
	}
	// bail out of jail
	public void Bail(int num) {
		players[num].jailCD = 0;
		players[num].cash -= salary/2;
		players[num].total -= salary/2;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork(" Bail out from Jail", Color.black, true);
		Sends("Bail", false);
		Sends(num + "", false);
		Sends(players[num].cash + "", false);
		Sends(players[num].total + "", true);
	}
	// hold a party
	public void Party(int gridNum) {
		land = (Land)grids[gridNum];
		land.buff = partyBuff;
		if(partyBuff < 5)
			partyBuff++;
		ShowWork("Grid " + gridNum + " throwed a party", Color.black, true);
		Sends("Party", false);
		Sends(partyPiv + "", false);
		Sends(gridNum + "", false);
		Sends(land.buff + "", true);
		partyPiv = 	gridNum;
		Turn();
	}
	// tell player to move
	public void MoveTo(int num, int des) {
		if(des < players[turn].loc && des != 7)
			giveSalary(turn);
		players[turn].loc = des;
		Sends("MoveTo", false);
		Sends(turn + "", false);
		Sends(players[turn].loc + "", true);		
	}
	// give player salary
	public void giveSalary(int num) {
		players[num].cash += salary;
		players[num].total += salary;
		ShowWork("Player " + num, listeners[num].textC, false);
		ShowWork( " earn salary "+ salary, Color.black, true);
		Sends("Earn", false);
		Sends(num + "", false);
		Sends(players[num].cash + "", false);
		Sends(players[num].total + "", true);
	}
	// player bankrupt
	public void Bankrupt(int num) {
		isBankrupt[num] = true;
		isSet[num] = true;
		for(int i = 0; i < 28; i ++) {
			if(grids[i].owner == num) {
				grids[i] = new Land(i);
			}
		}
		players[num].cash = 0;
		players[num].total = 0;
		ShowWork("Player " + turn, listeners[num].textC, false);
		ShowWork(" is bankrupts!", Color.black, true);
		Sends("Bankrupt", false);
		Sends(num + "", true);
		int count = 0, piv = -1;
		for(int i = 0; i < playNum; i++) {
			if(!isBankrupt[i]) {
				count++;
				piv = i;
			}
		}
		if(count == 1) {
			ShowWork("Player " + piv + " win ~~!!", listeners[piv].textC, true);
			Sends("Win", false);
			Sends(piv + "", true);
		}
		else if(turn == num)
			Turn();
	}
	// is all client ready for next move
	public boolean isAllSet() {
		boolean check = true;
		for(int i = 0 ; i < playNum; i++){
			if(isLink[i] && !isSet[i])
				check = false;
		}
		if(check) {
			for(int i = 0 ; i < playNum; i++){
				if(isLink[i])
					isSet[i] = false;
			}
		}
		return check;
	}
	// send msg to all client
	public synchronized void Sends(String msg, boolean isEnd) {
		// send massage to all clients
		for(int i = 0; i < playNum; i++) {
			if(isLink[i])
				listeners[i].Send(msg);
		}
		if(isEnd)
			ShowMsg(msg, Color.black, 1, isEnd);
		else
			ShowMsg(msg, Color.black, 2, isEnd);
	}
	// add msg on UI
	public synchronized void ShowMsg(String msg, Color c, int side, boolean isEnd) {
		if(side == 1)
			msg += " <<";
		else if(side == 0)
			msg = ">>"  + msg;
		if(isEnd)
			msg += "\n";
		else
			msg += ", ";
		doc = msgShow.getDocument();
		StyleConstants.setForeground(msgFont, c);
		try {
		      doc.insertString(doc.getLength(), msg, msgFont);
		} catch(BadLocationException e){
		} 
	}
	// add work on UI
	public synchronized void ShowWork(String msg, Color c, boolean isEnd) {
		if(isEnd)
			msg += "\n";
		doc = workShow.getDocument();
		StyleConstants.setForeground(msgFont, c);
		try {
		      doc.insertString(doc.getLength(), msg, msgFont);
		} catch(BadLocationException e){
		} 
	}
}
