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
	private KeyboardPanel keyPanel;
	
	private int currentNoteIndex = -1;
	private int correctAnswers = 0;
	//private int wrongAnswers = 0;
	
	private boolean currentBassNoteGraded = false;
	private boolean currentTrebbleNoteGraded = false;
	private int[] bassNoteSource = null; 
	private int[] trebbleNoteSource = null; 
	
	private boolean paused = true;
	
	public void Reset(StavePanel aPanel, DataPanel bPanel, KeyboardPanel cPanel) {
		dataPanel = bPanel;
		
		notePanel = aPanel;
		notePanel.shouldBass = doesBass;
		notePanel.shouldTrebble = doesTrebble;
		
		keyPanel = cPanel;
		keyPanel.shouldBass = doesBass;
		keyPanel.shouldTrebble = doesTrebble;
		keyPanel.songTitle = null;
		bassNoteSource = null; 
		trebbleNoteSource = null;
		
		if (timer != null) {
			timer.stop();
			timer = null;
			timeLeft = initialTimeValue;
		}
		currentNoteIndex = -1;
		correctAnswers = 0;
		//wrongAnswers = 0;
		currentBassNoteGraded = false;
		currentTrebbleNoteGraded = false;
		paused = true;
	}

	public GameCore(StavePanel aPanel, DataPanel bPanel, KeyboardPanel cPanel) {
		notePanel = aPanel;
		dataPanel = bPanel;
		keyPanel = cPanel;
	}
	
	public void Start() {
		paused = false;
		if(doesTime) {
			ActionListener action = new ActionListener() {   
		        @Override
		        public void actionPerformed(ActionEvent event) {
		            if(timeLeft == 0) {
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
		currentBassNoteGraded = false;
		currentTrebbleNoteGraded = false;
		if(doesBass) {
			if (bassNoteSource == null) {
				doesFile = false;
				fillUpRandomBassNotes();
			} 
			int bassNote = bassNoteSource[currentNoteIndex];
			keyPanel.currentBassOctave = bassNote/7;
			notePanel.InsertBassNote(bassNote);
		} 
		if(doesTrebble) {
			if (trebbleNoteSource == null) {
				doesFile = false;
				fillUpRandomTrebbleNotes();
			} 
			int trebbleNote = trebbleNoteSource[currentNoteIndex];
			keyPanel.currentTrebbleOctave = trebbleNote/7;
			notePanel.InsertTrebbleNote(trebbleNote);
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
		if(timer != null) {
			timer.stop();
		}
		paused = true;
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
	public boolean isPaused() {
		return paused;
	}
	
	private void updateStats() {
		dataPanel.updateScore("" + correctAnswers);
		switch(mode) {
		case BOTH:
			dataPanel.updateAccuracy(100.0*correctAnswers/(2*currentNoteIndex) + "%");
			break;
		default:
			dataPanel.updateAccuracy(100.0*correctAnswers/currentNoteIndex + "%");
			break;
		}
	}
	
	private void continueNotes() {
		Timer timer = new Timer(50, new ActionListener() {
			  @Override
			  public void actionPerformed(ActionEvent arg0) {
				  	notePanel.shouldDisplayBassGrades = false;
				  	notePanel.shouldDisplayTrebbleGrades = false;
				  	updateStats();
					generateNote();
			  }
			});
		timer.setRepeats(false); // Only execute once
		timer.start();
	}
	
	public void GradeBassNoteAndContinue(int note) {
		if(!currentBassNoteGraded) {
		  	notePanel.shouldDisplayBassGrades = true;
			if(note == bassNoteSource[currentNoteIndex]) {
				notePanel.MarkBassNote(true);
				correctAnswers++;
			} else {
				notePanel.MarkBassNote(false);
				//wrongAnswers++;
			}
			currentBassNoteGraded = true;
			
			switch(mode) {
				case BASS:
					currentNoteIndex++;
					continueNotes();
					break;
				case BOTH:
					if (currentTrebbleNoteGraded) {
						currentNoteIndex++;
						continueNotes();
					}
					break;
				default:				
			}
		}
	}
	
	public void GradeTrebbleNoteAndContinue(int note) {
		if(!currentTrebbleNoteGraded) {
		  	notePanel.shouldDisplayTrebbleGrades = true;
			if(note == trebbleNoteSource[currentNoteIndex]) {
				notePanel.MarkTrebbleNote(true);
				correctAnswers++;
			} else {
				notePanel.MarkTrebbleNote(false);
				//wrongAnswers++;
			}
			currentTrebbleNoteGraded = true;
			
			switch(mode) {
			case TREBBLE:
				currentNoteIndex++;
				continueNotes();
				break;
			case BOTH:
				if (currentBassNoteGraded) {
					currentNoteIndex++;
					continueNotes();
				}
				break;
			default:				
			}
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
				doesFile = true;
				//Parse file and fill up bass and trebbleNoteSource:
				String[] lines = notes.split("\n");
				bassNoteSource = new int[120]; 
				trebbleNoteSource = new int[120]; 
				int insertedBassNotes = 0;
				int insertedTrebbleNotes = 0;
				for(int i = 0; i < lines.length; i++) {
					if (i==0) {
						String[] titleSplit = lines[i].split(":");
						keyPanel.songTitle = titleSplit[1];
					} else {
						String[] lineSplit = lines[i].split(":");
						String header = lineSplit[0].toLowerCase();
						if(header.equals("bass")) {
							String[] bassNotes = lineSplit[1].split("\\s+");
							//Fill up the notes:
							for(int j=0 ; j <bassNotes.length && insertedBassNotes < 120; j++) {
								switch(bassNotes[j].toLowerCase()) {
								case "la":
									bassNoteSource[insertedBassNotes++] = 0;
									break;
								case "si":
									bassNoteSource[insertedBassNotes++] = 1;
									break;
								case "do":
									bassNoteSource[insertedBassNotes++] = 2;
									break;
								case "re":
									bassNoteSource[insertedBassNotes++] = 3;
									break;
								case "mi":
									bassNoteSource[insertedBassNotes++] = 4;
									break;
								case "fa":
									bassNoteSource[insertedBassNotes++] = 5;
									break;
								case "sol":
									bassNoteSource[insertedBassNotes++] = 6;
									break;
								default:
									System.out.println("Wierd note found: " + bassNotes[j]);
									break;
								}
							}
						} else if(header.equals("trebble")) {
							String[] trebbleNotes = lineSplit[1].split("\\s+");
							//Fill up the notes:
							for(int j=0 ; j <trebbleNotes.length && insertedTrebbleNotes < 120; j++) {
								switch(trebbleNotes[j].toLowerCase()) {
								case "fa":
									trebbleNoteSource[insertedTrebbleNotes++] = 0;
									break;
								case "sol":
									trebbleNoteSource[insertedTrebbleNotes++] = 1;
									break;
								case "la":
									trebbleNoteSource[insertedTrebbleNotes++] = 2;
									break;
								case "si":
									trebbleNoteSource[insertedTrebbleNotes++] = 3;
									break;
								case "do":
									trebbleNoteSource[insertedTrebbleNotes++] = 4;
									break;
								case "re":
									trebbleNoteSource[insertedTrebbleNotes++] = 5;
									break;
								case "mi":
									trebbleNoteSource[insertedTrebbleNotes++] = 6;
									break;
								default:
									System.out.println("Wierd note found: " + trebbleNotes[j]);
									break;
								}
							}
						} else {
							System.out.println("Unknown line with code: " + lineSplit[0]);
						}
					}					
				}
				
				for(int r = insertedBassNotes; r<120; r++) {
					bassNoteSource[r] = bassNoteSource[r-insertedBassNotes];
				}
				for(int r = insertedTrebbleNotes; r<120; r++) {
					trebbleNoteSource[r] = trebbleNoteSource[r-insertedBassNotes];
				}
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
		mode = m;
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
