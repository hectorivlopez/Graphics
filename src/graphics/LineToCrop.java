package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LineToCrop {
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public double m;
    public int[] end1;
    public int[] end2;
    public int[][] ends;

    public LineToCrop(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.end1 = new int[]{this.x1, this.y1};
        this.end2 = new int[]{this.x2, this.y2};
        this.ends = new int[][]{this.end1, this.end2};

        this.m = (double) (y2 - y1) / ((double) (x2 - x1));
    }

    public void draw(Color color, BufferedImage buffer) {
        Graphics g = buffer.getGraphics();
        g.setColor(color);

        g.drawLine(this.x1, this.y1, this.x2, this.y2);
    }

    public void setEnd(int end, int x, int y) {
        switch (end) {
            case 0:
                this.x1 = x;
                this.y1 = y;
                this.ends[0][0] = x;
                this.ends[0][1] = y;
                break;
            case 1:
                this.x2 = x;
                this.y2 = y;
                this.ends[1][0] = x;
                this.ends[1][1] = y;
                break;
        }
    }
}