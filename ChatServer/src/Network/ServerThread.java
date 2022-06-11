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

public class ServerThread extends Thread {
	public enum State {WAITING,CONNECT_ROOM}
	public static final byte GET_MESSAGE = 0;
	public static final byte SIGN_UP = 1;
	public static final byte CREATE_ROOM= 2;
	public static final byte GET_ROOM_LIST = 3;
	public static final char GET_IMAGE = 0;
	public static final char GET_USER_LIST = 1;
	  public static final char CHAT = 'a';
	  public static final char IMAGE = 'b';
	  public static final char USER_DATA = 'c';
	  public static final char ROOM_CREATE = 'd';
	  public static final char ROOM_CONNECT= 'e';
	  public static final char ROOM_OUT = 'f';
	  public static final char SERVER_WARNING = 'g';

	ArrayList<ServerThread> userList;
	ArrayList<ChatRoom> chatRoomList;
	DataInputStream in;
	DataOutputStream out;
	DataInputStream imageIn;
	DataOutputStream imageOut;
	int chatRoomNumber;
	State state;
	Socket clientSocket;
	Socket clientImageSocket;
	String nickName;
	
	
	ServerThread(Socket socket,Socket imageSocket,ArrayList<ServerThread> userList,ArrayList<ChatRoom> chatRoomList){
		clientSocket = socket;
		clientImageSocket = imageSocket;
		this.userList = userList;
		this.chatRoomNumber = 0;
		this.chatRoomList = chatRoomList;
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
	
	void SendMessage(String msg) {
		byte[] data = msg.getBytes();
		try {
			out.writeByte(GET_MESSAGE);
			out.write(data.length);
			out.write(data);
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
	
	synchronized public void recievString() {
		try {
			int type = in.readByte();
			switch(type) {
				case GET_MESSAGE:
					sendMessageToClient(receiveString());
				break;
				case SIGN_UP:
				break;
				case CREATE_ROOM:
					 receiveCreateRoom();
				break;
				case GET_ROOM_LIST:

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String receiveString() {
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	void receiveCreateRoom() {
		String name =null;
		String ps = null;
		try {
			name = in.readLine();
			ps = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConnectChatRoom(CreateChatRoom(name, ps));
		ConnectChatRoomMessage();
	}
	
	void GetMessage(String msg) {
		System.out.println("호로로롤" + msg);
		switch (msg.charAt(0)) {
		case (CHAT):
			if(state == State.CONNECT_ROOM)
				sendMessageToClient(msg.substring(1));
			break;
		case (IMAGE):
			if(state == State.CONNECT_ROOM)
			//이미지 송신
			break;
		case (USER_DATA):
			if(state == State.CONNECT_ROOM)
			//유저데이터 송신
			break;
		case (ROOM_CREATE):
			if(state == State.WAITING)
			break;
		case (ROOM_CONNECT):
			if(state == State.WAITING) {
				ConnectChatRoom(Integer.parseInt(msg.substring(1)));
				ConnectChatRoomMessage();
			}
			break;
		case (ROOM_OUT):
			if(state == State.CONNECT_ROOM)
				//방나감
			break;
		default:
			break;//아무것도 안함
		}
	}
	
	
	void sendMessageToClient(String msg) {
		List<ServerThread> array = userList.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
		array.forEach(s -> SendMessage(msg));
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
			state = State.CONNECT_ROOM;
			this.chatRoomNumber = chatRoomNumber;
			sendMessageToClient(ConnectChatRoomMessage());
	}


	String ConnectChatRoomMessage() {
		return ROOM_CONNECT + nickName + "님이 입장하셨습니다.";
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
