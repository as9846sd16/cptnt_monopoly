package Monopoly;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		// open a server
		Server server = new Server();
	}
	private final int playNum = 4; // how many players can play together
	private int playcount; // number of waiting players
	private int gamecount; // number of serving games 
	private ServerSocket server;
	
	Server(){
		gamecount = 1;
		// start server
		try {
			server = new ServerSocket(3104);
			Socket client;
			while(true) {
				// start new game server
				playcount = 0;
				GameServer game = new GameServer(this, gamecount, playNum);
				// get new client and send them to game server
				while(playcount < playNum) {
					client = server.accept();
					game.JoinClient(client);
					playcount++;
				}
				gamecount++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// client leave before the game start
	public void ClientLeave() {
		// ask one more client to join
		playcount--;
	}
}