import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class TargetLocation extends User {
	private Image img;
	//private String dir = "C:/Users/nam tran/neon/WareHouseBoss1/target.png";
	private String dir = "target.png";
	public TargetLocation(int x , int y){
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
	
}
