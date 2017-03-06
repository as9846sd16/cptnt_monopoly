package Monopoly;

public class Player {
	private Player player;//player
	private GameP.PlayerP UI;//playerP
	public int heroNum;//hero number
	public int num;// player number
	public int rank;// player rank
	public int cash;// player cash
	public int total;// player total money
	public int loc;// player location
	public int jailCD;// 0 for not in jail
	public boolean isFirstRound;// is this player is in the first round
	public String ID;// player id
	Player(int num){
		this.heroNum = 4;
		this.num = num;
		this.ID = "Player " + num;
		this.loc = 0;
		this.rank = num+1;
		this.cash = 3000000;
		this.total = this.cash;
		this.jailCD = 0;
		this.isFirstRound = true;
	}
	//set playerP
	public void setUI(GameP.PlayerP UI){
		this.UI = UI;
	}
	//set hero datas
	public void setHeroNum(int heroNum){
		this.heroNum = heroNum;
		UI.setHeroImg(heroNum);
	}
}
