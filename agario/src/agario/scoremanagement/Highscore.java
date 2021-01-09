package agario.scoremanagement;

/**
 * The winner's high score and name are saved as a highscore object.
 */
public class Highscore {
    private final String name;
    private int highScore;

    public Highscore(final int highScore, final String name) {
	this.highScore = highScore;
	this.name = name;
    }

    public String getName() {
	return name;
    }

    public int getHighScore() {
	return highScore;
    }

}
