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
	Socket clientImageSocket;
	ServerSocket serverSocket;
	ServerSocket serverImageSocket;
	int port;
	
	
	int PortNumber;
	Server(int port_){
		port = port_;
	}
	void ConnectClients() {
		userList = new ArrayList<ServerThread>();
		chatRoomList = new ArrayList<ChatRoom>();
		try {serverSocket = new ServerSocket(port);
			serverImageSocket = new ServerSocket(port+1);
			} 
		catch (IOException e1) {e1.printStackTrace();}
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				clientImageSocket = serverImageSocket.accept();
				ServerThread thread = new ServerThread(clientSocket,clientImageSocket,userList,chatRoomList);
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
