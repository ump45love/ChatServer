package Network;

import java.awt.Event;
import java.awt.EventQueue;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		Server a = new Server(40455);
		a.ConnectClients();
	}


}
