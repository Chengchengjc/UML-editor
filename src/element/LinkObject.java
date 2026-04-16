package element;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class LinkObject extends BasicObject {
    protected Port startPort, endPort;

    LinkObject(Port startPort, Port endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
    }

    @Override
    public void draw(Graphics g) {
        double x1 = startPort.getX();
        double y1 = startPort.getY();
        double x2 = endPort.getX();
        double y2 = endPort.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int arrowSize = 10;
        drawLink(g, angle, arrowSize, x1, y1, x2, y2);

    }

    public abstract void drawLink(Graphics g, double angle, int arrowSize, double x1, double y1, double x2,
            double y2);

    @Override
    public void move(int dx, int dy) {
    }

    @Override
    public boolean isInside(int dx, int dy) {
        return false;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }
}
