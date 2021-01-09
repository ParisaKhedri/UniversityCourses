package agario.gameobjects;

import agario.board.Board;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is the player class, in this class we have the code for everything that the players need.
 * One of the player has default controls and the setControl method sets the controls for the other player
 * There are different movement methods which are being called whenever the user presses a specific key
 * The player can shoot Enemies, in case of collision with the enemy the radius
 * of the player is reduced and increased in case of collision with food.
 */

public class Player extends Abstractshape {
    private String name;
    private Board board;
    private double speed;
    private Set<Direction> movement = new HashSet();
    private Map<Character,Direction> controls = new HashMap<>();
    private boolean firing;
    private long firingTimer;
    private long firingDelay;
    private boolean canShoot;
    private double inAndDecreaseSizeValue = 0.03;


    public Player(final double x, final double y, final Color color, final String name, final int radius, final Board board) {
	super(x, y, color, radius);
	this.name = name;
	this.speed = 5;
	this.board = board;
	setDefaultControls();
	this.firing = false;
	this.canShoot = true;
	this.firingTimer = System.nanoTime();  //System.nanoTime() returnerar det nuvarande värdet av timern i nanosekunder
	this.firingDelay = 200;

    }

    public void processControls(KeyEvent e) {
        Direction dir = controls.get(e.getKeyChar());
        if (dir==null) {
            return;
	}
        int iD = e.getID();
        switch (iD) {
	    case KeyEvent.KEY_PRESSED:
		movement.add(dir);
		break;
	    case KeyEvent.KEY_RELEASED:
		movement.remove(dir);
		break;
	}
    }

    private void setDefaultControls() {
	controls.put('w',Direction.UP);
	controls.put('W',Direction.UP);
	controls.put('s',Direction.DOWN);
	controls.put('S',Direction.DOWN);
	controls.put('d',Direction.RIGHT);
	controls.put('D',Direction.RIGHT);
	controls.put('a',Direction.LEFT);
	controls.put('A',Direction.LEFT);
	controls.put('q',Direction.SHOOT);
	controls.put('Q',Direction.SHOOT);
    }

    public void setAlternativeControls(){
	controls.clear();
	controls.put('o',Direction.UP);
	controls.put('O',Direction.UP);
	controls.put('l',Direction.DOWN);
	controls.put('L',Direction.DOWN);
	controls.put('ö',Direction.RIGHT);
	controls.put('Ö',Direction.RIGHT);
	controls.put('k',Direction.LEFT);
	controls.put('K',Direction.LEFT);
	controls.put('i',Direction.SHOOT);
	controls.put('I',Direction.SHOOT);
    }

    public void increaseSize(){
	this.radius += 1;
	this.speed -= inAndDecreaseSizeValue;
    }

    public void decreaseSize(){
	this.radius -= 1;
	this.speed += inAndDecreaseSizeValue;
    }

    public void setFiring(boolean b) {
        firing = b;
    }

    public void update() {
        for (Direction dir: movement){
	    move(dir);
	}
        if(firing) {
            long elapsed = (System.nanoTime()-firingTimer) / 1000000;
            canShoot = false;
	    if(elapsed > firingDelay) {
                board.bullets.add(new Bullet(270, x, y,Color.black, 4));
                firingTimer = System.nanoTime();
	    }
	}else{
            canShoot = true;
	}
    }

    public void move(Direction dir){
        switch (dir){
	    case UP:
	       this.moveUp();
	        break;
	    case DOWN:
	        this.moveDown();
	        break;
	    case LEFT:
	        this.moveLeft();
	        break;
	    case RIGHT:
	        this.moveRight();
	        break;
	    case SHOOT:
	        firing = true;
	        break;
	}
    }

    public void moveRight(){
        this.x+=speed;
	if (this.x >= board.getWidth() - this.radius){
	    this.x-=speed;
	}
    }

    public void moveLeft(){
	this.x -= speed;
        if(this.x <= 0){
	    this.x+= speed;
	}
    }

    public void moveUp(){
	this.y-=speed;
	if(this.y <= 0){
	    this.y+=speed;
	}
    }

    public void moveDown(){
	this.y+=speed;
	if(this.y >= board.getHeight() - this.radius){
	    this.y-=speed;
	}
    }

    @Override public void draw(final Graphics g) {
	g.setColor(color);
	g.fillOval((int)x, (int)y, radius, radius);
    }

    public String getName() {
	return name;
    }

    public double getSpeed() {
	return speed;
    }

    public boolean getCanShoot() {return canShoot;}
}

