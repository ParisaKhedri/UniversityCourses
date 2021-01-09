package agario.gui;

import agario.board.Board;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * this class has the duty to make Agario window and it's components
 * to show up on the screen
 * we also create our two timers in this class
 */
public class BoardViewer {
    private Board board;
    private JFrame frame;
    private BoardComponent boardComponent;

    public BoardViewer(Board board) {
	this.board = board;
	this.frame = new JFrame("Agario");
	this.boardComponent = new BoardComponent(board);
	this.frame.addKeyListener(boardComponent);
	this.frame.getContentPane().addMouseListener(new MouseInput(board));
	this.board.addBoardListener(this.boardComponent);
	this.boardComponent.addMouseListener(new MouseInput(board));

	final Action doOneStep = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		board.tick();
	    }
	};

	final Action timer = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {
		board.timer();
	    }
	};

	final Timer clockTimer = new Timer(16 , doOneStep);
	clockTimer.setCoalesce(true);
	clockTimer.start();

	final Timer mTimer = new Timer(1000 , timer);
	mTimer.setCoalesce(true);
	mTimer.start();
    }

    public void show(){
	frame.setLayout(new BorderLayout());
	frame.add(boardComponent, BorderLayout.CENTER);
	frame.pack(); frame.setVisible(true);
    }
}
