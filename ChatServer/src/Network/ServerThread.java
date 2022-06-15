package Network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import DB.DataBase;

public class ServerThread extends Thread {
	public static final byte GET_MESSAGE = 0;
	public static final byte SIGN_UP = 1;
	public static final byte CREATE_ROOM= 2;
	public static final byte GET_ROOM_LIST = 3;
	public static final byte GET_LOGIN = 4;
	public static final char GET_IMAGE = 0;
	public static final char GET_USER_LIST = 1;

	ArrayList<ServerThread> userList;
	ArrayList<ChatRoom> chatRoomList;
	ImageServerThread imgServerThread;
	DataInputStream in;
	DataOutputStream out;
	DataInputStream imageIn;
	DataOutputStream imageOut;
	int chatRoomNumber;
	State state;
	Socket clientSocket;
	String nickName;
	DataBase db;
	String userId;
	int data;
	String id;
	
	
	ServerThread(Socket socket,ArrayList<ServerThread> userList,ArrayList<ChatRoom> chatRoomList,DataBase db,int data,ImageServerThread imgServerThread){
		clientSocket = socket;
		this.imgServerThread = imgServerThread;
		this.userList = userList;
		this.chatRoomNumber = 0;
		this.chatRoomList = chatRoomList;
		this.db = db;
		this.data=data;
		ConnectClient();
	}
	void ConnectClient(){
		try {
			out = new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			imageOut = new DataOutputStream(clientSocket.getOutputStream());
			imageIn = new DataInputStream(clientSocket.getInputStream());
			state = State.WAITING; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void SendMessage(String msg,String id) {
		try {
			out.writeByte(GET_MESSAGE);
			out.writeUTF(id);
			out.writeUTF(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	synchronized public void recievImage() {
		int type = 0;
		try {
			type = imageIn.readByte();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch(type) {
			case GET_IMAGE:
				break;
			case GET_USER_LIST:
				break;
		}
		
	}
	
	synchronized public void receiveLogin() {
		String name =null;
		String ps = null;
		try {
			name = in.readUTF();
			ps = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(db.Login(name, ps)) {
			try {
				out.writeByte(4);
				out.writeByte(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			nickName = db.getNickname(id);
			id = name;
			imgServerThread.nickName = nickName;
			imgServerThread.userId = id;
		}
		else {
			try {
				out.writeByte(4);
				out.writeByte(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	synchronized public void receiveRoomList() {
		int size = chatRoomList.size();
		try {
			out.writeByte(3);
			out.writeByte(chatRoomList.size());
			for(int i =0; i<size; i++) {
				final int j=i;
				out.writeUTF(chatRoomList.get(i).getRoomName());
				out.writeUTF(chatRoomList.get(i).getPassWord());
				out.writeByte((int)userList.stream().filter((s) -> {return s.chatRoomNumber == j;}).count());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized public void recievString() {
		try {
			int type = in.readByte();
			System.out.println(type);
			switch(type) {
				case GET_MESSAGE:
					receiveChat();
				break;
				case SIGN_UP:
					receiveSignUp();
				break;
				case CREATE_ROOM:
					 receiveCreateRoom();
				break;
				case GET_ROOM_LIST:
					receiveRoomList();
				break;
				case GET_LOGIN:
					receiveLogin();
				break;

			}
			System.out.println("시발");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Disconnect();
		}
	}
	
	synchronized void receiveChat() {
		try {
			String id = in.readUTF();
			String content = in.readUTF();
			sendMessageToClient(content,id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized void receiveCreateRoom() {
		String name =null;
		String ps = null;
		try {
			name = in.readUTF();
			ps = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConnectChatRoom(CreateChatRoom(name, ps));
		ConnectChatRoomMessage();
	}
	
	
	
	synchronized void receiveSignUp() {
		String name =null;
		String ps = null;
		System.out.println("회원가입");
		try {
			name = in.readUTF();
			ps = in.readUTF();
			System.out.println(name + "a " + ps);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(db.InsertData(name, ps)) {
			System.out.println("성공");
			try {
				out.writeByte(1);
				out.flush();
				out.writeByte(1);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				System.out.println("실패");
				out.writeByte(1);
				out.writeByte(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	
	void sendMessageToClient(String msg,String id) {
		List<ServerThread> array = userList.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
		if(nickName.isEmpty())
			array.forEach(s -> SendMessage(msg,id));
		else
			array.forEach(s -> SendMessage(msg,nickName));
	}
	
	//
	
		//이미지 송신
	
	//
	
	//
	
		//유저데이터 송신

	//
	
	
	int CreateChatRoom(String name, String ps) {
		if(chatRoomList.size() == 0) {
			chatRoomList.add(new ChatRoom(1,ps,name));
		}
		else {
			chatRoomList.add(new ChatRoom(Collections.max(chatRoomList).getRoomNumber()+1,ps,name));
		}
		return chatRoomList.get(chatRoomList.size()-1).getRoomNumber();
	}
	
	
	void ConnectChatRoom(int chatRoomNumber) {
			this.chatRoomNumber = chatRoomNumber;
			imgServerThread.chatRoomNumber= chatRoomNumber;
			if(nickName.isEmpty())
				sendMessageToClient(ConnectChatRoomMessage(),id);
			else
				sendMessageToClient(ConnectChatRoomMessage(),nickName);
	}


	String ConnectChatRoomMessage() {
		if(nickName.isEmpty())
			return id + "님이 입장하셨습니다.";
		return nickName + "님이 입장하셨습니다.";
	}
	
	void Disconnect() {
		for(int i =0; i<userList.size(); i++) {
			if(userList.get(i).data == this.data)
				userList.get(i).stop();
				userList.remove(i);
		}
	}

	 @Override
	  public void run() {
		 while(true) {
			 try {Thread.sleep(10);} catch (InterruptedException e) 
			 {e.printStackTrace();}
			 recievString();
		 }
	  }


}
