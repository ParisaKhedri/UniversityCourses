package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Board;
import se.liu.ida.dinadress.tddd78.lab5.TetrisComponent;
import se.liu.ida.dinadress.tddd78.lab5.scoreComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TetrisViewer
{
    private Board board;
    private TetrisComponent tetrisComponent;
    private JFrame frame;
    private JFrame scoreFrame;
    private  scoreComponent scoreComponent;


    public TetrisViewer(Board board) {
	this.board = board;
	this.frame = new JFrame("Tetris");
	this.scoreFrame = new JFrame("score frame");
	final JMenuBar menubar = new JMenuBar();
	this.tetrisComponent = new TetrisComponent(board);
	this.scoreComponent = new scoreComponent(board);
	this.board.addBoardListener(this.tetrisComponent);
	this.board.addBoardListener(this.scoreComponent);
	this.frame.setJMenuBar(menubar);
	final JMenu menu = new JMenu("Menu");
	JMenuItem exit = new JMenuItem("Exit the game");
	menu.add(exit);
	menubar.add(menu);

	final Action doOneStep = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
	        board.tick();
	    }
	};


	final Timer clockTimer = new Timer(500, doOneStep);
	clockTimer.setCoalesce(true);
	clockTimer.start();
	class exitAction implements ActionListener{
	    @Override public void actionPerformed(final ActionEvent actionEvent) {
		if(askuser()){
		    System.exit(0);
		}
	    }
	}
	exit.addActionListener(new exitAction());

    }
    public boolean askuser(){
	String input = JOptionPane.showInputDialog("Exit?");
	return input.equals("yes");
    }


    public void show(){
	frame.setLayout(new BorderLayout());
	frame.add(tetrisComponent, BorderLayout.CENTER);
	frame.pack();
	frame.setVisible(true);
	scoreFrame.setLayout(new BorderLayout());
	scoreFrame.add(scoreComponent, BorderLayout.CENTER);
	scoreFrame.pack();
	scoreFrame.setVisible(true);
    }

}
