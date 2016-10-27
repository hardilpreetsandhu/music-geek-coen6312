package coen6312;

import javax.swing.*; 

import java.awt.*;

public class DataPanel extends JPanel { 
	private static final long serialVersionUID = 1L;
	
	private JLabel timeLabel;
	private JLabel scoreLabel;
	private JLabel accuracyLabel;

	private int score = 0;
	private int accuracy = 0;
	
	public DataPanel () { 
		super(new GridLayout(2,3)); 
        this.setBackground(Color.GREEN);
        
		timeLabel = new JLabel("", JLabel.CENTER);
		scoreLabel = new JLabel("" + score, JLabel.CENTER);
		accuracyLabel = new JLabel("" + accuracy, JLabel.CENTER);
		
		Font boldFont = new Font("Arial Black", Font.BOLD, 18);
		timeLabel.setFont(boldFont);
		scoreLabel.setFont(boldFont);
		accuracyLabel.setFont(boldFont);
		
		this.add(new JLabel("TIME:", JLabel.CENTER));
        this.add(new JLabel("SCORE:", JLabel.CENTER));
        this.add(new JLabel("ACCURACY:", JLabel.CENTER));
        
        this.add(timeLabel);
        this.add(scoreLabel);
        this.add(accuracyLabel);
	} 
   	
	public void updateTime(String s) {
		timeLabel.setText(s);
	}
	public void updateScore(String s) {
		scoreLabel.setText(s);
	}
	public void updateAccuracy(String s) {
		accuracyLabel.setText(s);
	}
	
	public void paintComponent(Graphics g) { 
		repaint(); 
	} 
}