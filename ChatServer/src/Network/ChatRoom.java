package Network;

public class ChatRoom implements Comparable<ChatRoom >{
	private int roomNumber;
	private int passWord;
	private String roomName;
	
	ChatRoom(){
		roomNumber = 0;
		passWord = 0;
		roomName =null;
	}	
	ChatRoom(int num,int passWord,String name){
		roomNumber = num;
		this.passWord = passWord;
		roomName = name;
	}
	
	void setChatRoom(int num,int passWord,String name) {
		roomNumber = num;
		this.passWord = passWord;
		roomName = name;
	}
	
	void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	int getRoomNumber() {
		return roomNumber;
	}
	int getPassWord() {
		return passWord;
	}
	String getRoomName() {
		return roomName;
	}

	@Override
	public int compareTo(ChatRoom o) {
		// TODO Auto-generated method stub
		return this.roomNumber - o.roomNumber;
	}
	

}
