package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Board;

public class BoardToTextConverter
{
    public static String convertToText(Board board){
        StringBuilder result = new StringBuilder();
        for(int i=0; i < board.getWidth(); i++){
            for (int j=0; j < board.getHeight(); j++){
                switch(board.getSquareAt(i, j)){
                    case EMPTY:
                        result.append("_");
                        break;
                    case I:
                        result.append("|");
                        break;
                    case J:
                        result.append("J");
                        break;
                    case L:
                        result.append("L");
			break;
                    case O:
                        result.append("#");
			break;
                    case S:
                        result.append("$");
			break;
                    case T:
                        result.append("T");
			break;
                    case Z:
                        result.append("%");
			break;
                    case OUTSIDE:
                        result.append("[]");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }

}
