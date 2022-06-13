package Main;

import DB.DataBase;
import Network.Server;

public class Main {
	public static void main(String[] args) {
		String url = "jdbc:mysql://lovecein4858.iptime.org:3306/serverdb?characterEncoding=UTF-8 & serverTimezone=UTC";
		DataBase b = new DataBase(url,"ump45","fjqTMlsdlekqb2@");
		b.InsertData("assd", "sadad");
		//b.DeleteData("asd");
		b.closedb();
		//Server a = new Server(5000);
		//a.ConnectClients();	
	}
}
