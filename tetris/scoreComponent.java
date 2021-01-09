package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Board;
import se.liu.ida.dinadress.tddd78.lab5.BoardListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class scoreComponent extends JComponent implements BoardListener
{
    private Board board;
    public scoreComponent(Board board){
        this.board = board;
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	g.setColor(Color.black);
	Font font = new Font("arial", 1, 15);
	g.setFont(font);
	ArrayList<String> scores = board.getScores();
	int n = 40;
	for(String score: scores){
	    for(int i =0;i<=10;i++){
		g.drawString("SCORE: " + score, 60, n+=2);
	    }
	}
    }

    public Dimension getPreferredSize(){
	return new Dimension( 800,800);
    }

    @Override public void boardChanged() {
	repaint();
    }


}
