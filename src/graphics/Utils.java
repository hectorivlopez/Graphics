package graphics;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;

public class Utils {
    public static int[][] multiplyMatrices(double[][] scaleMatrix, int[][] initialMatrix) {
        int n = scaleMatrix.length;
        int[][] result = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double newValue = 0;
                for (int k = 0; k < n; k++) {
                    newValue += scaleMatrix[i][k] * (double) initialMatrix[k][j];
                }
                result[i][j] = (int) newValue;
            }
        }

        return result;
    }

    public static int[] calVector(int length, double angle, String angUnit) {
        if (!angUnit.equals("R")) angle = Math.toRadians(angle);
        while (angle < 0) angle += (2 * Math.PI);

        double m = Math.tan(angle);
        int dx, dy;

        if (Math.abs(m) < 1) {
            int xDirection = angle > (Math.PI / 2) && angle < (3 * Math.PI / 2) ? -1 : 1;

            dx = xDirection * (int) Math.round(length / Math.sqrt(1 + m * m));
            dy = (int) Math.round(m * dx);
        } else {
            int yDirection = angle > 0 && angle < Math.PI ? 1 : -1;

            dy = yDirection * (int) Math.round(length / Math.sqrt(1 + 1 / (m * m)));
            dx = (int) Math.round(dy / m);
        }

        return new int[]{dx, dy};
    }

    public static int[] calVectorOrigin(int x0, int y0, int length, double angle, String angUnit) {
        // Calculate the direction vectors for the lines
        int[] vector = calVector(length, angle, angUnit);


        return new int[]{x0 + vector[0], y0 + vector[1]};
    }

    public static double[] calculatePerpendicularVector(int[] xPoints, int[] yPoints, int[] zPoints, int magnitude, double direction) {
        // Vectors u and v
        int[] A = new int[]{xPoints[0], yPoints[0], zPoints[0]};
        int[] B = new int[]{xPoints[1], yPoints[1], zPoints[1]};
        int[] C = new int[]{xPoints[2], yPoints[2], zPoints[2]};

        double[] u = new double[]{B[0] - A[0], B[1] - A[1], B[2] - A[2]};
        double[] v = new double[]{C[0] - A[0], C[1] - A[1], C[2] - A[2]};

        // Cross product u x v
        double[] normal = new double[]{
                u[1] * v[2] - u[2] * v[1],
                u[2] * v[0] - u[0] * v[2],
                u[0] * v[1] - u[1] * v[0]
        };

        // Normal vector
        double normalMagnitude = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
        if (normalMagnitude == 0) return new double[]{0, 0, 0};

        // Normalize the normal vector
        double[] normalized = new double[]{
                normal[0] / normalMagnitude,
                normal[1] / normalMagnitude,
                normal[2] / normalMagnitude
        };

        // Scale the normal vector
        double[] scaled = new double[]{
                /*(int)*/ (normalized[0] * magnitude * direction),
                /*(int)*/ (normalized[1] * magnitude * direction),
                /*(int)*/ (normalized[2] * magnitude * direction)
        };

        return scaled;
    }


    public static double calculateDotProduct(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static boolean isPointInPolygon(int x, int y, int[] polyX, int[] polyY) {
        boolean inside = false;
        int nPoints = polyX.length;
        for (int i = 0, j = nPoints - 1; i < nPoints; j = i++) {
            if ((polyY[i] > y) != (polyY[j] > y) &&
                    (x < (polyX[j] - polyX[i]) * (y - polyY[i]) / (polyY[j] - polyY[i]) + polyX[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static double circunf(String unit) {
        return unit.equals("R") ? 2 * Math.PI : 360;
    }

    // Drawing
    public static void floodFill(int x, int y, Color targetColor, BufferedImage buffer) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            int originalColor = buffer.getRGB(x, y);

            if (originalColor == targetColor.getRGB() || x < 0 || x >= buffer.getWidth() - 1 || y < 0 || y >= buffer.getHeight() - 1) {
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
                    Draw.draw(px, py, targetColor, buffer);

                    if (px + 1 >= 0 && px + 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                        stack.push(new int[]{px + 1, py});
                    }
                    if (px - 1 >= 0 && px - 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                        stack.push(new int[]{px - 1, py});
                    }
                    if (px >= 0 && px < buffer.getWidth() && py + 1 >= 0 && py + 1 < buffer.getHeight()) {
                        stack.push(new int[]{px, py + 1});
                    }
                    if (px >= 0 && px < buffer.getWidth() && py - 1 >= 0 && py - 1 < buffer.getHeight()) {
                        stack.push(new int[]{px, py - 1});
                    }

                }
            }

        }
    }

    public static void floodFillTarget(int x, int y, Color targetColor, Color fillColor, BufferedImage buffer) {
        int originalColor = buffer.getRGB(x, y);
        if (originalColor == fillColor.getRGB() || originalColor == targetColor.getRGB()) {
            return;
        }

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] currentPixel = stack.pop();
            int px = currentPixel[0];
            int py = currentPixel[1];

            if (buffer.getRGB(px, py) != targetColor.getRGB() && buffer.getRGB(px, py) != fillColor.getRGB()) {
                buffer.setRGB(px, py, fillColor.getRGB());

                if (px + 1 >= 0 && px + 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px + 1, py});
                }
                if (px - 1 >= 0 && px - 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px - 1, py});
                }
                if (px >= 0 && px < buffer.getWidth() && py + 1 >= 0 && py + 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py + 1});
                }
                if (px >= 0 && px < buffer.getWidth() && py - 1 >= 0 && py - 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py - 1});
                }
            }
        }
    }

    public static void changeFill(int x, int y, Color targetColor, Color color, BufferedImage buffer) {
        int originalColor = buffer.getRGB(x, y);

        if (originalColor == color.getRGB()) {
            return;
        }

        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});

        while (!stack.isEmpty()) {
            int[] currentPixel = stack.pop();
            int px = currentPixel[0];
            int py = currentPixel[1];

            if (buffer.getRGB(px, py) == targetColor.getRGB()) {
                //buffer.setRGB(px, py, targetColor.getRGB());
                Draw.draw(px, py, color, buffer);

                if (px + 1 >= 0 && px + 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px + 1, py});
                }
                if (px - 1 >= 0 && px - 1 < buffer.getWidth() && py >= 0 && py < buffer.getHeight()) {
                    stack.push(new int[]{px - 1, py});
                }
                if (px >= 0 && px < buffer.getWidth() && py + 1 >= 0 && py + 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py + 1});
                }
                if (px >= 0 && px < buffer.getWidth() && py - 1 >= 0 && py - 1 < buffer.getHeight()) {
                    stack.push(new int[]{px, py - 1});
                }

            }
        }
    }

    public static void fillScanLine(int xc, int yc, Color targetColor, BufferedImage buffer) {
        int y = yc;
        while (y >= 0 && y < buffer.getHeight() && buffer.getRGB(xc, y) != targetColor.getRGB()) {
            int x = xc;
            while (x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                Draw.draw(x++, y, targetColor, buffer);
            }
            x = xc - 1;
            while (x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                Draw.draw(x--, y, targetColor, buffer);
            }
            y++;
        }
        y = yc - 1;
        while (y >= 0 && y < buffer.getHeight() && buffer.getRGB(xc, y) != targetColor.getRGB()) {
            int x = xc;
            while (x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                Draw.draw(x++, y, targetColor, buffer);
            }
            x = xc - 1;
            while (x >= 0 && x < buffer.getWidth() && buffer.getRGB(x, y) != targetColor.getRGB()) {
                Draw.draw(x--, y, targetColor, buffer);
            }
            y--;
        }

    }

    public static int calcBit(int number) {
        if(number <= 0) return 0;
        else return 1;
    }

    public static void calcEndsPos(int[][] endsPos, int xMin, int xMax, int yMin, int yMax, LineToCrop line) {
        for(int j = 0; j <= 1; j++) {
            endsPos[j][0] = calcBit(yMin - line.ends[j][1]);
            endsPos[j][1] = calcBit(line.ends[j][1] - yMax);
            endsPos[j][2] = calcBit(xMin - line.ends[j][0]);
            endsPos[j][3] = calcBit(line.ends[j][0] - xMax);
        }
    }
}
