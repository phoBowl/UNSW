import java.awt.Image;

public class User {
	private final int DISTANCE = 80;
	private int x;
	private int y;
	private Image picture;
	public User( int x , int y){
		this.x =x;
		this.y =y;
	}
	public Image getImage(){
		return this.picture;
	}
	public void setImage(Image pic){
		picture = pic;
	}
	
	public int getX(){
		return this.x;
	}
	public int getY(){
		return this.y;
	}
	public void setX(int x){
		this.x = x;
	}
	public void setY(int y){
		this.y = y;
	}
	/**                y+DISTANCE
	 *                   ^
	 *                   |
	 *                   |       
	 * (x-DISTANCE  <-----person----> x+DISTANCE )
	 *                   |  
	 *                   |
	 *                   v
	 *                  y-DISTANCE
	 * 
	 * Check if touch object on the left
	 * @param boss
	 * @return boolean value
	 */
	public boolean isLeftTouch(User boss){
		if(((this.getX()-DISTANCE)  == boss.getX()) && (this.getY() == boss.getY())){
			return true;
		}
		else 
			return false;
	}
	/**
	 *                 y+DISTANCE
	 *                   ^
	 *                   |
	 *                   |       
	 * (x-DISTANCE  <-----person----> x+DISTANCE )
	 *                   |  
	 *                   |
	 *                   v
	 *                  y-DISTANCE
	 * Check if touch object on the right  
	 * @param boss
	 * @return boolean value
	 */
	public boolean isRightTouch(User boss){
		if(((this.getX()+ DISTANCE)  == boss.getX()) && (this.getY() == boss.getY())){
			return true;
		}
		else 
			return false;
	}
	
	/**
	 *                y+DISTANCE
	 *                   ^
	 *                   |
	 *                   |       
	 * (x-DISTANCE<-----person----> x+DISTANCE )
	 *                   |  
	 *                   |
	 *                   v
	 *                  y-DISTANCE
	 * Check if touch object on the top
	 * @param boss
	 * @return boolean value
	 */
	public boolean isTopTouch(User boss){
		if(((this.getY()-DISTANCE)  == boss.getY()) && (this.getX() == boss.getX())){
			return true;
		}
		else 
			return false;
	}
	
	/**
	 *               y+DISTANCE
	 *                   ^
	 *                   |
	 *                   |       
	 * (x-DISTANCE  <-----person----> x+DISTANCE )
	 *                   |  
	 *                   |
	 *                   v
	 *                  y-DISTANCE
	 * Check if touch object at the bottom
	 * @param boss
	 * @return boolean value
	 */
	public boolean isBottomTouch(User boss){
		if(((this.getY()+DISTANCE)  == boss.getY()) && (this.getX() == boss.getX())){
			return true;
		}
		else 
			return false;
	}
	
	public boolean isTouch(User boss){
		if((this.getX() == boss.getX()) && this.getY() == boss.getY()){
			return true;
		}else return false;
	}
}
