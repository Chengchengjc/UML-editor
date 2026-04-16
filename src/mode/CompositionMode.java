package mode;

import canvas.Canvas;
import element.BasicObject;
import element.CompositionObject;
import element.Port;

public class CompositionMode extends LinkMode {
    public CompositionMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    protected BasicObject createLink(Port startPort, Port endPort) {
        return new CompositionObject(startPort, endPort);
    }
}
