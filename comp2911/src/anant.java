import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;
/*
THING TO NOTES :

The images show an overhead view of the warehouse. 
Walls are in grey, the character is near the upper left corner, 
boxes are yellow, and the goal squares are designated by green crosses. 
Note that in the first game position, the green box is already located at a goal square.
*/

public class anant extends JPanel{
	private final int LEFT_TOUCH = 0;
	private final int RIGHT_TOUCH =1;
	private final int TOP_TOUCH = 2;
	private final int BOTTOM_TOUCH = 3;
	private final int OFFSET = 100;//distance from alignment (apply for new line '\n')
	private final int DISTANCE = 50; //object size
	private ArrayList<TargetLocation> targetLoc = new ArrayList<TargetLocation>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Box> boxes = new ArrayList<Box>();
	private Person person;
	private ArrayList<Ground> grounds = new ArrayList<Ground>();
	private int stepRecord[] = new int[100];
	private int boxStepRecord[]  = new int [100];
	private Box boxRecord[] = new Box[100];
	private ArrayList<String> levels = new ArrayList<String>();
	
	private int width = 0; //width
	private int height = 0; // height
	Random random = new Random();
	private int red = random.nextInt(255);
	private int yellow = random.nextInt(255);
	private int blue = random.nextInt(255);	
	
	private String originDir = "box.png";
	private String changeDir = "redbox.png";
	private String currentDir;
	
	private boolean isCompleted = false;
	private String defaultMap;
	MapGenerator str = new MapGenerator();
	private JButton startBtn;
	JMenuBar menuBar = new JMenuBar();
	JMenu gameMenu = new JMenu("Game");
	JMenu levelMenu = new JMenu("Level");
	JMenuItem level1 = new JMenuItem("Level 1");
	JMenuItem level2 = new JMenuItem("Level 2");
	JMenuItem level3 = new JMenuItem("Level 3");
	JMenuItem level4 = new JMenuItem("Level 4");
	JMenuItem level5 = new JMenuItem("Level 5");
	JMenuItem level6 = new JMenuItem("Level 6");
	JMenuItem level7 = new JMenuItem("Level 7");
	JMenuItem newGame = new JMenuItem("New Game");
	JMenuItem exit = new JMenuItem("Exit");
	JMenuItem restart = new JMenuItem("Restart");
	
	
	private JLabel gif;
	private JLabel count;
	private JLabel completeBox;
	private JLabel push;
	private JLabel timeCounter;
	
	private int counter =0;
	private int pushCounter = 0;
	private Timer myTimer;
	private int myTimerDelay;
	private int secondCounter =0;
	private Timer t;
	
	
	
	
	public void Panel(String mapType){
		addKeyListener(new TAdapter());
		constructMenuBar();
		setFocusable(true);
		initialiseGame(mapType);
		
		levels.add(str.getString1());
		levels.add(str.getString2());
		levels.add(str.getString3());
		levels.add(str.getString4());
		levels.add(str.getString5());
		levels.add(str.getString6());
		levels.add(str.getString7());
	}
	
	public int getBoardWidth(){
		return this.width;
	}
	public int getBoardHeight(){
		return this.height;
	}
	
	public void constructMenuBar(){
		levelMenu.add(level1);
		levelMenu.add(level2);
		levelMenu.add(level3);
		levelMenu.add(level4);
		levelMenu.add(level5);
		levelMenu.add(level6);
		levelMenu.add(level7);
	
		gameMenu.add(newGame);
		gameMenu.add(restart);
		gameMenu.addSeparator();
		gameMenu.add(levelMenu);
		gameMenu.addSeparator();
		gameMenu.add(exit);
		
		menuBar.add(gameMenu);
	}
	
	public void startClickButton(){
		startBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeAll();
				defaultMap = str.getString1();
				initialiseGame(defaultMap);
			}
		});
	}
	public void menuClickButton(){
		level1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString1();
				restart();
				changeBackground();
			}
		});
		
		level2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString2();
				restart();
				changeBackground();
			}
		});
		
		level3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString3();
				restart();
				changeBackground();
			}
		});
		
		
		level4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//currentMap = str.getString2();
				defaultMap = str.getString4();
				restart();
				changeBackground();
			}
		});
		
		level5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString5();
				restart();
				changeBackground();
			}
		});
		
		level6.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString6();
				restart();
				changeBackground();
			}
		});
		
		level7.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString7();
				restart();
				changeBackground();
			}
		});
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				startRestart();
			}
		});
		
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				restart();
			}
		});
			
		newGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				defaultMap = str.getString1();
				restart();
			}
		});
	}
		
	public void initialiseGame(String mapType){
		if(mapType.equals("Start Page")){
			try {
				this.setBackground(Color.BLACK);
				System.out.println("in start page init");
				URL url;
				url = new URL("https://upload.wikimedia.org/wikipedia/commons/4/4b/Sokoban_ani.gif");
				Icon icon = new ImageIcon(url);
				gif = new JLabel(icon);
				this.add(gif);
			} catch (MalformedURLException e) {
				System.out.println("Gif did not load");
				e.printStackTrace();
			}
			startBtn = new JButton("Start");
			startBtn.setBounds(100,200,100,50);
			this.add(startBtn);
			Font s = new Font("Calibri", Font.PLAIN,24);
			startBtn.setFont(s);
			startClickButton();
			repaint();
		} else{

			this.add(menuBar);
			
			menuClickButton();
		
			count = new JLabel("Moves: "+ counter);
			this.setLayout(new FlowLayout(FlowLayout.LEADING));
			count.setBounds(100,200,100,50);
			this.add(count);
			Font c = new Font("Calibri",Font.BOLD,24);
			count.setFont(c);
		
			push = new JLabel("Pushes: "+ pushCounter);
			push.setBounds(100,200,100,50);
			this.add(push);
			Font p = new Font("Calibri",Font.BOLD,24);
			push.setFont(p);
		
			completeBox = new JLabel("Boxes Complete: " + 0);
			completeBox.setBounds(100,200,100,50);
			this.add(completeBox);
			Font cb = new Font("Calibri",Font.BOLD,24);
			completeBox.setFont(cb);
		
			JLabel timeLabel = new JLabel("Time: 00:00");
			t = new Timer(1000, new ActionListener() {
				int time = 0;
				@Override
				public void actionPerformed(ActionEvent e) {
					time++;
					Font tFont = new Font("Calibri",Font.BOLD,24);
					timeLabel.setFont(tFont);
					timeLabel.setText("Time: " +format(time / 60) + ":" + format(time % 60));
					if (time == 0) {
						Timer timer = (Timer) e.getSource();
						timer.stop();
					}
				}
			});
			this.add(timeLabel);
			Font timefont = new Font("Calibri",Font.PLAIN,24);
			timeLabel.setFont(timefont);
			//  t.start();
			System.out.println("in game init");
			repaint();
			menuClickButton();
		
			Wall wall;
			Box b ; 
			Box overlapBox;
			TargetLocation a;
			TargetLocation overlapLoc;
			int x = OFFSET; //30
			int y = OFFSET;//30
			int i = 0;
			while(i < mapType.length()) {
				char item = mapType.charAt(i);
				if(item == '\n'){
					y += DISTANCE;
				if(this.width < x){
					this.width  =x;
				}
				x = OFFSET;
			}
			else if (item == '#'){
				wall = new Wall(x,y);
				walls.add(wall);
				x+= DISTANCE;
			}else if(item == '$'){
				b = new Box(x,y,originDir);
				boxes.add(b);
				x+=DISTANCE;
			}else if(item == '.'){
				a = new TargetLocation(x,y);
				targetLoc.add(a);
				x+=DISTANCE;
			}else if(item == '@'){
				person = new Person(x,y);
				x+=DISTANCE;
			}else if(item == ' '){
				x+=DISTANCE;
			}else if(item == '^'){
				overlapLoc = new TargetLocation(x,y);
				overlapBox = new Box(x,y,changeDir);
				targetLoc.add(overlapLoc);
				boxes.add(overlapBox);
				x+=DISTANCE;
			}
			height =y;
			i++;
			}
		}
	}	
	
	public void startRestart(){
		targetLoc.clear();
		boxes.clear();
		walls.clear();
		counter = 0;
		pushCounter = 0;
		person = null;
		t.stop();
		removeAll();
		if(isCompleted)
			isCompleted = false;
		initialiseGame("Start Page");
	}
	public void restart(){
		targetLoc.clear();
		boxes.clear();
		walls.clear();
		counter = 0;
		pushCounter = 0;
		person = null;
		t.stop();
		removeAll();
		if(isCompleted)
			isCompleted = false;
		initialiseGame(defaultMap);
	}
	public void restart1(String map){
		targetLoc.clear();
		boxes.clear();
		walls.clear();
		counter = 0;
		pushCounter = 0;
		person = null;
		t.stop();
		removeAll();
		if(isCompleted)
			isCompleted = false;
		//System.out.println(isCompleted);
		defaultMap = map;
		initialiseGame(defaultMap);
	}
	
	/**
	 * Add all objects into game then use Graphic to draw
	 * @param graphic
	 */
	public void buildGame(Graphics graphic){
		graphic.setColor(new Color(red,yellow,blue)); //background color
		//System.out.println("in build game");
		if(this.getComponentCount() > 2)
		graphic.fillRect(0, 90, this.getWidth(),this.getHeight());
		LinkedList<User> game = new LinkedList<User>();
		game.addAll(walls); 
		game.addAll(targetLoc);
		game.addAll(boxes);
		game.add(person);
		for(int i =0; i< game.size(); i++){
			User item =  game.get(i);
			if((item instanceof Person)|| (item instanceof Box) || (item instanceof Wall) || (item instanceof TargetLocation)){
				graphic.drawImage(item.getImage(), item.getX()+60, item.getY()+50, this);
			}/*else{
				graphic.drawImage(item.getImage(), item.getX()+50, item.getY()+50, this);
			}*/
			if(isCompleted){
				graphic.setColor(new Color(50,50,50));
				graphic.drawString("You are awesome - Try next challenges", 300, 110);
				Font font = new Font("Calibri", Font.BOLD,20);
				setFont(font);
			}
		}
	}
	
	private void changeBackground() {
		Random random = new Random();
		this.red = random.nextInt(255);
		this.yellow = random.nextInt(255);
		this.blue = random.nextInt(255);
	}

	/**
	 * paint the game
	 */
	public void paint(Graphics graphic){
		super.paint(graphic);
		buildGame(graphic);
	}
	
	
	
	/**
	 * When key is press the person should move
	 * The Aim here to is if the person touch the 
	 *
	 *
	 */
	private class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent event){
			int key = event.getKeyCode();
			if(!isCompleted)
				t.start();
			else t.stop();
			if(key == KeyEvent.VK_LEFT){
				if(isWallTouch(person,LEFT_TOUCH)) return;
				if(isBoxTouch(LEFT_TOUCH)) return;
				
				isTargetTouch(LEFT_TOUCH);
				isOnGround();
				if(!isCompleted){
					person.move(-DISTANCE, 0);
					playMusic(move);
					counter++;
					count.setText("Moves: "+ counter);
					stepRecord[counter] = 0;
				}	
			}else if(key == KeyEvent.VK_RIGHT){
				
				if(isWallTouch(person,RIGHT_TOUCH)) return;
				
				if(isBoxTouch(RIGHT_TOUCH)) return;
				isTargetTouch(RIGHT_TOUCH);
				isOnGround();
				if(!isCompleted){
					person.move(DISTANCE,0);
					counter++;
					playMusic(move);
					count.setText("Moves: "+ counter);
					stepRecord[counter] = 1;
				}
			}else if(key == KeyEvent.VK_UP){
				if(isWallTouch(person,TOP_TOUCH)) return;
				
				if(isBoxTouch(TOP_TOUCH)) return ;
				isTargetTouch(TOP_TOUCH);
				isOnGround();
				if(!isCompleted){
				person.move(0, -DISTANCE);
				counter++;
				playMusic(move);
				count.setText("Moves: "+ counter);
				stepRecord[counter] = 2;
				}	
			}else if(key == KeyEvent.VK_DOWN){
				if(isWallTouch(person,BOTTOM_TOUCH)) return;
				if(isBoxTouch(BOTTOM_TOUCH)) return;
				isTargetTouch(BOTTOM_TOUCH);
				isOnGround();
				if(!isCompleted){ 
				person.move(0, DISTANCE);	
				counter++;
				playMusic(move);
				count.setText("Moves: "+ counter);
				stepRecord[counter] = 3;
				}
			}
			
			//undo person steps
			else if (key == KeyEvent.VK_BACK_SPACE && counter > 0){
				if(stepRecord[counter] == 0){
					stepRecord[counter] = -1;
					if(isWallTouch(person,RIGHT_TOUCH)) return;
					if(isBoxTouch(RIGHT_TOUCH)) return;
					//isBoxTouch(RIGHT_TOUCH);
					isTargetTouch(RIGHT_TOUCH);
					if(!isCompleted){
						counter--;
						count.setText("Moves: "+ counter);
						person.move(DISTANCE, 0);
						if(pushCounter>0) undoBox();
						isOnGround();
						
					}
					
				}else if(stepRecord[counter] == 1){
					stepRecord[counter] = -1;
					if(isWallTouch(person,LEFT_TOUCH)) return;
					if(isBoxTouch(LEFT_TOUCH)) return;
					isTargetTouch(LEFT_TOUCH);
					if(!isCompleted){
						counter--;
						count.setText("Moves: "+ counter);
						person.move(-DISTANCE, 0);
						if(pushCounter>0) undoBox();
						isOnGround();
						
					}
				}else if(stepRecord[counter] == 2){
					stepRecord[counter] = -1;
					if(isWallTouch(person,BOTTOM_TOUCH)) return;
					if(isBoxTouch(BOTTOM_TOUCH)) return;
					isTargetTouch(BOTTOM_TOUCH);
					if(!isCompleted){ 
						counter--;
						count.setText("Moves: "+ counter);
						person.move(0, DISTANCE);
						if(pushCounter>0) undoBox();
						isOnGround();
						if(counter == 0) t.stop();
					}
				}else if(stepRecord[counter] == 3){
					stepRecord[counter] = -1;
					if(isWallTouch(person,TOP_TOUCH)) return;
					if(isBoxTouch(TOP_TOUCH)) return;
					isTargetTouch(TOP_TOUCH);
					if(!isCompleted){
						counter--;		
						count.setText("Moves: "+ counter);
						person.move(0,-DISTANCE);
						if(pushCounter>0) undoBox();
						isOnGround();
					}	
				}else{
					
					return;
				}
			}
			repaint();
		}
	}
	
	private void undoBox(){
		if(boxStepRecord[counter] == 0){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],RIGHT_TOUCH)) return;
			//if(isBoxTouch(RIGHT_TOUCH)) return;
			isTargetTouch(RIGHT_TOUCH);
			boxRecord[counter].move(DISTANCE, 0);
			playMusic(sliding);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if(boxStepRecord[counter] == 1){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],LEFT_TOUCH)) return;
			//if(isBoxTouch(LEFT_TOUCH)) return;
			isTargetTouch(LEFT_TOUCH);
			boxRecord[counter].move(-DISTANCE, 0);
			playMusic(sliding);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if(boxStepRecord[counter] == 2){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],BOTTOM_TOUCH)) return;
			//if(isBoxTouch(BOTTOM_TOUCH)) return;
			isTargetTouch(BOTTOM_TOUCH);
			boxRecord[counter].move(0, DISTANCE);
			playMusic(sliding);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if (boxStepRecord[counter] == 3){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],TOP_TOUCH)) return;
			//if(isBoxTouch(TOP_TOUCH)) return;
			isTargetTouch(TOP_TOUCH);
			boxRecord[counter].move(0, -DISTANCE);
			playMusic(sliding);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else {
			return;
		}
	}
	/*
	 * 
	 */
	private boolean isWallTouch(User character, int type){
		if(type == LEFT_TOUCH){
			for(int i =0 ; i < walls.size();i++){
				Wall wall =  walls.get(i);
				if(character.isLeftTouch(wall)){
					return true;
				}
			}
			return false;
		}else if (type == RIGHT_TOUCH){
			for(int i = 0 ; i < walls.size();i++){
				Wall wall = (Wall) walls.get(i);
				if(character.isRightTouch(wall)){
					return true;
				}
			}
			return false;
		}else if (type == TOP_TOUCH){
			for(int i = 0 ; i < walls.size();i++){
				Wall wall =  walls.get(i);
				if(character.isTopTouch(wall)){
					return true;
				}
			}
			return false;
		}else if (type == BOTTOM_TOUCH){
			for(int i = 0 ; i < walls.size();i++){
				Wall wall =  walls.get(i);
				if(character.isBottomTouch(wall)){
					return true;
				}
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private boolean isBoxTouch(int type){
		if(type == LEFT_TOUCH){
			for(int i =0; i < boxes.size();i++){
				Box boxItem = boxes.get(i);
				if(person.isLeftTouch(boxItem)){
					for(int j = 0 ; j< boxes.size();j++){
						Box item = boxes.get(j);
						if(!boxItem.equals(item)){
							if(boxItem.isLeftTouch(item)){
								return true;
							}
						}
						if(isWallTouch(boxItem,LEFT_TOUCH)){
							return true;
						}
					}
					isTargetTouch(LEFT_TOUCH);
					isOnGround();
					boxItem.move(-DISTANCE, 0);
					playMusic(sliding);
					boxStepRecord[counter] = 0;
					boxRecord[counter] = boxItem;
					pushCounter++;
					push.setText("Pushes: "+ pushCounter);
					checkCompleted();
				}
			}
			return false;
		}else if(type == RIGHT_TOUCH){
			for(int i =0; i < boxes.size();i++){
				Box boxItem = (Box) boxes.get(i);
				if(person.isRightTouch(boxItem)){
					for(int j = 0 ; j< boxes.size();j++){
						Box item = (Box) boxes.get(j);
						if(!boxItem.equals(item)){
							if(boxItem.isRightTouch(item)){
								return true;
							}
						}
						if(isWallTouch(boxItem,RIGHT_TOUCH)){
							return true;
						}
					}
					isTargetTouch(RIGHT_TOUCH);
					isOnGround();
					boxItem.move(DISTANCE, 0);
					playMusic(sliding);
					boxStepRecord[counter] = 1;
					boxRecord[counter] = boxItem;
					pushCounter++;
					push.setText("Pushes: "+ pushCounter);
					checkCompleted();
				}
			}
			return false;
		}else if(type == TOP_TOUCH){
			for(int i =0; i < boxes.size();i++){
				Box boxItem = boxes.get(i);
				if(person.isTopTouch(boxItem)){
					for(int j = 0 ; j< boxes.size();j++){
						Box item = boxes.get(j);
						if(!boxItem.equals(item)){
							if(boxItem.isTopTouch(item)){
								return true;
							}
						}
						if(isWallTouch(boxItem,TOP_TOUCH)){
							return true;
						}
					}
					isTargetTouch(TOP_TOUCH);
					isOnGround();
					boxItem.move(0, -DISTANCE);
					playMusic(sliding);
					boxStepRecord[counter] = 2;
					boxRecord[counter] = boxItem;
					pushCounter++;
					push.setText("Pushes: "+ pushCounter);
					checkCompleted();
				}
			}
			return false;
		}else if(type == BOTTOM_TOUCH){
			for(int i =0; i < boxes.size();i++){
				Box boxItem = boxes.get(i);
				if(person.isBottomTouch(boxItem)){
					for(int j = 0 ; j< boxes.size();j++){
						Box item = boxes.get(j);
						if(!boxItem.equals(item)){
							if(boxItem.isBottomTouch(item)){
								return true;
							}
						}
						if(isWallTouch(boxItem,BOTTOM_TOUCH)){
							return true;
						}
					}
					isTargetTouch(BOTTOM_TOUCH);
					isOnGround();
					boxItem.move(0, DISTANCE);
					playMusic(sliding);
					boxStepRecord[counter] = 3;
					boxRecord[counter] = boxItem;
					//saveBox = boxItem; // save the current box to undo
					pushCounter++;
					push.setText("Pushes: "+ pushCounter);
					checkCompleted();
				}
			}
		}
		return false;
	} 
	/**
	 * Check if the boxes meet Target Location
	 * @param type
	 */
	public void isTargetTouch(int type){
		for(int i = 0 ; i < targetLoc.size(); i++){
			TargetLocation targetItem = targetLoc.get(i);
			for(int j =0 ; j < boxes.size(); j++){
				Box boxItem = boxes.get(j);
					if(boxItem.isTouch(targetItem)){
						currentDir = changeDir;
						boxItem.updateImage(currentDir);
						playMusic(aim);
					}
			}
		}
}	
public void isOnGround(){
	for(int i = 0 ; i < grounds.size(); i++){
		Ground groundItem = grounds.get(i);
		for(int j =0 ; j < boxes.size(); j++){
			Box boxItem = boxes.get(j);
			
				if(boxItem.isTouch(groundItem)){
					currentDir = originDir;
					boxItem.updateImage(currentDir);
			}
		}
	}
}	
	
	/**
	 * Check if the game is completed
	 */
	public void checkCompleted(){
		int boxesCompleted = 0;
		for(int i =0; i < boxes.size();i++){
			Box boxItem = boxes.get(i);
			for(int j = 0; j<boxes.size(); j++){ //check if boxes put into their places
				TargetLocation targetItem = targetLoc.get(j);
				if((boxItem.getX() == targetItem.getX()) && (boxItem.getY() == targetItem.getY())){
					boxesCompleted +=1;
					completeBox.setText("Boxes Complete: " + boxesCompleted);
				}
			}
		}
		if(boxesCompleted == boxes.size()){
			isCompleted = true;
			t.stop();
			Random random = new Random();
			int currLevel = levels.lastIndexOf(defaultMap);
			int currRandom = random.nextInt(5);
			while(currRandom == currLevel)
				currRandom = random.nextInt(5);
			changeBackground();
			restart1(levels.get(currRandom));
		}
	}
   
	/*
	 * 
	 */
	private static String format(int i) {
	        String result = String.valueOf(i);
	        if (result.length() == 1) {
	            result = "0" + result;
	        }
	        return result;
	 }
	
	static void playMusic(String soundFile){
		try{
			File f = new File(soundFile);
			URI uri = f.toURI();
			URL url = uri.toURL();
			AudioClip aau;
			aau = Applet.newAudioClip(url);
			aau.play();
		//	aau.loop();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private static String sliding = "sliding.wav";
	private static String undo = "undo.wav";
	private static String move = "move.wav";
	private static String aim = "aim.wav";
	private static String win = "win.wav";
	
}

