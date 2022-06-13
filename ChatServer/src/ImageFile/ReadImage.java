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
	
	ReadImage(String dir){
		try {
			read = new FileInputStream(dir);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread run = new Thread(()->{
			data=null;
			try {
				data = read.readAllBytes();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		run.start();
	}
	
	byte[] Getbyte() {
		return data;
	}
	
	Image GetImage() {
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
