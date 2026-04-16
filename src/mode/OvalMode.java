package mode;

import canvas.Canvas;
import element.BasicObject;
import element.OvalObject;

public class OvalMode extends ShapeMode {
    public OvalMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    public BasicObject createShapeAt(int x, int y) {
        int startX = x - FIXED_WIDTH / 2;
        int startY = y - FIXED_HEIGHT / 2;
        int endX = x + FIXED_WIDTH / 2;
        int endY = y + FIXED_HEIGHT / 2;
        return new OvalObject(startX, startY, endX, endY);
    }
}
