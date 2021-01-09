package se.liu.ida.dinadress.tddd78.tetris;


import se.liu.ida.dinadress.tddd78.lab5.SquareType;

public class Poly
{
    private SquareType[][] squares;
    private int width;
    private int  height;

    public Poly(final SquareType[][] squares) {
	this.squares = squares;
	this.height = squares.length;
        this.width = squares[0].length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight(){
        return height;
    }
    public SquareType[][] getPolyArray(){
        return squares;
    }

    public Poly rotateRight() {
        Poly newPoly = new Poly(new SquareType[width][height]);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++){
                newPoly.squares[c][width - 1 - r] = this.squares[r][c];
            }
        }
        return newPoly;
    }


    public Poly rotateLeft(){
        return this.rotateRight().rotateRight().rotateRight();
    }

}
