package ImageFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteImage {
	FileOutputStream write;
	byte data[];
	String name;
	
	public WriteImage(byte[] data,String name){
		try {
			this.name= name;
			write = new FileOutputStream(name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.data = data;
	}
	
	public void Download() {
		try {
			System.out.println("받았는데?");
			write.write(data);
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetName() {
		return name;
	}
}
