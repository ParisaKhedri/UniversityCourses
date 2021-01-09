package agario.gameobjects;

import java.awt.*;

/**
 * The abstract class for the classes we have which have a ĺot in common.
 */
public abstract class Abstractshape implements Shape {
    protected double x;
    protected double y;
    protected Color color;
    protected int radius;


    protected Abstractshape(final double x, final double y, final Color color, final int radius) {
	this.x = x;
	this.y = y;
	this.color = color;
	this.radius = radius;
    }

    @Override public double getX(){
        return x;
    }

    @Override public double getY(){
        return y;
    }

    @Override public int getRadius(){
	return radius;
    }

    @Override public Color getColor() {
	return color;
    }


}
