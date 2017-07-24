import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public final class WareHouseBoss extends JFrame {
	private final int OFFSET = 50;
	private final int MAX_LEVEL = 5;
	private ArrayList<String> levels;
	MapGenerator str = new MapGenerator();
	private Panel panel;
	private String defaultMap = "Start Page";
	public static void main (String[] args) throws IOException{
		EventQueue.invokeLater(() -> {
			WareHouseBoss warehouse;
			try {
				warehouse = new WareHouseBoss();
				warehouse.setVisible(true);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		});
		
	}
	
	public WareHouseBoss() throws IOException{
		constructMenuBar();
		UserInterface();
	}
	
	public void UserInterface() throws IOException{
			panel = new Panel(defaultMap);
			levels = panel.getLevels();
			add(panel);
			ImageIcon icon1 = new ImageIcon("icon.jpg");
			setIconImage(icon1.getImage());
			setTitle("Warehouse Boss");
			setSize(panel.getBoardWidth()+50*OFFSET,panel.getBoardHeight()+50*OFFSET);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void constructMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help");
		JMenu gameMenu = new JMenu("Game");
		JMenu levelMenu = new JMenu("Level");
		JMenu optionsMenu = new JMenu("Options");
		JMenu backgroundMenu = new JMenu("Set Background");
		JMenu soundMenu = new JMenu("Sound");
		JMenu musicMenu = new JMenu("Background Music");
		JMenu gameSoundMenu = new JMenu("Game Sound");
		ButtonGroup colorGroup = new ButtonGroup();
		ButtonGroup musicGroup = new ButtonGroup();
		ButtonGroup gameSoundGroup = new ButtonGroup();
		//statusbar;
		
		ImageIcon newIcon = new ImageIcon("player.png");
		newIcon.setImage(getScaledImage(newIcon.getImage(), 15, 20));
		ImageIcon restartIcon = new ImageIcon("restart.png");
		restartIcon.setImage(getScaledImage(restartIcon.getImage(), 20, 20));
		ImageIcon quitIcon = new ImageIcon("quit.jpg");
		quitIcon.setImage(getScaledImage(quitIcon.getImage(), 15, 15));
		ImageIcon exitIcon = new ImageIcon("exit.jpg");
		exitIcon.setImage(getScaledImage(exitIcon.getImage(), 15, 20));
		JMenuItem randomLevel = new JMenuItem("Random");
		JMenuItem level1 = new JMenuItem("Level 1");
		JMenuItem level2 = new JMenuItem("Level 2");
		JMenuItem level3 = new JMenuItem("Level 3");
		JMenuItem level4 = new JMenuItem("Level 4");
		JMenuItem level5 = new JMenuItem("Level 5");
		JMenuItem level6 = new JMenuItem("Level 6");
		JMenuItem level7 = new JMenuItem("Level 7");
		JRadioButtonMenuItem r = new JRadioButtonMenuItem("Red");
		JRadioButtonMenuItem orange = new JRadioButtonMenuItem("Orange");
		JRadioButtonMenuItem y = new JRadioButtonMenuItem("Yellow");
		JRadioButtonMenuItem green = new JRadioButtonMenuItem("Green");
		JRadioButtonMenuItem b = new JRadioButtonMenuItem("Blue");
		JRadioButtonMenuItem violet = new JRadioButtonMenuItem("Violet");
		JRadioButtonMenuItem purple = new JRadioButtonMenuItem("Purple");
		JRadioButtonMenuItem pink = new JRadioButtonMenuItem("Pink");
		JRadioButtonMenuItem def = new JRadioButtonMenuItem("Default");
		JRadioButtonMenuItem onMusic = new JRadioButtonMenuItem("Turn on background music");
		JRadioButtonMenuItem offMusic = new JRadioButtonMenuItem("Turn off background music");
		JRadioButtonMenuItem onSound = new JRadioButtonMenuItem("Turn on game sound");
		JRadioButtonMenuItem offSound = new JRadioButtonMenuItem("Turn off game sound");
		JMenuItem newGame = new JMenuItem("New game", newIcon);
		newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		JMenuItem saveGame = new JMenuItem("Save game");
		saveGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		JMenuItem loadGame = new JMenuItem("Load game");
		loadGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		JMenuItem exit = new JMenuItem("Exit", exitIcon);
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		JMenuItem quit = new JMenuItem("Quit", quitIcon);
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		JMenuItem restart = new JMenuItem("Restart", restartIcon);
		restart.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		JMenuItem howToPlay = new JMenuItem("How to play");
		JMenuItem changeSettings = new JMenuItem("FAQ");
		
		levelMenu.add(randomLevel);
		levelMenu.add(level1);
		levelMenu.add(level2);
		levelMenu.add(level3);
		levelMenu.add(level4);
		levelMenu.add(level5);
		levelMenu.add(level6);
		levelMenu.add(level7);
		
		colorGroup.add(r);
		colorGroup.add(orange);
		colorGroup.add(y);
		colorGroup.add(green);
		colorGroup.add(b);
		colorGroup.add(violet);
		colorGroup.add(purple);
		colorGroup.add(pink);
		colorGroup.add(def);
		
		backgroundMenu.add(r);
		backgroundMenu.add(orange);
		backgroundMenu.add(y);
		backgroundMenu.add(green);
		backgroundMenu.add(b);
		backgroundMenu.add(violet);
		backgroundMenu.add(purple);
		backgroundMenu.add(pink);
		backgroundMenu.add(def);
		
		musicGroup.add(onMusic);
		musicGroup.add(offMusic);
		gameSoundGroup.add(onSound);
		gameSoundGroup.add(offSound);
		
		musicMenu.add(onMusic);
		musicMenu.add(offMusic);
		gameSoundMenu.add(onSound);
		gameSoundMenu.add(offSound);
		
		soundMenu.add(musicMenu);
		soundMenu.add(gameSoundMenu);
		
		optionsMenu.add(soundMenu);
		optionsMenu.add(backgroundMenu);
	
		gameMenu.add(newGame);
		gameMenu.add(saveGame);
		gameMenu.add(loadGame);
		gameMenu.add(restart);
		gameMenu.addSeparator();
		gameMenu.add(levelMenu);
		gameMenu.addSeparator();
		gameMenu.add(optionsMenu);
		gameMenu.addSeparator();
		gameMenu.add(exit);
		gameMenu.add(quit);
		
		helpMenu.add(howToPlay);
		helpMenu.add(changeSettings);
		
		menuBar.add(gameMenu);
		//menuBar.addSeparator();
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	
		
		randomLevel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				int currLevel = levels.indexOf(panel.getDefaultMap());
				Random random = new Random();
				int levelSelector = random.nextInt(MAX_LEVEL);
				while(levelSelector == currLevel)
					levelSelector = random.nextInt(5);
				panel.setDefaultMap(levels.get(MAX_LEVEL));
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString1());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString2());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString3());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		
		level4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//currentMap = str.getString2();
				panel.setDefaultMap(str.getString4());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString5());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level6.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString6());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		level7.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString7());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				panel.setDefaultMap("Start Page");
				panel.restart();
			}
		});
		
		restart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.restart();
			}
		});
			
		newGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap(str.getString1());
				panel.changeBackground();
				panel.restart();
			}
		});
		
		saveGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.saveGame();
			}
		});
		
		loadGame.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(panel.getIsSaveGame())
				panel.loadGame();
			}
		});
		
		quit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		howToPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				panel.setDefaultMap("How To Play");
				panel.initialiseGame(panel.getDefaultMap());
			}
		});
		
		r.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(255, 51, 51);
                //statusbar.setText("Hard");
            }
        });
		
		orange.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(255, 153, 51);
                //statusbar.setText("Hard");
            }
        });
		
		y.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(255, 255, 51);
                //statusbar.setText("Hard");
            }
        });
		
		green.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(51, 255, 51);
                //statusbar.setText("Hard");
            }
        });
		
		b.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(51, 51, 255);
                //statusbar.setText("Hard");
            }
        });
		
		violet.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(255, 51, 255);
                //statusbar.setText("Hard");
            }
        });
		
		purple.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(153, 51, 255);
                //statusbar.setText("Hard");
            }
        });
		
		pink.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackground(255, 51, 153);
                //statusbar.setText("Hard");
            }
        });
		
		def.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setBackgroundSet(false);
                //statusbar.setText("Hard");
            }
        });
		
		onMusic.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	if(panel.getGameMusicOff())
            	panel.playMusic(panel.getBackgroundMusic(), 1);
            }
        });
		
		offMusic.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.stopBackgroundMusic();
            }
        });
		
		onSound.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setGameSoundOff(false);
            }
        });
		
		offSound.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setGameSoundOff(true);
            }
        });
		
		changeSettings.addItemListener((ItemEvent e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
            	panel.setDefaultMap("FAQ");
				panel.initialiseGame(panel.getDefaultMap());
            }
        });
	}
	
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}
	
}
