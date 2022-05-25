package Network;

import java.io.BufferedReader;
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
	  public static final char CHAT = 'a';
	  public static final char IMAGE = 'b';
	  public static final char USER_DATA = 'c';
	  public static final char ROOM_CREATE = 'd';
	  public static final char ROOM_CONNECT= 'e';
	  public static final char ROOM_OUT = 'f';
	  public static final char SERVER_WARNING = 'g';

	ArrayList<ServerThread> userList;
	ArrayList<ChatRoom> chatRoomList;
	BufferedReader in;
	PrintWriter out;
	int chatRoomNumber;
	State state;
	Socket clientSocket;
	String nickName;
	
	
	ServerThread(Socket socket,ArrayList<ServerThread> userList,ArrayList<ChatRoom> chatRoomList){
		clientSocket = socket;
		this.userList = userList;
		this.chatRoomNumber = 0;
		this.chatRoomList = chatRoomList;
		ConnectClient();
	}
	void ConnectClient(){
		try {
			out = new PrintWriter(clientSocket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			state = State.WAITING; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void SendMessage(String msg) {
		out.write(msg+"\n");
		out.flush();
	}	
	void readMessage() {
		try {
			String read = in.readLine();
			if(read != null)
				GetMessage(read);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				CreateChatRoom();
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
		System.out.println(msg+"메시지 송신 수신");
	}
	
	//
	
		//이미지 송신
	
	//
	
	//
	
		//유저데이터 송신

	//
	
	
	void CreateChatRoom() {
		if(chatRoomList.size() == 0) {
			chatRoomList.add(new ChatRoom(1,1,"asd"));
		}
		else {
			chatRoomList.add(new ChatRoom(Collections.max(chatRoomList).getRoomNumber()+1,1,"asd"));
		}
		String num = Integer.toString(chatRoomList.get(chatRoomList.size()-1).getRoomNumber());
		GetMessage(ROOM_CONNECT+num);
		System.out.println("방생성");
	}
	
	void ConnectChatRoom(int chatRoomNumber) {
			state = State.CONNECT_ROOM;
			this.chatRoomNumber = chatRoomNumber;
			sendMessageToClient(ConnectChatRoomMessage());
			System.out.println("방연결");
	}


	String ConnectChatRoomMessage() {
		return ROOM_CONNECT + nickName + "님이 입장하셨습니다.";
	}

	 @Override
	  public void run() {
		 Thread read = new Thread(() -> readMessage());
		 while(true) {
			 try {Thread.sleep(10);} catch (InterruptedException e) 
			 {e.printStackTrace();}
			 readMessage();
		 }
	  }

}
