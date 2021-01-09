package se.liu.ida.dinadress.tddd78.tetris;

import se.liu.ida.dinadress.tddd78.lab5.Board;
import se.liu.ida.dinadress.tddd78.lab5.BoardListener;
import se.liu.ida.dinadress.tddd78.lab5.SquareType;
import se.liu.ida.dinadress.tddd78.lab5.Start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EnumMap;

public class TetrisComponent extends JComponent implements BoardListener{
    private Board board;
    private EnumMap<SquareType, Color> gg;
    static final int blocksize = 22;
    private Start st;

    public TetrisComponent(Board board){
	this.board = board;
        st = board.getStart();
        gg = new EnumMap<>(SquareType.class);
        gg.put(SquareType.I, Color.red);
        gg.put(SquareType.O, Color.blue);
        gg.put(SquareType.T, Color.pink);
        gg.put(SquareType.S, Color.green);
        gg.put(SquareType.Z, Color.gray);
        gg.put(SquareType.J, Color.orange);
        gg.put(SquareType.L, Color.white);
        gg.put(SquareType.EMPTY, Color.black);
        gg.put(SquareType.OUTSIDE, Color.yellow);

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"),"movetoleft");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"),"movetoright");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "rotateright");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "rotateleft");

        this.getActionMap().put("movetoleft", new MoveLeft());
        this.getActionMap().put("movetoright", new MoveRight());
        this.getActionMap().put("rotateright", new RotateRight());
        this.getActionMap().put("rotateleft", new RotateLeft());

    }

    private class MoveLeft extends AbstractAction{
        @Override public void actionPerformed(final ActionEvent e){
            board.moveToLeft();
        }
    }

    private class MoveRight extends AbstractAction{
        @Override public void actionPerformed(final ActionEvent e){
            board.moveToRight();
        }
    }

    private class RotateRight extends AbstractAction{
        @Override public void actionPerformed(final ActionEvent e){
            board.rotate(true);
        }
    }

    private class RotateLeft extends AbstractAction{
        @Override public void actionPerformed(final ActionEvent e){
            board.rotate(false);
        }
    }


    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean startGame = board.isStartGame();
        final Graphics2D g2d = (Graphics2D) g;
        if (!startGame){
            st.paintt(g);
        }else{
            for(int i=0; i < board.getWidth(); i++) {
                for (int j = 0; j < board.getHeight(); j++) {
                    SquareType ts = board.getSquareAt(i,j);
                    g2d.setColor(gg.get(ts));
                    g.fillRect(i*blocksize, j*blocksize, blocksize,blocksize);
                }
            }
        }
        printInfo(g2d);
    }

    public void printInfo(Graphics2D var1) {
        var1.setColor(Color.WHITE);
        Font font = new Font("arial", 1, 15);
        var1.setFont(font);
        int score = board.getCurrentScore();

        var1.drawString("SCORE: " + score, 20, 40);
        /*var1.drawString("RESULT PREVIOUS GAME: " + "score = " + highestp + " name = " + highestNamep, 40, 60);
        var1.drawString("RADIUS OF BALL: Player1 = " + score1 + " Player2 = " + score2,40,80);
        var1.drawString("TIME: " + board.time,40,100);*/
    }

    public Dimension getPreferredSize(){
        return new Dimension(board.getWidth()*blocksize, board.getHeight()*blocksize);
    }

    @Override public void boardChanged() {
        repaint();
    }


}
