package coen6312;

import javax.swing.*; 

import java.io.*; 
import java.awt.*; 
import java.awt.image.*; 

import javax.imageio.*; 

public class StavePanel extends JPanel { 
	private static final long serialVersionUID = 1L;

	public static BufferedImage bassImage; 
	public static BufferedImage trebbleImage; 
	public static BufferedImage noteImage;

	public int currentBassNote = -1;
	public int currentTrebbleNote = -1;
	
	public boolean shouldBass = true;
	public boolean shouldTrebble = false;
	
	public void InsertBassNote(int b) {
		currentBassNote = b;
		repaint();
	}
	public void InsertTrebbleNote(int b) {
		currentTrebbleNote = b;
		repaint();
	}
	public StavePanel () { 
		super(new GridLayout(1,2)); 
        this.setBackground(Color.WHITE);
        
		try {
			bassImage = ImageIO.read(new File("./src/Bass.png"));
			trebbleImage = ImageIO.read(new File("./src/Trebble.png"));
			noteImage = ImageIO.read(new File("./src/Note.png"));
		} 
		catch (IOException e) { 
			System.out.println("Problem loading stave image: " + e); 
		} 
	} 

	public void paintComponent(Graphics g) { 
		if(shouldBass) {
			g.drawImage(bassImage, 145, 50, null); 
		}
		if(shouldTrebble){
			g.drawImage(trebbleImage, 455, 50, null); 
		}
		if(currentBassNote > -1) {
			g.drawImage(noteImage, 196, 165 - currentBassNote/2 - currentBassNote*5, null);
			currentBassNote = -1;
		}
		if(currentTrebbleNote > -1) {
			g.drawImage(noteImage, 506, 165 - currentTrebbleNote/2 - currentTrebbleNote*5, null);
			currentTrebbleNote = -1;
		}
	} 
}