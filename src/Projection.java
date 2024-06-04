import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class Projection extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private int width;
    private int height;
    private int titleBarHeight;
    private CustomPanel canvasPanel;
    private JPanel bgPanel;
    JPanel bottomBar;
    public JLabel directorLabel;
    public int xPressed, yPressed;
    public int xDirInit, yDirInit;
    boolean firstResize;
    public boolean isMacOS;

    public Projection() {
        this.isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMacOS) {
            final JRootPane rootPane = this.getRootPane();
            rootPane.putClientProperty("apple.awt.fullWindowContent", true);
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        titleBarHeight = 28;
        if (isMacOS) titleBarHeight = 0;

        width = 1001;
        height = 501;

        setSize(width, height + titleBarHeight + 40);
        setLocationRelativeTo(null);
        setLayout(null);
        firstResize = true;
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();

            }
        });

        bgPanel = new JPanel();
        bgPanel.setBounds(0, 0, width, height);
        bgPanel.setLayout(null);
        setContentPane(bgPanel);

        canvasPanel = new CustomPanel(width, height);
        canvasPanel.setBounds(0, 0, width, height);
        canvasPanel.setLayout(null);
        canvasPanel.addMouseListener(this);
        canvasPanel.addMouseMotionListener(this);
        canvasPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                canvasPanel.director[2] += notches;
                canvasPanel.repaint();
                directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
            }
        });
        bgPanel.add(canvasPanel);


    }

    private void updateSize() {
        width = getWidth();
        height = getHeight() - titleBarHeight;
        setBackground(new Color(39, 43, 76));

        bgPanel.setBounds(0, 0, width, height);
        if (!firstResize) {
            bottomBar.remove(directorLabel);
            bgPanel.remove(bottomBar);
        }

        bottomBar = new JPanel();
        bottomBar.setLayout(null);
        bottomBar.setBounds(0, height - 40, this.width, 40);
        bottomBar.setBackground(new Color(30, 32, 47));
        bgPanel.add(bottomBar);

        directorLabel = new JLabel();
        directorLabel.setBounds(0, 0, this.width, 40);
        directorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        directorLabel.setVerticalAlignment(SwingConstants.CENTER);
        directorLabel.setForeground(Color.white);
        directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
        bottomBar.add(directorLabel);
        bgPanel.repaint();

        // Set new bounds for canvasPanel
        canvasPanel.setBounds(0, 0, width, height - 40);
        canvasPanel.resize(width, height - 40);
        canvasPanel.repaint();

        firstResize = false;
    }

    // ---------------------------------------- Panel ----------------------------------------
    public class CustomPanel extends JPanel {
        public BufferedImage buffer;
        public int[] director;
        public int[] origin2D;
        public int width;
        public int height;

        public double scalejeje;
        public boolean growing;

        public CustomPanel(int width, int height) {
            this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            this.width = width;
            this.height = height;

            this.director = new int[]{0, 0, 20};

            this.origin2D = new int[]{width / 2, height / 2};

            this.scalejeje = 1;
            this.growing = false;

            CustomThread thread = new CustomThread(() -> {
                if (growing) {
                    scalejeje += 0.1;
                    if (scalejeje >= 10) growing = false;
                }
                else {
                    scalejeje -= 0.1;
                    if (scalejeje <= 0.5) growing = true;
                }
            }, 20, () -> false);
            thread.start();
        }

        public void resize(int width, int height) {
            this.width = width;
            this.height = height;

            this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            origin2D = new int[]{width / 2, height / 2};
        }

        // ------------------------------ Guides ------------------------------
        public void grid() {
            Color gray2 = new Color(68, 75, 114);
            Color gray = new Color(49, 55, 91);

            // Vertical Lines
            for (int i = 0; i < width; i++) {
                if (i % 50 == 0) {
                    for (int j = 0; j < height; j++) {
                        if (i % 100 == 0) {
                            this.buffer.setRGB(i, j, gray2.getRGB());
                        } else {
                            this.buffer.setRGB(i, j, gray.getRGB());
                        }
                    }
                }
            }

            // Horizontal Lines
            for (int j = 0; j < height; j++) {
                if (j % 50 == 0) {
                    for (int i = 0; i < width; i++) {
                        if (j % 100 == 0) {
                            this.buffer.setRGB(i, j, gray2.getRGB());
                        } else {
                            this.buffer.setRGB(i, j, gray.getRGB());
                        }
                    }
                }
            }
        }

        public void axis(int[] director, double scale, BufferedImage buffer) {
            int[][] points = new int[][]{
                    new int[]{0, 0, 0},
                    new int[]{500, 0, 0},
                    new int[]{-500, 0, 0},
                    new int[]{0, 500, 0},
                    new int[]{0, -500, 0},
                    new int[]{0, 0, 500},
                    new int[]{0, 0, -500},
            };
            int[][] projectedPoints = projection(points, director, "oblique");

            int[] xPoints = projectedPoints[0];
            int[] yPoints = projectedPoints[1];

            for (int i = 1; i <= 3; i++) {
                drawLine(xPoints[(i * 2) - 1], yPoints[(i * 2) - 1], xPoints[i * 2], yPoints[i * 2], null, buffer);
            }
        }

        // ------------------------------ Utils ------------------------------
        public int[][] multiplyMatrices(double[][] scaleMatrix, int[][] initialMatrix) {
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

        public double[] calculatePerpendicularVector(int[] xPoints, int[] yPoints, int[] zPoints, int magnitude) {
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
                    /*(int)*/ (normalized[0] * magnitude),
                    /*(int)*/ (normalized[1] * magnitude),
                    /*(int)*/ (normalized[2] * magnitude)
            };

            return scaled;
        }

        public double calculateDotProduct(double[] a, double[] b) {
            return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        }

        public boolean isPointInPolygon(int x, int y, int[] polyX, int[] polyY) {
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

        // ------------------------------ Drawing ------------------------------
        private void draw(int x, int y, Color color, BufferedImage buffer) {
            if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
                buffer.setRGB(x, y, color.getRGB());
            }
        }

        public void drawLine(int x1, int y1, int x2, int y2, Color color, BufferedImage buffer) {
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

        public void drawPolygon(int[] xPoints, int[] yPoints, Color color, BufferedImage buffer) {
            for (int i = 0; i < xPoints.length - 1; i++) {
                drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1], color, buffer);
            }
            drawLine(xPoints[xPoints.length - 1], yPoints[yPoints.length - 1], xPoints[0], yPoints[0], color, buffer);

        }

        public void floodFill(int x, int y, Color targetColor, BufferedImage buffer) {
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
                        draw(px, py, targetColor, buffer);

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

        public void fillPolygon(int[] xPoints, int[] yPoints, int[] center, Color color, BufferedImage buffer) {
            int nPoints = xPoints.length;

            drawPolygon(xPoints, yPoints, color, buffer);

            int sumX = 0;
            int sumY = 0;
            for (int i = 0; i < nPoints; i++) {
                sumX += xPoints[i];
                sumY += yPoints[i];
            }
            int centroidX = sumX / nPoints;
            int centroidY = sumY / nPoints;

            if (center == null) {
                if (isPointInPolygon(centroidX, centroidY, xPoints, yPoints)) {
                    floodFill(centroidX, centroidY, color, buffer);
                }
            } else {
                floodFill(center[0], center[1], color, buffer);
            }
        }

        public int[][] scale(int[] xPoints, int[] yPoints, int xc, int yc, boolean center, double xScale, double yScale) {
            int xCenter = 0 + xc;
            int yCenter = 0 + yc;

            // Calculate the center
            if (center) {
                int sumX = 0;
                int sumY = 0;

                for (int i = 0; i < xPoints.length; i++) {
                    sumX += xPoints[i];
                    sumY += yPoints[i];
                }

                xCenter = sumX / xPoints.length;
                yCenter = sumY / yPoints.length;
            }

            // Create initial matrix
            int[][] initialMatrix = new int[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] = 0 + xPoints[i];
                initialMatrix[1][i] = 0 + yPoints[i];
            }

            // Setting center to [0, 0]
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] -= xCenter;
                initialMatrix[1][i] -= yCenter;
            }

            for (int i = 2; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    initialMatrix[i][j] = 1;
                }
            }

            // Create resizing matrix
            double[][] scaleMatrix = new double[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    if (i == j) {
                        scaleMatrix[i][j] = 1;
                    } else {
                        scaleMatrix[i][j] = 0;
                    }
                }
            }

            scaleMatrix[0][0] = xScale;
            scaleMatrix[1][1] = yScale;

            int[][] resultMatrix = multiplyMatrices(scaleMatrix, initialMatrix);

            // Translation to origin al center
            for (int i = 0; i < xPoints.length; i++) {
                resultMatrix[0][i] += xCenter;
                resultMatrix[1][i] += yCenter;
            }

            return resultMatrix;
        }

        public int[][] scale3D(int[] xPoints, int[] yPoints, int[] zPoints, int xc, int yc, int zc, boolean center, double xScale, double yScale, double zScale) {
            int xCenter = 0 + xc;
            int yCenter = 0 + yc;
            int zCenter = 0 + zc;

            // Calculate the center
            if (center) {
                int sumX = 0;
                int sumY = 0;
                int sumZ = 0;

                for (int i = 0; i < xPoints.length; i++) {
                    sumX += xPoints[i];
                    sumY += yPoints[i];
                    sumZ += zPoints[i];
                }

                xCenter = sumX / xPoints.length;
                yCenter = sumY / yPoints.length;
                zCenter = sumZ / zPoints.length;
            }

            // Create initial matrix
            int[][] initialMatrix = new int[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] = 0 + xPoints[i];
                initialMatrix[1][i] = 0 + yPoints[i];
                initialMatrix[2][i] = 0 + zPoints[i];
            }

            // Setting center to [0, 0]
            for (int i = 0; i < xPoints.length; i++) {
                initialMatrix[0][i] -= xCenter;
                initialMatrix[1][i] -= yCenter;
                initialMatrix[2][i] -= zCenter;
            }

            for (int i = 3; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    initialMatrix[i][j] = 1;
                }
            }

            // Create resizing matrix
            double[][] scaleMatrix = new double[xPoints.length][xPoints.length];
            for (int i = 0; i < xPoints.length; i++) {
                for (int j = 0; j < xPoints.length; j++) {
                    if (i == j) {
                        scaleMatrix[i][j] = 1;
                    } else {
                        scaleMatrix[i][j] = 0;
                    }
                }
            }

            scaleMatrix[0][0] = xScale;
            scaleMatrix[1][1] = yScale;
            scaleMatrix[2][2] = zScale;

            int[][] resultMatrix = multiplyMatrices(scaleMatrix, initialMatrix);

            // Translation to origin al center
            for (int i = 0; i < xPoints.length; i++) {
                resultMatrix[0][i] += xCenter;
                resultMatrix[1][i] += yCenter;
                resultMatrix[2][i] += zCenter;
            }
            /*for(int i = 0; i < initialMatrix.length; i++) {
                System.out.print("[ ");
                for(int j = 0; j < initialMatrix[0].length; j++) {
                    System.out.print(initialMatrix[i][j] + ", ");
                }
                System.out.println("]");
            }*/

            return resultMatrix;
        }

        public int[][] projection(int[][] points, int[] director, String projection) {
            int[][] projectedPoints = new int[2][points[0].length];

            if (projection.equals("oblique")) {
                for (int i = 0; i < points[0].length; i++) {
                    double u = -((double) points[2][i]) / ((double) director[2]);

                    int x = points[0][i] + (int) ((double) director[0] * u);
                    int y = points[1][i] + (int) ((double) director[1] * u);

                    projectedPoints[0][i] = x;
                    projectedPoints[1][i] = y;
                }
            }

            if (projection.equals("orthogonal")) {
                for (int i = 0; i < points[0].length; i++) {
                    int x = points[0][i];
                    int y = points[1][i];

                    projectedPoints[0][i] = x;
                    projectedPoints[1][i] = y;
                }
            }

            if (projection.equals("perspective")) {
                for (int i = 0; i < points[0].length; i++) {
                    if (points[2][i] == director[2]) return null;

                    double u = -((double) director[2]) / ((double) (points[2][i] - director[2]));

                    int x = director[0] + (int) ((double) (points[0][i] - director[0]) * u);
                    int y = director[1] + (int) ((double) (points[1][i] - director[1]) * u);

                    projectedPoints[0][i] = x;
                    projectedPoints[1][i] = y;
                }
            }


            //int[][] scaledPoints = scale(projectedPoints[0], projectedPoints[1], p0[0], p0[1], false, scale, scale);

            return projectedPoints;
        }

        public void cube(int x0, int y0, int z0, int width, int depth, int height, int[] director, double scale, String projection, Color color, BufferedImage buffer) {
            int[][] points1 = new int[][]{
                    new int[]{x0, x0 + width, x0 + width, x0},
                    new int[]{y0, y0, y0 + depth, y0 + depth},
                    new int[]{z0, z0, z0, z0},

            };
            int[][] points2 = new int[][]{
                    new int[]{x0, x0 + width, x0 + width, x0},
                    new int[]{y0, y0, y0 + depth, y0 + depth},
                    new int[]{z0 + height, z0 + height, z0 + height, z0 + height},

            };

            // Scale
            int[][] scaledPoints1 = scale3D(points1[0], points1[1], points1[2], x0, y0, z0, false, scale, scale, scale);
            int[][] scaledPoints2 = scale3D(points2[0], points2[1], points2[2], x0, y0, z0, false, scale, scale, scale);

            // Projection
            int[][] projectedPoints1 = projection(scaledPoints1, director, projection);
            int[][] projectedPoints2 = projection(scaledPoints2, director, projection);

            if (projectedPoints1 != null && projectedPoints2 != null) {
                for (int i = 0; i < projectedPoints1[0].length; i++) {
                    drawLine(projectedPoints1[0][i], projectedPoints1[1][i], projectedPoints2[0][i], projectedPoints2[1][i], color, buffer);
                }

                drawPolygon(
                        projectedPoints1[0],
                        projectedPoints1[1],
                        color,
                        buffer
                );
                drawPolygon(
                        projectedPoints2[0],
                        projectedPoints2[1],
                        color,
                        buffer
                );
            }

        }

        public void prism(int[][] points, int height, int[] director, double scale, int[] p0, String projection, int direction, Color color, BufferedImage buffer) {
            // ---------- Calculations ----------
            // Calculate the centroid
            int[] xPoints = points[0];
            int[] yPoints = points[1];
            int[] zPoints = points[2];

            int numVertices = xPoints.length;

            int xSum = 0;
            int ySum = 0;
            int zSum = 0;

            for (int i = 0; i < numVertices; i++) {
                xSum += xPoints[i];
                ySum += yPoints[i];
                zSum += zPoints[i];
            }

            int xc = (int) ((double) xSum / ((double) numVertices));
            int yc = (int) ((double) ySum / ((double) numVertices));
            int zc = (int) ((double) zSum / ((double) numVertices));

            // Height vector
            double[] heightVector = calculatePerpendicularVector(points[0], points[1], points[2], height);

            xc += (int) heightVector[0];
            yc += (int) heightVector[1];
            zc += (int) heightVector[2];

            if(p0 == null) p0 = new int[]{xc, yc, zc};

            // Top face
            int[][] topPoints = new int[3][points[0].length];
            for (int i = 0; i < points[0].length; i++) {
                topPoints[0][i] = points[0][i] + (int) (direction * heightVector[0]);
                topPoints[1][i] = points[1][i] + (int) (direction * heightVector[1]);
                topPoints[2][i] = points[2][i] + (int) (direction * heightVector[2]);
                /*System.out.println("Cara1: (" + points[i][0] + ", " + points[i][1] + ", " + points[i][2] + ")");
                System.out.println("Cara2: (" + topPoints[i][0] + ", " + topPoints[i][1] + ", " + topPoints[i][2] + ")");*/
            }

            // Scale
            int[][] scaledPoints1 = scale3D(points[0], points[1], points[2], p0[0], p0[1], p0[2], false, scale, scale, scale);
            int[][] scaledPoints2 = scale3D(topPoints[0], topPoints[1], topPoints[2], p0[0], p0[1], p0[2], false, scale, scale, scale);

            // Projection
            int[][] projectedPoints1 = projection(scaledPoints1, director, projection);
            int[][] projectedPoints2 = projection(scaledPoints2, director, projection);

            // ---------- Drawing ----------
            if (projectedPoints1 != null && projectedPoints2 != null) {
                for (int i = 0; i < points[0].length; i++) {
                    drawLine(projectedPoints1[0][i], projectedPoints1[1][i], projectedPoints2[0][i], projectedPoints2[1][i], color, buffer);
                }

                // Bottom face
                drawPolygon(
                        projectedPoints1[0],
                        projectedPoints1[1],
                        color,
                        buffer
                );

                // Top face
                drawPolygon(
                        projectedPoints2[0],
                        projectedPoints2[1],
                        color,
                        buffer
                );
            }
        }

        public void biPyramid(int[][] points, int height, int[] director, double scale, int[] p0, String projection, Boolean onlyFront, Color borderColor, Color color, BufferedImage buffer) {
            // Calculate the centroid
            int[] xPoints = points[0];
            int[] yPoints = points[1];
            int[] zPoints = points[2];

            int numVertices = xPoints.length;

            int xSum = 0;
            int ySum = 0;
            int zSum = 0;

            for (int i = 0; i < numVertices; i++) {
                xSum += xPoints[i];
                ySum += yPoints[i];
                zSum += zPoints[i];
            }

            int xc = (int) ((double) xSum / ((double) numVertices));
            int yc = (int) ((double) ySum / ((double) numVertices));
            int zc = (int) ((double) zSum / ((double) numVertices));

            int[] centroid = {xc, yc, zc};

            if(p0 == null) p0 = centroid;

            // Calculate height vectors
            double[] heightVector1 = calculatePerpendicularVector(xPoints, yPoints, zPoints, height);
            double[] heightVector2 = calculatePerpendicularVector(xPoints, yPoints, zPoints, -height);

            if (onlyFront) {
                // Matrix with all the points
                int[][] allPoints = new int[3][points[0].length + 2];

                for (int i = 0; i < 3; i++) {
                    int[] newPoints = Arrays.copyOf(points[i], points[i].length + 2);
                    newPoints[newPoints.length - 2] = centroid[i] + (int) heightVector1[i];
                    newPoints[newPoints.length - 1] = centroid[i] + (int) heightVector2[i];

                    allPoints[i] = newPoints;
                }

                // Scale
                int[][] scaledAllPoints = scale3D(allPoints[0], allPoints[1], allPoints[2], p0[0], p0[1], p0[2], false, scale, scale, scale);

                // Projection
                int[][] projectedPoints = projection(scaledAllPoints, director, projection);

                if (projectedPoints != null) {
                    // Vertices of each face
                    int[][] faces = new int[numVertices * 2][3];
                    for (int i = 0; i < numVertices; i++) {
                        if (i < numVertices - 1) {
                            faces[i] = new int[]{i, i + 1, numVertices};
                            faces[numVertices + i] = new int[]{i, i + 1, numVertices + 1};
                        } else {
                            faces[i] = new int[]{i, 0, numVertices};
                            faces[numVertices + i] = new int[]{i, 0, numVertices + 1};
                        }
                    }

                    // Back-face culling
                    for (int i = 0; i < faces.length; i++) {
                        int[] face = faces[i];
                        int[] faceXPoints = {scaledAllPoints[0][face[0]], scaledAllPoints[0][face[1]], scaledAllPoints[0][face[2]]};
                        int[] faceYPoints = {scaledAllPoints[1][face[0]], scaledAllPoints[1][face[1]], scaledAllPoints[1][face[2]]};
                        int[] faceZPoints = {scaledAllPoints[2][face[0]], scaledAllPoints[2][face[1]], scaledAllPoints[2][face[2]]};

                        double[] perpendicularVector = calculatePerpendicularVector(faceXPoints, faceYPoints, faceZPoints, 10);

                        double[] directionVector = new double[]{
                                director[0],
                                director[1],
                                director[2]
                        };

                        if (projection.equals("orthogonal")) {
                            directionVector = new double[]{0, 0, 1};
                        }

                        if (projection.equals("perspective")) {
                            double[] faceCentroid = {
                                    (faceXPoints[0] + faceXPoints[1] + faceXPoints[2]) / 3.0,
                                    (faceYPoints[0] + faceYPoints[1] + faceYPoints[2]) / 3.0,
                                    (faceZPoints[0] + faceZPoints[1] + faceZPoints[2]) / 3.0
                            };

                            directionVector = new double[]{
                                    director[0] - faceCentroid[0],
                                    director[1] - faceCentroid[1],
                                    director[2] - faceCentroid[2]
                            };
                        }

                        double dotProduct = calculateDotProduct(perpendicularVector, directionVector);

                        // Drawing
                        if (dotProduct < 0 && i >= numVertices || dotProduct > 0 && i < numVertices) {
                            int[] projectedFaceXPoints = {projectedPoints[0][face[0]], projectedPoints[0][face[1]], projectedPoints[0][face[2]]};
                            int[] projectedFaceYPoints = {projectedPoints[1][face[0]], projectedPoints[1][face[1]], projectedPoints[1][face[2]]};

                            if (color != null)
                                fillPolygon(projectedFaceXPoints, projectedFaceYPoints, null, color, buffer);
                            drawLine(projectedFaceXPoints[0], projectedFaceYPoints[0], projectedFaceXPoints[1], projectedFaceYPoints[1], borderColor, buffer);
                            drawLine(projectedFaceXPoints[1], projectedFaceYPoints[1], projectedFaceXPoints[2], projectedFaceYPoints[2], borderColor, buffer);
                            drawLine(projectedFaceXPoints[2], projectedFaceYPoints[2], projectedFaceXPoints[0], projectedFaceYPoints[0], borderColor, buffer);
                        }
                    }
                }
            } else {
                int[][] heightsPoints = new int[][]{
                        new int[]{xc + (int) heightVector1[0], xc + (int) heightVector2[0]},
                        new int[]{yc + (int) heightVector1[1], yc + (int) heightVector2[1]},
                        new int[]{zc + (int) heightVector1[2], zc + (int) heightVector2[2]},
                };

                // Scale
                int[][] scaledPoints = scale3D(points[0], points[1], points[2], p0[0], p0[1], p0[2], false, scale, scale, scale);
                int[][] scaledHeightsPoints = scale3D(heightsPoints[0], heightsPoints[1], heightsPoints[2], p0[0], p0[1], p0[2], false, scale, scale, scale);

                // Projection
                int[][] projectedPoints = projection(scaledPoints, director, projection);
                int[][] projectedHeightsPoints = projection(scaledHeightsPoints, director, projection);

                // Drawing
                if (projectedPoints != null && projectedHeightsPoints != null) {
                    drawPolygon(
                            projectedPoints[0],
                            projectedPoints[1],
                            borderColor,
                            buffer
                    );

                    for (int i = 0; i < projectedPoints.length; i++) {
                        drawLine(projectedPoints[0][i], projectedPoints[1][i], projectedHeightsPoints[0][0], projectedHeightsPoints[1][0], borderColor, buffer);
                        drawLine(projectedPoints[0][i], projectedPoints[1][i], projectedHeightsPoints[0][1], projectedHeightsPoints[1][1], borderColor, buffer);
                    }
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

            int[][] starPoints = new int[3][10];
            for (int i = 0; i < 10; i++) {
                starPoints[0][i] = xPoints[i];
                starPoints[1][i] = yPoints[i];
                starPoints[2][i] = 10;
            }

            return starPoints;
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics gBuffer = buffer.getGraphics();
            gBuffer.setColor(new Color(39, 43, 76));
            gBuffer.fillRect(0, 0, this.getWidth(), this.getHeight());
            grid();
            //axis(director, 1, buffer);

            int[][] tPoints = new int[][]{
                    new int[]{-60, 0, 0, 60, 60, 0, 0, -60},
                    new int[]{-90, -90, -30, -30, 30, 30, 90, 90},
                    new int[]{120, 180, 180, 240, 240, 180, 180, 120},
            };

            int[][] points = new int[][]{
                    new int[]{-50, 50, 50, -50},
                    new int[]{0, 0, 0, 0},
                    new int[]{100, 100, 200, 200},
            };

            int[][] points1 = new int[][]{
                    new int[]{-200, -100, -100, -200},
                    new int[]{0, 0, 0, 0},
                    new int[]{100, 100, 200, 200},
            };

            int[][] points2 = new int[][]{
                    new int[]{100, 200, 200, 100},
                    new int[]{0, 0, 0, 0},
                    new int[]{100, 100, 200, 200},
            };

            int[][] points3 = new int[][]{
                    new int[]{-10 + origin2D[0], 0 + origin2D[0], 10 + origin2D[0]},
                    new int[]{0 + origin2D[1], 10 + origin2D[1], 0 + origin2D[1]},
                    new int[]{0, 0, 0},
            };

            int[][] points4 = new int[][]{
                    new int[]{-40, -30, -20, -30},
                    new int[]{0, 10, 0, -10},
                    new int[]{0, 0, 0, 0},
            };

            int[][] points5 = new int[][]{
                    new int[]{20 + origin2D[0], 30 + origin2D[0], 40 + origin2D[0], 30 + origin2D[0]},
                    new int[]{0 + origin2D[1], 10 + origin2D[1], 0 + origin2D[1], -10 + origin2D[1]},
                    new int[]{0, 0, 0, 0},
            };

            //cube(-10, -10, 0, 20, 20, 30, director, 5, origin2D, "perspective", Color.white, buffer);

            //biPyramid(points1, 70, director, 1, origin2D, "orthogonal", false, Color.white, Color.red, buffer);
            //biPyramid(points2, 10, director, 1, origin2D, "perspective", false, Color.white, Color.green, buffer);
            //biPyramid(points, 70, director, 1, origin2D, "perspective", false, Color.white, Color.blue, buffer);

            //biPyramid(points3, 10, director, 10, null, "perspective", true, Color.white, Color.red, buffer);
           // biPyramid(points4, 10, director, 10, origin2D, "orthogonal", true, Color.white, Color.green, buffer);
            biPyramid(points5, 10, director, 10, null, "perspective", true, Color.white, null, buffer);

           /* int[][] star = star(0, 0);
            prism(star, 85, director, 10, origin2D, "oblique", 1, Color.green, buffer);*/

            g.drawImage(buffer, 0, 0, this);
        }
    }

    // ---------------------------------------- Listeners ----------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.xPressed = e.getX();
        this.yPressed = e.getY();

        this.xDirInit = canvasPanel.director[0];
        this.yDirInit = canvasPanel.director[1];
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        int xDirector = -(e.getX() - xPressed) / 1;
        int yDirector = -(e.getY() - yPressed) / 1;

        canvasPanel.director[0] = xDirInit + xDirector;
        canvasPanel.director[1] = yDirInit + yDirector;
        canvasPanel.repaint();

        directorLabel.setText("P(" + canvasPanel.director[0] + ", " + canvasPanel.director[1] + ", " + canvasPanel.director[2] + ")");
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    // ---------------------------------------- Execution ----------------------------------------
    public static void main(String[] args) {
        Projection window = new Projection();

        CustomThread thread = new CustomThread(() -> {
            window.bgPanel.repaint();
        }, 20, () -> false);
        thread.start();

    }
}
