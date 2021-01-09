package agario.scoremanagement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Manages saving scores and high scores in a temp-file.
 */
public class ScoreManager {
    private int currentHighScore;
    private String filePath;
    private String temp = "TEMP.tmp";
    private boolean fileDeleted = false;


    public ScoreManager() {
	filePath = new File("").getAbsolutePath();
	File f = new File(filePath, temp);
	if (f.isFile()){
	    List<Highscore> scores = getScores();
	    if(scores.size() > 300){
	        deleteScores(f);
		createFile(f);
	    }
	}else{
	    createFile(f);
	}
    }

    public void deleteScores(File f) {
	if(f.isFile()){
	    f.delete();
	}
    }

    public void createFile(File f){
	try{
	    f.createNewFile();
	}catch(IOException e){
	    //an error occurred
	    e.printStackTrace();
	}
    }

    public void addScore(Highscore score){
	Gson gson = new Gson();
	List<Highscore> scores = getScores();
	if (scores == null){
	    scores = new ArrayList<>();
	}
	scores.add(score);
	try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath, temp)))){
	    String listAsJson = gson.toJson(scores);
	    writer.write(listAsJson);
	    writer.close();
	}catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public List<Highscore> getScores() {
	Gson gson = new Gson();
	List<Highscore> scores = new ArrayList<>();
	try {
	    scores = gson.fromJson(new FileReader(new File(filePath, temp)), new TypeToken<ArrayList<Highscore>>(){}.getType());
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	return scores;
    }

    public int getHighestScore(){
	List<Highscore> scores = getScores();
	Highscore highest = new Highscore(0,"");
	if(scores != null){
	    for (Highscore score : scores){
		if (score.getHighScore() > highest.getHighScore() ){
		    highest = score;
		}
	    }
	}
	return highest.getHighScore();
    }

    public String getWinnerAllTimeName(){
	List<Highscore> scores = getScores();
	Highscore highest = new Highscore(0,"");
	if(scores != null){
	    for (Highscore score : scores){
		if (score.getHighScore() > highest.getHighScore() ){
		    highest = score;
		}
	    }
	}
	return highest.getName();
    }

    public String getPreviuosWinner(){
	List<Highscore> scores = getScores();
	if(scores == null){
	    return"";
	}else{
	    return scores.get(scores.size() - 1).getName();
	}
    }

    public int getPreviuosHighScore(){
	List<Highscore> scores = getScores();
	if(scores == null){
	    return 0;
	}
	return scores.get(scores.size() - 1).getHighScore();
    }

}