package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Shapes {
    public static void parabola(double x0, int y0, double minLim, double maxLim, int numPoints, double scale, int invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / numPoints;

        // f(x) = f((x - x0) / scale) * scale + x0

        for (int i = 0; i < numPoints ; i++) {
            double x1 = (minLim + (i * step)) * scale + x0;
            double y1 = Math.sin((x1 - x0) / scale) * scale * invert + y0;

            double x2 = (minLim + ((i + 1) * step)) * scale + x0;
            double y2 = Math.sin((x2 - x0) / scale) * scale * invert + y0;

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public static void spring(int x0, double y0, double minLim, double maxLim, int numPoints, double stepParam, double scale, double invert, Color color, BufferedImage buffer) {
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

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public static void smoke(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
        double step = Math.abs(maxLim - minLim) / (double) numPoints;

        // f(y) = f((y - y0) * invert / scale) * scale + x0

        for (int i = 0; i < numPoints - 1; i++) {
            double y1 = (minLim + (i * step)) * scale * invert + y0;
            double x1 = ((y1 - y0) * invert / scale) * Math.cos(4 * (y1 - y0) * invert / scale) * scale + x0;

            double y2 = (minLim + ((i + 1) * step)) * scale * invert + y0;
            double x2 = ((y2 - y0) * invert / scale) * Math.cos(4 * (y2 - y0) * invert / scale) * scale + x0;

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public static void infinity(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
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

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }

    }

    public static void flower(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
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

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }
    }

    public static void sun(int x0, double y0, double minLim, double maxLim, int numPoints, double scale, double invert, Color color, BufferedImage buffer) {
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

            Draw.drawPolylineIndividual((int) x1, (int) y1, (int) x2, (int) y2, color, numPoints, i + 1, buffer);
        }

    }

    public static void drawGrid(int x, int y, int width, int height, int intWidth, Color color, BufferedImage buffer) {
        int numXPoints = (int) ((double) width / ((double) intWidth));
        int numYPoints = (int) ((double) height / ((double) intWidth));

        // Horizontal lines
        for (int i = 0; i <= numYPoints; i++) {
            for (int j = 0; j < numXPoints; j++) {
                Draw.drawPolylineIndividual(x + (j * intWidth), y + (i * intWidth), x + ((j + 1) * intWidth), y + (i * intWidth), color, numXPoints + 1, j + 1, buffer);
            }
        }

        // Vertical lines
        for (int i = 0; i <= numXPoints; i++) {
            for (int j = 0; j < numYPoints; j++) {
                Draw.drawPolylineIndividual(x + (i * intWidth), y + (j * intWidth), x + (i * intWidth), y + ((j + 1) * intWidth), color, numYPoints + 1, j + 1, buffer);
            }
        }

        // Nodes points
        for (int i = 0; i <= numYPoints; i++) {
            for (int j = 0; j <= numXPoints; j++) {
                //Shape.draw(x + (j * intWidth), y + (i * intWidth), Color.black, buffer);
                Draw.fillCircle(x + (j * intWidth), y + (i * intWidth), 1, Color.cyan, buffer);
            }
        }

    }

    public int[][] star(int xc, int yc) {
        int outerRadius = 10;
        int innerRadius = 5;
        int numPoints = 5;

        int[] xPoints = new int[numPoints * 2];
        int[] yPoints = new int[numPoints * 2];

        for (int i = 0; i < numPoints * 2; i++) {
            double angle = Math.PI / 2 + (i * Math.PI / numPoints);
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = xc + (int) (radius * Math.cos(angle));
            yPoints[i] = yc + (int) (radius * Math.sin(angle));

            xPoints[i] = 2 * xc - xPoints[i];
            yPoints[i] = 2 * yc - yPoints[i];
        }

        int[][] starPoints = new int[2][10];
        for (int i = 0; i < 10; i++) {
            starPoints[0][i] = xPoints[i];
            starPoints[1][i] = yPoints[i];
        }

        return starPoints;
    }

    public int[][] star3d(int xc, int yc) {
        int outerRadius = 10;
        int innerRadius = 5;
        int numPoints = 5;

        int[] xPoints = new int[numPoints * 2];
        int[] yPoints = new int[numPoints * 2];

        for (int i = 0; i < numPoints * 2; i++) {
            double angle = Math.PI / 2 + (i * Math.PI / numPoints);
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = xc + (int) (radius * Math.cos(angle));
            yPoints[i] = yc + (int) (radius * Math.sin(angle));

            xPoints[i] = 2 * xc - xPoints[i];
            yPoints[i] = 2 * yc - yPoints[i];
        }

        int[][] starPoints = new int[3][10];
        for (int i = 0; i < 10; i++) {
            starPoints[0][i] = xPoints[i];
            starPoints[1][i] = yPoints[i];
            starPoints[2][i] = 10;
        }

        return starPoints;
    }
}
