import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.Deque;

public class Shape {

    public static void draw(int x, int y, Color color, BufferedImage buffer) {
        if(x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    /*public static void drawRectangle(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        if (x1 > x2) {
            int xAux = x1;
            x1 = x2;
            x2 = xAux;
        }
        if (y1 > y2) {
            int yAux = y1;
            y1 = y2;
            y2 = yAux;
        }

        for (int x = x1; x < x2; x++) {
            draw(x, y1, color, buffer);
            draw(x, y2, color, buffer);
        }

        for (int y = y1; y < y2; y++) {
            draw(x1, y, color, buffer);
            draw(x2, y, color, buffer);
        }
    }*/

    public static void drawRectangle(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        if(color != null) {
            Line.drawLine(x1, y1, x2, y1, color, buffer);
            Line.drawLine(x1, y1, x1, y2, color, buffer);
            Line.drawLine(x2, y1, x2, y2, color, buffer);
            Line.drawLine(x1, y2, x2, y2, color, buffer);
        }
        else {
            Line.drawPolyline(x1, y1, x2, y1, color, 2, 1, buffer);
            Line.drawPolyline(x1, y1, x1, y2, color, 2, 1, buffer);
            Line.drawPolyline(x2, y1, x2, y2, color, 2, 2, buffer);
            Line.drawPolyline(x1, y2, x2, y2, color, 2, 2, buffer);
        }

    }

    public static void drawCircle(int xc, int yc, int r, Color color, BufferedImage buffer) {
        for (int x = xc - r; x < xc + r; x++) {
            int ySup = yc + (int) Math.sqrt((Math.pow(r, 2) - Math.pow(x - xc, 2)));
            int yInf = yc - (int) Math.sqrt((Math.pow(r, 2) - Math.pow(x - xc, 2)));

            draw(x, ySup, color, buffer);
            draw(x, yInf, color, buffer);
        }
        for (int y = yc - r; y < yc + r; y++) {
            int xSup = xc + (int) Math.sqrt((Math.pow(r, 2) - Math.pow(y - yc, 2)));
            int xInf = xc - (int) Math.sqrt((Math.pow(r, 2) - Math.pow(y - yc, 2)));

            draw(xSup, y, color, buffer);
            draw(xInf, y, color, buffer);
        }
    }

    public static void drawCirclePolar(int xc, int yc, int r, Color color, BufferedImage buffer) {
        int numPoints = (int) (2 * Math.PI * r); // Calculate number of points based on circumference

        int R = 255;
        int g = 255;
        int b = 50;

        double colorRate = (double) numPoints  / 205;
        int colorIncrement = 1;
        if (colorRate < 1) {
            colorIncrement = (int) Math.floor(1 / colorRate);
            colorRate = 1;
        } else {
            colorRate = Math.ceil(colorRate);
        }

        for (int i = 0; i < numPoints ; i++) {
            double t = (i * 2 * Math.PI) / numPoints;

            int x = xc + (int) (r * Math.cos(t));
            int y = yc + (int) (r * Math.sin(t));

            Color grad = new Color(R, g, b);

            draw(x, y, color != null ? color : grad, buffer);

            if (i % (int) colorRate == 0) {
                b += colorIncrement;
                g -= colorIncrement;
            }
        }
    }


    public static void drawElipse(int xc, int yc, int rx, int ry, Color color, BufferedImage buffer) {
        int numPoints = (int) (2 * Math.PI * Math.max(rx, ry)); // Calculate number of points based on circumference

        int R = 255;
        int g = 255;
        int b = 50;

        double colorRate = (double) numPoints  / 205;
        int colorIncrement = 1;
        if (colorRate < 1) {
            colorIncrement = (int) Math.floor(1 / colorRate);
            colorRate = 1;
        } else {
            colorRate = Math.ceil(colorRate);
        }

        for (int i = 0; i < numPoints ; i++) {
            double t = (i * 2 * Math.PI) / numPoints;

            int x = xc + (int) (rx * Math.cos(t));
            int y = yc + (int) (ry * Math.sin(t));

            Color grad = new Color(R, g, b);

            draw(x, y, color != null ? color : grad, buffer);

            if (i % (int) colorRate == 0) {
                b += colorIncrement;
                g -= colorIncrement;
            }
        }
    }

    // Fill
    public static void floodFill(int x, int y, Color targetColor, BufferedImage buffer) {
        int originalColor = buffer.getRGB(x, y);

        if (originalColor == targetColor.getRGB()) {
            return;
        }

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] currentPixel = stack.pop();
            int px = currentPixel[0];
            int py = currentPixel[1];

            if (buffer.getRGB(px, py) != targetColor.getRGB()) {
                //buffer.setRGB(px, py, targetColor.getRGB());
                draw(px, py, targetColor, buffer);

                if(px + 1 >= 0 && px + 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px + 1, py});
                }
                if(px - 1 >= 0 && px - 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px - 1, py});
                }
                if(px >= 0 && px < buffer.getWidth() && py + 1 >= 0 && py + 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py + 1});
                }
                if(px >= 0 && px < buffer.getWidth() && py - 1 >= 0 && py - 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py - 1});
                }

            }
        }
    }

    public static void fillScanLine(int xc, int yc, Color targetColor, BufferedImage buffer) {
        int y = yc;
        while(y >= 0 && y < buffer.getHeight() && buffer.getRGB(xc, y) != targetColor.getRGB()) {
            int x = xc;
            while(x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                draw(x++, y, targetColor, buffer);
            }
            x = xc - 1;
            while(x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                draw(x--, y, targetColor, buffer);
            }
            y++;
        }
        y = yc - 1;
        while(y >= 0 && y < buffer.getHeight() && buffer.getRGB(xc, y) != targetColor.getRGB()) {
            int x = xc;
            while(x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                draw(x++, y, targetColor, buffer);
            }
            x = xc - 1;
            while(x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                draw(x--, y, targetColor, buffer);
            }
            y--;
        }

    }

    public static void fillCircle(int xc, int yc, int r, Color color, BufferedImage buffer) {
        drawCircle(xc, yc, r, color, buffer);

        floodFill(xc,yc,color, buffer);
    }

    public static void fillRhombus(int xc, int yc, int width, int height, Color color, BufferedImage buffer) {
        Line.drawLine(xc, yc + (height / 2), xc + (width / 2), yc, color, buffer);
        Line.drawLine(xc + (width / 2), yc, xc, yc - (height / 2), color, buffer);
        Line.drawLine(xc, yc - (height / 2), xc - (width / 2), yc, color, buffer);
        Line.drawLine(xc - (width / 2), yc, xc, yc + (height / 2), color, buffer);

        fillScanLine(xc, yc, color, buffer);
    }

    public static int[][] translate(int[] xPoints, int[] yPoints, int xMove, int yMove) {
        int[][] initialMatrix = new int[xPoints.length][xPoints.length];
        initialMatrix[0] = xPoints;
        initialMatrix[1] = yPoints;

        for(int i = 2; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                initialMatrix[i][j] = 1;
            }
        }

        int[][] translatelMatrix = new int[xPoints.length][xPoints.length];
        for(int i = 0; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                if(i == j) {
                    translatelMatrix[i][j] = 1;
                }
                else {
                    translatelMatrix[i][j] = 0;
                }
            }
        }


        translatelMatrix[0][xPoints.length - 1] = xMove;
        translatelMatrix[1][xPoints.length - 1] = yMove;

        int[][] resultMatrix = multiplyMatrices(translatelMatrix, initialMatrix);

       /* System.out.print("\n");
        for(int i = 0; i < xPoints.length; i ++) {
            for(int j = 0; j < xPoints.length; j++) {
                System.out.print(initialMatrix[i][j] + " ");
            }
            System.out.print("\n");
        }

        System.out.print("\n");
        for(int i = 0; i < xPoints.length; i ++) {
            for(int j = 0; j < xPoints.length; j++) {
                System.out.print(translatelMatrix[i][j] + " ");
            }
            System.out.print("\n");
        }

        System.out.print("\n");
        for(int i = 0; i < xPoints.length; i ++) {
            for(int j = 0; j < xPoints.length; j++) {
                System.out.print(resultMatrix[i][j] + " ");
            }
            System.out.print("\n");
        }*/
        return resultMatrix;
     }


    public static int[][] multiplyMatrices(int[][] matrix1, int[][] matrix2) {
        int n = matrix1.length;
        int[][] result = new int[n][n];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {

            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    /*System.out.println("i: " + i + "   j: " + j);
                    System.out.println(matrix1[i][k]);
                    System.out.println(matrix2[k][j]);*/
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }

        return result;
    }



    public static void drawPolygon(int[] xPoints, int[] yPoints, Color color, BufferedImage buffer) {
        for (int i = 0; i < xPoints.length - 1; i++) {
            Line.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], color, buffer);
        }
        Line.drawLine(xPoints[xPoints.length - 1], yPoints[yPoints.length - 1], xPoints[0], yPoints[0], color, buffer);

    }


    public static void fillPolygon(int[] xPoints, int[] yPoints, int[] center, Color color, BufferedImage buffer) {
        int nPoints = xPoints.length;

        drawPolygon(xPoints, yPoints, color, buffer);

        // Calculate centroid of the polygon
        int sumX = 0;
        int sumY = 0;
        for (int i = 0; i < nPoints; i++) {
            sumX += xPoints[i];
            sumY += yPoints[i];
        }
        int centroidX = sumX / nPoints;
        int centroidY = sumY / nPoints;

        // Now fill the polygon using flood fill from the centroid
        if (center == null) {
            floodFill(centroidX, centroidY, color, buffer);
        } else {
            floodFill(center[0], center[1], color, buffer);
        }
    }



}
