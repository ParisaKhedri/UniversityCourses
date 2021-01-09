package se.liu.ida.dinadress.tddd78.tetris;


import se.liu.ida.dinadress.tddd78.lab5.Board;
import se.liu.ida.dinadress.tddd78.lab5.HighScore;

import java.util.ArrayList;


public class HighscoreList
{
    private ArrayList<String> highScoreList;

    public HighscoreList(){
        this.highScoreList = new ArrayList<>();
    }

    public void addScore(HighScore score){
	highScoreList.add(score.toString());
    }

    public ArrayList<String> getScores() {
	return highScoreList;
    }

    /*public void dispose(){
        this.highScoreList = highScoreList.remove(0);
    }*/

    public static void main(String[] args){
        Board b = new Board(100,100);
        HighscoreList sm = new HighscoreList();
        sm.addScore(new HighScore(100,"Aida"));
        sm.addScore(new HighScore(200,"Tahera"));
	System.out.println(sm.getScores());
    }
}
