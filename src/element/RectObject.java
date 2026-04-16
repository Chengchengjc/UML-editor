package element;

import java.awt.Graphics;
import java.awt.Point;

public class RectObject extends ShapeObject {

    public RectObject(int startX, int startY, int endX, int endY) {
        super(startX, startY, endX, endY);
        updatePorts();
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(objectColor);
        g.fillRect(x, y, width, height);
        g.drawRect(x, y, width, height);
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
        return dx >= this.x && dx <= this.x + width && dy >= this.y && dy <= this.y + height;
    }

    private static final int MIN_SIZE = 60;

    private void updateHorizontalBounds(int newX, int anchorX) {
        width = Math.max(Math.abs(newX - anchorX), MIN_SIZE);
        if (newX < anchorX)
            x = anchorX - width; // 往左拉，anchor 在右
        else
            x = anchorX; // 往右拉，anchor 在左
    }

    private void updateVerticalBounds(int newY, int anchorY) {
        height = Math.max(Math.abs(newY - anchorY), MIN_SIZE);
        if (newY < anchorY)
            y = anchorY - height; // 往上拉，anchor 在下
        else
            y = anchorY; // 往下拉，anchor 在上

    }

    @Override
    public void resize(int portIndex, int newX, int newY, int anchorX, int anchorY) {
        switch (portIndex) {
            case 0: // 左上
            case 2: // 右上
            case 4: // 右下
            case 6: // 左下
                updateHorizontalBounds(newX, anchorX);
                updateVerticalBounds(newY, anchorY);
                break;
            case 1: // 上
            case 5: // 下
                updateVerticalBounds(newY, anchorY);
                break;

            case 3: // 右
            case 7: // 左
                updateHorizontalBounds(newX, anchorX);
                break;
            default:
                return;
        }

        updatePorts();
    }

    @Override
    protected void updatePorts() {
        int left = x;
        int right = x + width;
        int top = y;
        int bottom = y + height;
        int midX = x + width / 2;
        int midY = y + height / 2;

        // 8 ports: 4 corners + 4 edge centers.
        double[][] positions = {
                { left, top }, { midX, top },
                { right, top }, { right, midY },
                { right, bottom }, { midX, bottom },
                { left, bottom }, { left, midY }
        };

        if (ports.size() != positions.length) { // 新物件
            ports.clear();
            for (double[] position : positions) {
                ports.add(new Port(position[0], position[1]));
            }
            return;
        }

        for (int i = 0; i < positions.length; i++) { // 已存在物件，更新port位置
            ports.get(i).setPosition(positions[i][0], positions[i][1]);
        }
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
                return new Point(right, bottom); // 左上 -> 固定右下
            case 1:
                return new Point(midX, bottom); // 上中 -> 固定下中
            case 2:
                return new Point(left, bottom); // 右上 -> 固定左下
            case 3:
                return new Point(left, midY); // 右中 -> 固定左中
            case 4:
                return new Point(left, top); // 右下 -> 固定左上
            case 5:
                return new Point(midX, top); // 下中 -> 固定上中
            case 6:
                return new Point(right, top); // 左下 -> 固定右上
            case 7:
                return new Point(right, midY); // 左中 -> 固定右中
            default:
                return null;
        }
    }
}
