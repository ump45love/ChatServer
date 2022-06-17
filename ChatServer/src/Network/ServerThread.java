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
	public static final byte CONNECT_ROOM= 5;
	public static final byte NICK_CHANGE=6;
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
		this.nickName = new String();
		ConnectClient();
	}
	void ConnectClient(){
		try {
			System.out.println("성공");
			out = new DataOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			imageOut = new DataOutputStream(clientSocket.getOutputStream());
			imageIn = new DataInputStream(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	 public void recievString() {
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
				case CONNECT_ROOM:
					ConnectRoom();
				break;
				case NICK_CHANGE:
					chageNickName();
				break;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Disconnect();
		}
	}
	
	
	void SendMessage(String msg,String id) {
		try {
			out.writeByte(GET_MESSAGE);
			out.writeUTF(id);
			out.writeUTF(msg);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	 public void chageNickName() {
		try {
			String s = in.readUTF();
			db.setNickname(id, s);
			nickName = db.getNickname(id);
			imgServerThread.nickName = nickName;
			System.out.println("닉변경완료");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	 public void receiveLogin() {
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
				out.flush();
				id = name;
				imgServerThread.userId = id;
				nickName = db.getNickname(id);
				imgServerThread.nickName = nickName;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				out.writeByte(4);
				out.writeByte(0);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 public void receiveRoomList() {
		 UpdateChatRoom();
		int size = chatRoomList.size();
		try {
			out.writeByte(3);
			out.writeByte(chatRoomList.size());
			for(ChatRoom data : chatRoomList) {
				out.writeUTF(data.getRoomName());
				out.writeUTF(data.getPassWord());
				out.writeByte((int)userList.stream().filter((s) -> {return s.chatRoomNumber == data.getRoomNumber();}).count());
				out.writeByte(data.getRoomNumber());
				out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	 public void ConnectRoom() {
		try {
			int num = in.readByte();
			ConnectChatRoom(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 void receiveChat() {
		try {
			System.out.println("번호"+chatRoomNumber);
			String content = in.readUTF();
			sendMessageToClient(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 void receiveCreateRoom() {

		String name =null;
		String ps = null;
		try {
			name = in.readUTF();
			ps = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ConnectChatRoom(CreateChatRoom(name, ps));
	}
	
	
	
	 void receiveSignUp() {
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
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	
	void sendMessageToClient(String msg) {
		List<ServerThread> array = userList.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).filter(s-> s.data != this.data).collect(Collectors.toList());
		if(nickName.isEmpty()) {
			for(ServerThread s : array)
				s.SendMessage(msg,id);
			System.out.println("전송완료");
		}
		else
			array.forEach(s -> {s.SendMessage(msg,nickName);});
	}
	

	
	int CreateChatRoom(String name, String ps) {

		int number = 0;
		if(chatRoomList.size() == 0) {
			number=1;
			chatRoomList.add(new ChatRoom(number,ps,name));
		}
		else {
			number = Collections.max(chatRoomList).getRoomNumber()+1;
			chatRoomList.add(new ChatRoom(number,ps,name));
		}
		return number;
		
	}
	
	
	void ConnectChatRoom(int chatRoomNumber) {
			int save = this.chatRoomNumber;
			this.chatRoomNumber = chatRoomNumber;
			imgServerThread.chatRoomNumber= chatRoomNumber;
			if(nickName.isEmpty())
				sendMessageToClient(ConnectChatRoomMessage());
			else
				sendMessageToClient(ConnectChatRoomMessage());
			List<ImageServerThread> t = imgServerThread.userListimg.stream().filter(s->s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
			for(ImageServerThread s: t)
				s.receiveUserList();
			if(save != 0) {
				List<ImageServerThread> q = imgServerThread.userListimg.stream().filter(s->s.chatRoomNumber == save).collect(Collectors.toList());
				for(ImageServerThread s : q)
					s.receiveUserList();
			}
	}


	String ConnectChatRoomMessage() {
		if(nickName.isEmpty())
			return id + "님이 입장하셨습니다.";
		return nickName + "님이 입장하셨습니다.";
	}
	void Disconnect() {
		for(ServerThread i: userList) {
			if(i.data == this.data) {
				List<ImageServerThread> q = imgServerThread.userListimg.stream().filter(s->s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
				for(ImageServerThread s : q)
					s.receiveUserList();
				userList.remove(i);
				i.stop();
			}
		}
	}
	

	void UpdateChatRoom() {
		ArrayList<ChatRoom> save = new ArrayList<ChatRoom>();
		for(ChatRoom i : chatRoomList) {
			boolean chk = true;
			for(ServerThread j: userList) {
				if(i.getRoomNumber() == j.chatRoomNumber) {
					chk = false;
					break;
				}
			}
			if(chk)
				save.add(i);
		}
		for(int i = 0; i< save.size(); i++) {
			chatRoomList.remove(save.get(i));
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
