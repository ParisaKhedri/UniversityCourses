package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.BoardListener;
import se.liu.ida.dinadress.tddd78.lab5.HighScore;
import se.liu.ida.dinadress.tddd78.lab5.HighscoreList;
import se.liu.ida.dinadress.tddd78.lab5.Poly;
import se.liu.ida.dinadress.tddd78.lab5.SquareType;
import se.liu.ida.dinadress.tddd78.lab5.Start;
import se.liu.ida.dinadress.tddd78.lab5.TetrominoMaker;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board
{
    private SquareType[][] squares;
    private List<BoardListener> boardListeners;
    private int width;
    private int height;
    private  Random rnd;
    private Poly falling = null;
    private int fallingX;
    private int fallingY;
    private boolean gameover = false;
    //start
    private Start start;
    private int time = 0;
    private boolean startGame = false;
    //score managing
    private int currentScore = 0;
    private int fullRowCounter = 0;
    private HighscoreList sm;


    public Board(int width, int height) {
	this.width = width;
	this.height = height;
	this.boardListeners = new ArrayList<>();
	rnd = new Random();
	this.squares = new SquareType[height+4][width+4];
	this.start = new Start();
	this.sm = new HighscoreList();

	for(int x=2; x < width+2; x++){
	    for (int y=2; y < height+2; y++){
	        this.squares[y][x] = SquareType.EMPTY;
	    }
	}

	for(int x=0; x < width+4; x++){
	    for (int y=0; y < height+4 ; y++){
		if (squares[y][x] != SquareType.EMPTY) {
		    this.squares[y][x] = SquareType.OUTSIDE;
		}
	    }
	}
	notifyListeners();
    }


    public void createFallingPoly(int i) {
	TetrominoMaker tm = new TetrominoMaker();
	this.falling = tm.getPoly(i);
    }




    public SquareType getSquareTypes(int x, int y) {
	return squares[y+2][x+2];
    }

    public SquareType getSquareAt(int x, int y){
        if (this.falling == null) {
            return getSquareTypes(x,y);
	}
	if( x < fallingX || x > fallingX + falling.getWidth()-1 || y < fallingY || y > fallingY + falling.getHeight()-1){
	    return getSquareTypes(x,y);
	} else{
	    if (falling.getPolyArray()[x - fallingX][y - fallingY] == SquareType.EMPTY) {
	        return getSquareTypes(x,y);
	    }else{
	        return falling.getPolyArray()[x - fallingX][y - fallingY];
	    }
	}

    }

    public void randomBoard(){
	final SquareType[] myArray = SquareType.values();
	for(int i=0; i <width; i++){
	    for (int j=0; j < height; j++){
		int index = rnd.nextInt(myArray.length);
		squares[i][j] = myArray[index];
	    }
	}
	notifyListeners();
    }

    public void addBoardListener(BoardListener bl){
	boardListeners.add(bl);
    }

    private void notifyListeners(){
	for(BoardListener boardListener : boardListeners){
	    boardListener.boardChanged();
	}
    }

    public void gameOver(){
	this.gameover = true;
	falling = null;
	for(int x=2; x < width+2; x++){
	    for (int y=2; y < height+2; y++){
		this.squares[y][x] = SquareType.EMPTY;
	    }
	}
	String name = askName();
	System.out.println("game over!");
	HighScore score = new HighScore(currentScore, name);
	sm.addScore(score);
	notifyListeners();
    }

    public String askName(){
	String input = JOptionPane.showInputDialog("enter your name :");
	return input;
    }

    public void paintInBackground(){
	for(int x = 0; x <  this.falling.getWidth(); x++){
	    for (int y = 0; y < this.falling.getHeight(); y++){
	        if (this.falling.getPolyArray()[x][y] != SquareType.EMPTY){
	            this.squares[y+fallingY+2][x+fallingX+2] = this.falling.getPolyArray()[x][y];
		}
	    }
	}
	this.falling = null;
	notifyListeners();
    }

    public void tick(){
        time+=1;
        if(time>6){
            startGame = true;
	}
	if (!this.gameover && startGame){
	    if (this.falling == null) {
		int n = rnd.nextInt(7);
		createFallingPoly(n);
		this.fallingX = this.width / 2;
		this.fallingY = 0;
		if(hasCollison()){
		    gameOver();
		}
	    } else {
		this.fallingY += 1;
		if(hasCollison()){
		    this.fallingY -= 1;
		    paintInBackground();

		}
	    }
	}
	ifFull();
	setCurrentScore();
	notifyListeners();
    }

    public void setCurrentScore(){
	System.out.println(fullRowCounter);

	if(fullRowCounter == 1){
	    currentScore+=100;
	}else if (fullRowCounter == 2){
	    currentScore+=300;
	}else if (fullRowCounter == 3){
	    currentScore+=500;
	}else if (fullRowCounter == 4){
	    currentScore+=800;
	}
    }

    public void moveToLeft(){
	this.fallingX -= 1;
        if (hasCollison()){
	    this.fallingX += 1;
	}
	notifyListeners();
    }

    public void moveToRight(){
	this.fallingX += 1;
        if(hasCollison()){
	    this.fallingX -= 1;
	}
	notifyListeners();
    }

    public void rotate(boolean right){
	Poly orgPoly = this.falling;
	if(right){
	    falling = falling.rotateRight();
	}else{
	    falling = falling.rotateLeft();
	}
	if(hasCollison()){
	    falling = orgPoly;
	}
    }

    public boolean hasCollison(){
	for(int x = 0; x <  falling.getWidth(); x++){
	    for (int y = 0; y < falling.getHeight(); y++){
		if (falling.getPolyArray()[x][y] != SquareType.EMPTY && getSquareTypes(x+fallingX,y+fallingY) != SquareType.EMPTY){
		    return true;
		}
	    }
	}
	return false;
    }

    public void ifFull(){
        fullRowCounter = 0;
	for(int y=2; y < height+2; y++){
	    boolean full = true;
	    for (int x=2; x < width+2; x++){
		if (squares[y][x] == SquareType.EMPTY) {
		    full = false;
		    break;
		}
	    }
	    if(full){
	        fullRowCounter +=1;
		System.out.println(fullRowCounter);
		deleteFullRow(y);
		moveDownRow(y);
	    }
	}
    }

    public void deleteFullRow(int y){
	for(int x=2; x < width+2; x++){
	    squares[y][x] = SquareType.EMPTY;
	}
    }

    public void moveDownRow(int y){
        for (int i = y; 3 <= i; i--){
            squares[i] = squares[i-1];
	}
	for(int x=2; x < width+2; x++) {
	    squares[2][x] = SquareType.EMPTY;
	}
    }

    public Start getStart(){
        return start;
    }

    public boolean isStartGame(){
        return startGame;
    }

    public int getCurrentScore(){
        return currentScore;
    }

    public ArrayList<String> getScores(){
        return sm.getScores();
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

}


