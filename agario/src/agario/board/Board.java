package agario.board;

import agario.gui.BoardListener;
import agario.gui.Menu;
import agario.gui.MouseInput;
import agario.gameobjects.Enemy;
import agario.gameobjects.Food;
import agario.gameobjects.Player;
import agario.gameobjects.Bullet;
import agario.scoremanagement.Highscore;
import agario.scoremanagement.ScoreManager;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is the game engine,it takes care of everything,
 * Creating enemies, foods and players. Foods and players at random positions ant enemies in the middle of the board.
 */

public class Board {
    public long time = 0; /**Boardcomponent needs access to this field*/
    private float nf = 2.0f;
    private int width;
    private int height;
    private List<Food> foods;
    private List<BoardListener> boardListeners;
    private List<KeyListener> keyListeners = null;
    private MouseListener mouseListener = null;
    private MouseInput mouseInput;
    private List<Player> players;
    private Random rnd;
    private boolean gameOver = false;
    public List<Bullet> bullets; /**Player needs needs access to this field*/
    private List<Enemy> enemies;
    /**score managing*/
    private ScoreManager sm;
    private int player1CurrentScore;
    private int player2CurrentScore;
    private String winnerPreviousGame = null;
    private int highScorePreviousGame;
    private int highScoreAllTime;
    private String winnerAllTime = null;
    /**menu*/
    private agario.gui.Menu menu;
    private boolean menuState = true;

    public Board(final int width, final int height) {
	this.width = width;
	this.height = height;
	rnd = new Random();
	menu = new agario.gui.Menu(this);
	mouseInput = new MouseInput(this);
	this.foods = new ArrayList<>();
	this.boardListeners = new ArrayList<>();
	this.players = new ArrayList<>();
	this.bullets = new ArrayList<>();
	this.enemies = new ArrayList<>();
	this.sm = new ScoreManager();
	setScorings();
	createRandomEnemy();
	createRandomFood(50);
	createPlayers();
	notifyListeners();
    }

    public void setScorings() {
	this.highScoreAllTime = sm.getHighestScore();
	this.winnerAllTime = sm.getWinnerAllTimeName();
	this.winnerPreviousGame = sm.getPreviuosWinner();
	this.highScorePreviousGame = sm.getPreviuosHighScore();
    }

    public void addBoardListener(BoardListener bl) {
	boardListeners.add(bl);
    }
    
    public void notifyListeners() {
	for (BoardListener boardListener : boardListeners) {
	    boardListener.boardChanged();
	}
    }

    public void createRandomFood(int n) {
	int radius = 6;
	for (int i = 0; i < n; i++) {
	    int x = rnd.nextInt(this.width);
	    int y = rnd.nextInt(this.height);
	    float r = rnd.nextFloat();
	    float g = rnd.nextFloat() / nf;
	    float b = rnd.nextFloat() / nf;
	    Color rndColor = new Color(r, g, b);
	    Food freshFood = new Food(x, y, rndColor, radius);
	    this.foods.add(freshFood);
	}
	notifyListeners();
    }

    public void createRandomEnemy() {
	enemies = new ArrayList<>();
	double enemyX = Math.random() *  this.width / 2 + this.height / 4;  //Randomposition där enemyn startar
	double enemyY = Math.random() *  this.height / 2 + this.width / 4;
	Color enemyColor = Color.red;
	for (int i = 0; i < 5; i++) {
	    enemies.add(new Enemy(enemyX, enemyY, enemyColor,5));
	}
	notifyListeners();
    }

    public static int checkDistance(double x1, double y1, double x2, double y2) {
	return (int) Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
    }

    public void saveSores() {
	if (players.get(0).getRadius() > players.get(1).getRadius()) {
	    Highscore score = new Highscore(players.get(0).getRadius(), players.get(0).getName());
	    sm.addScore(score);
	} else {
	    Highscore score2 = new Highscore(players.get(1).getRadius(), players.get(1).getName());
	    sm.addScore(score2);
	}
    }

    public void checkCollision() {
	if (players.isEmpty()) {
	    System.out.println("NO PLAYERS");
	    return;
	}
	if (players.size() == 2) {
	    if (checkDistance(players.get(0).getX(), players.get(0).getY(), players.get(1).getX(),
			      players.get(1).getY()) < players.get(0).getRadius() + players.get(1).getRadius()) {
		if (players.get(0).getRadius() > players.get(1).getRadius()) {
		    saveSores();
		    players.remove(1);
		} else {
		    saveSores();
		    players.remove(0);
		}
		this.gameOver = true;
		System.out.println("GAME OVER");
	    }

	    List<Food> foodsListToRemove = new ArrayList<>();

	    for (Player p : players) {
		for (Enemy e : enemies) {
		    for (Food f : foods) {
			if (checkDistance(p.getX(), p.getY(), e.getX(), e.getY()) < p.getRadius() + e.getRadius()) {
			    p.decreaseSize();
			    if (p.getRadius() < 10) {
				saveSores();
				players.remove(p);
				this.gameOver = true;
				System.out.println("GAME OVER");
			    }
			}
			if (checkDistance(p.getX(), p.getY(), f.getX(), f.getY()) < p.getRadius() + f.getRadius()) {
			    p.increaseSize();
			    foodsListToRemove.add(f);
			}
		    }
		}
	    }

	    if (enemies.size() == 2) {
		createRandomEnemy();
	    }
	    for (Food f : foodsListToRemove) {
		foods.remove(f);
	    }
	    if (foods.size() == 15) {
		createRandomFood(10);
	    }

	    //Kollisionen mellan skottet och fienden
	    for (int i = 0; i < bullets.size(); i++) {
		Bullet s = bullets.get(i);
		double sx = s.getX();
		double sy = s.getY();
		double sr = s.getRadius();
		for (int j = 0; j < enemies.size(); j++) {
		    Enemy e = enemies.get(j);
		    double ex = e.getX();
		    double ey = e.getY();
		    double er = e.getRadius();
		    double dx = sx - ex;                  //För att hitta distansen mellan de två punkterna
		    double dy = sy - ey;
		    double distance = Math.sqrt(dx * dx + dy * dy);

		    if (distance < sr + er) {
			e.hit();
			bullets.remove(i);
			i--;
			break;
		    }
		}
	    }
	    notifyListeners();
	}
    }

    public void setScores(){
	for (Player p : players){
	    if(p.getName().equals("player1")){
		this.player1CurrentScore = p.getRadius();
	    }else{
		this.player2CurrentScore = p.getRadius();
	    }
	}
    }

    public void timer() {
        if (!gameOver) {
            this.time += 1;
            if (time >= 180) {
                saveSores();
                this.gameOver = true;
            }
        }
    }

    public void tick() {

       if (!gameOver && !menuState) {
	   for (Player p : this.players) {
	       p.update();
	       if (p.getCanShoot()) {
	           bullets.add(new Bullet(270, 5, 2, Color.black, 4 ));
	       }
	   }
	   for (int k = 0; k < bullets.size(); k++) {
	       boolean remove = bullets.get(k).update(this.width, this.height);
	       if (remove) {
		   bullets.remove(k);
		   k--;
	       }
	   }
	   for (int i = 0; i < enemies.size(); i++) {
	       enemies.get(i).update(this.width,this.height);
	   }
	   for (int i = 0; i < enemies.size(); i++) {
	       if (enemies.get(i).isDead()) {
		   enemies.remove(i);
		   i--;
	       }
	   }
       }
	setScores();
	checkCollision();
	notifyListeners();
    }

    public void createPlayers() {
	for (int i = 1; i < 3; i++) {
	    float r = rnd.nextFloat();
	    float g = rnd.nextFloat() / nf;
	    float b = rnd.nextFloat() / nf;
	    Color rndC = new Color(r, g, b);
	    int playerX = rnd.nextInt(this.width);
	    int playerY = rnd.nextInt(this.height);
	    Player player = new Player(playerX, playerY, rndC, "player" + i, 30, this);
	    this.players.add(player);
	}
	for (Player p: players){
	    if(p.getName().equals("player2")){
		p.setAlternativeControls();
	    }
	}
	notifyListeners();
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public Menu getMenu(){
        return menu;
    }

    public boolean getMenuState(){
        return menuState;
    }

    public void setMenuState(boolean b){
        this.menuState = b;
    }

    public MouseInput getMouseInput(){
        return mouseInput;
    }

    public List<Food> getFoods() {
	return foods;
    }

    public List<Player> getPlayers() {
	return players;
    }

    public List<Bullet> getBullets() {
	return bullets;
    }

    public void setBullets(List<Bullet> bullets){
        this.bullets = bullets;
    }

    public List<Enemy> getEnemies(){
        return enemies;
    }

    public int getPlayer1CurrentScore() {
	return player1CurrentScore;
    }

    public int getPlayer2CurrentScore() {
	return player2CurrentScore;
    }

    public String getWinnerAllTime(){
	return this.winnerAllTime;
    }

    public String getWinnerPreviousGame() {
	return winnerPreviousGame;
    }

    public int getHighScoreAllTime(){
	return this.highScoreAllTime;
    }

    public int getHighScorePreviousGame() {
	return highScorePreviousGame;
    }


}



