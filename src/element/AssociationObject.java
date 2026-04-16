package element;

import java.awt.Color;
import java.awt.Graphics;

public class AssociationObject extends LinkObject {

    public AssociationObject(Port startPort, Port endPort) {
        super(startPort, endPort);
    }

    @Override
    public void drawLink(Graphics g, double angle, int arrowSize, double x1, double y1, double x2, double y2) {
        int xA = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yA = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));

        int xB = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yB = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));
        g.setColor(Color.BLACK);

        g.drawLine((int) x2, (int) y2, xA, yA);
        g.drawLine((int) x2, (int) y2, xB, yB);

        g.drawLine((int) startPort.getX(), (int) startPort.getY(),
                (int) endPort.getX(), (int) endPort.getY());
    }
}
