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
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url,id,ps);
			stmt = con.createStatement();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	boolean InsertData(String id, String ps) {
		String sha = testSHA256(ps);
		String s = "INSERT INTO userData (user_id, user_ps) VALUES ('" + id + "','" + sha+"');";
		try {
			int i = stmt.executeUpdate(s);
			return i==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	boolean ImageInsert(String id,String dir) {
		String s = "UPDATE userData SET image =  " + "'"+dir+"' WHERE user_id = "+"'"+id+"';";
		try {
			int i =stmt.executeUpdate(s);
			return i==1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	boolean Login(String id, String ps) {
		String s = "SELECT * FROM userData WHERE user_id = '" + id + "';";
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
