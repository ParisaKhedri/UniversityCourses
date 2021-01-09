package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Board;
import se.liu.ida.dinadress.tddd78.lab5.TetrisViewer;

public class BoardTest
{
    public static void main(String[] args) {
	Board b = new Board(10, 20);
	TetrisViewer tw = new TetrisViewer(b);
	tw.show();
    }
}
