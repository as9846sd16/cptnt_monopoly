package Monopoly;

public class Grid{
	public int gridNum;// grid number
	public int owner;// -1 for no owner -1~4
	public int level; // (-1=5) for no level -1~5
	Grid(int num) {
		this.gridNum = num;
		this.owner = -1;
		this.level = 4;
	}
	
	public void Step() {
		
	}
}
