package element;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class BasicObject {
    boolean selected = false;

    public abstract void draw(Graphics g);

    public abstract void move(int dx, int dy);

    public abstract boolean isInside(int dx, int dy);

    public abstract Rectangle getBounds();

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void drawSelection(Graphics g) {
        // 預設物件沒有選取狀態，只有ShapeObject、groupObject有，所以BasicObject的drawSelection預設不做任何事
    }
}
