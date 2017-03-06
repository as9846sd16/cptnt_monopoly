package Monopoly;

public class Land extends Grid{
	public int price[];// land price
	public int fee[];// land fee
	public int buff;// land buff
	
	Land(int num) {
		super(num);
		price = new int[6];
		price[0] = 20000+(num/7)*52000+(num%7/4)*22000+(num%7)/3*6000;
		price[1] = (num/7*2+num%7/4+1)*10000;
		price[2] = (num/7*2+num%7/4+1)*30000;
		price[4] = price[3] = (num/7*2+num%7/4+1)*50000;
		price[5] = 0;
		fee = new int[6];
		fee[0] = price[0]/100*(10+2*(num/7*4+num%7/4+num%7/3))/2000*2000;
		fee[1] = price[1]/100*(50+10*(num/7*4+num%7/4+num%7/3))/2000*2000;
		fee[2] = price[2]/100*(70+4*(num/7*4+num%7/4+num%7/3))/2000*2000;
		fee[3] = price[3]/100*(115+4*(num/7*4+num%7/4+num%7/3))/2000*2000;
		fee[4] = price[4]*(10-num/7*2-num%7/4)/2;
		fee[5] = 0;
		for(int i = 1; i<5; i++){
			price[i] = price[i] + price[i-1];
			fee[i] = fee[i] + fee[i-1];
		}
		owner = -1;
		level = -1;
		buff = 1;
	}
	//check index
	public int checkIndex(int i){
		if(i == -1){
			return 5;
		}
		else{
			return i;
		}
	}
	@Override
	public void Step() {
	}
}