package element;

import java.awt.Color;
import java.awt.Graphics;

public class CompositionObject extends LinkObject {
    public CompositionObject(Port startPort, Port endPort) {
        super(startPort, endPort);
    }

    @Override
    public void drawLink(Graphics g, double angle, int arrowSize, double x1, double y1, double x2, double y2) {
        // 頂點（接線的點）
        int xTip = (int) x2;
        int yTip = (int) y2;

        // 往回一點（菱形中心）
        int xMid = (int) (x2 - arrowSize * Math.cos(angle));
        int yMid = (int) (y2 - arrowSize * Math.sin(angle));

        // 左右偏移（垂直方向）
        int xLeft = (int) (xMid + arrowSize / 2 * Math.cos(angle + Math.PI / 2));
        int yLeft = (int) (yMid + arrowSize / 2 * Math.sin(angle + Math.PI / 2));

        int xRight = (int) (xMid + arrowSize / 2 * Math.cos(angle - Math.PI / 2));
        int yRight = (int) (yMid + arrowSize / 2 * Math.sin(angle - Math.PI / 2));

        // 最後一個點（尾巴）
        int xBack = (int) (x2 - 2 * arrowSize * Math.cos(angle));
        int yBack = (int) (y2 - 2 * arrowSize * Math.sin(angle));

        int[] xPoints = { xTip, xLeft, xBack, xRight };
        int[] yPoints = { yTip, yLeft, yBack, yRight };

        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, 4);

        // 畫線
        int offset = 2 * arrowSize;

        int newX2 = (int) (x2 - offset * Math.cos(angle));
        int newY2 = (int) (y2 - offset * Math.sin(angle));

        g.drawLine((int) x1, (int) y1, newX2, newY2);
    }
}
