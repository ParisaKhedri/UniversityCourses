package agario.gameobjects;


import java.awt.*;

/**
 * This class handles the enemies which decrease the size of the player when they collide.
 * We have one type of enemy
 * The enemies start from the middle of the board
 */

public class Enemy extends Abstractshape{

    private double dx;
    private double dy;
    private double radian;
    private double speed;
    private int size;
    private boolean ready;
    private boolean dead;

    public Enemy(final double x, final double y, final Color color, final int radius) {
        super(x,y,color,radius);
        speed = 2;
        size = 1;
        double angle = Math.random() * 140 + 20;
        radian = Math.toRadians(angle);
        dx = Math.cos(radian) * speed;
        dy = Math.sin(radian) * speed;
        ready = false;
        dead = false;
    }

    @Override public void draw(final Graphics g) {

    }

    public boolean isDead(){
        return dead;
    }

    public void hit(){
        size--;
        if(size <= 0) {
            dead = true;
        }
    }

    public void update(int width, int height){
        x += dx;
        y += dy;

        if(!ready) {
            if(x > radius && x < width - radius && y > radius && y < height -radius) {   //Den är inuti board (spelplanen)
                ready = true;
            }
        }else if (x < radius && dx < 0){
            dx = -dx; //Så att de inte ska gå ur planen
        }else if (y < radius && dy < 0){
            dy = -dy;
        }else if(x > width - radius && dx > 0){
            dx = -dx;
        }else if(y > height -radius && dy > 0) {
            dy = -dy;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int)(x-radius), (int)(y-radius), 2*radius, 2*radius);
        g.setStroke(new BasicStroke(3));                                         //Tjockare gräns
        g.setColor(color.darker());
        g.drawOval((int)(x-radius), (int)(y-radius), 2*radius, 2*radius);
        g.setStroke(new BasicStroke(1));
    }
}

