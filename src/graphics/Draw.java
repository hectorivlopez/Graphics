package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Draw {
    public static void draw(int x, int y, Color color, BufferedImage buffer) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            buffer.setRGB(x, y, color.getRGB());
        }
    }

    private static void drawThick(int x, int y, Color color, BufferedImage buffer) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            draw(x, y, color, buffer);
            draw(x + 1, y, color, buffer);
            draw(x - 1, y, color, buffer);
            draw(x, y + 1, color, buffer);
            draw(x, y - 1, color, buffer);
            // buffer.setRGB(x + 1, y + 1, color.getRGB());
            // buffer.setRGB(x + 1, y - 1, color.getRGB());
            //buffer.setRGB(x - 1, y + 1, color.getRGB());
            //buffer.setRGB(x - 1, y - 1, color.getRGB());
        }
    }

    // Crop
    public static void crop(int xMin, int xMax, int yMin, int yMax, Color targetColor, Color color, BufferedImage buffer) {
        for(int y = yMin; y <= yMax; y++) {
            for(int x = xMin; x <= xMax; x++) {
                int pointColor = buffer.getRGB(x, y);
                if(pointColor == targetColor.getRGB()) {
                    buffer.setRGB(x, y, color.getRGB());
                }
            }
        }
    }

    public static void cropLines(int xMin, int xMax, int yMin, int yMax, LineToCrop[] lines, Color color, BufferedImage buffer) {
        Graphics g = buffer.getGraphics();
        g.setColor(Color.white);

        g.drawPolygon(new int[]{xMin, xMax, xMax, xMin}, new int[]{yMax, yMax, yMin, yMin}, 4);
        g.setColor(Color.red);

        // Up, down, left, right
        int[] edges = new int[]{yMin, yMax, xMin, xMax};

        for(LineToCrop line : lines) {
            boolean outside = false;
            boolean inside = true;

            int[][] endsPos = new int[2][4];
            Utils.calcEndsPos(endsPos, xMin, xMax, yMin, yMax, line);

            for(int j = 0; j < 4; j++) {
                if(endsPos[0][j] != 0 || endsPos[1][j] != 0) {
                    inside = false;
                }
                if(endsPos[0][j] == 1 && endsPos[1][j] == 1) {
                    outside = true;
                    break;
                }
            }
            if(outside) continue;
            if(inside) {
                line.draw(color, buffer);
                continue;
            }

            // Intersection
            LineToCrop newLine = new LineToCrop(line.x1, line.y1, line.x2, line.y2);
            for(int j = 0; j <= 1; j++) {
                // Up and down
                for(int k = 0; k < 2; k++) {
                    if(endsPos[j][k] == 1) {
                        int y = edges[k];
                        int x = newLine.x1 + (int) ((double)(y - newLine.y1 ) / newLine.m);
                        newLine.setEnd(j, x, y);
                        Draw.fillCircle(x, y, 2, Color.red, buffer);
                    }
                }

                Utils.calcEndsPos(endsPos, xMin, xMax, yMin, yMax, newLine);
                // Left and right
                for(int k = 2; k < 4; k++) {
                    if(endsPos[j][k] == 1) {
                        int x = edges[k];
                        int y = (int) (newLine.m * (x - newLine.x1)) + newLine.y1 ;
                        newLine.setEnd(j, x, y);
                        Draw.fillCircle(x, y, 2, Color.red, buffer);
                    }
                }
                Utils.calcEndsPos(endsPos, xMin, xMax, yMin, yMax, newLine);
            }

            newLine.draw(Color.red, buffer);
        }
    }

    // ---------------------------------------- Line ----------------------------------------
    public static void drawLine(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        double m = (double) (y2 - y1) / (x2 - x1);
        if ((x2 - x1) == 0) {
            m = 0;
        }

        int r = 20;
        int g = 255;
        int b = 50;

        // Evaluate x
        if (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) {
            // views.Line color
            double colorRate = (double) (Math.abs(x2 - x1)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double y = y1;
            if (x2 > x1) {
                for (int x = x1; x <= x2; x++) {
                    Color gradColor = new Color(r, g, b);
                    draw(x, (int) y, color != null ? color : gradColor, buffer);

                    y += m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    Color gradColor = new Color(r, g, b);
                    draw(x, (int) y, color != null ? color : gradColor, buffer);

                    y -= m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }

        // Evaluate y
        else {
            // views.Line color
            double colorRate = (double) (Math.abs(y2 - y1)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double x = x1;
            if (y2 > y1) {
                for (int y = y1; y <= y2; y++) {
                    Color gradColor = new Color(r, g, b);
                    draw((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x += 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int y = y1; y >= y2; y--) {
                    Color gradColor = new Color(r, g, b);
                    draw((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x -= 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }

    }

    public static void drawLineThick(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        double m = (double) (y2 - y1) / (x2 - x1);
        if ((x2 - x1) == 0) {
            m = 0;
        }

        int r = 20;
        int g = 255;
        int b = 50;

        // Evaluate x
        if (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) {
            // Line color
            double colorRate = (double) (Math.abs(x2 - x1)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double y = y1;
            if (x2 > x1) {
                for (int x = x1; x <= x2; x++) {
                    Color gradColor = new Color(r, g, b);
                    drawThick(x, (int) y, color != null ? color : gradColor, buffer);

                    y += m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    Color gradColor = new Color(r, g, b);
                    drawThick(x, (int) y, color != null ? color : gradColor, buffer);

                    y -= m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }

        // Evaluate y
        else {
            // Line color
            double colorRate = (double) (Math.abs(y2 - y1)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double x = x1;
            if (y2 > y1) {
                for (int y = y1; y <= y2; y++) {
                    Color gradColor = new Color(r, g, b);
                    drawThick((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x += 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int y = y1; y >= y2; y--) {
                    Color gradColor = new Color(r, g, b);
                    drawThick((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x -= 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }

    }

    public static void drawPolyline(int[] xPoints, int[] yPoints, Color color, BufferedImage buffer) {
        int numPoints = xPoints.length;
        for (int i = 0; i < numPoints - 1; i++) {
            drawPolylineIndividual(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], color, numPoints - 1, i + 1, buffer);
        }

    }

    public static void drawPolylineIndividual(int x1, int y1, int x2, int y2, Color color, int numFragments, int fragment, BufferedImage buffer) {
        double m = (double) (y2 - y1) / (x2 - x1);
        if ((x2 - x1) == 0) {
            m = 0;
        }

        int r = 20 + (int) ((((double) fragment - 1) * (205 / (double) numFragments)));
        int g = 255 - (int) ((((double) fragment - 1) * (205 / (double) numFragments)));
        int b = 50 + (int) ((((double) fragment - 1) * (205 / (double) numFragments)));

        // Evaluate x
        if (Math.abs(x2 - x1) >= Math.abs(y2 - y1)) {
            // views.Line color
            double colorRate = (double) (Math.abs(x2 - x1) * (numFragments)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double y = y1;
            if (x2 > x1) {
                for (int x = x1; x <= x2; x++) {
                    Color gradColor = new Color(r, g, b);
                    draw(x, (int) y, color != null ? color : gradColor, buffer);

                    y += m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int x = x1; x >= x2; x--) {
                    Color gradColor = new Color(r, g, b);
                    draw(x, (int) y, color != null ? color : gradColor, buffer);

                    y -= m;

                    if (x % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }

        // Evaluate y
        else {
            // views.Line color
            double colorRate = (double) (Math.abs(y2 - y1) * (numFragments)) / 205;
            int colorIncrement = 1;
            if (colorRate < 1) {
                colorIncrement = (int) Math.floor(1 / colorRate);
                colorRate = 1;
            } else {
                colorRate = Math.ceil(colorRate);
            }

            double x = x1;
            if (y2 > y1) {
                for (int y = y1; y <= y2; y++) {
                    Color gradColor = new Color(r, g, b);
                    draw((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x += 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            } else {
                for (int y = y1; y >= y2; y--) {
                    Color gradColor = new Color(r, g, b);
                    draw((int) x, y, color != null ? color : gradColor, buffer);

                    if (m != 0) {
                        x -= 1 / m;
                    }

                    if (y % (int) colorRate == 0) {
                        b += colorIncrement;
                        r += colorIncrement;
                        g -= colorIncrement;
                    }
                }
            }
        }


    }

    public static int[] drawVector(int x0, int y0, int length, double angle, String angUnit, Color color, BufferedImage buffer) {
        // Calculate the direction vectors for the lines
        int[] vector = Utils.calVector(length, angle, angUnit);

        drawLine(x0, y0, x0 + vector[0], y0 + vector[1], color, buffer);

        return new int[]{x0 + vector[0], y0 + vector[1]};
    }

    public static int[] drawVectorThick(int x0, int y0, int length, double angle, String angUnit, Color color, BufferedImage buffer) {
        // Calculate the direction vectors for the lines
        int[] vector = Utils.calVector(length, angle, angUnit);

        drawLineThick(x0, y0, x0 + vector[0], y0 + vector[1], color, buffer);

        return new int[]{x0 + vector[0], y0 + vector[1]};
    }

    public static void drawOrthogonalVector(int x0, int y0, int length, double angle, String angUnit, Color color, BufferedImage buffer) {
        if (angUnit.equals("D") && angle > 270) angle -= 360;

        int halfLength = length / 2;

        int[] vector1 = Utils.calVector(halfLength, angle + (Utils.circunf(angUnit) / 4), angUnit);
        int[] vector2 = Utils.calVector(halfLength, angle - (Utils.circunf(angUnit) / 4), angUnit);

        drawLine(x0, y0, x0 + vector1[0], y0 + vector1[1], color, buffer);
        drawLine(x0, y0, x0 + vector2[0], y0 + vector2[1], color, buffer);
    }

    public static void drawOrthogonalVectorThick(int x0, int y0, int length, double angle, String angUnit, Color color, BufferedImage buffer) {
        if (angUnit.equals("D") && angle > 270) angle -= 360;

        int halfLength = length / 2;

        int[] vector1 = Utils.calVector(halfLength, angle + (Utils.circunf(angUnit) / 4), angUnit);
        int[] vector2 = Utils.calVector(halfLength, angle - (Utils.circunf(angUnit) / 4), angUnit);

        drawLineThick(x0, y0, x0 + vector1[0], y0 + vector1[1], color, buffer);
        drawLineThick(x0, y0, x0 + vector2[0], y0 + vector2[1], color, buffer);
    }

    public static void drawLineLimits(int xc, int yc, double angle, String angUnit, Color targetColor, Color color, BufferedImage buffer) {
        if (!angUnit.equals("R")) angle = Math.toRadians(angle);
        while (angle < 0) angle += (2 * Math.PI);

        double m = Math.tan(angle);

        if (Math.abs(m) < 1) {
            double y = 0 + yc;
            int x = 0 + xc;
            while (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight() && buffer.getRGB(x, (int) y) != targetColor.getRGB()) {
                draw(x, (int) y, color, buffer);

                y += m;
                x++;
            }

            y = 0 + yc;
            x = 0 + xc;
            while (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight() && buffer.getRGB(x, (int) y) != targetColor.getRGB()) {
                draw(x, (int) y, color, buffer);

                y -= m;
                x--;
            }

        } else {
            double x = 0 + xc;
            int y = 0 + yc;
            while (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight() && buffer.getRGB((int) x, y) != targetColor.getRGB()) {
                draw((int) x, y, color, buffer);

                if (m != 0) {
                    x += 1 / m;
                }
                y++;
            }

            x = 0 + xc;
            y = 0 + yc;
            while (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight() && buffer.getRGB((int) x, y) != targetColor.getRGB()) {
                draw((int) x, y, color, buffer);

                if (m != 0) {
                    x -= 1 / m;
                }
                y--;
            }

        }


    }

    public static void drawArc(int x1, int y1, int x2, int y2, int ry, Color color, BufferedImage buffer) {
        int xc = (int) (Math.min(x1, x2) + Math.abs(x2 - x1) / (double) 2);
        int yc = (int) (Math.min(y1, y2) + Math.abs(y2 - y1) / (double) 2);

        //int rx = (int) (Math.abs(x2 - x1) / (double) 2);
        int rx = (int) (Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2))) / 2;

        int numPoints = (int) (Math.PI * Math.max(rx, ry)); // Calculate number of points based on circumference

        double dx = x2 - x1;
        double dy = y2 - y1;
        double alpha = dx != 0 ? Math.atan(dy / dx) : Math.PI / 2;
        if (dx < 0) {
            alpha += Math.PI;
        }
        if (x1 > x2) {
            alpha += Math.PI;
        }

        if (x1 > x2 || (x1 == x2 && y1 > y2)) ry *= -1;

        for (int i = 0; i < numPoints; i++) {
            double t = (i * Math.PI) / numPoints;
            double tf = ((i + 1) * Math.PI) / numPoints;

           /* int x = xc + (int) (rx * Math.cos(t));
            int y = yc + (int) (ry * Math.sin(t));*/

            int x = xc + (int) ((double) rx * Math.cos(t) * Math.cos(alpha) - (double) ry * Math.sin(t) * Math.sin(alpha));
            int y = yc + (int) ((double) rx * Math.cos(t) * Math.sin(alpha) + (double) ry * Math.sin(t) * Math.cos(alpha));

            int xf = xc + (int) ((double) rx * Math.cos(tf) * Math.cos(alpha) - (double) ry * Math.sin(tf) * Math.sin(alpha));
            int yf = yc + (int) ((double) rx * Math.cos(tf) * Math.sin(alpha) + (double) ry * Math.sin(tf) * Math.cos(alpha));

            if (i == 0) {
                if (x1 > x2 || (x1 == x2 && y1 > y2)) {
                    x = x1;
                    y = y1;
                } else {
                    x = x2;
                    y = y2;
                }

            }
            if (i == numPoints - 1) {
                if (x1 > x2 || (x1 == x2 && y1 > y2)) {
                    xf = x2;
                    yf = y2;
                } else {
                    xf = x1;
                    yf = y1;
                }
            }
            /*if(i == numPoints - 1) {
                xf = x2;
                yf = y2;
            }*/
            drawPolylineIndividual(x, y, xf, yf, color, numPoints + 1, i + 1, buffer);


        }
        /* fillCircle(x1, y1, 2, Color.white, buffer);*/
        /* fillCircle(x2, y2, 2, Color.black, buffer);*/
    }

    public static void drawArc2(int x1, double x2, int y0, double rx, double ry, BufferedImage buffer) {
        for (double x = -rx; x <= rx; x++) {
            double t = Math.acos(x / rx);
            double y = ry * Math.sin(t);

            draw((int) x + x1, (int) y + y0, Color.red, buffer);
        }


    }

    // ---------------------------------------- Shapes ----------------------------------------
    public static void drawRect(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        if (color != null) {
            drawLine(x1, y1, x2, y1, color, buffer);
            drawLine(x1, y1, x1, y2, color, buffer);
            drawLine(x2, y1, x2, y2, color, buffer);
            drawLine(x1, y2, x2, y2, color, buffer);
        } else {
            drawPolylineIndividual(x1, y1, x2, y1, color, 2, 1, buffer);
            drawPolylineIndividual(x1, y1, x1, y2, color, 2, 1, buffer);
            drawPolylineIndividual(x2, y1, x2, y2, color, 2, 2, buffer);
            drawPolylineIndividual(x1, y2, x2, y2, color, 2, 2, buffer);
        }

    }

    public static void drawPolygon(int[] xPoints, int[] yPoints, Color color, BufferedImage buffer) {
        for (int i = 0; i < xPoints.length - 1; i++) {
            drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], color, buffer);
        }
        drawLine(xPoints[xPoints.length - 1], yPoints[yPoints.length - 1], xPoints[0], yPoints[0], color, buffer);

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

        double colorRate = (double) numPoints / 205;
        int colorIncrement = 1;
        if (colorRate < 1) {
            colorIncrement = (int) Math.floor(1 / colorRate);
            colorRate = 1;
        } else {
            colorRate = Math.ceil(colorRate);
        }

        for (int i = 0; i < numPoints; i++) {
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

    public static void drawEllipse(int xc, int yc, int rx, int ry, double angle, Color color, BufferedImage buffer) {
        int numPoints = (int) (2 * Math.PI * Math.max(rx, ry)); // Calculate number of points based on circumference
        while (angle >= 360) angle -= 360;
        double alpha = Math.toRadians(angle);

        int R = 255;
        int g = 255;
        int b = 50;

        double colorRate = (double) numPoints / 205;
        int colorIncrement = 1;
        if (colorRate < 1) {
            colorIncrement = (int) Math.floor(1 / colorRate);
            colorRate = 1;
        } else {
            colorRate = Math.ceil(colorRate);
        }

        for (int i = 0; i <= numPoints; i++) {
            double t = (i * 2 * Math.PI) / numPoints;
            double tf = ((i + 1) * 2 * Math.PI) / numPoints;
           /* int x = xc + (int) (rx * Math.cos(t));
            int y = yc + (int) (ry * Math.sin(t));*/
            int x = xc + (int) ((double) rx * Math.cos(t) * Math.cos(alpha) - (double) ry * Math.sin(t) * Math.sin(alpha));
            int y = yc + (int) ((double) rx * Math.cos(t) * Math.sin(alpha) + (double) ry * Math.sin(t) * Math.cos(alpha));

            int xf = xc + (int) ((double) rx * Math.cos(tf) * Math.cos(alpha) - (double) ry * Math.sin(tf) * Math.sin(alpha));
            int yf = yc + (int) ((double) rx * Math.cos(tf) * Math.sin(alpha) + (double) ry * Math.sin(tf) * Math.cos(alpha));


            Color grad = new Color(R, g, b);

            /* draw(x, y, color != null ? color : grad, buffer);*/
            drawPolylineIndividual(x, y, xf, yf, color, numPoints + 1, i + 1, buffer);

            if (i % (int) colorRate == 0) {
                b += colorIncrement;
                g -= colorIncrement;
            }
        }
    }

    public static void drawEllipsePartial(int xc, int yc, int rx, int ry, double angle, double limit1, double limit2, Color color, BufferedImage buffer) {
        int numPoints = (int) (2 * Math.PI * Math.max(rx, ry)); // Calculate number of points based on circumference
        while (angle >= 360) angle -= 360;
        double alpha = Math.toRadians(angle);

        int R = 255;
        int g = 255;
        int b = 50;

        double colorRate = (double) numPoints / 205;
        int colorIncrement = 1;
        if (colorRate < 1) {
            colorIncrement = (int) Math.floor(1 / colorRate);
            colorRate = 1;
        } else {
            colorRate = Math.ceil(colorRate);
        }

        for (int i = 0; i <= numPoints; i++) {
            double t = (i * 2 * Math.PI) / numPoints;
            double tf = ((i + 1) * 2 * Math.PI) / numPoints;
           /* int x = xc + (int) (rx * Math.cos(t));
            int y = yc + (int) (ry * Math.sin(t));*/
            if(t > limit1 && t < limit2) {
                int x = xc + (int) ((double) rx * Math.cos(t) * Math.cos(alpha) - (double) ry * Math.sin(t) * Math.sin(alpha));
                int y = yc + (int) ((double) rx * Math.cos(t) * Math.sin(alpha) + (double) ry * Math.sin(t) * Math.cos(alpha));

                int xf = xc + (int) ((double) rx * Math.cos(tf) * Math.cos(alpha) - (double) ry * Math.sin(tf) * Math.sin(alpha));
                int yf = yc + (int) ((double) rx * Math.cos(tf) * Math.sin(alpha) + (double) ry * Math.sin(tf) * Math.cos(alpha));

                Color grad = new Color(R, g, b);

                /* draw(x, y, color != null ? color : grad, buffer);*/
                drawPolylineIndividual(x, y, xf, yf, color, numPoints + 1, i + 1, buffer);

                if (i % (int) colorRate == 0) {
                    b += colorIncrement;
                    g -= colorIncrement;
                }
            }
        }
    }

    public static int[] drawEllipsePoints(int x1, int y1, int x2, int y2, int ry, Color color, BufferedImage buffer) {
        int xc = (int) (Math.min(x1, x2) + Math.abs(x2 - x1) / (double) 2);
        int yc = (int) (Math.min(y1, y2) + Math.abs(y2 - y1) / (double) 2);

        int rx = (int) (Math.sqrt(Math.pow(Math.abs(x2 - x1), 2) + Math.pow(Math.abs(y2 - y1), 2))) / 2;
        int numPoints = (int) (2 * Math.PI * Math.max(rx, ry)); // Calculate number of points based on circumference

        double dx = x2 - x1;
        double dy = y2 - y1;
        double alpha = dx != 0 ? Math.atan(dy / dx) : Math.PI / 2;
        if (dx < 0) {
            alpha += Math.PI;
        }
        if (x1 > x2) {
            alpha += Math.PI;
        }

        for (int i = 0; i < numPoints; i++) {
            double t = (i * 2 * Math.PI) / numPoints;
            double tf = ((i + 1) * 2 * Math.PI) / numPoints;

           /* int x = xc + (int) (rx * Math.cos(t));
            int y = yc + (int) (ry * Math.sin(t));*/

            int x = xc + (int) ((double) rx * Math.cos(t) * Math.cos(alpha) - (double) ry * Math.sin(t) * Math.sin(alpha));
            int y = yc + (int) ((double) rx * Math.cos(t) * Math.sin(alpha) + (double) ry * Math.sin(t) * Math.cos(alpha));

            int xf = xc + (int) ((double) rx * Math.cos(tf) * Math.cos(alpha) - (double) ry * Math.sin(tf) * Math.sin(alpha));
            int yf = yc + (int) ((double) rx * Math.cos(tf) * Math.sin(alpha) + (double) ry * Math.sin(tf) * Math.cos(alpha));


            if (i == 0 || i == numPoints - 1) {
                if (x1 > x2 || (x1 == x2 && y1 > y2)) {
                    x = x1;
                    y = y1;
                } else {
                    x = x2;
                    y = y2;
                }

            }
            if (i == numPoints / 2) {
                if (x1 > x2 || (x1 == x2 && y1 > y2)) {
                    xf = x2;
                    yf = y2;
                } else {
                    xf = x1;
                    yf = y1;
                }
            }

            drawPolylineIndividual(x, y, xf, yf, color, numPoints + 1, i + 1, buffer);

        }
        return new int[]{xc, yc};
    }

    // Filled
    public static void fillRect(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        drawRect(x1, y1, x2, y2, color, buffer);

        int xc = Math.min(x1, x2) + (Math.abs(x1 - x2) / 2);
        int yc = Math.min(y1, y2) + (Math.abs(y1 - y2) / 2);

        Utils.floodFill(xc, yc, color, buffer);
    }

    public static void fillRectScan(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
        drawRect(x1, y1, x2, y2, color, buffer);

        int xc = Math.min(x1, x2) + (Math.abs(x1 - x2) / 2);
        int yc = Math.min(y1, y2) + (Math.abs(y1 - y2) / 2);

        Utils.fillScanLine(xc, yc, color, buffer);
    }

    public static void fillBackground(Color color, BufferedImage buffer) {
        for (int i = 0; i < buffer.getHeight(); i++) {
            for (int j = 0; j < buffer.getWidth(); j++) {
                draw(j, i, color, buffer);
            }
        }
    }

    public static void fillCircle(int xc, int yc, int r, Color color, BufferedImage buffer) {
        drawCircle(xc, yc, r, color, buffer);

        if (r > 0) Utils.floodFill(xc, yc, color, buffer);
    }

    public static void fillEllipse(int xc, int yc, int rx, int ry, double angle, Color color, BufferedImage buffer) {
        drawEllipse(xc, yc, rx, ry, angle, color, buffer);
        Utils.floodFill(xc, yc, color, buffer);
    }

    public static void fillEllipsePoints(int x1, int y1, int x2, int y2, int ry, Color color, BufferedImage buffer) {
        int[] pc = drawEllipsePoints(x1, y1, x2, y2, ry, color, buffer);
        Utils.floodFill(pc[0], pc[1], color, buffer);
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
            Utils.floodFill(centroidX, centroidY, color, buffer);
        } else {
            Utils.floodFill(center[0], center[1], color, buffer);
        }
    }

    public static void fillRectScaled(int x1, int y1, int x2, int y2, double scale, int x0, int y0, Color color, BufferedImage buffer) {
        int[] rectXPoints = new int[]{x1, x2, x2, x1};
        int[] rectYPoints = new int[]{y1, y1, y2, y2};

        int[][] newRectCoords = Transformations.scale(rectXPoints, rectYPoints, x0, y0, false, scale, scale);

        int[] xPoints = newRectCoords[0];
        int[] yPoints = newRectCoords[1];

        fillRect(xPoints[0], yPoints[0], xPoints[1], yPoints[2], color, buffer);
    }

    public static void fillCircleScaled(int xc, int yc, int r, double scale, int x0, int y0, Color color, BufferedImage buffer) {
        int[] circleXPoints = new int[]{xc, xc + r, r};
        int[] circleYPoints = new int[]{yc, yc + r, r};

        int[][] newCircleCoords = Transformations.scale(circleXPoints, circleYPoints, x0, y0, false, scale, scale);
        fillCircle(newCircleCoords[0][0], newCircleCoords[1][0], newCircleCoords[0][1] - newCircleCoords[0][0], color, buffer);

    }

    public static void fillCircleScaledRotated(int xc, int yc, int r, double scale, double angle, int x0Scale, int y0Scale, int x0Rotate, int y0Rotate, Color color, BufferedImage buffer) {
        int[] circleXPoints = new int[]{xc, xc + r, r};
        int[] circleYPoints = new int[]{yc, yc + r, r};

        int[][] scaledPoints = Transformations.scale(circleXPoints, circleYPoints, x0Scale, y0Scale, false, scale, scale);
        int[] scaledXPoints = scaledPoints[0];
        int[] scaledYPoints = scaledPoints[1];

        int scaledR = scaledXPoints[1] - scaledXPoints[0];

        int[][] rotatedPoints = Transformations.rotate(scaledXPoints, scaledYPoints, x0Rotate, y0Rotate, false, angle);

        fillCircle(rotatedPoints[0][0], rotatedPoints[1][0], scaledR, color, buffer);
    }

    public static void fillEllipseScaled(int xc, int yc, int rx, int ry, double angle, double scale, int x0, int y0, Color color, BufferedImage buffer) {
        int[] ellipseXPoints = new int[]{xc, xc + rx, rx};
        int[] ellipseYPoints = new int[]{yc, yc + ry, ry};

        int[][] newEllipseCoords = Transformations.scale(ellipseXPoints, ellipseYPoints, x0, y0, false, scale, scale);

        drawEllipse(
                newEllipseCoords[0][0],
                newEllipseCoords[1][0],
                newEllipseCoords[0][1] - newEllipseCoords[0][0],
                newEllipseCoords[1][1] - newEllipseCoords[1][0],
                angle,
                color,
                buffer
        );
        Utils.floodFill(newEllipseCoords[0][0], newEllipseCoords[1][0], color, buffer);
    }

    public static void drawEllipseScaledRotated(int xc, int yc, int rx, int ry, double angle, double xScale, double yScale, double angRotate, int x0Scale, int y0Scale, int x0Rotate, int y0Rotate, Color color, BufferedImage buffer) {
        int[] ellipseXPoints = new int[]{xc, xc + rx, 1};
        int[] ellipseYPoints = new int[]{yc, yc + ry, 1};

        //int[][] newEllipseCoords = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, scale, scale);

        int[][] scaledPoints = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, xScale, yScale);
        int[] scaledXPoints = scaledPoints[0];
        int[] scaledYPoints = scaledPoints[1];

        int scaledRx = scaledXPoints[1] - scaledXPoints[0];
        int scaledRy = scaledYPoints[1] - scaledYPoints[0];

        int[][] rotatedPoints = Transformations.rotate(scaledXPoints, scaledYPoints, x0Rotate, y0Rotate, false, angRotate);

        drawEllipse(
                rotatedPoints[0][0],
                rotatedPoints[1][0],
                scaledRx,
                scaledRy,
                angle + Math.toDegrees(angRotate),
                color,
                buffer
        );

        //drawCircle(rotatedPoints[0][0], rotatedPoints[1][0], 3, Color.white, buffer);
        //floodFill(rotatedPoints[0][0], rotatedPoints[1][0], color, buffer);
    }

    public static void drawEllipseScaledRotatedPartial(int xc, int yc, int rx, int ry, double angle, double xScale, double yScale, double angRotate, int x0Scale, int y0Scale, int x0Rotate, int y0Rotate, double limit1, double limit2, Color color, BufferedImage buffer) {
        int[] ellipseXPoints = new int[]{xc, xc + rx, 1};
        int[] ellipseYPoints = new int[]{yc, yc + ry, 1};

        //int[][] newEllipseCoords = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, scale, scale);

        int[][] scaledPoints = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, xScale, yScale);
        int[] scaledXPoints = scaledPoints[0];
        int[] scaledYPoints = scaledPoints[1];

        int scaledRx = scaledXPoints[1] - scaledXPoints[0];
        int scaledRy = scaledYPoints[1] - scaledYPoints[0];

        int[][] rotatedPoints = Transformations.rotate(scaledXPoints, scaledYPoints, x0Rotate, y0Rotate, false, angRotate);

        drawEllipsePartial(
                rotatedPoints[0][0],
                rotatedPoints[1][0],
                scaledRx,
                scaledRy,
                angle + Math.toDegrees(angRotate),
                limit1,
                limit2,
                color,
                buffer
        );

        //drawCircle(rotatedPoints[0][0], rotatedPoints[1][0], 3, Color.white, buffer);
        //floodFill(rotatedPoints[0][0], rotatedPoints[1][0], color, buffer);
    }

    public static void fillEllipseScaledRotated(int xc, int yc, int rx, int ry, double angle, double xScale, double yScale, double angRotate, int x0Scale, int y0Scale, int x0Rotate, int y0Rotate, Color color, BufferedImage buffer) {
        int[] ellipseXPoints = new int[]{xc, xc + rx, 1};
        int[] ellipseYPoints = new int[]{yc, yc + ry, 1};

        //int[][] newEllipseCoords = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, scale, scale);

        int[][] scaledPoints = Transformations.scale(ellipseXPoints, ellipseYPoints, x0Scale, y0Scale, false, xScale, yScale);
        int[] scaledXPoints = scaledPoints[0];
        int[] scaledYPoints = scaledPoints[1];

        int scaledRx = scaledXPoints[1] - scaledXPoints[0];
        int scaledRy = scaledYPoints[1] - scaledYPoints[0];

        int[][] rotatedPoints = Transformations.rotate(scaledXPoints, scaledYPoints, x0Rotate, y0Rotate, false, angRotate);

        drawEllipse(
                rotatedPoints[0][0],
                rotatedPoints[1][0],
                scaledRx,
                scaledRy,
                angle + Math.toDegrees(angRotate),
                color,
                buffer
        );

        //drawCircle(rotatedPoints[0][0], rotatedPoints[1][0], 3, Color.white, buffer);
        Utils.floodFill(rotatedPoints[0][0], rotatedPoints[1][0], color, buffer);
    }

}
