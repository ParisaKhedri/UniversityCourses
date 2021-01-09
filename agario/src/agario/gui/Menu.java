package agario.gui;

import agario.board.Board;

import java.awt.*;

/**
 * This is the menu class with two buttons (play and quit)
 * The constructor takes Board as parameter and the buttons are initilazed as rectangles in the constructor.
 * The render method draws the title (AGARIO) and the title of the buttons.
 */
public class Menu
{
    private Board b;
    private Rectangle playButton;
    private Rectangle quitButton;


    public Menu(Board b) {
        int height = 50;
        int width = 100;
        int y = 150;
        this.b = b;
        this.playButton = new Rectangle(b.getWidth() / 2 - height, y, width, height);
        this.quitButton = new Rectangle(b.getWidth() / 2 - height, y + width, width, height);
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("arial", Font.BOLD, 50);
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString("AGARIO", b.getWidth() / 2 - 100, 100);
        Font font1 = new Font("arial", Font.BOLD, 30);
        g.setFont(font1);
        g.drawString("Play", playButton.x + 15, playButton.y + 35);
        g2d.draw(playButton);
        g.drawString("Quit", quitButton.x + 15, quitButton.y + 35);
        g2d.draw(quitButton);
    }

}