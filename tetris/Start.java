package se.liu.ida.dinadress.tddd78.tetris;

import javax.swing.*;
import java.awt.*;

public class Start extends JComponent{
    final ImageIcon icon;

    public Start(){
	icon = new ImageIcon(ClassLoader.getSystemResource("start.jpg"));

    }

    public void paintt(final Graphics g) {
	final Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	this.icon.paintIcon(this, g, 0, 0);
    }

}
