package agario.board;

import agario.gui.BoardViewer;

/**
 * This class shows the board which has a width and a height.
 */
public class BoardTest {
    public static void main(String[] args) {
	Board b = new Board(800, 800);
	BoardViewer bv = new BoardViewer(b);
	bv.show();
    }
}
