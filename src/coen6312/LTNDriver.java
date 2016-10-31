package coen6312;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;


public class LTNDriver extends JFrame implements ActionListener, ItemListener {
	private DataPanel dataPanel;
	private StavePanel stavePanel;
	private KeyboardPanel kPanel;
	
	private JButton startButton;
	
	private GameCore game;
	
	private int currentMinimumScore = 0;
	private int currentNumberOfScores = 0;
	
	private static final long serialVersionUID = 1L;
	private static final Map<String, String[]> menuOptions = new LinkedHashMap<String, String[]>() {
				private static final long serialVersionUID = 1L;
			{
			     put("File", new String[] {"Load song", "Exit"});
			     put("ScoreBoards", new String[] {"View all", "Reset scores"});
			     
			     //Do not change the name Options. Hard-coded functionality attached to this name.
			     //First strings define a radio group. Last element (Time) is a checkbox
			     put("Options", new String[] {"Bass scale", "Trebble scale", "Both", "Time"});
			     
			     put("Help", new String[] {"Music Theory", "About"});
			}};
    
	
	public void showScoreDialog(int score) {
		//Only games under time record scores:
		if(game.isTime()) {
			if(currentNumberOfScores < 10) {
				String name = JOptionPane.showInputDialog(
				        this, 
				        "You've set a new record (" + score + "). Please type in your name...", 
				        "New record!", 
				        JOptionPane.QUESTION_MESSAGE
				    );
				writeStringToFile("\n" + name + "::" + score,"./src/Scoreboard.txt", true);
			} else if (score > currentMinimumScore) {
			    String name = JOptionPane.showInputDialog(
				        this, 
				        "You've set a new record (" + score + "). Please type in your name...", 
				        "New record!", 
				        JOptionPane.QUESTION_MESSAGE
				    );	
				//Must remove the lowest score and replace with this one!
				String wholeFile = readFile("./src/Scoreboard.txt");
				String[] lines = wholeFile.split("\n");
				for(int i = 1 ; i < lines.length ; i++) {
					int num = Integer.parseInt(lines[i].split("::")[1]);
					if(num == currentMinimumScore) {
						lines[i] = name + "::" + score;
						break;
					}
				}
				String finalFile = String.join("\n", lines);
				writeStringToFile(finalFile,"./src/Scoreboard.txt", false);
			}
		}
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LTNDriver window = new LTNDriver();
				window.setTitle("Music Freak's Learn the Notes Game - D4"); 
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.setPreferredSize(new Dimension(700, 500));
				window.pack();
			    window.setLocationRelativeTo(null);
				window.setVisible(true);
				
				//Add key press events:
				KeyboardFocusManager.getCurrentKeyboardFocusManager()
				  .addKeyEventDispatcher(new KeyEventDispatcher() {
				      @Override
				      public boolean dispatchKeyEvent(KeyEvent e) {
				    	  int id = e.getID();
				          if (window.game != null && !window.game.isPaused() && window.kPanel != null && id == KeyEvent.KEY_TYPED) {
				        	  int notePressed = window.kPanel.ProcessKeyPress(e.getKeyChar());
				        	  if (notePressed >= 0) {
				        		  if(notePressed >= 100) {
				        			window.game.GradeTrebbleNoteAndContinue(notePressed-100);
				        		  } else {
					        		window.game.GradeBassNoteAndContinue(notePressed);
				        		  }
				        	  }
				          }
				          return false;
				      }
				});
			}
		});
	}
	
	private String readFile(String Path) {
		File file = new File(Path);
		FileInputStream fis = null;
		String resp = null;
		try {
			fis = new FileInputStream(file);
			resp = "";
			int character;
			while ((character = fis.read()) != -1) {
				resp += (char)character;   
			}			
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		try {
			if (fis != null) {
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resp;
	}
	
	private void writeStringToFile(String content, String f, boolean append) {
    	FileOutputStream fop = null;
		File file;
		try {
			file = new File(f);
			fop = new FileOutputStream(file, append);

 			if (!file.exists()) {
				file.createNewFile();
			}

			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			System.out.println("File " + f + ( append ? "updated!" : "reseted" ) );
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}	
	}
	
	public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String actionName = source.getText().toLowerCase();
        if (actionName.contains("exit")) {
        	System.exit(0);
        } else if (source instanceof JRadioButtonMenuItem) {
        	if (actionName.contains("trebble")) {
        		game.setMode(GameMode.TREBBLE);
        		this.restartGame();
            } else if (actionName.contains("bass")) {
        		game.setMode(GameMode.BASS);
            	this.restartGame();
            } else if (actionName.contains("both")) {
        		game.setMode(GameMode.BOTH);
            	this.restartGame();
            } 
        } else if (actionName.contains("view")) {
        	String scoreFile = readFile("./src/Scoreboard.txt");
        	String finalMessage = scoreFile.replace(':', ' ');
        	if(finalMessage.length()<13) {
        		finalMessage = "No scores currently found!\nPlay some more :)";
        	}
    		JOptionPane.showMessageDialog(this,
        		    finalMessage,
        		    "Learn the Notes Scoreboard",
        		    JOptionPane.PLAIN_MESSAGE);     	
        } else if (actionName.contains("reset")) {
        	writeStringToFile("Name::Scores","./src/Scoreboard.txt",false);
        } else if (actionName.contains("music")) {
        	
        	JPanel pane = new JPanel(); 
            pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));  
            pane.setPreferredSize(new Dimension(700, 400));
            pane.setBackground(Color.WHITE);
            pane.setAlignmentX( Component.LEFT_ALIGNMENT );
            
			JLabel label1 = new JLabel("<html>In music, each note represents a sound. <br>" + 
        		    "The notes are named Do, Re, Mi, Fa, Sol, La, and Si. <br>" +
        		    "The Stave is made of five lines and four spaces (counted upwards). <br>" +
        		    "The notes are written on and between the lines of a Stave. <br>" +
        		    "Notes are named according to the Clef. The Clef shown below is the Bass (or Fa Clef). <br></html>", null, JLabel.LEFT);
            
			ImageIcon bassImage = new ImageIcon(getClass().getResource("../BassClef.png"),null);
			JLabel label2 = new JLabel(bassImage, JLabel.CENTER);
			
			JLabel label3 = new JLabel("<html>In the same way, we have the Trebble (or Sol Clef) shown below. <br></html>", null, JLabel.LEFT);
			
			ImageIcon trebbleImage = new ImageIcon(getClass().getResource("../TrebbleClef.png"),null);
			JLabel label4 = new JLabel(trebbleImage, JLabel.CENTER);
			
			JLabel label5 = new JLabel("<html>Notice how, depending on the Clef, <b>the same position on the Stave may represent a different note</b>, " +
									  "so you'll really have to learn two note values for the same Stave position!<br></html>", null, JLabel.RIGHT);
			
			pane.add(label1);
			pane.add(label2);
			pane.add(label3);
			pane.add(label4);
			pane.add(label5);
			
        	JOptionPane.showMessageDialog(this, pane, "Music Theory", JOptionPane.PLAIN_MESSAGE);
        } else if (actionName.contains("about")) {
        	JOptionPane.showMessageDialog(this,
        		    "Software developed for the Fall 2016 COEN 6312 class using MDD. \n" +
        		    "Code base partially generated using UML + OCL.\n" +
        		    "Members:\n" +
        		    "Esteban Garro\n" +
        		    "Mandeep G\n" +
        		    "Ramanjit D\n" +
        		    "Hardilpreet\n" +
        		    "Sukh Bhatti\n",
        		    "About Learn the Notes",
        		    JOptionPane.PLAIN_MESSAGE);
        } else if (actionName.contains("load")) {
        	//Create a file chooser
        	JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                //This is where a real application would open the file.
                System.out.println("Opening: " + file.getName() + ".\n");
        		this.restartGame();
        		game.setNoteSource(file);
            } else {
                System.out.println("Opening canceled...\n");
            }        
        } else {
        	System.out.println("Time to " + actionName);
        }
    }
 
    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String actionName = source.getText().toLowerCase();
        
        if (actionName.contains("time")) {
        	if (e.getStateChange() == ItemEvent.SELECTED) {
        		this.restartGame();
        		game.setupTimedGame(10);
            } else {
        		this.restartGame();
        		game.setupTimedGame(0);
            }
        }
    }
    
    public void findAndStoreMinimumScore() {
    	String scoreFile = readFile("./src/Scoreboard.txt");
    	String[] lines = scoreFile.split("\n");
		int min = Integer.MAX_VALUE;
		for(int i = 1; i < lines.length; i++) {
			currentNumberOfScores++;
			int tmp = Integer.parseInt(lines[i].split("::")[1]);
			if( tmp < min ) {
				min = tmp;
			}
		}		
		if( min == Integer.MAX_VALUE) {
			currentMinimumScore = 0;
		} else {
			currentMinimumScore = min;	
		}
    }
    
	public LTNDriver() {	
		 this.initializeUI();
		 this.restartGame();
		 this.findAndStoreMinimumScore();
	}
	
	void restartGame() {
		this.getContentPane().removeAll();
		this.invalidate();
		
		this.resetUI();
		this.validate();
		
		if (game != null) {
			game.Reset(stavePanel,dataPanel,kPanel,this);
		} else {
			game = new GameCore(stavePanel,dataPanel,kPanel,this);
		}

		startButton.addActionListener (new ActionListener() {
      	  public void actionPerformed(ActionEvent e) {
      		  if(startButton.getText().toLowerCase().contains("start")) {
      			  game.Start();
      			  startButton.setText("Stop");
      		  } else {
      			  game.Stop();
      			  startButton.setText("Start!");
      		  }      		  
      	  }
		});
		
		this.repaint();
	}
	
	void initializeUI() {	
		//General setup:
        UIManager.put("OptionPane.background", Color.white);
        UIManager.put("Panel.background", Color.white);
		
        //Main layout
		this.getContentPane().setLayout(new BorderLayout());
        this.setJMenuBar(buildMenuBar()); 
	}
	
	void resetUI() {
	    //Where all GUI elements are created:				
        dataPanel = new DataPanel(); 
        dataPanel.setPreferredSize(new Dimension(700, 40));
        
        stavePanel = new StavePanel(); 
        stavePanel.setPreferredSize(new Dimension(700, 200));
        
        kPanel = new KeyboardPanel(); 
        kPanel.setPreferredSize(new Dimension(700, 200));
        
        this.getContentPane().add(dataPanel, BorderLayout.PAGE_START);
        this.getContentPane().add(stavePanel, BorderLayout.CENTER);
        this.getContentPane().add(kPanel, BorderLayout.PAGE_END);
        
        startButton = new JButton("Start!");
        this.getContentPane().add(startButton, BorderLayout.WEST);
	}
	
	private JMenuBar buildMenuBar() {	
        JMenuBar menuBar = new JMenuBar();
                
        //Build the menuBar.
        for (String key : menuOptions.keySet()) {
        	String[] array = menuOptions.get(key);
        	JMenu menu = new JMenu(key);
            menuBar.add(menu);
            if( key == "Options") {
            	//First options define a radio group:
            	ButtonGroup group = new ButtonGroup();
            	for (int i = 0; i < array.length - 1; i++){
            		JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(array[i]);
                    rbMenuItem.setSelected(true);
                    rbMenuItem.addActionListener(this);
                    group.add(rbMenuItem);
                    menu.add(rbMenuItem);
            	}
            	//Last option is a checkbox:
            	menu.addSeparator();
            	JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(array[array.length-1]);
                cbMenuItem.addItemListener(this);
                menu.add(cbMenuItem);
            } else { 
	            for (int i = 0; i < array.length; i++){
	            	JMenuItem menuItem = new JMenuItem(array[i]);
	                 menuItem.addActionListener(this);
	                 menu.add(menuItem);
	            }
            }
        }
        
        return menuBar;
	}
}
