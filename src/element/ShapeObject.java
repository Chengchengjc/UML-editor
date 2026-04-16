package element;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class ShapeObject extends BasicObject {
    protected int x, y, width, height;
    protected ArrayList<Port> ports = new ArrayList<>();
    protected String labelText = null;
    protected Color objectColor = Color.GRAY;

    ShapeObject(int startX, int startY, int endX, int endY) {
        this.x = Math.min(startX, endX);
        this.y = Math.min(startY, endY);
        this.width = Math.abs(endX - startX);
        this.height = Math.abs(endY - startY);
    }

    public Port getClosestPort(double targetX, double targetY) {
        Port closestPort = null;
        double minDistance = Double.MAX_VALUE;

        for (Port port : ports) {
            double distance = Math.sqrt(Math.pow(port.getX() - targetX, 2) + Math.pow(port.getY() - targetY, 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestPort = port;
            }
        }

        return closestPort;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void drawSelection(Graphics g) {
        if (selected) drawPorts(g);
    }

    public void drawPorts(Graphics g) {
        for (Port port : ports)  port.drawPort(g);
    }

    public void setLabel(String labelText, Color objectColor) {
        this.labelText = labelText;
        this.objectColor = objectColor;
    }

    public String getLabelText() {
        return labelText;
    }

    public Color getObjectColor() {
        return objectColor;
    }

    protected void drawLabel(Graphics g) {
        if (labelText == null || labelText.trim().isEmpty())
            return;

        Rectangle bounds = getBounds();
        if (bounds.width <= 0 || bounds.height <= 0)
            return;

        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(labelText);
        int textX = bounds.x + (bounds.width - textWidth) / 2;
        int textY = bounds.y + (bounds.height + metrics.getAscent() - metrics.getDescent()) / 2;

        Color originalColor = g.getColor();
        g.setColor(Color.BLACK);
        g.drawString(labelText, textX, textY);
        g.setColor(originalColor);
    }

    protected abstract void updatePorts();

    public abstract Point getResizeAnchor(int portIndex);

    public int getPortIndexAt(int targetX, int targetY) {
        int hitRadius = 10; // 點到的範圍
        for (int i = 0; i < ports.size(); i++) {
            Port port = ports.get(i);
            if (Math.abs(port.getX() - targetX) <= hitRadius && Math.abs(port.getY() - targetY) <= hitRadius) {
                return i;
            }
        }
        return -1;
    }

    abstract public void resize(int portIndex, int newX, int newY, int anchorX, int anchorY);
}
