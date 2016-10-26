package coen6312;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.*; 


public class GameCore {
	private GameMode mode = GameMode.BASS;
	private boolean doesBass = true;
	private boolean doesTrebble = false;
	
	private boolean doesFile = false;
	
	private int initialTimeValue; // the duration in seconds
	private boolean doesTime = false;
	private Timer timer;
	private int delay = 1000; // the countdown interval (1 second)	    
	private int timeLeft;
	
	private DataPanel dataPanel;
	private StavePanel notePanel;
	
	private int currentNoteIndex = -1;
	private boolean currentBassNoteGraded = false;
	private boolean currentTrebbleNoteGraded = false;
	private int[] bassNoteSource = null; 
	private int[] trebbleNoteSource = null; 
	
	public void Start(StavePanel aPanel, DataPanel bPanel) {
		notePanel = aPanel;
		switch(mode) {
		case BASS:
			notePanel.shouldBass = true;
			notePanel.shouldTrebble = false;
			break;
		case TREBBLE:
			notePanel.shouldBass = false;
			notePanel.shouldTrebble = true;
			break;
		default:
			notePanel.shouldBass = true;
			notePanel.shouldTrebble = true;
			break;
		}
		
		dataPanel = bPanel;
		if(doesTime) {
			ActionListener action = new ActionListener() {   
		        @Override
		        public void actionPerformed(ActionEvent event) {
		            if(timeLeft == 0) {
		                timer.stop();
		                dataPanel.updateTime("Timed out!");
		                Stop();
		            }
		            else {
		            	dataPanel.updateTime(currentTimeStamp());
		            	timeLeft--;
		            }
		        }
		    };
		    
			timer = new Timer(delay, action);
		    timer.setInitialDelay(0);
		    timer.start();				
		}
		currentNoteIndex = 0;
		generateNote();
	}
	
	private void generateNote() {
		if(doesBass) {
			if (bassNoteSource == null) {
				doesFile = false;
				fillUpRandomBassNotes();
			} 
			notePanel.InsertBassNote(bassNoteSource[currentNoteIndex]);
		} 
		if(doesTrebble) {
			if (trebbleNoteSource == null) {
				doesFile = false;
				fillUpRandomTrebbleNotes();
			} 
			notePanel.InsertTrebbleNote(trebbleNoteSource[currentNoteIndex]);
		} 
	}
	
	private void fillUpRandomBassNotes() {
		bassNoteSource = new int[120];       
	    for(int i = 0; i < 120; i++) {
	      bassNoteSource[i] = (int)(Math.random()*21);
	    }
	}
	private void fillUpRandomTrebbleNotes() {
	    trebbleNoteSource = new int[120];       
	    for(int i = 0; i < 120; i++) {
	    	trebbleNoteSource[i] = (int)(Math.random()*21);
	    }
	}
	
	public void Stop() {
		
	}
	
	public boolean isBass() {
		return doesBass;
	}	
	public boolean isTrebble() {
		return doesTrebble;
	}
	public boolean isTime() {
		return doesTime;
	}
	public boolean isRandom() {
		return !doesFile;
	}
	
	public void GradeBassNoteAndContinue(int note) {
		if(note == bassNoteSource[currentNoteIndex]) {
			//Correct!
			System.out.println("CORRECT!");
		} else {
			//Incorrect!
			System.out.println("WRONG!");
		}
		currentBassNoteGraded = true;
		
		switch(mode) {
			case BASS:
				currentNoteIndex++;
				generateNote();
				break;
			case BOTH:
				if (currentTrebbleNoteGraded) {
					currentNoteIndex++;
					generateNote();
				}
				break;
			default:				
		}
	}
	
	public void GradeTrebbleNoteAndContinue(int note) {
		if(note == trebbleNoteSource[currentNoteIndex]) {
			//Correct!
			System.out.println("CORRECT!");
		} else {
			//Incorrect!
			System.out.println("WRONG!");
		}
		currentTrebbleNoteGraded = true;
		
		switch(mode) {
		case TREBBLE:
			currentNoteIndex++;
			generateNote();
			break;
		case BOTH:
			if (currentBassNoteGraded) {
				currentNoteIndex++;
				generateNote();
			}
			break;
		default:				
		}
	}
	
	public void setNoteSource(File noteFile) {
		if(noteFile != null) {
			FileInputStream fis = null;
    		try {
    			fis = new FileInputStream(noteFile);
				String notes = "";
				int character;
				while ((character = fis.read()) != -1) {
						notes += (char)character;   
				}
				System.out.println("Got all the notes: " + notes);
				doesFile = true;
				//Parse file and fill up bass and trebbleNoteSource:
				
    		} catch (IOException e1) {
    			e1.printStackTrace();
    		} finally {
    			try {
    				if (fis != null)
    					fis.close();
    			} catch (IOException ex) {
    				ex.printStackTrace();
    			}
    		}
		} else {
			doesFile = false;
			if(doesBass) {
				fillUpRandomBassNotes();
			}
			if(doesTrebble) {
				fillUpRandomTrebbleNotes();
			}
		}
	}
	
	public void setupTimedGame(int t) {
		if (t > 0) {
			doesTime = true;
			initialTimeValue = t;
			timeLeft = initialTimeValue;
			dataPanel.updateTime(currentTimeStamp());
		} else {
			doesTime = false;
		}
	}
	
	public void setMode(GameMode m) {
		switch(m) {
			case BASS:
				doesBass = true;
				doesTrebble = false;
				break;
			case TREBBLE:
				doesBass = false;
				doesTrebble = true;
				break;
			default:
				doesBass = true;
				doesTrebble = true;
				break;
		}
	}
	public GameMode getMode() {
		return mode;
	}
	
	private String currentTimeStamp() {
		String resp = "";
		int seconds = 0;
		if(timeLeft >= 60) {
			int minutes = timeLeft/60;
			seconds = timeLeft % 60;
			if(minutes>9) {
				resp += "" + minutes + ":";
			} else {
				resp += "0" + minutes + ":";
			}
		} else {
			resp += "00:";
			seconds = timeLeft;
		}
		
		if (seconds>9) {
			resp += "" + seconds;	
		} else {
			resp += "0" + seconds;	
		}		
		return resp;
	}
}
