import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
public class Person extends User {
	private Image img;
	private String dir = "player.png";
	public Person(int x , int y){
		super(x,y);
		
		try {
			img = ImageIO.read(new File(dir));
			this.setImage(img);
			
		} catch (IOException e) {
			System.out.println("Image not loaded");
			e.printStackTrace();
		}
	}

	public void updateImage(String newImage){
		this.dir = newImage;
		try {
			this.img = ImageIO.read(new File(newImage));
			this.setImage(this.img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getCurrentDir(){
		return this.dir;
	}
	public void move(int m,int n){
		int moveX = this.getX() + m;
		int moveY = this.getY() + n;
		this.setX(moveX);
		this.setY(moveY);
	}
}