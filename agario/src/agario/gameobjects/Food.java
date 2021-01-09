package agario.gameobjects;

import java.awt.*;

/**
 * This is the food class, it has the constructor, draw method getter for the radius.
 * The foods are the objects that the players eat in order to grow bigger.
 */

public class Food extends Abstractshape {

    public Food(final double x, final double y, final Color color,final int radius) {
	super(x,y,color,radius);
    }

    @Override public void draw(final Graphics g) {
        g.setColor(color);
        g.fillOval((int) x, (int) y, radius, radius);
    }
}
