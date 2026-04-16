package mode;

import canvas.Canvas;
import element.BasicObject;
import element.GeneralizationObject;
import element.Port;

public class GeneralizationMode extends LinkMode {
    public GeneralizationMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    protected BasicObject createLink(Port startPort, Port endPort) {
        return new GeneralizationObject(startPort, endPort);
    }
}
