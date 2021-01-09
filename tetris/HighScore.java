package se.liu.ida.dinadress.tddd78.tetris;

public class HighScore
{
    private String name;
    private int highScore;

    public HighScore(int highScore, String name) {
	this.highScore = highScore;
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public int getHighScore() {
	return highScore;
    }

    @Override public String toString() {
	return "name = " + name + " highScore = " + highScore;
    }


}
