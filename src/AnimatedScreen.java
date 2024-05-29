import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class AnimatedScreen extends JPanel {

    public BufferedImage buffer;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean away;

    public AnimatedScreen(int width, int height) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        this.away = true;
        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void grid() {
        Color gray2 = new Color(68, 75, 114);
        Color gray = new Color(49, 55, 91);

        // Vertical Lines
        for (int i = 0; i < getWidth(); i++) {
            if (i % 50 == 0) {
                for (int j = 0; j < getHeight(); j++) {
                    if (i % 100 == 0) {
                        this.buffer.setRGB(i, j, gray2.getRGB());
                    } else {
                        this.buffer.setRGB(i, j, gray.getRGB());
                    }
                }
            }
        }

        // Horizontal Lines
        for (int j = 0; j < getHeight(); j++) {
            if (j % 50 == 0) {
                for (int i = 0; i < getWidth(); i++) {
                    if (j % 100 == 0) {
                        this.buffer.setRGB(i, j, gray2.getRGB());
                    } else {
                        this.buffer.setRGB(i, j, gray.getRGB());
                    }
                }
            }
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics gBuffer = buffer.getGraphics();
        gBuffer.setColor(new Color(39, 43, 76));
        gBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
        grid();


        /*Line.drawPolyline(100, 110, 800, 110, null, 2, 1, buffer);
        Line.drawPolyline(100, 120, 800, 120, null, 2, 2, buffer);*/

        /*double i = 0;
        while (i < 15) {
            buffer.setRGB((int) (i * 10) + 200, (int) Math.pow(i, 2) + 100, Color.green.getRGB());
            buffer.setRGB((int) (i * -10) + 200, (int) Math.pow(i, 2) + 100, Color.green.getRGB());
            i += 0.01;
        }*/


        parabola(100, 120, 0, Math.PI, 1000, 100, -1, null, buffer);
        parabola(100, 140, 0, Math.PI, 8, 100, -1, null, buffer);
        spring(175, 250, 0, 10, 61, 1, 10, -1, null, buffer);
        smoke(500, 480, 0, 2 * Math.PI, 1000, 25, -1, null, buffer);
        infinity(800, 70, 0, 2 * Math.PI, 100, 150, -1, null, buffer);
        flower(200, 380, 0, 2 * Math.PI, 1000, 70, -1, null, buffer);
        sun(800, 380, 0, 14 * Math.PI, 1000, 5, -1, null, buffer);

        drawGrid(450, 20, 150, 100, 10, null, buffer);

        g.drawImage(buffer, 0, 0, this);

    }

    public void parabola(double x0, int y0, double minLim, double maxLim, int numPoints, double scale, int invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / numPoints;

        // f(x) = f((x - x0) / scale) * scale + x0

        for (int i = 0; i < numPoints ; i++) {
            double x1 = (minLim + (i * step)) * scale + x0;
            double y1 = Math.sin((x1 - x0) / scale) * scale * invert + y0;

            double x2 = (minLim + ((i + 1) * step)) * scale + x0;
            double y2 = Math.sin((x2 - x0) / scale) * scale * invert + y0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public void spring(int x0, double y0, double minLim, double maxLim, int numPoints, double stepParam, double scale, double invert, Color color, BufferedImage buffer) {
        double step = stepParam == 0 ? Math.abs(maxLim - minLim) / (double) numPoints : stepParam;

        // x(t) = x(t) * scale + x0
        // y(t) = t(t) * scale * invert +y0

        for (int i = 0; i < numPoints; i++) {
            double t1 = minLim + (i * step);
            double t2 = minLim + ((i + 1) * step);

            double x1 = (t1 - 3 * Math.sin(t1)) * scale + x0;
            double y1 = (4 - 3 * Math.cos(t1)) * scale * invert + y0;

            double x2 = (t2 - 3 * Math.sin(t2)) * scale + x0;
            double y2 = (4 - 3 * Math.cos(t2)) * scale * invert + y0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public void smoke(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / (double) numPoints;

        // f(y) = f((y - y0) * invert / scale) * scale + x0

        for (int i = 0; i < numPoints - 1; i++) {
            double y1 = (minLim + (i * step)) * scale * invert + y0;
            double x1 = ((y1 - y0) * invert / scale) * Math.cos(4 * (y1 - y0) * invert / scale) * scale + x0;

            double y2 = (minLim + ((i + 1) * step)) * scale * invert + y0;
            double x2 = ((y2 - y0) * invert / scale) * Math.cos(4 * (y2 - y0) * invert / scale) * scale + x0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public void infinity(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / (double) numPoints;

        // x(t) = x(t) * scale + x0
        // y(t) = t(t) * scale * invert +y0

        for (int i = 0; i < numPoints; i++) {
            double t1 = minLim + (i * step);
            double t2 = minLim + ((i + 1) * step);

            double x1 = (Math.sin(t1) / (1 + Math.pow(Math.cos(t1), 2))) * scale + x0;
            double y1 = (Math.sin(t1) * Math.cos(t1) / (1 + Math.pow(Math.cos(t1), 2))) * scale * invert + y0;

            double x2 = (Math.sin(t2) / (1 + Math.pow(Math.cos(t2), 2))) * scale + x0;
            double y2 = (Math.sin(t2) * Math.cos(t2) / (1 + Math.pow(Math.cos(t2), 2))) * scale * invert + y0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }

    }

    public void flower(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / (double) numPoints;

        // x(t) = x(t) * scale + x0
        // y(t) = t(t) * scale * invert +y0

        for (int i = 0; i < numPoints; i++) {
            double t1 = minLim + (i * step);
            double t2 = minLim + ((i + 1) * step);

            double x1 = (Math.cos(t1) + Math.cos(7 * t1) / 2 + Math.sin(17 * t1) / 3) * scale + x0;
            double y1 = (Math.sin(t1) + Math.sin(7 * t1) / 2 + Math.cos(17 * t1) / 3) * scale * invert + y0;

            double x2 = (Math.cos(t2) + Math.cos(7 * t2) / 2 + Math.sin(17 * t2) / 3) * scale + x0;
            double y2 = (Math.sin(t2) + Math.sin(7 * t2) / 2 + Math.cos(17 * t2) / 3) * scale * invert + y0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public void sun(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / (double) numPoints;

        // x(t) = x(t) * scale + x0
        // y(t) = t(t) * scale * invert +y0

        for (int i = 0; i < numPoints; i++) {
            double t1 = minLim + (i * step);
            double t2 = minLim + ((i + 1) * step);

            double x1 = (17 * Math.cos(t1) + 7 * Math.cos(17 * t1 / 7)) * scale + x0;
            double y1 = (17 * Math.sin(t1) - 7 * Math.sin(17 * t1 / 7)) * scale * invert + y0;

            double x2 = (17 * Math.cos(t2) + 7 * Math.cos(17 * t2 / 7)) * scale + x0;
            double y2 = (17 * Math.sin(t2) - 7 * Math.sin(17 * t2 / 7)) * scale * invert + y0;

            Line.drawPolyline((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }

    }

    public void drawGrid(int x, int y, int width, int height, int intWidth, Color color, BufferedImage buffer) {
        int numXPoints = (int) ((double) width / ((double) intWidth));
        int numYPoints = (int) ((double) height / ((double) intWidth));

        // Horizontal lines
        for (int i = 0; i <= numYPoints; i++) {
            for (int j = 0; j < numXPoints; j++) {
                Line.drawPolyline(x + (j * intWidth), y + (i * intWidth), x + ((j + 1) * intWidth), y + (i * intWidth), color, numXPoints + 1, j + 1, buffer);
            }
        }

        // Vertical lines
        for (int i = 0; i <= numXPoints; i++) {
            for (int j = 0; j < numYPoints; j++) {
                Line.drawPolyline(x + (i * intWidth), y + (j * intWidth), x + (i * intWidth), y + ((j + 1) * intWidth), color, numYPoints + 1, j + 1, buffer);
            }
        }

        // Nodes points
        for (int i = 0; i <= numYPoints; i++) {
            for (int j = 0; j <= numXPoints; j++) {
                //Shape.draw(x + (j * intWidth), y + (i * intWidth), Color.black, buffer);
                Shape.fillCircle(x + (j * intWidth), y + (i * intWidth), 1, Color.cyan, buffer);
            }
        }

    }
}
