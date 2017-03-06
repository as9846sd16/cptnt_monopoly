package Monopoly;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
//the ui of this game
public class GameP extends JPanel{
	public Client client;// this client
	private GameP gameP;//pointer to gameP itself
	private JPanel leftP;//panel to hold player 0 and 3
	private JPanel borderP;// panel to keep space for border(board)
	private JPanel midP;//panel to hold grids
	private JPanel mUP;//panel to hold grids
	private JPanel mDP;//panel to hold grids
	private JPanel mLP;//panel to hold grids
	private JPanel mRP;//panel to hold grids
	public JPanel mCP;//panel of the center
	public DiceP diceP;//panel of dice
	public MyButton diceBt;//button to throw dices
	public ChessP chessP[];//panel of chess
	public int round;
	private JPanel curDetailP;//current detailP
	public JPanel curBuyP;// current stepP
	public JPanel curTakeP;// current takeP
	private JPanel rightP;//panel to hold player 1 and 2
	public ButtonP detailBP;// detail button panel
	private Grid[] grids;// grid data
	public Player[] players;// player data
	public GridP gridsP[];// ui of grid
	private PlayerP playersP[];// ui of player
	private Image gamePBg;// game Background
	private Image borderImg;// border(board) Image
	
	GameP(Client client){
		//setting
		this.client = client;
		this.grids = client.grids;
		this.players = client.players;
		//System.out.println("client.playerNum: " + client.playerNum);
		setLayout(new BorderLayout());
		setSize(1000, 660);
		//initialize
		borderImg = new ImageIcon(getClass().getResource("/image/Board.png")).getImage();
		gamePBg = new ImageIcon(getClass().getResource("/image/Background.png")).getImage();
		gameP = this;
		leftP = new JPanel();
		borderP = new JPanel();
		midP = new JPanel();
		mUP = new JPanel();
		mDP = new JPanel();
		mLP = new JPanel();
		mRP = new JPanel();
		mCP = new JPanel();
		chessP = new ChessP[4];
		round = 30;
		curDetailP = new JPanel();
		curBuyP = new JPanel();
		curTakeP = new JPanel();
		rightP = new JPanel();
		playersP = new PlayerP[4];
		for(int i = 0; i<4; i++){
			playersP[i] = new PlayerP(players[i]);
			players[i].setUI(playersP[i]);
			chessP[i] = new ChessP(players[i], this.client);
		}
		diceP = new DiceP(this);
		diceBt = new MyButton("Roll!");
		diceBt.setLocation(140, 350);
		diceBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameP.client.println("Dice");
			}
		});
		detailBP = new ButtonP();
		for(int i = 0; i<28; i++){
			detailBP.bt[i].addActionListener(new DetailAct(i));
		}
		gridsP = new GridP[28];
		for(int i = 0; i<28; i++){
			if(i == 0){
				gridsP[i] = new StartP((Start)this.grids[i]);
			}
			else if(i == 7){
				gridsP[i] = new JailP((Jail)this.grids[i]);
			}
			else if(i == 14){
				gridsP[i] = new PartyP((Party)this.grids[i]);
			}
			else if(i == 21){
				gridsP[i] = new TravelP((Travel)this.grids[i]);
			}
			else if(i == 4 || i == 11 || i == 18 || i == 25){
				gridsP[i] = new ChanceP((Chance)this.grids[i]);
			}
			else{
				gridsP[i] = new LandP((Land)this.grids[i]);		
			}
		}
		//setting and add
		leftP.setPreferredSize(new Dimension(170, 170));
		leftP.setLayout(new GridLayout(2, 1));
		leftP.add(playersP[0]);
		leftP.add(playersP[3]);
		leftP.setOpaque(false);
		borderP.setOpaque(false);
		borderP.setLayout(null);
		midP.setBounds(10, 10, 640, 640);
		midP.setPreferredSize(new Dimension(640, 640));
		midP.setLayout(new BorderLayout());
		midP.setOpaque(false);
		mCP.setPreferredSize(new Dimension(480,480));
		mCP.setOpaque(false);
		mLP.setPreferredSize(new Dimension(80,480));
		mUP.setPreferredSize(new Dimension(640,80));
		mRP.setPreferredSize(new Dimension(80,480));
		mDP.setPreferredSize(new Dimension(640,80));
		mUP.setLayout(new GridLayout(1,8));
		mRP.setLayout(new GridLayout(6,1));
		mDP.setLayout(new GridLayout(1,8));
		mLP.setLayout(new GridLayout(6,1));
		mCP.setLayout(null);
		
		rightP.setPreferredSize(new Dimension(170, 170));
		rightP.setLayout(new GridLayout(2,1));
		rightP.add(playersP[1]);
		rightP.add(playersP[2]);
		rightP.setOpaque(false);
		add(leftP, BorderLayout.WEST);
		add(borderP, BorderLayout.CENTER);
		borderP.add(midP);
		borderP.add(detailBP, 0);
		for(int i = 0; i < 4; i++)
			borderP.add(chessP[i],0);
		midP.add(mCP, BorderLayout.CENTER);
		midP.add(mUP, BorderLayout.NORTH);
		for(int i = 0; i<=7; i++){
			mUP.add(gridsP[i]);
		}
		midP.add(mRP, BorderLayout.EAST);
		for(int i = 8; i<=13; i++){
			mRP.add(gridsP[i]);
		}
		midP.add(mDP, BorderLayout.SOUTH);
		for(int i = 21 ; i >= 14; i--){
			mDP.add(gridsP[i]);
		}
		midP.add(mLP, BorderLayout.WEST);
		for(int i = 27; i >= 22; i--){
			mLP.add(gridsP[i]);
		}
		add(rightP, BorderLayout.EAST);
	}
	//detail action listener
	class DetailAct implements ActionListener{
		private int i;
		DetailAct(int i){
			this.i = i;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			mCP.remove(curDetailP);
			curDetailP = gridsP[i].detailP;
			mCP.add(curDetailP,0);
			client.revalidate();
			client.repaint();
		}
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(gamePBg, 0, 0, 1000, 660, this);
		g.drawImage(borderImg, 170,0,660,660,this);
	}
	//ui of player
	public class PlayerP extends JPanel{
		private Player player;// player data
		private Image heroImg;// hero image of this player
		private Image playerBg;// playerP background
		private Image[] rankImgs;// rank image
		private Image youImg;// point out which playerP is of the client
		private Image coverImg;// dead player
		private Image bankruptImg;//dead player
		PlayerP(Player player){
			this.player = player;
			setOpaque(false);
			setPreferredSize(new Dimension(170, 330));
			setHeroImg(4);
			rankImgs = new Image[4];
			youImg = new ImageIcon(getClass().getResource("/image/Arrow.png")).getImage();
			for(int i = 0; i<4; i++){
				rankImgs[i] =new ImageIcon(getClass().getResource("/image/Player/Rank"+(i+1)+".png")).getImage();
			}
			heroImg = new ImageIcon(getClass().getResource("/image/Player/Head4.png")).getImage();
			playerBg = new ImageIcon(getClass().getResource("/image/Player/PlayerBg4.png")).getImage();
			coverImg = new ImageIcon(getClass().getResource("/image/Grid/Cover.png")).getImage();
			bankruptImg = new ImageIcon(getClass().getResource("/image/Player/Bankrupt.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 18));
			g.setColor(Color.WHITE);
			FontMetrics fm = g.getFontMetrics();
			//images
			g.drawImage(playerBg, 0, 0, getWidth(), getHeight(), this);
			g.drawImage(heroImg, (int)(getWidth() * 1/5.0), (int)(getWidth() * 1/5.0), (int)(getWidth() * 3/5.0), (int)(getWidth() * 3/5.0), this);
			g.drawImage(rankImgs[player.rank - 1], (int)(getWidth() * 3/5.0), (int)(getWidth() * 3/5.0),(int)(getWidth() * 1/5.0), (int)(getWidth() * 1/5.0), this);
			//id
			String text;
			text = "" + player.ID;
			g.drawString("ID:", 30, getHeight()/2 + 0);
			g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, getHeight()/2 + 20);
			//cash
			text = "$ " + player.cash;
			g.drawString("Cash:", 30, getHeight()/2 + 40);
			g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, getHeight()/2 + 60);
			//total
			text = "$ " + player.total;
			g.drawString("Total:", 30, getHeight()/2 + 80);
			g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, getHeight()/2 + 100);
			
			g.drawString("num: " + player.num, 0, 0);
			g.drawString("Hero: " + player.heroNum, 50, 0);
			if(player.total <= 0){
				g.drawImage(coverImg, 0, 0, getWidth(), getHeight(), this);
				g.drawImage(bankruptImg, 0, 0, getWidth(), getHeight(), this);
			}
			if(player.num == client.playerNum){
				g.drawImage(youImg, 0, 0, 70, 70, this);
			}
		}
		//set image
		public void setHeroImg(int heroNum){
			heroImg = new ImageIcon(getClass().getResource("/image/Player/Head" + heroNum + ".png")).getImage();
			playerBg = new ImageIcon(getClass().getResource("/image/Player/PlayerBg" + heroNum + ".png")).getImage();
		}
	}
	//ui of grid
	class GridP extends JPanel{
		DetailP detailP;// detail panel of grid
		
		GridP(Grid grid){
			detailP = new DetailP(grid);
			
			setPreferredSize(new Dimension(80, 80));
			setOpaque(false);
		}
		//step
		public void step(){
			
		}
	}
	//ui of start
	class StartP extends GridP{
		private Image startBg;// start background
		private Start start;// start data
		private ButtonP startBP;// start button panel
		private DoStartP doStartP;//panel to notice you step start
		StartP(Start start){
			super(start);
			this.start = start;
			this.startBP = new ButtonP();
			for(int i = 0; i<28; i++){
				startBP.bt[i].addActionListener(new StartAct(i));
			}
			this.doStartP = new DoStartP();
			startBg = new ImageIcon(getClass().getResource("/image/Grid/Start.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(startBg, 0,0,80,80,this);
			
		}
		@Override
		public void step(){
			boolean isCanBuy = false;
			// initial startBP
			startBP.EnableBt(true);
			for(int i = 0; i<28; i++){
				if(grids[i].owner != client.playerNum){
					startBP.Cover(i);
				}
				else if(grids[i].level == 4){
					startBP.Cover(i);
				}
				else{
					isCanBuy = true;
				}
			}
			if(isCanBuy == true){
				detailBP.EnableBt(false);
				borderP.add(startBP, 0);
				mCP.add(doStartP, 0);
			}
			else{
				startBP.EnableBt(false);
				client.println("Nothing");
				startBP.Clean();
			}
			client.revalidate();
			client.repaint();
		}
		//panel to notice you step start
		class DoStartP extends JPanel{
			private Image Bg;// background
			private JButton cBt;// button to close doStartP
			private MyButton noBt;// button to do nothing after step start
			DoStartP(){
				setLayout(null);
				setOpaque(false);
				int w = 480;
				setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
				Bg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
				this.cBt = new JButton();
				this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
				this.cBt.setContentAreaFilled(false);
				this.cBt.setBorderPainted(false);
				this.add(cBt);
				this.cBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						mCP.remove(doStartP);
						mCP.revalidate();
						mCP.repaint();
					}
				});
				this.noBt = new MyButton("No");
				this.add(noBt);
				noBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						client.println("Nothing");
						borderP.remove(startBP);
						mCP.remove(doStartP);
						startBP.Clean();
						startBP.EnableBt(false);
						detailBP.EnableBt(true);
						client.revalidate();
						client.repaint();
					}
				});
				noBt.setBounds(getWidth()/2 - 100, 250, 200, 70);
			}
			@Override
			protected void paintComponent(Graphics g){
				int w = 480;
				g.drawImage(Bg, 0, 0, (int)(w*2/3.0), (int)(w*4/5.0), this);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				String text;
				text = "You can upgrade your land!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 200);
			}
		}
		// start action listener
		class StartAct implements ActionListener{
			private int i;
			StartAct(int i){
				this.i = i;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LandP landP = (LandP)gridsP[i];
				landP.step();
				borderP.remove(startBP);
				mCP.remove(doStartP);
				startBP.Clean();
				startBP.EnableBt(false);
				detailBP.EnableBt(true);
			}
			
		}
	}
	
	//ui of jail
	class JailP extends GridP{
		private Image jailBg;// jail background
		private Jail jail;// jail data
		private PayJailP payJailP;//panel to notice you step start
		JailP(Jail jail){
			super(jail);
			this.jail = jail;
			payJailP = new PayJailP();
			jailBg = new ImageIcon(getClass().getResource("/image/Grid/Jail.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(jailBg, 0,0,80,80,this);
			
		}
		// add payJailP to mCP
		public void addPayJailP(){
			mCP.add(payJailP,0);
			mCP.revalidate();
			mCP.repaint();
		}
		@Override
		public void step(){
			client.println("Jail");
		}
		//panel to notice you step jail
		class PayJailP extends JPanel{
			private Image Bg;// background
			private JButton cBt;// button to close and dont pay
			private MyButton yesBt;// button to pay for bail
			PayJailP(){
				
				setLayout(null);
				setOpaque(false);
				int w = 480;
				setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
				Bg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
				this.cBt = new JButton();
				this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
				this.cBt.setContentAreaFilled(false);
				this.cBt.setBorderPainted(false);
				this.add(cBt);
				this.cBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						mCP.remove(payJailP);
						client.println("Dice");
						mCP.revalidate();
						mCP.repaint();
					}
				});
				this.yesBt = new MyButton("Yes");
				this.add(yesBt);
				yesBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						if(players[client.playerNum].cash >= 150000){
							client.println("Bail");
							mCP.remove(payJailP);
							mCP.revalidate();
							mCP.repaint();
						}
					}
				});
				yesBt.setBounds(getWidth()/2 - 100, 250, 200, 70);
			}
			@Override
			protected void paintComponent(Graphics g){
				int w = 480;
				g.drawImage(Bg, 0, 0, (int)(w*2/3.0), (int)(w*4/5.0), this);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 18));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				String text;
				text = "You are in Jail!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 120);
				text = "Do you want to pay?";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 200);
				
				
			}
		}
	}
	
	//ui of party
	class PartyP extends GridP{
		private Image partyBg;// party background
		private Party party;// party data
		private ButtonP partyBP;// party button panel for grids
		private DoPartyP doPartyP;// panel to notice you step party
		PartyP(Party party){
			super(party);
			this.party = party;
			this.partyBP = new ButtonP();
			for(int i = 0; i<28; i++){
				partyBP.bt[i].addActionListener(new PartyAct(i));
			}
			this.doPartyP = new DoPartyP();
			partyBg = new ImageIcon(getClass().getResource("/image/Grid/Party.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(partyBg, 0,0,80,80,this);			
		}
		@Override
		public void step(){
			boolean isCanBuff = false;
			partyBP.EnableBt(true);
			for(int i = 0; i<28; i++){
				if(grids[i].owner != client.playerNum){
					partyBP.Cover(i);
				}
				else{
					isCanBuff = true;
				}
			}
			if(isCanBuff == true){
				detailBP.EnableBt(false);
				borderP.add(partyBP, 0);
				System.out.println("add partyBp");
				mCP.add(doPartyP, 0);
				client.revalidate();
				client.repaint();
			}
			else{
				partyBP.EnableBt(false);
				client.println("Nothing");
			}
		}
		// panel to notice you step party
		class DoPartyP extends JPanel{
			private Image Bg;// background
			private JButton cBt;// button to close
			private MyButton noBt;// button to do nothing
			DoPartyP(){
				setLayout(null);
				setOpaque(false);
				int w = 480;
				setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
				Bg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
				this.cBt = new JButton();
				this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
				this.cBt.setContentAreaFilled(false);
				this.cBt.setBorderPainted(false);
				this.add(cBt);
				this.cBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						mCP.remove(doPartyP);
						mCP.revalidate();
						mCP.repaint();
					}
				});
				this.noBt = new MyButton("No");
				this.add(noBt);
				noBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						client.println("Nothing");
						borderP.remove(partyBP);
						mCP.remove(doPartyP);
						partyBP.Clean();
						partyBP.EnableBt(false);
						detailBP.EnableBt(true);
						client.revalidate();
						client.repaint();
					}
				});
				noBt.setBounds(getWidth()/2 - 100, 250, 200, 70);
			}
			@Override
			protected void paintComponent(Graphics g){
				int w = 480;
				g.drawImage(Bg, 0, 0, (int)(w*2/3.0), (int)(w*4/5.0), this);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				String text;
				text = "You can buff any your grid!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 200);
			}
		}
	//party action listener
		class PartyAct implements ActionListener{
			private int i;
			PartyAct(int i){
				this.i = i;
			}
			
			@Override
			public synchronized void actionPerformed(ActionEvent e) {
				LandP landP = (LandP)gridsP[i];
				client.println("Party");
				System.out.println("click buff");
				client.println(i);
				borderP.remove(partyBP);
				mCP.remove(doPartyP);
				partyBP.Clean();
				partyBP.EnableBt(false);
				detailBP.EnableBt(true);
			}
			
		}
	}
	//ui of party
	class TravelP extends GridP{
		private Image travelBg;//travel background
		private Travel travel;// corner data
		private ButtonP travelBP;// travel button panel for grids
		private DoTravelP doTravelP;// panel to notice you step travel
		TravelP(Travel travel){
			super(travel);
			this.travel = travel;
			this.travelBP = new ButtonP();
			for(int i = 0; i<28; i++){
				travelBP.bt[i].addActionListener(new TravelAct(i));
			}
			this.doTravelP = new DoTravelP();
			travelBg = new ImageIcon(getClass().getResource("/image/Grid/Travel.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(travelBg, 0,0,80,80,this);	
		}
		@Override
		public void step(){
			
			detailBP.EnableBt(false);
			borderP.add(travelBP, 0);
			travelBP.EnableBt(true);
			for(int i = 0; i<28; i++){
				if(i == 21){
					travelBP.Cover(i);
					
				}
			}
			mCP.add(doTravelP, 0);
			client.revalidate();
			client.repaint();
		}
		// panel to notice you step travel
		class DoTravelP extends JPanel{
			private Image Bg;// background
			private JButton cBt;// button to close
			private MyButton noBt;// button to do nothing
			DoTravelP(){
				setLayout(null);
				setOpaque(false);
				int w = 480;
				setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
				Bg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
				this.cBt = new JButton();
				this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
				this.cBt.setContentAreaFilled(false);
				this.cBt.setBorderPainted(false);
				this.add(cBt);
				this.cBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						mCP.remove(doTravelP);
						mCP.revalidate();
						mCP.repaint();
					}
				});
				this.noBt = new MyButton("No");
				this.add(noBt);
				noBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						client.println("Nothing");
						borderP.remove(travelBP);
						mCP.remove(doTravelP);
						travelBP.Clean();
						travelBP.EnableBt(false);
						detailBP.EnableBt(true);
						client.revalidate();
						client.repaint();
					}
				});
				noBt.setBounds(getWidth()/2 - 100, 250, 200, 70);
			}
			@Override
			protected void paintComponent(Graphics g){
				int w = 480;
				g.drawImage(Bg, 0, 0, (int)(w*2/3.0), (int)(w*4/5.0), this);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				String text;
				text = "You can go to any grid!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 200);
			}
		}
		// travel action listener
		class TravelAct implements ActionListener{
			private int i;
			TravelAct(int i){
				this.i = i;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				client.println("MoveTo");
				client.println(i);
				mCP.remove(doTravelP);
				borderP.remove(travelBP);
				travelBP.Clean();
				travelBP.EnableBt(false);
				detailBP.EnableBt(true);
			}
			
		}
	}
	//ui of land
	class LandP extends GridP{
		private Image houseImg;// diamond image
		private Color color;
		public Land land;// land data
		public TakeP takeP;
		private BuyP buyP;
		LandP(Land land){
			super(land);
			this.land = land;
			this.buyP = new BuyP(land);
			this.takeP = new TakeP(land);
			switch(land.gridNum/7*2+land.gridNum%7/4) {
			case 0 :
				color = new Color(150, 180, 30);
				break;
			case 1 :
				color = new Color(20, 120, 20);
				break;
			case 2 :
				color = new Color(60, 170, 220);
				break;
			case 3 :
				color = new Color(50, 130, 210);
				break;
			case 4 :
				color = new Color(220, 110, 200);
				break;
			case 5 :
				color = new Color(130, 100, 200);
				break;
			case 6 :
				color = new Color(200, 90, 0);
				break;
			case 7 :
				color = new Color(180, 20, 0);
				break;
			}
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.setColor(color);
			g.fillRect(0, 0, this.getWidth(), this.getHeight()/2);
			if(land.owner != -1){// if the land is owned
				switch(land.level){
				case -1:
					break;
				case 0://level 0
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 0 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 20,-30,40,60,this);
					break;
				case 4://level 4
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 4 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 0, 0, this.getWidth(), this.getHeight()/2,this);
					break;
				case 3://level 3
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 3 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 50,-5,30,45,this);
				case 2://level 2
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 2 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 25,-5,30,45,this);
				case 1://level1
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 1 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 0,-5,30,45,this);
					break;
				}
			}
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 24));
			FontMetrics fm = g.getFontMetrics();
			if(land.buff > 1){
				String text = "X" + land.buff;
				g.drawString(text, 40 - fm.stringWidth(text)/2, 60 + fm.getAscent()/2);
			}
		}
		@Override
		public void step(){
			if(land.owner == -1 || land.owner == client.playerNum){
				if(land.level == 4){
					client.println("Nothing");
				}
				else{
					this.buyP.setBtState();
					mCP.remove(curBuyP);
					curBuyP = this.buyP;
					mCP.add(curBuyP, 0);
					mCP.revalidate();
					mCP.repaint();
				}
			}
			else{
				// pay
				client.println("Fee");
				client.println(land.gridNum);
			}
		}
		public void setLand(Land land){
			this.land = land;
			this.buyP = new BuyP(land);
			this.takeP = new TakeP(land);
			this.detailP = new DetailP(land);
		}
	}
	//ui of chance
	class ChanceP extends GridP{
		private Chance chance;//chance data
		private Image chanceBg;// chance background
		public NoticeP noticeP;
		ChanceP(Chance chance){
			super(chance);
			this.chance = chance;
			chanceBg = new ImageIcon(getClass().getResource("/image/Grid/Chance.png")).getImage();
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(chanceBg, 0, 0, 80, 80, this);
		}
		@Override
		public void step(){
			int mode = (int)(Math.random()*5);
			int des = 0;
			String modeS = "Buff";
			switch(mode){
			//move to start
			case 0:
				des = 0;
				modeS = "Start";
				break;
			//move to jail
			case 1:
				des = 7;
				modeS = "Jail";
				break;
			//move to party
			case 2:
				des = 14;
				modeS = "Party";
				break;
			//move to travel
			case 3:
				des = 21;
				modeS = "Travel";
				break;
			//move to buff
			case 4:
				Party party = (Party)grids[14];
				des = party.place;
				modeS = "Buff";
				break;
			}
			noticeP = new NoticeP(des, modeS);
			mCP.add(noticeP);
			mCP.revalidate();
			mCP.repaint();
			if(des == -1){
				client.println("Nothing");
			}
			else{
				client.println("MoveTo");
				client.println(des);
			}
		}
		class NoticeP extends JPanel{
			private Image Bg;// background
			private JButton cBt;// button to close
			private Image desImg;
			private String modeS;
			private int des;
			NoticeP(int des, String modeS){
				setLayout(null);
				setOpaque(false);
				this.des = des;
				this.modeS = modeS;
				if(modeS == "Buff"){
					desImg = new ImageIcon(getClass().getResource("/image/Grid/Party.png")).getImage();
				}
				else{
					desImg = new ImageIcon(getClass().getResource("/image/Grid/" + modeS + ".png")).getImage();
				}
				int w = 480;
				setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
				Bg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
				this.cBt = new JButton();
				this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
				this.cBt.setContentAreaFilled(false);
				this.cBt.setBorderPainted(false);
				this.add(cBt);
				this.cBt.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						mCP.remove(noticeP);
						mCP.revalidate();
						mCP.repaint();
					}
				});
			}
			@Override
			protected void paintComponent(Graphics g){
				int w = 480;
				g.drawImage(Bg, 0, 0, (int)(w*2/3.0), (int)(w*4/5.0), this);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
				g.setColor(Color.BLACK);
				FontMetrics fm = g.getFontMetrics();
				g.drawImage(desImg, getWidth()/2 - 75, 80,150,150,this);
				String text;
				if(des == -1){
					text = "There is no buff!~";
				}
				else{
					text = "You are going to " + modeS +"!~";
				}
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 300);
			}
		}
	}
	//detail of grids
	class DetailP extends JPanel{
		private int gridNum;// number of grid
		public Grid gridSource;// grid data
		private Image ownerImg;// owner image
		private JButton cBt;// button to close detailP
		private Image detailPBg;// detailP background
		DetailP(Grid grid){
			this.gridSource = grid;
			this.gridNum = grid.gridNum;
			this.cBt = new JButton();
			this.detailPBg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
			setLayout(null);
			setOpaque(false);
			int w = 480;
			setBounds((int)(w*1/6.0), (int)(w*1/10.0), (int)(w*2/3.0), (int)(w*4/5.0));
			this.cBt.setBounds(270, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
			this.cBt.setContentAreaFilled(false);
			this.cBt.setBorderPainted(false);
			this.add(cBt);
			this.cBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					mCP.remove(curDetailP);
					mCP.revalidate();
					mCP.repaint();
				}
			});
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 16));
			g.setColor(Color.BLACK);
			FontMetrics fm = g.getFontMetrics();
			g.drawImage(detailPBg, 0, 0, getWidth(), getHeight(), this);
			g.drawString("" + gridNum, 40, 40);
			String text;
			//start
			if(gridNum == 0){
				g.drawImage(new ImageIcon(getClass().getResource("/image/Grid/Start.png")).getImage(), getWidth()/2 - 75, 80, 150, 150, this);
				text = "You can upgrade your land!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 300);
			}//jail
			else if(gridNum == 7){
				g.drawImage(new ImageIcon(getClass().getResource("/image/Grid/Jail.png")).getImage(), getWidth()/2 - 75, 80, 150, 150, this);
				text = "You cannot move";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 280);
				text = "for two turns!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 320);
			}//party
			else if(gridNum == 14){
				g.drawImage(new ImageIcon(getClass().getResource("/image/Grid/Party.png")).getImage(), getWidth()/2 - 75, 80, 150, 150, this);
				text = "You can buff your land!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 300);
			}//travel
			else if(gridNum == 21){
				g.drawImage(new ImageIcon(getClass().getResource("/image/Grid/Travel.png")).getImage(), getWidth()/2 - 75, 80, 150, 150, this);
				text = "You can go to any grid!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 300);
			}
			//chance
			else if(gridNum == 4 || gridNum == 11 || gridNum == 18 || gridNum == 25){
				g.drawImage(new ImageIcon(getClass().getResource("/image/Grid/Chance.png")).getImage(), getWidth()/2 - 75, 80, 150, 150, this);
				text = "You can have a chance!";
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 300);
			}//land
			else{
				Land land = (Land)gridSource;
				if(land.owner == -1){
					ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner4.png")).getImage();
				}
				else{
					ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner" + players[land.owner].heroNum + ".png")).getImage();
				}
				g.drawImage(ownerImg, 10, 50, 80, 80, this);
				g.drawString("Level", 100, 100);
				g.drawString("Price", 200, 100);
				for(int i = 0; i<5; i++){
					g.drawString("" + (i+1), 100, 100 + 30*(i+1));
					g.drawString("$ " + land.price[i], 190, 100 + 30*(i+1));
				}
				int index = land.checkIndex(land.level);
				g.drawString("Fee: ", 50, 300);
				g.drawString("$ " + land.buff *land.fee[index], 150, 300);
				g.drawString("Buy: ", 50, 330);
				g.drawString("$ " + 2 * land.price[index], 150, 330);
				
			}
		}
	}
	class TakeP extends JPanel{
		private int gridNum;//number of grid
		public Grid gridSource;// grid data
		private JButton cBt;// button to close stepP
		private Image takePBg;//stepP background
		private Image ownerImg;// owner image
		private Image houseImg;// house image
		private int spend;//money to spend
		public MyButton takeBt;// button to take this land
		TakeP(Grid grid){
			this.gridSource = grid;
			this.gridNum = grid.gridNum;
			this.cBt = new JButton();
			this.takeBt = new MyButton("Take");
			this.takePBg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
			setLayout(null);
			setOpaque(false);
			int w = 480;//mCP width
			setBounds((int)(w*1/10.0), (int)(w*1/10.0), (int)(w*4/5.0), (int)(w*4/5.0));
			
			this.cBt.setBounds(320, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
			this.cBt.setContentAreaFilled(false);
			this.cBt.setBorderPainted(false);
			this.add(cBt);
			this.cBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					client.println("Nothing");
					mCP.remove(curTakeP);
					mCP.revalidate();
					mCP.repaint();
					
				}
			});
			this.add(takeBt);
			takeBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Land land = (Land)gridSource;
					client.println("Take");
					client.println(gridSource.gridNum);
				}
			});
			takeBt.setBounds(92, 280, 200, 70);
		}
		@Override
		protected void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 20));
			g.setColor(Color.BLACK);
			FontMetrics fm = g.getFontMetrics();
			int w = 480;
			g.drawImage(takePBg, 0, 0, (int)(w*4/5.0), (int)(w*4/5.0), this);
			Land land = (Land)gridSource;
			if(land.owner == -1){
				ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner4.png")).getImage();
			}
			else{
				ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner" + players[land.owner].heroNum + ".png")).getImage();
			}
			g.drawImage(ownerImg, 10, 50, 80, 80, this);
			if(land.owner != -1){
				switch(land.level){
				case -1:
					break;
				case 0://level 0
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 0 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 152,0,80,100,this);
					break;
				case 4://level 4
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 4 + players[land.owner].heroNum +".png")).getImage();
					break;
				case 3://level 3
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 3 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 30+2*(80+28),100,80,100,this);
				case 2://level 2
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 2 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 30 +80+28,100,80,100,this);
				case 1://level1
					houseImg = new ImageIcon(getClass().getResource("/image/Grid/build" + 1 + players[land.owner].heroNum +".png")).getImage();
					g.drawImage(houseImg, 30,100,80,100,this);
					break;
				}
			}
			int index = land.checkIndex(land.level);
			spend =2*land.price[index];
			g.drawString("Buy: ", 60, 250);
			g.drawString("$ " + spend, 150, 250);
		}
	}
	//step panel of grids
	class BuyP extends JPanel{
		private int gridNum;//number of grid
		public Land land;
		private JButton cBt;// button to close stepP
		private Image buyPBg;//stepP background
		private Image ownerImg;// owner image
		public LevelBt[] levelBts;// level buttons 
		public MyButton buyBt;// buy the level selected
		private int choseLevel;// selected level
		private int spend;// money to spend
		BuyP(Land inland){
			this.land = inland;
			this.gridNum = land.gridNum;
			this.choseLevel = 0;
			this.cBt = new JButton();
			this.buyBt = new MyButton("Buy");
			this.buyPBg = new ImageIcon(getClass().getResource("/image/Grid/Detail.png")).getImage();
			setLayout(null);
			setOpaque(false);
			int w = 480;//mCP width
			setBounds((int)(w*1/10.0), (int)(w*1/10.0), (int)(w*4/5.0), (int)(w*4/5.0));
			this.cBt.setBounds(320, 10, (int)(getWidth()*1/8.0), (int)(getWidth()*1/8.0));
			this.cBt.setContentAreaFilled(false);
			this.cBt.setBorderPainted(false);
			this.add(cBt);
			this.cBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					client.println("Nothing");
					mCP.remove(curBuyP);
					mCP.revalidate();
					mCP.repaint();
				}
			});
			levelBts = new LevelBt[5];
			for(int i = 0; i<5; i++){
				levelBts[i] = new LevelBt(i);
				if(i!=4){
					this.add(levelBts[i]);
				}
			}
			this.add(buyBt);
			buyBt.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(choseLevel > land.level && players[client.playerNum].cash >= land.price[choseLevel]-land.price[land.checkIndex(land.level)]){
						client.println("Buy");
						client.println(land.gridNum);
						client.println(choseLevel);
					}
				}
			});
			buyBt.setBounds(92, 280, 200, 70);
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Rockwell Extra Bold", Font.BOLD, 24));
			FontMetrics fm = g.getFontMetrics();
			String text = "$ ";
			int w = 480;
			g.drawImage(buyPBg, 0, 0, (int)(w*4/5.0), (int)(w*4/5.0), this);
			g.drawString("" + gridNum, 40, 40);
			//corner
			if(gridNum == 0 || gridNum == 7 || gridNum == 14 || gridNum == 21){

			}//chance
			else if(gridNum == 4 || gridNum == 11 || gridNum == 18 || gridNum == 25){

			}//land
			else{
				if(land.owner == -1){
					ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner4.png")).getImage();
				}
				else{
					ownerImg = new ImageIcon(getClass().getResource("/image/Grid/Owner" + players[land.owner].heroNum + ".png")).getImage();
				}
				g.drawImage(ownerImg, 10, 50, 80, 80, this);
				int index = land.checkIndex(land.level);
				spend = land.price[choseLevel] - land.price[index];
				text = text + spend;
				g.drawString(text, getWidth()/2 - fm.stringWidth(text)/2, 250);
			}
		}
		//set the button state of this buy
		public void setBtState(){
			// initial state
			if(land.level == -1){
				choseLevel = 0;
			}
			else{
				choseLevel = land.level;
			}
			// already have three houses
			if(land.level == 3){
				for(int i = 0; i<4;i++){
					this.remove(levelBts[i]);
				}
				this.add(levelBts[4]);
			}
			else{// already have less than three houses
				for(int i = 0; i<4; i++){
					if(i <= land.level){
						levelBts[i].setEnabled(false);
					}
					else{
						levelBts[i].setEnabled(true);
					}
				}
			}
			// if the player is in the first round, can't buy level >=3
			if(players[client.playerNum].isFirstRound){
				this.levelBts[3].setEnabled(false);
			}
			// if the player have no enough money than he can't buy
		    for(int i = 0; i<5; i++){
		    	int index = land.checkIndex(land.level);
		    	if(players[client.playerNum].cash < (land.price[i] - land.price[index])){
		    		levelBts[i].setEnabled(false);
		    	}
		    }
		}
		// button for levels
		class LevelBt extends JButton{
			private int level;
			private Image levelBtImg;
			LevelBt(int level){
				//setContentAreaFilled(false);
				this.level = level;
				if(level == 4){
					this.setBounds(192 - 100, 120, 200,100);
				}
				else{
					this.setBounds(14+(level)*92, 120, 80,100);
				}
				this.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						LevelBt bt = (LevelBt)e.getSource();
						choseLevel = bt.level;
						bt.getParent().revalidate();
						bt.getParent().repaint();
					}
				});
			}
			@Override
			protected void paintComponent(Graphics g){
				super.paintComponent(g);
				//set levelBt img
				if(players[client.playerNum].heroNum != 4){
					levelBtImg = new ImageIcon(getClass().getResource("/image/Grid/build" + level + players[client.playerNum].heroNum + ".png")).getImage();
					if(level == 4){
						g.drawImage(levelBtImg, 0, 0, 200, 100, this);
					}
					else{
						g.drawImage(levelBtImg, 0, 0, 80, 100, this);
					}
				}
				if(level <= land.level){
					setBorder(new LineBorder(Color.RED, 2));
					this.setBorderPainted(true);
				}
				else{
					
					if(level <= choseLevel){	
						if(getModel().isRollover()){
							setBorder(new LineBorder(Color.GREEN, 2));
						}
						else{
							setBorder(new LineBorder(Color.RED, 2));
						}
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
			}
		}
	}
}
