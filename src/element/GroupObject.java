package element;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

public class GroupObject extends BasicObject {
    ArrayList<BasicObject> members = new ArrayList<>();
    int x, y, width, height;

    public GroupObject(Collection<BasicObject> objects) {
        members.addAll(objects);
        updateBounds();
    }

    @Override
    public void draw(Graphics g) {
        for (BasicObject obj : members)
            obj.draw(g);
    }

    @Override
    public void move(int dx, int dy) {
        for (BasicObject obj : members)
            obj.move(dx, dy);
        updateBounds();
    }

    @Override
    public boolean isInside(int dx, int dy) {
        return dx >= x && dx <= x + width && dy >= y && dy <= y + height;
    }

    @Override
    public Rectangle getBounds() {
        updateBounds();
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void drawSelection(Graphics g) { // 畫group 框
        g.setColor(Color.GREEN);
        g.drawRect(x, y, width, height);
    }

    public ArrayList<BasicObject> getMembers() {
        return members;
    }

    private void updateBounds() {
        if (members.isEmpty()) {
            x = 0;
            y = 0;
            width = 0;
            height = 0;
            return;
        }

        int left = Integer.MAX_VALUE;
        int top = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int bottom = Integer.MIN_VALUE;

        for (BasicObject obj : members) {
            Rectangle shape = obj.getBounds();
            left = Math.min(left, shape.x);
            top = Math.min(top, shape.y);
            right = Math.max(right, shape.x + shape.width);
            bottom = Math.max(bottom, shape.y + shape.height);
        }

        x = left;
        y = top;
        width = right - left;
        height = bottom - top;
    }

    public void addChild(BasicObject obj) {
        members.add(obj);
        updateBounds();
    }

    public void removeChild(BasicObject obj) {
        members.remove(obj);
        updateBounds();
    }
}
