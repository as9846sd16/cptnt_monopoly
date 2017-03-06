package Monopoly;
import java.net.Socket;
import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ClientListener extends Thread {
	private GameServer server;
	private int num; // listener's number
	private Socket client; // pointer
	private PrintWriter writer;
	private BufferedReader reader;
	private String msgString;
	private int msgInt; // used for getting msg
	private int gridNum, level, price; // use for getting msg
	public Color textC; // client's color on server UI
	
	ClientListener(GameServer server, int num, Socket client) {
		// set status
		this.server = server;
		this.num = num;
		this.client = client;
		textC = Color.black;
		try {
			// create read and writer
			reader = new BufferedReader(new InputStreamReader( client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
		} catch(IOException e) {
			server.ShowWork("Player " + num + " can't get link", textC, true);
		}
	}
	// keep listen to client
	public synchronized void run() {
		// keep listen to the client
		while(true) {
			try {
				msgString= getStringMsg(true, false);
				Work(msgString);
			} catch (IOException e) {
				server.ClientLeave(num);
				break;
			}
		}
	}
	// take action on different msg
	public synchronized void Work(String msg) throws IOException{
			switch(msg) {
			case "Wait" :  // check client is still there
				server.ShowMsg("", textC, 2, true);
				break;
			case "ID" : // set ID
				String id = getStringMsg(false, true);
				server.setID(num, id);
				break;
			case "Hero" : // set hero
				int hero = getIntMsg(false, true);
				server.setHero(num, hero);
				break;
			case "Dice" : // ask for throw dices
				server.ShowMsg("", textC, 2, true);
				server.Dice(num);
				break;
			case "Throw" : // finish throwing dices
				server.ShowMsg("", textC, 2, true);
				server.Throw(num);
				break;
			case "Arrive" : // finish moving
				int des = getIntMsg(false, true);
				server.Step(num, des);
				break;
			case "Nothing" : // player do nothing, so start new turn
				server.ShowMsg("", textC, 2, true);
				server.Turn();
				break;
			case  "Buy" : // buy or upgrade land
				gridNum = getIntMsg(false, false);
				level = getIntMsg(false, true);
				server.Buy(num, gridNum, level);
				break;
			case "Fee" : // pay for fee
				gridNum = getIntMsg(false, true);
				server.Fee(num, gridNum);
				break;
			case "Take" : // take land from other player
				gridNum = getIntMsg(false, true);
				server.Take(num, gridNum);
				break;
			case "Jail" : // sent to jail
				server.ShowMsg("", textC, 2, true);
				server.Jail(num);
				break;
			case "Bail" : // bail out of jail
				server.ShowMsg("", textC, 2, true);
				server.Bail(num);
				break;
			case "Party" : // hold a party
				gridNum = getIntMsg(false, true);
				server.Party(gridNum);
				break;
			case"MoveTo" : // player be sent to somewhere
				gridNum = getIntMsg(false, true);
				server.MoveTo(num, gridNum);
				break;
			}
	}
	// get msg as String
	public synchronized String getStringMsg(boolean isFirst, boolean isEnd) throws IOException {
			msgString = reader.readLine();
			if(isFirst)
				server.ShowMsg(msgString, textC, 0, isEnd);
			else
				server.ShowMsg(msgString, textC, 2, isEnd);
			return msgString;
	}
	// get msg as int
	public synchronized int getIntMsg(boolean isFirst, boolean isEnd) throws IOException{
		try {
			msgInt = Integer.parseInt(reader.readLine());
			if(isFirst)
				server.ShowMsg(msgInt + "", textC, 0, isEnd);
			else
				server.ShowMsg(msgInt + "", textC, 2, isEnd);
			return msgInt;
		} catch (NumberFormatException e) {
			server.ShowMsg("Catch wrong message!!", textC, 0, true);
			return getIntMsg(isFirst, isEnd);
		}
	}
	// send msg to client
	public void Send(String msg) {
		writer.println(msg);
	}
}
