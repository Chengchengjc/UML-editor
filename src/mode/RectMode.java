package mode;

import canvas.Canvas;
import element.BasicObject;
import element.RectObject;

public class RectMode extends ShapeMode {
    public RectMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    protected BasicObject createShapeAt(int x, int y) {
        int startX = x - FIXED_WIDTH / 2;
        int startY = y - FIXED_HEIGHT / 2;
        int endX = x + FIXED_WIDTH / 2;
        int endY = y + FIXED_HEIGHT / 2;
        return new RectObject(startX, startY, endX, endY);
    }
}
