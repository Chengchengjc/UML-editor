package mode;

import java.awt.Graphics;

import canvas.Canvas;

public abstract class Mode { // 新增物件
    protected Canvas canvas;
    int currentX, currentY; // 紀錄目前滑鼠位置，方便在拖曳時畫出預覽圖

    public Mode(Canvas canvas) {
        this.canvas = canvas;
    }

    public abstract void draw(Graphics g);

    public abstract void mousePressed(int x, int y);

    public abstract void mouseDragged(int x, int y);

    public abstract void mouseReleased(int x, int y);

    public void mouseMoved(int x, int y) {
    }

    public boolean group() {
        return false; // 預設不能 group
    }

    public boolean ungroup() {
        return false;
    }
}
