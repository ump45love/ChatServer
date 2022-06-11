package Network;

public class ChatRoom implements Comparable<ChatRoom >{
	private int roomNumber;
	private String passWord;
	private String roomName;
	
	ChatRoom(){
		roomNumber = 0;
		passWord = null;
		roomName =null;
	}	
	ChatRoom(int num,String passWord,String name){
		roomNumber = num;
		this.passWord = passWord;
		roomName = name;
	}
	
	void setChatRoom(int num,String passWord,String name) {
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
	String getPassWord() {
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
