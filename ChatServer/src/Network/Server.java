package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	ArrayList<ServerThread> userList;
	ArrayList<ChatRoom> chatRoomList;
	Socket clientSocket;
	ServerSocket serverSocket;
	int port;
	
	
	int PortNumber;
	Server(int port_){
		port = port_;
	}
	void ConnectClients() {
		System.out.println("asdasd");
		userList = new ArrayList<ServerThread>();
		try {serverSocket = new ServerSocket(port);} 
		catch (IOException e1) {e1.printStackTrace();}
		while(true) {
			try {
				System.out.println("asdasdasdasdasdasdasd");
				Socket clientSocket = serverSocket.accept();
				System.out.println("qqqqqqqqqqqqqqq");
				ServerThread thread = new ServerThread(clientSocket,userList,chatRoomList);
				userList.add(thread);
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void StopConnect() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
