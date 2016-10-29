package coen6312;

import javax.swing.*; 

import java.io.*; 
import java.util.HashMap;
import java.util.Map;
import java.awt.*; 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*; 

import javax.imageio.*; 
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

    
public class KeyboardPanel extends JPanel { 
	private static final long serialVersionUID = 1L;
	private static final Map<String, Integer> keyWeights = new HashMap<String, Integer>() {	
			private static final long serialVersionUID = 1L;
			{
			     put("q", 0);
			     put("w", 1);
			     put("e", 2);
			     put("r", 3);
			     put("t", 4);
			     put("g", 5);
			     put("h", 6);
			     put("b", 0);
			     put("n", 1);
			     put("j", 2);
			     put("k", 3);
			     put("l", 4);
			     put(";", 5);
			     put("'", 6);
			}};
			
	private Timer timer;
			
	public static BufferedImage keyboardImage;
	public static BufferedImage maskImage;
	
	public int currentBassOctave = 0;
	public int currentTrebbleOctave = 0;
	public char currentBassKey = 0;	
	public char currentTrebbleKey = 0;	
	
	public boolean shouldBass = true;
	public boolean shouldTrebble = false;
	
	public String songTitle = null;
	
	public KeyboardPanel () { 
		super(); 
		ActionListener action = new ActionListener() {   
	        @Override
	        public void actionPerformed(ActionEvent event) {
	           timer.stop();
	           repaint();
	        }
	    };
	    
		timer = new Timer(0, action);
	    timer.setInitialDelay(800);

		try {
			keyboardImage = ImageIO.read(new File("./src/Keyboard.png")); 
			maskImage = ImageIO.read(new File("./src/Mask.png")); 
		} 
		catch (IOException e) { 
			//Not handled.
			System.out.println("No image image found:" + e);
		} 
	} 
	
	public int ProcessKeyPress(char c) {
		Integer t = keyWeights.get(""+c);
		int fileNum = -1;
		if(t != null) {
			String fileToPlay = "./sounds/";
			if(shouldBass && (c=='q' || c=='w' || c=='e' || c=='r' || c=='t' || c=='g' || c=='h')) {
				currentBassKey = c;
				fileNum = currentBassOctave*7 + t;
				fileToPlay += "b" + fileNum + ".wav";
				playAudioFile(fileToPlay);
			} else if(shouldTrebble && (c=='\'' || c==';' || c=='l' || c=='k' || c=='j' || c=='n' || c=='b')) {
				currentTrebbleKey = c;
				fileNum = currentTrebbleOctave*7 + t;
				fileToPlay += "t" + fileNum + ".wav";
				playAudioFile(fileToPlay);
				fileNum += 100;
			} 
		}
		return fileNum;
	}

	private void playAudioFile(String path) {
		 try {	    	
		    	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
		    	Clip clip = AudioSystem.getClip();
		    	clip.open(audioInputStream);
		    	clip.start( );
		    }
		    catch(Exception ex) {  
		    	System.out.println("Problem playing audio file: " + ex);
		    }	    
			repaint();
	}
	
	public void paintComponent(Graphics g) { 
		g.drawImage(keyboardImage, 0, 0, null); 
		if(shouldBass) {
			g.drawImage(maskImage, currentBassOctave*105, 0, null);
		}
		if(shouldTrebble) {
			g.drawImage(maskImage, 595 - (2-currentTrebbleOctave)*105, 0, null);
		}
		
		g.setColor(Color.BLACK);
		
		if(songTitle != null) {
			g.setFont(new Font("Arial Black", Font.ITALIC, 18));
			g.drawString(songTitle, 200, 140);
		}
		
        g.setFont(new Font("Arial Black", Font.BOLD, 11));
        
        //Draw the Bass guide letters:
        int currentX = 4;
        for(int i=0;i<21;i++) {
        	switch(i%7) {
        		case 0:
        			g.drawString("q", currentX, 90);
        			break;
        		case 1:
        			g.drawString("w", currentX, 90);
        			break;
        		case 2:
        			g.drawString("e", currentX, 90);
        			break;
        		case 3:
        			g.drawString("r", currentX, 90);
        			break;
        		case 4:
        			g.drawString("t", currentX, 90);
        			break;
        		case 5:
        			g.drawString("g", currentX, 90);
        			break;
        		case 6:
        			g.drawString("h", currentX, 90);
        			break;
        	}
        	currentX += 15;
        }
        //Draw the Bass guide letters:
        currentX = 690;
        for(int j=0;j<21;j++) {
        	switch(j%7) {
        		case 0:
        			g.drawString("'", currentX, 90);
        			break;
        		case 1:
        			g.drawString(";", currentX, 90);
        			break;
        		case 2:
        			g.drawString("l", currentX, 90);
        			break;
        		case 3:
        			g.drawString("k", currentX, 90);
        			break;
        		case 4:
        			g.drawString("j", currentX, 90);
        			break;
        		case 5:
        			g.drawString("n", currentX, 90);
        			break;
        		case 6:
        			g.drawString("b", currentX, 90);
        			break;
        	}
        	currentX -= 15;
        }
     
    	g.setColor(Color.RED);
        
        if(currentBassKey>0) {
        	int circleX = currentBassOctave*105;        	
        	switch(currentBassKey) {
    		case 'q':
    			circleX += 0;
    			break;
    		case 'w':
    			circleX += 15;
    			break;
    		case 'e':
    			circleX += 30;
    			break;
    		case 'r':
    			circleX += 45;
    			break;
    		case 't':
    			circleX += 60;
    			break;
    		case 'g':
    			circleX += 75;
    			break;
    		case 'h':
    			circleX += 90;
    			break;
        	}
        	g.fillOval(circleX, 70, 12, 12);
        	currentBassKey = 0;
        }
        
        if(currentTrebbleKey>0) {
        	int circleX = 688 - (2-currentTrebbleOctave)*105;        	
        	switch(currentTrebbleKey) {
    		case '\'':
    			circleX -= 0;
    			break;
    		case ';':
    			circleX -= 15;
    			break;
    		case 'l':
    			circleX -= 30;
    			break;
    		case 'k':
    			circleX -= 45;
    			break;
    		case 'j':
    			circleX -= 60;
    			break;
    		case 'n':
    			circleX -= 75;
    			break;
    		case 'b':
    			circleX -= 90;
    			break;
        	}
        	g.fillOval(circleX, 70, 12, 12);
        	currentTrebbleKey = 0;
        }
        
        timer.start();
	} 
}