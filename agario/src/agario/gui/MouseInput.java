package agario.gui;

import agario.board.Board;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * In this class we use the mousePressed method to get the right coordinates for the buttons.
 */
public class MouseInput extends MouseAdapter {

    private Board b;

    public MouseInput(Board b){
        this.b = b;
    }

    public void mousePressed(final MouseEvent e) {
         int mx = e.getX();
         int my = e.getY();
         int i = 55;
         if (mx >= b.getWidth() / 2 -i && mx <= b.getWidth() / 2 + i) {
             if (my >= 150 && my <= 200) {
                 b.setMenuState(false);
             }
         }
         if (mx >= b.getWidth() / 2 - i && mx <= b.getWidth() / 2 + i) {
             if (my >= 250 && my <= 300) {
                 System.exit(1);
             }
         }
     }

}

