package canvas;

import java.awt.Rectangle;
import java.util.ArrayList;

import element.BasicObject;
import element.ShapeObject;

class SelectionManager {
    private ArrayList<BasicObject> objects;

    public SelectionManager(ArrayList<BasicObject> objects) {
        this.objects = objects;
    }

    public void clearSelection() {
        for (BasicObject obj : objects)
            obj.setSelected(false);
    }

    public void moveSelectedObjects(int dx, int dy) {
        for (BasicObject obj : objects) { // 有可能是group obj
            if (obj.isSelected())
                obj.move(dx, dy);
        }
    }

    public void selectObjectsInArea(Rectangle selectRect) { // 框選區域內的物件被選取
        for (BasicObject obj : objects) {
            obj.setSelected(false); // 先把所有物件的選取狀態清掉，再重新判斷哪些物件被框選到

            Rectangle objRect = obj.getBounds();
            if (selectRect.contains(objRect))
                obj.setSelected(true);
        }
    }

    public int countSelectedObjects() {
        int count = 0;
        for (BasicObject obj : objects) {
            if (obj.isSelected())
                count++;
        }
        return count;
    }

    public ShapeObject getSingleSelectedShapeObject() {
        ShapeObject selectedShape = null;

        for (BasicObject obj : objects) {
            if (obj.isSelected() && obj instanceof ShapeObject) {
                if (selectedShape != null)
                    return null; // 已經找到一個被選取的ShapeObject了，現在又找到第二個，回傳null表示不只一個ShapeObject被選取
                selectedShape = (ShapeObject) obj;
            }
        }

        return selectedShape;
    }
}
