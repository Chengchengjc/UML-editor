package element;

import java.awt.Color;
import java.awt.Graphics;

public class Port {
    private double x;
    private double y;

    Port(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void drawPort(Graphics g) {
        int r = 4;
        g.setColor(Color.RED);
        g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
