import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
public class Wall extends User{
	private Image img;
	//private String dir = "C:/Users/nam tran/neon/WareHouseBoss1/wall.png";
	private String dir = "wall.png";
	public Wall(int x , int y){
		super(x,y);
		try {
			img = ImageIO.read(new File(dir));
			this.setImage(img);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Image not loaded");
			e.printStackTrace();
		}
	}
	/*
	public Wall(int x , int y){
		super(x,y);
		try {
			//URL ilink = new URL("https://cdn2.iconfinder.com/data/icons/architecture-interior/24/architecture-interior-19-20.png");
			URL ilink = new URL("https://cdn0.iconfinder.com/data/icons/video-gaming-1-filled/64/brick_hit_retro_gaming-20.png");
			ImageIcon ImgIcon = new ImageIcon(ilink);
			Image img = ImgIcon.getImage();
			this.setImage(img);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
