package agario.gameobjects;
import java.awt.*;

/**
 * The shooting class for our game, the players shoot whenever a key is pressed.
 */

public class Bullet extends Abstractshape{
    private double dx;
    private double dy;
    private double radian;
    private double speed;
    private boolean firing;

    public Bullet(double angle, final double x, final double y, final Color color,final int radius){
        super(x,y,color,radius);
        radian = Math.toRadians(angle);
        speed = 15;
        dx = Math.cos(radian) * speed;
        dy = Math.sin(radian) * speed;
    }

    public boolean update(int width, int height) {
	x += dx;
	y += dy;
        return x < -radius || x > width + radius || y < -radius || y > height + radius;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int) (x-radius), (int)(y -radius), 2 * radius, 2 * radius);
    }

    @Override public void draw(final Graphics g) {

    }

}
