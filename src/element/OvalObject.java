package element;

import java.awt.Graphics;
import java.awt.Point;

public class OvalObject extends ShapeObject {
    public OvalObject(int startX, int startY, int endX, int endY) {
        super(startX, startY, endX, endY);
        updatePorts();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(objectColor);
        g.fillOval(x, y, width, height);
        g.drawOval(x, y, width, height);
        drawLabel(g);
    }

    @Override
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        updatePorts();
    }

    @Override
    public boolean isInside(int dx, int dy) {
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;
        double a = width / 2.0; // horizontal radius
        double b = height / 2.0; // vertical radius
        // 橢圓公式
        double value = ((dx - centerX) * (dx - centerX)) / (a * a)
                + ((dy - centerY) * (dy - centerY)) / (b * b);

        return value <= 1.0;
    }

    @Override
    protected void updatePorts() {
        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;

        double[][] positions = {
                { centerX, y }, // 上
                { x + width, centerY }, // 右
                { centerX, y + height }, // 下
                { x, centerY } // 左
        };

        if (ports.size() != positions.length) {
            ports.clear();
            for (double[] position : positions) {
                ports.add(new Port(position[0], position[1]));
            }
            return;
        }

        for (int i = 0; i < positions.length; i++) {
            ports.get(i).setPosition(positions[i][0], positions[i][1]);
        }
    }

    @Override
    public void resize(int portIndex, int newX, int newY, int anchorX, int anchorY) {
        int minSize = 60; // 設定最小size = 60
        switch (portIndex) {
            case 0: // 上，固定 bottom
            case 2: // 下，固定 top
                height = Math.max(Math.abs(newY - anchorY), minSize);
                if (newY < anchorY)
                    y = anchorY - height; // 往上拉，anchorY 是 bottom
                else
                    y = anchorY; // 往下拉，anchorY 是 top
                break;

            case 1: // 右，固定 left
            case 3: // 左，固定 right
                width = Math.max(Math.abs(newX - anchorX), minSize);
                if (newX < anchorX)
                    x = anchorX - width; // 往左拉，anchorX 是 right
                else
                    x = anchorX; // 往右拉，anchorX 是 left
                break;

            default:
                return;
        }
        updatePorts();
    }

    public Point getResizeAnchor(int portIndex) { // 對角座標
        int left = x;
        int right = x + width;
        int top = y;
        int bottom = y + height;
        int midX = x + width / 2;
        int midY = y + height / 2;

        switch (portIndex) {
            case 0:
                return new Point(midX, bottom); // 上 → anchor 在下
            case 1:
                return new Point(left, midY); // 右 → anchor 在左
            case 2:
                return new Point(midX, top); // 下 → anchor 在上
            case 3:
                return new Point(right, midY); // 左 → anchor 在右
            default:
                return null;
        }
    }
}
