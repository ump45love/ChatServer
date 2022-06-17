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
import ImageFile.WriteImage;

public class ImageServerThread extends Thread {
	public static final byte GET_IMAGE = 0;
	public static final byte GET_USER_PROFILE = 1;
	public static final byte GET_USER_LIST = 2;

	ArrayList<ImageServerThread> userListimg;
	ArrayList<ChatRoom> chatRoomList;
	DataInputStream imageIn;
	DataOutputStream imageOut;
	int chatRoomNumber;
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
		this.nickName = new String();
		ConnectClient();
	}
	void ConnectClient(){
		try {
			System.out.println("성공");
			imageOut = new DataOutputStream(clientImageSocket.getOutputStream());
			imageIn = new DataInputStream(clientImageSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	synchronized public void reciveProfileImage() {
		try {
			String name = imageIn.readUTF();
			int size = imageIn.readInt();
			byte[] data = new byte[size];
			imageIn.read(data);
			WriteImage download = new WriteImage(data,userId+name);
			download.Download();
			db.SetProfileImage(userId, "./"+userId+name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	synchronized public void recievImage() {
		int type = 0;
		try {
			imageOut.flush();
			type = imageIn.readByte();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Disconnect();
			e.printStackTrace();
		}
		switch(type) {
			case GET_IMAGE:
				break;
			case GET_USER_PROFILE:
				reciveProfileImage();
				break;
			case GET_USER_LIST:
				receiveUserList();
				break;
		}
		
	}
	
	void receiveUserList() {
		userListimg.stream().forEach((s) -> {System.out.println(s.userId+s.chatRoomNumber);});
		List<ImageServerThread> data = this.userListimg.stream().filter(s -> s.chatRoomNumber == this.chatRoomNumber).collect(Collectors.toList());
		int count = (int)data.stream().count();
		try {
			imageOut.writeByte(2);
			imageOut.writeByte(count);
			for(ImageServerThread save :data) {
				if( save.nickName.isEmpty())
					 imageOut.writeUTF( save.userId);
				else
					 imageOut.writeUTF( save.nickName);
				String dir =  save.db.getImage( save.userId);
				if(dir.isEmpty()) {
					 imageOut.writeInt(0);
				}
				else {
					save.readImage.SetImage(dir);
					imageOut.writeInt( save.readImage.Getbyte().length);
					imageOut.flush();
					System.out.println("크기:"+save.readImage.Getbyte().length);
					imageOut.write( save.readImage.Getbyte());
					imageOut.flush();
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("유저리스트 완료");
	}
	


	void Disconnect() {
		for(ImageServerThread i: userListimg) {
			if(i.data == this.data) {
				userListimg.remove(i);
				i.stop();
			}
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
