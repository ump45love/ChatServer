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
	
	void setChatRoom(int num,int passWord,String name) {
		roomNumber = num;
		this.passWord = passWord;
		roomName = name;
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
