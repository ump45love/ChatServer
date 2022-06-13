package DB;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	Connection con;
	Statement stmt;
	public DataBase(String url,String id,String ps) {
		try {
			con = DriverManager.getConnection(url,id,ps);
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean DeleteData(String id) {
		String s = "DELETE FROM data WHERE userID = '"+id +"';";
		try {
			int i = stmt.executeUpdate(s);
			return i==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	public boolean CheckData(String id) {
		String s = "SELECT * FROM data WHERE userID = '" + id + "';";
		String chk_id=null;
		try {
			ResultSet save = stmt.executeQuery(s);
			if(save.next()) {
				chk_id = save.getString("userID");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(chk_id);
		return !(chk_id == null);
	}
	
	public boolean InsertData(String id, String ps) {
		String sha = testSHA256(ps);
		if(CheckData(id))
			return false;
		String s = "INSERT INTO data (userID, userPS) VALUES ('" + id + "','" + sha+"');";
		try {
			int i = stmt.executeUpdate(s);
			return i==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean ImageInsert(String id,String dir) {
		String s = "UPDATE data SET imageDIR =  " + "'"+dir+"' WHERE userID = "+"'"+id+"';";
		try {
			int i =stmt.executeUpdate(s);
			return i==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean Login(String id, String ps) {
		String s = "SELECT * FROM data WHERE userID = '" + id + "';";
		String chk_ps = null;
		try {
			ResultSet save = stmt.executeQuery(s);
			if(save.next()) {
				chk_ps = save.getString("user_ps");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(chk_ps.equals(testSHA256(ps)))
			return true;
		return false;
		
	}
	public void closedb() {
		try {
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String testSHA256(String pwd) {
		try{

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(pwd.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			//Ãâ·Â
			return hexString.toString();
			
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
}
