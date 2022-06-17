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

import DB.DataBase;

public class Server {
	ArrayList<ServerThread> userList;
	ArrayList<ImageServerThread> imgUserList;
	ArrayList<ChatRoom> chatRoomList;
	Socket clientSocket;
	Socket clientImageSocket;
	ServerSocket serverSocket;
	int port;
	DataBase db;
	final String url = "jdbc:mysql://lovecein4858.iptime.org:3306/serverdb?characterEncoding=UTF-8 & serverTimezone=UTC";
	
	
	int PortNumber;
	public Server(int port_){
		port = port_;
	}
	public void ConnectClients() {
		int data = 0;
		userList = new ArrayList<ServerThread>();
		imgUserList = new ArrayList<ImageServerThread>();
		chatRoomList = new ArrayList<ChatRoom>();
		db = new DataBase(url,"ump45","fjqTMlsdlekqb2@");
		try {
			serverSocket = new ServerSocket(port);
			} 
		catch (IOException e1) {e1.printStackTrace();}
		while(true) {
			try {
				clientImageSocket = serverSocket.accept();
				ImageServerThread threadImg = new ImageServerThread(clientImageSocket,imgUserList,chatRoomList,db,data);
				clientSocket = serverSocket.accept();
				ServerThread thread = new ServerThread(clientSocket,userList,chatRoomList,db,data++, threadImg);
				userList.add(thread);
				imgUserList.add(threadImg);
				thread.start();
				threadImg.start();
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
