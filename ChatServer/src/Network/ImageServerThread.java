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
import ImageFile.ReadImage;

public class ImageServerThread extends Thread {
	public static final byte GET_MESSAGE = 0;
	public static final byte SIGN_UP = 1;
	public static final byte CREATE_ROOM= 2;
	public static final byte GET_ROOM_LIST = 3;
	public static final byte GET_LOGIN = 4;
	public static final char GET_IMAGE = 0;
	public static final char GET_USER_LIST = 1;

	ArrayList<ImageServerThread> userListimg;
	ArrayList<ChatRoom> chatRoomList;
	DataInputStream in;
	DataOutputStream out;
	DataInputStream imageIn;
	DataOutputStream imageOut;
	int chatRoomNumber;
	State state;
	Socket clientImageSocket;
	String nickName;
	DataBase db;
	String userId;
	int data;
	ReadImage readImage;
	
	ImageServerThread(Socket imageSocket,ArrayList<ImageServerThread> userListimg,ArrayList<ChatRoom> chatRoomList,DataBase db,int data){
		clientImageSocket = imageSocket;
		this.userListimg = userListimg;
		this.chatRoomNumber = 0;
		this.chatRoomList = chatRoomList;
		this.db = db;
		this.data=data;
		this.readImage = new ReadImage();
		ConnectClient();
	}
	void ConnectClient(){
		try {
			out = new DataOutputStream(clientImageSocket.getOutputStream());
			in = new DataInputStream(clientImageSocket.getInputStream());
			imageOut = new DataOutputStream(clientImageSocket.getOutputStream());
			imageIn = new DataInputStream(clientImageSocket.getInputStream());
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
			Disconnect();
			e.printStackTrace();
		}
		switch(type) {
			case GET_IMAGE:
				break;
			case GET_USER_LIST:
				break;
		}
		
	}
	
	void receiveUserList() {
		int count = (int) userListimg.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).count();
		try {
			imageOut.writeByte(2);
			imageOut.writeByte(count);
			for(int i = 0; i< count; i++) {
				if(nickName.isEmpty())
					imageOut.writeUTF(userId);
				else
					imageOut.writeUTF(nickName);
				String dir = db.getImage(userId);
				if(readImage.SetImage(dir)) {
					imageOut.writeInt(readImage.Getbyte().length);
					imageOut.write(readImage.Getbyte());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	



	

	void sendMessageToClient(String msg,String id) {
		List<ImageServerThread> array = userListimg.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
		array.forEach(s -> SendMessage(msg,id));
	}
	
	//
	
		//이미지 송신
	
	//
	
	//
	
		//유저데이터 송신

	//
	
	


	void Disconnect() {
		for(int i =0; i<userListimg.size(); i++) {
			if(userListimg.get(i).data == this.data)
				userListimg.get(i).stop();
			userListimg.remove(i);
		}
	}

	 @Override
	  public void run() {
		 while(true) {
			 try {Thread.sleep(10);} catch (InterruptedException e) 
			 {e.printStackTrace();}
			 recievImage();
		 }
	  }

}
