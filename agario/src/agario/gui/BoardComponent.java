package agario.gui;

import agario.board.Board;
import agario.gameobjects.Enemy;
import agario.gameobjects.Food;
import agario.gameobjects.Player;
import agario.gameobjects.Bullet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * The component class, here we draw the objects.
 */

public class BoardComponent extends JComponent implements KeyListener, BoardListener {
    private Board board;
    private Menu menu;
    private boolean menuState;

    public BoardComponent(Board board) {
	this.board = board;
	this.menu = board.getMenu();
	this.board.addBoardListener(this);
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(board.getWidth(), board.getHeight());
    }

    @Override protected void paintComponent(Graphics g) {
	final Graphics2D g2d = (Graphics2D) g;
	menuState = board.getMenuState();
	if(!menuState) {
	    for (int i = 0; i < board.getFoods().size(); i++) {
		Food f = board.getFoods().get(i);
		f.draw(g2d);
	    }
	    for (int i = 0; i < board.getPlayers().size(); i++) {
		Player p = board.getPlayers().get(i);
		p.draw(g2d);
	    }
	    List<Bullet> bullets = board.getBullets();
	    for (int i = 0; i < board.getBullets().size(); i++) {
		bullets.get(i).draw(g2d);
	    }
	    List<Enemy> enemies = board.getEnemies();
	    for (int i = 0; i < enemies.size(); i++) {
		enemies.get(i).draw(g2d);
	    }
	    printInfoBall(g2d);
	}else{
	    menu.render(g);
	}
    }

    public void printInfoBall(Graphics2D var1) {
	var1.setColor(Color.BLACK);
	Font font = new Font("arial", 1, 15);
	var1.setFont(font);
	int score1= board.getPlayer1CurrentScore();
	int score2 = board.getPlayer2CurrentScore();
	int highest = board.getHighScoreAllTime();
	String highestName = board.getWinnerAllTime();
	int highestp = board.getHighScorePreviousGame();
	String highestNamep = board.getWinnerPreviousGame();
	int position = 40;
	var1.drawString("HIGHSCORE: " + "score = " + highest + " name = " + highestName, position, position);
	var1.drawString("RESULT PREVIOUS GAME: " + "score = " + highestp + " name = " + highestNamep, position, position+20);
	var1.drawString("RADIUS OF BALL: Player1 = " + score1 + " Player2 = " + score2,position,position*2);
	var1.drawString("TIME: " + board.time,position,position*2+20);

    }

    @Override public void update(Graphics g) {
	paintComponent(g);
    }

    @Override public void boardChanged() {
	repaint();
    }

    @Override public void keyPressed(KeyEvent e) {
	char c = e.getKeyChar();
	if(!menuState) {
	    for (Player p : board.getPlayers()) {
		p.processControls(e);
	    }
	}
	board.notifyListeners();
    }

    @Override public void keyReleased(KeyEvent e) {
	char c = e.getKeyChar();
	for (Player p : board.getPlayers()) {
	    p.processControls(e);
	}
	for (Player p : board.getPlayers()) {
	    p.setFiring(false);
	}
	board.notifyListeners();
    }

    @Override public void keyTyped(KeyEvent e) {

    }

}


