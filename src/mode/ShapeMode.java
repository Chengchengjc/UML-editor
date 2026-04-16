package mode;

import java.awt.Graphics;

import canvas.Canvas;
import element.BasicObject;

public abstract class ShapeMode extends Mode {
    int startX, startY;
    protected static final int FIXED_WIDTH = 100;
    protected static final int FIXED_HEIGHT = 60;

    public ShapeMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void mousePressed(int x, int y) {
    }

    @Override
    public void mouseDragged(int x, int y) {
    }

    @Override
    public void mouseReleased(int x, int y) {
        BasicObject shape = createShapeAt(x, y);
        canvas.addObject(shape);
        canvas.switchToPreviousMode(); // 繪製完後切回上一個模式
    }

    @Override
    public void draw(Graphics g) {
    }

    protected abstract BasicObject createShapeAt(int x, int y);
}
