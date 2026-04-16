package mode;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import canvas.Canvas;
import element.BasicObject;
import element.GroupObject;
import element.ShapeObject;

public class SelectMode extends Mode {
    int startX, startY, endX, endY;
    int resizeAnchorX, resizeAnchorY; // 紀錄resize開始的固定點
    BasicObject selectedObject;
    ShapeObject hoveredShape;
    boolean isSelectingObj = false, isResizingObj = false, isSelectingArea = false;
    Rectangle groupRect = null;
    int resizingPortIndex = -1;

    public SelectMode(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void mousePressed(int x, int y) { // 點物件 或點選好的框 或重新拉框
        resetPressState(x, y); // 負責初始化這次 press 的暫時狀態

        // 1. 先檢查有沒有點到已存在的群組方框
        if (handleGroupRectPress(x, y))
            return;

        // 2. 再檢查有沒有點到物件(port、shapeobject)
        for (BasicObject curObj : canvas.getObjectsFromTop()) { // 最上層開始
            // 檢查點到的是不是shapeobj的port
            if (handleResizePress(curObj, x, y))
                return;

            // 點到物件
            if (handleObjectPress(curObj, x, y))
                return;
        }

        // 3. 都沒點到，表示重新拉框
        startAreaSelection();
    }

    @Override
    public void mouseDragged(int x, int y) {
        currentX = x;
        currentY = y;

        if (isResizingObj)
            handleResizeDrag();
        else if (isSelectingObj)
            handleObjectDrag();
        else if (isSelectingArea)
            handleAreaSelectionDrag(x, y);
    }

    @Override
    public void mouseReleased(int x, int y) {
        if (isSelectingArea)
            updateGroupRect();

        resetActionState(); // 結束這次的 action，重置狀態
    }

    @Override
    public void mouseMoved(int x, int y) {
        hoveredShape = canvas.findShapeAt(x, y);
    }

    @Override // selectedObj 要顯示port、groupRect要顯示框選的綠色方框
    public void draw(Graphics g) {
        for (BasicObject obj : canvas.getObjects()) {
            if (obj.isSelected())
                obj.drawSelection(g);
        }

        if (hoveredShape != null && !hoveredShape.isSelected())
            hoveredShape.drawPorts(g);

        Rectangle rectToDraw = null;
        if (isSelectingArea) {
            int left = Math.min(startX, endX);
            int top = Math.min(startY, endY);
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            rectToDraw = new Rectangle(left, top, width, height);
        } else if (groupRect != null) { // 點到selectingArea框
            rectToDraw = groupRect;
        }

        if (rectToDraw != null) {
            g.setColor(Color.GREEN);
            g.drawRect(rectToDraw.x, rectToDraw.y, rectToDraw.width, rectToDraw.height);
        }
    }

    private boolean handleGroupRectPress(int x, int y) {
        if (groupRect != null && groupRect.contains(x, y)) {
            isSelectingObj = true; // 點到框選的方框
            return true;
        }
        return false;
    }

    private boolean handleObjectPress(BasicObject curObj, int x, int y) {
        if (curObj.isInside(x, y)) { // 點到物件
            selectedObject = curObj;

            // 如果這個物件原本沒被選到，就清掉其他選取
            if (!curObj.isSelected()) {
                canvas.clearSelection();
                curObj.setSelected(true);
                groupRect = null; // 單選時先把群組框清掉
            }

            // 被選到的物件直接提到最上層，避免之後被其他物件蓋住
            canvas.bringToFront(selectedObject);
            isSelectingObj = true;
            return true;
        }
        return false;

    }

    private boolean handleResizePress(BasicObject curObj, int x, int y) {
        if (groupRect == null && curObj.isSelected() && curObj instanceof ShapeObject) {
            ShapeObject shape = (ShapeObject) curObj;
            int portIndex = shape.getPortIndexAt(x, y);
            if (portIndex != -1) {
                saveResizeAnchor(shape, portIndex); // 存對角port的座標，resize時會以這個點為固定點
                selectedObject = curObj;
                isResizingObj = true;
                resizingPortIndex = portIndex;
                return true;
            }
        } // if
        return false;
    }

    private void handleResizeDrag() {
        if (selectedObject instanceof ShapeObject)
            ((ShapeObject) selectedObject).resize(resizingPortIndex, currentX, currentY, resizeAnchorX, resizeAnchorY);
    }

    private void handleObjectDrag() {
        int dx = currentX - startX;
        int dy = currentY - startY;

        canvas.moveSelectedObjects(dx, dy);
        if (groupRect != null) { // 移動框
            groupRect.translate(dx, dy);
        }

        // 更新起始點為目前點的位置
        startX = currentX;
        startY = currentY;
    }

    private void handleAreaSelectionDrag(int x, int y) {
        endX = x;
        endY = y;
        int left = Math.min(startX, endX);
        int right = Math.max(startX, endX);
        int top = Math.min(startY, endY);
        int bottom = Math.max(startY, endY);

        Rectangle selectRect = new Rectangle(left, top, right - left, bottom - top);
        canvas.selectObjectsInArea(selectRect); // 根據框選區域更新物件的選取狀態
    }

    private void startAreaSelection() {
        canvas.clearSelection();
        groupRect = null;
        isSelectingArea = true;
    }

    private void resetActionState() {
        selectedObject = null;
        isSelectingObj = false;
        isResizingObj = false;
        isSelectingArea = false;
        resizingPortIndex = -1;
    }

    private void updateGroupRect() {
        int left = Math.min(startX, endX);
        int right = Math.max(startX, endX);
        int top = Math.min(startY, endY);
        int bottom = Math.max(startY, endY);

        if (canvas.countSelectedObjects() > 1) {
            groupRect = new Rectangle(left, top, right - left, bottom - top);
        } else
            groupRect = null;
    }

    @Override
    public boolean group() {
        return groupSelectedObjects();
    }

    @Override
    public boolean ungroup() {
        return ungroupSelectedObjects();
    }

    public boolean ungroupSelectedObjects() {
        boolean result = canvas.ungroupSelectedObjects();

        if (result) {
            groupRect = null;
            selectedObject = null;
            isSelectingObj = false;
            isSelectingArea = false;
        }

        return result;
    }

    public boolean groupSelectedObjects() {
        GroupObject groupObject = canvas.groupSelectedObjects();

        if (groupObject != null) {
            Rectangle bounds = groupObject.getBounds();
            groupRect = new Rectangle(bounds.x, bounds.y, bounds.width, bounds.height);
            selectedObject = groupObject;
            isSelectingObj = false;
            isSelectingArea = false;
        }

        return groupObject != null;
    }

    private void resetPressState(int x, int y) {
        startX = x;
        startY = y;
        endX = x;
        endY = y;
        resetActionState();
    }

    private void saveResizeAnchor(ShapeObject shape, int portIndex) {
        Point anchor = shape.getResizeAnchor(portIndex);
        if (anchor != null) {
            resizeAnchorX = anchor.x;
            resizeAnchorY = anchor.y;
        }
    }
}
