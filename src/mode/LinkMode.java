package mode;

import java.awt.Color;
import java.awt.Graphics;

import canvas.Canvas;
import element.BasicObject;
import element.Port;
import element.ShapeObject;

public abstract class LinkMode extends Mode {
    protected Port startPort, endPort;
    private ShapeObject startShape;

    public LinkMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void mousePressed(int x, int y) {
        currentX = x;
        currentY = y;
        this.startShape = canvas.findShapeAt(x, y);
        if (this.startShape == null) {
            System.out.println("No shape found at (" + x + ", " + y + ")");
            return; // 沒有找到物件，直接返回
        }
        startPort = this.startShape.getClosestPort(x, y);
    }

    @Override
    public void mouseDragged(int x, int y) {
        currentX = x;
        currentY = y;

        // draw endport 預覽
        ShapeObject target = canvas.findShapeAt(x, y);
        if (target != null && target != startShape)
            endPort = target.getClosestPort(x, y);
        else
            endPort = null;
    }

    @Override
    public void mouseReleased(int x, int y) {
        ShapeObject target = canvas.findShapeAt(x, y);
        if (target == null || startPort == null || target == this.startShape) { // 起始同一個object -> reset
            resetPorts();
            return;
        }

        canvas.addObject(createLink(startPort, endPort));
        resetPorts();
    }

    protected abstract BasicObject createLink(Port startPort, Port endPort);

    protected void resetPorts() {
        startPort = null;
        endPort = null;
        startShape = null;
    }

    @Override
    public void draw(Graphics g) {
        if (startPort == null)
            return;

        g.setColor(Color.BLACK);
        startPort.drawPort(g);
        g.drawLine((int) startPort.getX(), (int) startPort.getY(), currentX, currentY);

        if (endPort != null)
            endPort.drawPort(g);
    }
}
