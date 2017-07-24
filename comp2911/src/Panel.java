import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.jws.soap.SOAPBinding.Style;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import java.applet.*;
import java.net.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Panel extends JPanel {
	private final int LEFT_TOUCH = 0;
	private final int RIGHT_TOUCH =1;
	private final int TOP_TOUCH = 2;
	private final int BOTTOM_TOUCH = 3;
	private final int OFFSET = 100;//distance from alignment (apply for new line '\n')
	private final int DISTANCE = 80; //object size
	
	private boolean playingGame;
	private boolean inHowToPlay;
	private boolean inChangeSettings;
	private boolean inStartScreen;

	
	private ArrayList<TargetLocation> targetLoc = new ArrayList<TargetLocation>();
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Box> boxes = new ArrayList<Box>();
	private Person person;
	private ArrayList<Ground> grounds = new ArrayList<Ground>();
	private int stepRecord[] = new int[1000];
	private int boxStepRecord[]  = new int [1000];
	private Box boxRecord[] = new Box[1000];
	private ArrayList<TargetLocation> saveTargetLoc = new ArrayList<TargetLocation>();
	private ArrayList<Wall> saveWalls = new ArrayList<Wall>();
	private ArrayList<Box> saveBoxes = new ArrayList<Box>();
	private Person savePerson;
	private ArrayList<Ground> saveGrounds = new ArrayList<Ground>();
	private int saveStepRecord[] = new int[1000];
	private int saveBoxStepRecord[]  = new int [1000];
	private Box saveBoxRecord[] = new Box[1000];
	private int width =0; //width
	private int height = 0; // height
	
	Random random = new Random();
	
	private int red = random.nextInt(255-100) + 100;
	private int green = random.nextInt(255-100) + 100;
	private int blue = random.nextInt(255-100) + 100;
	
	Clip audioClip;
	private static String sliding = "sliding.wav";
	private static String intro = "intro.wav";
	private static String backgroundMusic = "backgroundMusic.wav";
	private static String silence = "silence.wav";
	//private static String undo = "undo.wav";
	private static String move = "move.wav";
	//private static String aim = "aim.wav";
	//private static String win = "win.wav";
	
	private String faceBottom = "player.png";
	private String faceLeft = "faceLeft.png" ;
	private String faceRight = "faceRight.png";
	private String faceTop = "faceTop.png";
	private String walkRight = "walkRight.png";
	private String originDir = "box.png";
	private String changeDir = "targetBox.png";
	private String currentDir;
	private boolean isCompleted = false;
	private boolean lastMovePlayed = false;
	private boolean backgroundSet = false;
	private boolean gameSoundOff = false;
	private boolean gameMusicOff = false;
	private boolean isSaveGame = false;
	private boolean isStarting = true;
	private boolean paused = false;
	private String defaultMap;
	private String saveDefaultMap;
	MapGenerator str = new MapGenerator();
	private JButton startBtn;
	
	private JLabel count;
	private JLabel push;
	private JLabel timeCounter;
	private JLabel gif;
	private JLabel completeBox;
	
	private JButton prevLevel;
	private JButton nextLevel;
	private JButton restartLevel;
	private JButton mute;
	private JButton exit;
	private ArrayList<String> levels = new ArrayList<String>();
	
	private int counter = 0;
	private int saveCounter;
	private int pushCounter = 0;
	private int savePushCounter;
	private Timer myTimer;
	private int myTimerDelay;
	private int secondCounter = 0;
	private Timer t;
	private int time = 0;
	private int saveTime;
	
	private boolean isUndo = false;
	
	private Clip introDataLine;
	
	
	private TAdapter keyboard ;
	public Panel(String mapType){
		keyboard = new TAdapter();
		addKeyListener(keyboard);
		setFocusable(true);
		initialiseGame(mapType);
		playMusic(intro, 0);
		myTimer = new Timer(1000, new ActionListener(){
			int t = 0;
			public void actionPerformed(ActionEvent e){
				t++;
				if(t == 4)
						playMusic(backgroundMusic, 1);
			}
		});
		myTimer.start();
		
		levels.add(str.getString1());
		levels.add(str.getString2());
		levels.add(str.getString3());
		levels.add(str.getString4());
		levels.add(str.getString5());
		levels.add(str.getString6());
		levels.add(str.getString7());
	}
	
	
	/**
	 * @return the defaultMap
	 */
	public String getDefaultMap() {
		return defaultMap;
	}
	
	public String getBackgroundMusic(){
		return Panel.backgroundMusic;
	}
	
	public boolean getInStartScreen(){
		return this.inStartScreen;
	}
	/**
	 * @param defaultMap the defaultMap to set
	 */
	public void setDefaultMap(String defaultMap) {
		this.defaultMap = defaultMap;
	}
	
	public void setBackgroundSet(boolean b){
		this.backgroundSet = b;
	}
	public boolean getBackgroundSet(){
		return this.backgroundSet;
	}
	
	public boolean getPlayingGame(){
		return this.playingGame;
	}
	
	public void setBackground(int r, int g, int b) {
		this.backgroundSet = true;
		this.red = r;
		this.green = g;
		this.blue = b;
		repaint();
	}
	
	public boolean getGameMusicOff(){
		return this.gameMusicOff;
	}
	
	public boolean getIsSaveGame(){
		return this.isSaveGame;
	}
	
	public void setGameSoundOff(boolean b){
		this.gameSoundOff = b;
	}
	public void stopBackgroundMusic(){
		this.gameMusicOff = true;
		introDataLine.stop();
	}
	
	public int getBoardWidth(){
		return this.width;
	}
	public int getBoardHeight(){
		return this.height;
	}
	
	/**
	 * @return the levels
	 */
	public ArrayList<String> getLevels() {
		return levels;
	}
	
	public void playMusic(String fileSource, int type){
		if (type == 1){
			try {
				File audioFile = new File(fileSource);
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				
					this.introDataLine = (Clip) AudioSystem.getLine(info);
					
					introDataLine.open(audioStream);
					introDataLine.start();
					introDataLine.loop(Clip.LOOP_CONTINUOUSLY);
					
					gameMusicOff = false;
			
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				if(gameSoundOff){
					fileSource = silence;
				}
				File audioFile = new File(fileSource);
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				
				audioClip = (Clip) AudioSystem.getLine(info);
				
				audioClip.open(audioStream);
				audioClip.start();
				
				//audioClip.close();
				//audioStream.close();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				System.out.println("here");
				e.printStackTrace();
			}
		}
	}
	
	public void saveGame(){
		isSaveGame = true;
		saveDefaultMap = defaultMap;
		saveTargetLoc = targetLoc;
		saveWalls = walls;
		saveBoxes = boxes;
		System.out.println(boxes);
		savePerson = person;
		saveGrounds = grounds;
		saveStepRecord = stepRecord;
		saveBoxStepRecord = boxStepRecord;
		saveBoxRecord = boxRecord;
		saveCounter = counter;
		savePushCounter = pushCounter;
		saveTime = time;
		
		
	}
	
	public void loadGame(){
		System.out.println("load");
		defaultMap = saveDefaultMap;
		targetLoc = saveTargetLoc;
		walls = saveWalls;
		boxes = saveBoxes;
		System.out.println(boxes);
		person = savePerson;
		grounds = saveGrounds;
		stepRecord = saveStepRecord;
		boxStepRecord = saveBoxStepRecord;
		boxRecord = saveBoxRecord;
		counter = saveCounter;
		pushCounter = savePushCounter;	
		time = saveTime;
		System.out.println(defaultMap);		
	}
	
	public void startClickButton(){
		startBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				isStarting = false;
				removeAll();
				defaultMap = str.getString1();
				initialiseGame(defaultMap);
			}
		});
		startBtn.setMnemonic(KeyEvent.VK_ENTER);		
	}
	
	public void buttonClickButton(){
		prevLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int currLevel = levels.indexOf(defaultMap);
				if(currLevel == 0){
					//buttonRestart();
				} else{
					defaultMap = levels.get(currLevel-1);
					restart();
					if(!backgroundSet)
					changeBackground();
				}
			}
		});
		prevLevel.setMnemonic(KeyEvent.VK_P);
		
		nextLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int currLevel = levels.indexOf(defaultMap);
				if(currLevel == 5){
					//buttonRestart();
				} else{
					defaultMap = levels.get(currLevel+1);
					restart();
					if(!backgroundSet)
					changeBackground();
				}
			}
		});
		nextLevel.setMnemonic(KeyEvent.VK_N);
		
		restartLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				restart();
			}
		});
		
		mute.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(gameSoundOff == false || gameMusicOff == false){
					setGameSoundOff(true);
					stopBackgroundMusic();
				} else if(gameSoundOff && gameMusicOff){
					setGameSoundOff(false);
					playMusic(backgroundMusic, 1);
				}
				
			}
		});
		mute.setMnemonic(KeyEvent.VK_M);
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
	}
	
	public void initialiseGame(String mapType){
		if(mapType.equals("Start Page")){
			
			this.setLayout(null);
			playingGame = false;
			inHowToPlay = false;
			inChangeSettings = false;
			inStartScreen = true;
			this.setBackground(Color.BLACK);
			
			startBtn = new JButton("Play");
			Font s = new Font("Calibri", Font.PLAIN,24);
			startBtn.setFont(s);
			startBtn.setBounds(899, 420, 100, 50);
			this.add(startBtn);
			
			ImageIcon image = new ImageIcon("startScreen.gif");
			image.setImage(image.getImage().getScaledInstance(500, 500, Image.SCALE_DEFAULT));
			//this.add(image);
			gif = new JLabel(image);
			gif.setBounds(700, 200, 500, 500);
			this.add(gif);
			
			startClickButton();
			repaint();
		} else if(mapType.equals("How To Play")){
			this.removeAll();
			inHowToPlay = true;
			playingGame = false;
			inChangeSettings = false;
			inStartScreen = false;
			repaint();
		} else if (mapType.equals("FAQ")){
			this.removeAll();
			inChangeSettings = false;
			inHowToPlay = false;
			playingGame = false;
			inStartScreen = false;
			repaint();
		}else if(mapType.equals(defaultMap + "a")){
			lastMovePlayed = false;
			playingGame =  true;
			inHowToPlay = false;
			inChangeSettings = false;
			inStartScreen = false;
		
			prevLevel = new JButton("<-Prev");
			//prevLevel.setBounds(100, 200, 100, 50);
			Font f = new Font("Calibri", Font.PLAIN,24);
			prevLevel.setFont(f);
			prevLevel.setPreferredSize(new Dimension(150,40));
			this.add(prevLevel);
			
			nextLevel = new JButton("Next->");
			nextLevel.setFont(f);
			nextLevel.setPreferredSize(new Dimension(150,40));
			this.add(nextLevel);
			
			restartLevel = new JButton("Restart");
			restartLevel.setFont(f);
			restartLevel.setPreferredSize(new Dimension(150,40));
			this.add(restartLevel);
			
			mute = new JButton("Mute/Unmute");
			mute.setFont(f);
			mute.setPreferredSize(new Dimension(220,40));
			this.add(mute);
			mute.setFocusable(false);
			
			exit = new JButton("Exit");
			exit.setFont(f);
			exit.setPreferredSize(new Dimension(100,40));
			this.add(exit);
			
			count = new JLabel("Moves: "+ counter);
			this.setLayout(new FlowLayout(FlowLayout.LEADING));
			count.setBounds(100,200,100,50);
			this.add(count);
			Font c = new Font("Calibri",Font.BOLD,24);
			count.setFont(c);
			count.setForeground(Color.WHITE);
			
			push = new JLabel("Pushes: "+ pushCounter);
			push.setBounds(100,200,100,50);
			this.add(push);
			Font p = new Font("Calibri",Font.BOLD,24);
			push.setFont(p);
			push.setForeground(Color.WHITE);
			
			JLabel timeLabel = new JLabel("Time: " +format(time / 60) + ":" + format(time % 60));
			timeLabel.setForeground(Color.WHITE);
	        ActionListener[] o = t.getActionListeners();
	        ActionListener r = o[0];
	        t.removeActionListener(r);
	        t.addActionListener(new ActionListener(){
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
			
	        repaint();
	        
			} else{
				lastMovePlayed = false;
				playingGame =  true;
				inHowToPlay = false;
				inChangeSettings = false;
				inStartScreen = false;
			
				prevLevel = new JButton("<-Prev");
				//prevLevel.setBounds(100, 200, 100, 50);
				Font f = new Font("Calibri", Font.PLAIN,24);
				prevLevel.setFont(f);
				prevLevel.setPreferredSize(new Dimension(150,40));
				this.add(prevLevel);
				
				
				nextLevel = new JButton("Next->");
				nextLevel.setFont(f);
				nextLevel.setPreferredSize(new Dimension(150,40));
				this.add(nextLevel);
				
				restartLevel = new JButton("Restart");
				restartLevel.setFont(f);
				restartLevel.setPreferredSize(new Dimension(150,40));
				this.add(restartLevel);
				
				mute = new JButton("Mute/Unmute");
				mute.setFont(f);
				mute.setPreferredSize(new Dimension(220,40));
				this.add(mute);
				mute.setFocusable(false);
				
				exit = new JButton("Exit");
				exit.setFont(f);
				exit.setPreferredSize(new Dimension(100,40));
				this.add(exit);
			
				count = new JLabel("Moves: "+ counter);
				this.setLayout(new FlowLayout(FlowLayout.LEADING));
				count.setBounds(100,200,100,50);
				this.add(count);
				Font c = new Font("Calibri",Font.BOLD,24);
				count.setFont(c);
				count.setForeground(Color.WHITE);
				
				push = new JLabel("Pushes: "+ pushCounter);
				push.setBounds(100,200,100,50);
				this.add(push);
				Font p = new Font("Calibri",Font.BOLD,24);
				push.setFont(p);
				push.setForeground(Color.WHITE);
				
				JLabel timeLabel = new JLabel("Time: 00:00");
				timeLabel.setForeground(Color.WHITE);
		        t = new Timer(1000, new ActionListener(){
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
		        
				repaint();
				buttonClickButton();
				Wall wall;
				Box b ; 
				TargetLocation a;
				TargetLocation overlapLoc;
				Box overlapBox;
				Ground ground;
				int x = OFFSET; //30
				int y = OFFSET;//30
				
					for(int i =0 ; i < mapType.length();i++){
						char item = mapType.charAt(i);
						if(item == '\n'){
							y += DISTANCE;
						if(this.width < x){
							this.width =x;
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
						ground = new Ground(x,y);
						grounds.add(ground);
						
						x+=DISTANCE;
					}else if(item == '^'){
						overlapLoc = new TargetLocation(x,y);
						overlapBox = new Box(x,y,originDir);
						targetLoc.add(overlapLoc);
						boxes.add(overlapBox);
						x+=DISTANCE;
					}
					
					for(int m = 0 ; m < 100; m ++){
						stepRecord[m] = -1;
						boxStepRecord[m] = -1;
					}
					height = y;
					}
			}
	}	
	
	public void startRestart(){
		targetLoc.clear();
		boxes.clear();
		walls.clear();
		grounds.clear();
		counter = 0;
		pushCounter = 0;
		person = null;
		if(t != null)
		t.stop();
		time = 0;
		removeAll();
		if(isCompleted)
			isCompleted = false;
		initialiseGame("Start Page");
	}
	public void buttonRestart(){
		removeAll();
		initialiseGame(defaultMap + "a");
	}
	public void restart(){
		targetLoc.clear();
		boxes.clear();
		walls.clear();
		grounds.clear();
		counter = 0;
		pushCounter = 0;
		person = null;
		if(t != null)
		t.stop();
		time = 0;
		removeAll();
		if(isCompleted)
			isCompleted = false;
		initialiseGame(defaultMap);
	}

	public void changeBackground() {
		Random random = new Random();
		this.red = random.nextInt(255-100) + 100;
		this.green = random.nextInt(255-100) + 100;
		this.blue = random.nextInt(255-100) + 100;
	}
	/**
	 * Add all objects into game then use Graphic to draw
	 * @param graphic
	 */
	public void buildGame(Graphics graphic){
		graphic.setColor(new Color(red,green,blue)); //background color
		if(inHowToPlay){
			graphic.drawString(playInstructions1, 100, 110);
			graphic.drawString(playInstructions2, 100, 140);
			graphic.drawString(playInstructions3, 100, 170);
			graphic.drawString(playInstructions4, 100, 200);
			graphic.drawString(playInstructions5, 100, 230);
			graphic.drawString(playInstructions6, 100, 260);
			graphic.drawString(playInstructions7, 100, 290);
			graphic.drawString(playInstructions8, 100, 320);
			graphic.drawString(playInstructions9, 100, 350);
			graphic.drawString(playInstructions10, 100, 380);
			graphic.drawString(playInstructions11, 100, 410);
			graphic.drawString(playInstructions12, 100, 440);
			graphic.drawString(playInstructions13, 100, 470);
			graphic.drawString(playInstructions14, 100, 500);
			graphic.drawString(playInstructions15, 100, 500);
			graphic.drawString(playInstructions16, 100, 530);
			graphic.drawString(playInstructions17, 100, 560);
			graphic.drawString(playInstructions18, 100, 590);
			graphic.drawString(playInstructions19, 100, 620);
			graphic.drawString(playInstructions20, 100, 650);
			graphic.drawString(playInstructions21, 100, 680);
			graphic.drawString(playInstructions22, 100, 710);
			graphic.drawString(playInstructions23, 100, 740);
			graphic.drawString(playInstructions24, 100, 770);
			graphic.drawString(playInstructions25, 100, 800);
			graphic.drawString(playInstructions26, 100, 830);
			graphic.drawString(playInstructions27, 100, 860);
			graphic.drawString(playInstructions28, 100, 890);
			graphic.drawString(playInstructions29, 100, 920);
			graphic.drawString(playInstructions30, 100, 950);
			graphic.setColor(new Color(255,255,255));
			Font font = new Font("Calibri", Font.BOLD,20);
			setFont(font);
		}
		
		if(inChangeSettings){
			graphic.drawString(faq1, 100, 110);
			graphic.drawString(faq2, 100, 140);
			graphic.drawString(faq3, 100, 170);
			graphic.drawString(faq4, 100, 200);
			graphic.drawString(faq5, 100, 230);
			graphic.drawString(faq6, 100, 260);
			graphic.drawString(faq7, 100, 290);
			graphic.drawString(faq8, 100, 320);
			graphic.drawString(faq9, 100, 350);
			graphic.drawString(faq10, 100, 380);
			graphic.drawString(faq11, 100, 410);
			graphic.drawString(faq12, 100, 440);
			graphic.drawString(faq13, 100, 470);
			graphic.drawString(faq14, 100, 500);
			graphic.drawString(faq15, 100, 500);
			graphic.drawString(faq16, 100, 530);
			graphic.drawString(faq17, 100, 560);
			graphic.drawString(faq18, 100, 590);
			graphic.drawString(faq19, 100, 620);
			graphic.drawString(faq20, 100, 650);
			graphic.drawString(faq21, 100, 680);
			graphic.drawString(faq22, 100, 710);
			graphic.setColor(new Color(255,255,255));
			Font faq = new Font("Calibri", Font.BOLD,20);
			setFont(faq);
			/*
			graphic.drawString(faq23, 100, 740);
			graphic.drawString(faq24, 100, 770);
			graphic.drawString(faq25, 100, 800);
			graphic.drawString(faq26, 100, 830);
			graphic.drawString(faq27, 100, 860);
			graphic.drawString(faq28, 100, 890);
			graphic.drawString(faq29, 100, 920);
			graphic.drawString(faq30, 100, 950);
			*/
		}
		if(playingGame){
			graphic.fillRect(0, 90, this.getWidth(),this.getHeight());
			LinkedList<User> game = new LinkedList<User>();
			game.addAll(walls); 
			game.addAll(targetLoc);
			game.addAll(boxes);
			game.add(person);
			game.addAll(grounds);
			for(int i =0; i< game.size(); i++){
				User item =  game.get(i);
				if((item instanceof Person)|| (item instanceof Box) || (item instanceof Wall) || (item instanceof TargetLocation)
						|| item instanceof Ground){
					if(defaultMap.equals(str.getString5()))
						graphic.drawImage(item.getImage(), item.getX()+400, item.getY()+100, this);
					else 
						graphic.drawImage(item.getImage(), item.getX()+700, item.getY()+100, this);
				}
				if(isCompleted == true && lastMovePlayed == false){
					graphic.setColor(new Color(255,255,255));
					graphic.drawString("You are awesome! - Try another level", 900, 110);
					Font font = new Font("Calibri", Font.BOLD,20);
					setFont(font);
					//playMusic(win);
				}
			}
		}
	}
	
	/**
	 * paint the game
	 */
	public void paint(Graphics graphic){
		super.paint(graphic);
		buildGame(graphic);
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
	
	/*
	 * 
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
					boxItem.move(-DISTANCE, 0);
					playMusic(sliding, 0);
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
					boxItem.move(DISTANCE, 0);
					playMusic(sliding, 0);
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
					boxItem.move(0, -DISTANCE);
					playMusic(sliding, 0);
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
					boxItem.move(0, DISTANCE);
					playMusic(sliding, 0);
					boxStepRecord[counter] = 3;
					boxRecord[counter] = boxItem;
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
				}
			}
		}
		if(boxesCompleted == boxes.size()){
			isCompleted = true;
			lastMovePlayed = true;
			t.stop();
			
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
	
	/**
	 * When key is press the person should move
	 * The Aim here to is if the person touch the 
	 */
	private class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent event){
			int key = event.getKeyCode();
			if(isCompleted == false && isStarting == false)
				t.start();
			//else (t.stop();
			if(key == KeyEvent.VK_LEFT){
				if(isWallTouch(person,LEFT_TOUCH)) return;
				if(isBoxTouch(LEFT_TOUCH)) return;
				
				isTargetTouch(LEFT_TOUCH);
				isOnGround();
				if(!isCompleted && !paused){
					person.move(-DISTANCE, 0);
					person.updateImage(faceLeft);
					playMusic(move, 0);
					counter++;
					count.setText("Moves: "+ counter);
					stepRecord[counter] = 0;
				}	
				if(isCompleted && lastMovePlayed){
					lastMovePlayed = false;
					person.move(-DISTANCE, 0);
					person.updateImage(faceLeft);
					playMusic(move, 0);
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
					person.updateImage(walkRight);
					repaint();
					person.updateImage(faceRight);
					counter++;
					playMusic(move, 0);
					count.setText("Moves: "+ counter);
					stepRecord[counter] = 1;
				}
				if(isCompleted && lastMovePlayed){
					lastMovePlayed = false;
					person.move(DISTANCE,0);
					person.updateImage(walkRight);
					repaint();
					person.updateImage(faceRight);
					counter++;
					playMusic(move, 0);
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
				person.updateImage(faceTop);
				counter++;
				playMusic(move, 0);
				count.setText("Moves: "+ counter);
				stepRecord[counter] = 2;
				}	
				if(isCompleted && lastMovePlayed){
					lastMovePlayed = false;
					person.move(0, -DISTANCE);
					person.updateImage(faceTop);
					counter++;
					playMusic(move, 0);
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
				person.updateImage(faceBottom);
				counter++;
				playMusic(move, 0);
				count.setText("Moves: "+ counter);
				stepRecord[counter] = 3;
				}
				if(isCompleted && lastMovePlayed){
					lastMovePlayed = false;
					person.move(0, DISTANCE);
					person.updateImage(faceBottom);
					playMusic(move, 0);
					counter++;
					count.setText("Moves: "+ counter);
					stepRecord[counter] = 3;
				}
				
			}else if (key == KeyEvent.VK_BACK_SPACE && counter > 0){
				undoPerson();
			}else{
				return;
			}
			repaint();	
		}
	}
	
		private void undoPerson(){
			if(stepRecord[counter] == 0){
				stepRecord[counter] = -1;
				if(isWallTouch(person,RIGHT_TOUCH)) return;
				if(isBoxTouch(RIGHT_TOUCH)) return;
				isTargetTouch(RIGHT_TOUCH);
				if(!isCompleted){
					counter--;
					count.setText("Moves: "+ counter);
					person.move(DISTANCE, 0);
					if(stepRecord[counter] == 0)
						person.updateImage(faceLeft);
					if(stepRecord[counter] == 1)
						person.updateImage(faceRight);
					if(stepRecord[counter] == 2)
						person.updateImage(faceTop);
					if(stepRecord[counter] == 3)
						person.updateImage(faceBottom);
					
					person.updateImage(faceLeft);
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

					if(stepRecord[counter] == 0)
						person.updateImage(faceLeft);
					if(stepRecord[counter] == 1)
						person.updateImage(faceRight);
					if(stepRecord[counter] == 2)
						person.updateImage(faceTop);
					if(stepRecord[counter] == 3)
						person.updateImage(faceBottom);
					
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
					
					if(stepRecord[counter] == 0)
						person.updateImage(faceLeft);
					if(stepRecord[counter] == 1)
						person.updateImage(faceRight);
					if(stepRecord[counter] == 2)
						person.updateImage(faceTop);
					if(stepRecord[counter] == 3)
						person.updateImage(faceBottom);

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

					if(stepRecord[counter] == 0)
						person.updateImage(faceLeft);
					if(stepRecord[counter] == 1)
						person.updateImage(faceRight);
					if(stepRecord[counter] == 2)
						person.updateImage(faceTop);
					if(stepRecord[counter] == 3)
						person.updateImage(faceBottom);
					
					if(pushCounter>0) undoBox();
					isOnGround();
				}	
			}
			repaint();
			//this.setFocusable(false);
		}
	
	private void undoBox(){
		if(boxStepRecord[counter] == 0){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],RIGHT_TOUCH)) return;
			//if(isBoxTouch(RIGHT_TOUCH)) return;
			isTargetTouch(RIGHT_TOUCH);
			boxRecord[counter].move(DISTANCE, 0);
			playMusic(sliding, 0);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if(boxStepRecord[counter] == 1){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],LEFT_TOUCH)) return;
			//if(isBoxTouch(LEFT_TOUCH)) return;
			isTargetTouch(LEFT_TOUCH);
			boxRecord[counter].move(-DISTANCE, 0);
			playMusic(sliding, 0);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if(boxStepRecord[counter] == 2){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],BOTTOM_TOUCH)) return;
			//if(isBoxTouch(BOTTOM_TOUCH)) return;
			isTargetTouch(BOTTOM_TOUCH);
			boxRecord[counter].move(0, DISTANCE);
			playMusic(sliding, 0);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else if (boxStepRecord[counter] == 3){
			boxStepRecord[counter] = -1;
			if(isWallTouch(boxRecord[counter],TOP_TOUCH)) return;
			//if(isBoxTouch(TOP_TOUCH)) return;
			isTargetTouch(TOP_TOUCH);
			boxRecord[counter].move(0, -DISTANCE);
			playMusic(sliding, 0);
			isOnGround();
			pushCounter--;
			push.setText("Pushes: " + pushCounter);
		}else {
			return;
		}
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
	private String playInstructions1 = "Wait for the game to load, then click the 'Play' button.";
	private String playInstructions2 = " ";
	private String playInstructions3 = "Sokoban is Japanese for 'warehouse keeper'. The object is ";
	private String playInstructions4 = "to cover all the red dots (O's) by moving the boxes over them.\n";
	private String playInstructions5 = " ";
	private String playInstructions6 = "The classic Sokoban game was invented in 1982 ";
	private String playInstructions7 = "by Hiroyuki Imabayashi, president of Thinking Rabbit Inc. in Japan.\n";
	private String playInstructions8 = " ";
	private String playInstructions9 = "HOW TO PLAY. Following are the rules of Sokoban.\n";
	private String playInstructions10 = " ";
	private String playInstructions11 = "1. Move boxes by pushing them using the arrow keys.\n";
	private String playInstructions12 = " ";
	private String playInstructions13 = "2. You can only push one box at a time.\n";
	private String playInstructions14 = " ";
	private String playInstructions15 = "3.The level is won when all the blue holders are covered.\n";
	private String playInstructions16 = " ";
	private String playInstructions17 ="Sometimes you might push a box into a corner, or against ";
	private String playInstructions18 = "another box, in such a way that it is impossible to move ";
	private String playInstructions19 = "the box further. In that case, click the Restart button ";
	private String playInstructions20 = "to try the level again.\n";
	private String playInstructions21 = " ";
	private String playInstructions22 = "The trick to solving a Sokoban level is to plan ahead. ";
	private String playInstructions23 = "Before moving any blocks, picture in your mind the sequence ";
	private String playInstructions24 = "of steps necessary to clear the level.\n";
	private String playInstructions25 = " ";
	private String playInstructions26 = "That's where the brain training comes in. By visualizing ";
	private String playInstructions27 = "the steps necessary to beat the level, you train short-term ";
	private String playInstructions28 = "memory, logical and analytical thinking, and other brain skills.\n";
	private String playInstructions29 = " ";
	private String playInstructions30 = "This can be quite challenging!";
	
	private String faq1 = "Question: How can I move the character ?";
	private String faq2 = "Answer: Use four arrow keys LEFT, RIGHT, UP and DOWN";
	private String faq3 = "Question : How can I start the game ?";
	private String faq4 = "Answer: Click Play then enjoy you game";
	private String faq5 = "Question: The music is annoying, I want to turn it off !";
	private String faq6 = "Answer: Click on Mute/Unmute Button or you can use Ctrl+M";
	private String faq7 = "Question: Any quick key to create new game ?";
	private String faq8 = "Answer: Yes! Beside New Game in the Game (top left), you can do Ctrl+N";
	private String faq9 = "Question: I go wrong in my step ? How can i go back?";
	private String faq10 = "Answer: Use BackSpace key on you keyboard my friend :)";
	private String faq11 = "Question: I want to challenging with random map. How could i do that ?";
	private String faq12 = "Answer: Sure. Go to Game (top left) -> Level -> Random";
	private String faq13 = "Question: Can i restart the game ?";
	private String faq14 = "Answer : You could either go to Game -> Restart or Ctlr+R";
	private String faq15 = "Question : How to change the level quickly ?";
	private String faq16 = "Answer: Click on <-Prev to go previous level or Alt+P";
	private String faq17 = "Or Click on Next-> or Alt+N";
	private String faq18 = "Question: How could I exit the Game ?";
	private String faq19 = "Answer: Click on Exit label or go to Game -> Quit or Ctrl+E";
	private String faq20 =" ";
	private String faq21;
	private String faq22;
}
	


