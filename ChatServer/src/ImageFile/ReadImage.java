package ImageFile;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ReadImage {
	FileInputStream read;
	byte data[];
	
	public ReadImage(){
		
	}
	
	public ReadImage(String dir){
		SetImage(dir);
	}
	
	public byte[] Getbyte() {
		return data;
	}
	
	public boolean SetImage(String dir) {
			try {
				read = new FileInputStream(dir);
				data = read.readAllBytes();
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
	}
	
	public Image GetImage() {
	      ByteArrayInputStream bis = new ByteArrayInputStream(data);
	      BufferedImage image = null;
		try {
			image = ImageIO.read(bis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
	
	
}
