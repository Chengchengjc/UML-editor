package mode;

import canvas.Canvas;
import element.AssociationObject;
import element.BasicObject;
import element.Port;

public class AssociationMode extends LinkMode {

    public AssociationMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    protected BasicObject createLink(Port startPort, Port endPort) {
        return new AssociationObject(startPort, endPort);
    }
}
