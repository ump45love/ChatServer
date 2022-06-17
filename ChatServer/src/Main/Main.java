package Main;

import DB.DataBase;
import Network.Server;

public class Main {
	public static void main(String[] args) {
		Server a = new Server(5123);
		a.ConnectClients();
	}
}
