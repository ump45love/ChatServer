package ImageFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WriteImage {
	FileOutputStream write;
	byte data[];
	WriteImage(String dir){
		try {
			write = new FileOutputStream(dir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
