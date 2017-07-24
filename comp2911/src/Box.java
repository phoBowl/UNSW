import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Box extends User{
	private Image img;
	private String dir;
	public Box(int x , int y, String originDir){
		super(x,y);
		this.dir = originDir;
		try {
			File file = new File(dir);
			img = ImageIO.read(file);
			this.setImage(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method update the image for the box it goes to target location
	 * @param newImage
	 */
	public void updateImage(String newImage){
		this.dir = newImage;
		try {
			this.img = ImageIO.read(new File(newImage));
			this.setImage(this.img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get current diretory of the current picture
	 * @return
	 */
	public String getCurrentDir(){
		return this.dir;
	}
	
	/**
	 * To move the box function
	 * @param x
	 * @param y
	 */
	public void move(int x,int y){
		int moveX = this.getX() + x;
		int moveY = this.getY() + y;
		this.setX(moveX);
		this.setY(moveY);
	}
}

