package canvas;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;

import element.BasicObject;
import element.GroupObject;
import element.ShapeObject;
import mode.Mode;

public class Canvas { // 控制
    private ArrayList<BasicObject> objects = new ArrayList<>();
    private Mode currentMode;
    private Mode previousMode; // 追蹤上一個模式

    private SelectionManager selectionManager = new SelectionManager(objects);
    private GroupService groupService = new GroupService(objects);

    public void setPreviousMode() {
        this.previousMode = currentMode;
    }

    public void switchToPreviousMode() {
        // 如果當前是 RectMode 或 OvalMode，就切回上一個模式
        if (previousMode != null) {
            currentMode = previousMode;
        }
    }

    public void mousePressed(int x, int y) {
        currentMode.mousePressed(x, y);
    }

    public void mouseDragged(int x, int y) {
        currentMode.mouseDragged(x, y);
    }

    public void mouseReleased(int x, int y) {
        currentMode.mouseReleased(x, y);
    }

    public void mouseMoved(int x, int y) {
        currentMode.mouseMoved(x, y);
    }

    public ShapeObject findShapeAt(int x, int y) {
        for (int i = objects.size() - 1; i >= 0; i--) { // 從上層往下找
            if (objects.get(i) instanceof ShapeObject) {
                ShapeObject obj = (ShapeObject) objects.get(i);
                if (obj.isInside(x, y))
                    return obj;
            }
        }

        return null;
    }

    public ArrayList<BasicObject> getObjects() {
        return objects;
    }

    public ArrayList<BasicObject> getObjectsFromTop() {
        ArrayList<BasicObject> reversed = new ArrayList<>(objects);
        Collections.reverse(reversed);
        return reversed;
    }

    public String getSelectedLabelText() {
        ShapeObject selectedShape = selectionManager.getSingleSelectedShapeObject();
        return selectedShape != null && selectedShape.getLabelText() != null
                ? selectedShape.getLabelText()
                : "";
    }

    public Color getSelectedObjectColor() {
        ShapeObject selectedShape = selectionManager.getSingleSelectedShapeObject();
        return selectedShape != null // 只有一個ShapeObject被選取，才回傳它的顏色；如果選取了多於一個ShapeObject，就回傳灰色
                ? selectedShape.getObjectColor()
                : Color.gray;
    }

    public void addObject(BasicObject obj) {
        objects.add(obj);
    }

    public boolean applyLabelToSelectedObjects(String labelText, Color objectColor) {
        boolean updated = false;

        for (BasicObject obj : objects) {
            if (obj.isSelected() && obj instanceof ShapeObject) {
                ShapeObject shapeObject = (ShapeObject) obj;
                shapeObject.setLabel(labelText, objectColor);
                updated = true;
            }
        }

        return updated;
    }

    public void removeObject(BasicObject obj) {
        objects.remove(obj);
    }

    public Mode getMode() {
        return currentMode;
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    public void bringToFront(BasicObject obj) { // 更新object深度
        if (objects.remove(obj))
            objects.add(obj);
    }

    // selection related methods
    public void clearSelection() {
        selectionManager.clearSelection();
    }

    public void moveSelectedObjects(int dx, int dy) {
        selectionManager.moveSelectedObjects(dx, dy);
    }

    public void selectObjectsInArea(Rectangle selectRect) { // 框選區域內的物件被選取
        selectionManager.selectObjectsInArea(selectRect);
    }

    public int countSelectedObjects() {
        return selectionManager.countSelectedObjects();
    }

    // group & ungroup methods
    public boolean handleGroupAction() {
        return currentMode.group();
    }

    public GroupObject groupSelectedObjects() {
        return groupService.groupSelectedObjects();
    }

    public boolean handleUngroupAction() {
        return currentMode.ungroup();
    }

    public boolean ungroupSelectedObjects() {
        return groupService.ungroupSelectedObjects();
    }
}
