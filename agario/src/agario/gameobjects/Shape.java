package agario.gameobjects;

import java.awt.*;
/**
 * Interface with some common methods
 * @param g
 */
public interface Shape {
    public void draw(final Graphics g);

    abstract double getX();

    abstract double getY();

    abstract Color getColor();

    abstract int getRadius();

}

