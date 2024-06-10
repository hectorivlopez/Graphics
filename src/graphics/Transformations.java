package graphics;

public class Transformations {
    public static int[][] translate(int[] xPoints, int[] yPoints, int xMove, int yMove) {
        int[][] initialMatrix = new int[xPoints.length][xPoints.length];
        initialMatrix[0] = xPoints;
        initialMatrix[1] = yPoints;

        for(int i = 2; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                initialMatrix[i][j] = 1;
            }
        }

        double[][] translateMatrix = new double[xPoints.length][xPoints.length];
        for(int i = 0; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                if(i == j) {
                    translateMatrix[i][j] = 1;
                }
                else {
                    translateMatrix[i][j] = 0;
                }
            }
        }

        translateMatrix[0][xPoints.length - 1] = xMove;
        translateMatrix[1][xPoints.length - 1] = yMove;

        int[][] resultMatrix = Utils.multiplyMatrices(translateMatrix, initialMatrix);

        return resultMatrix;
    }

    public static int[][] translate3d(int[] xPoints, int[] yPoints, int[] zPoints, int dx, int dy, int dz) {
        int[][] initialMatrix = new int[xPoints.length][xPoints.length];
        initialMatrix[0] = xPoints;
        initialMatrix[1] = yPoints;
        initialMatrix[2] = zPoints;

        for(int i = 3; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                initialMatrix[i][j] = 1;
            }
        }

        double[][] translateMatrix = new double[xPoints.length][xPoints.length];
        for(int i = 0; i < xPoints.length; i++) {
            for(int j = 0; j < xPoints.length; j++) {
                if(i == j) {
                    translateMatrix[i][j] = 1;
                }
                else {
                    translateMatrix[i][j] = 0;
                }
            }
        }

        translateMatrix[0][xPoints.length - 1] = dx;
        translateMatrix[1][xPoints.length - 1] = dy;
        translateMatrix[2][xPoints.length - 1] = dz;

        int[][] resultMatrix = Utils.multiplyMatrices(translateMatrix, initialMatrix);

        return resultMatrix;
    }

    public static int[][] scale(int[] xPoints, int[] yPoints, int xc, int yc, boolean center, double xScale, double yScale) {
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
            initialMatrix[0][i] = xPoints[i];
            initialMatrix[1][i] = yPoints[i];
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
        double[][] scalelMatrix = new double[xPoints.length][xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            for (int j = 0; j < xPoints.length; j++) {
                if (i == j) {
                    scalelMatrix[i][j] = 1;
                } else {
                    scalelMatrix[i][j] = 0;
                }
            }
        }

        scalelMatrix[0][0] = xScale;
        scalelMatrix[1][1] = yScale;

        int[][] resultMatrix = Utils.multiplyMatrices(scalelMatrix, initialMatrix);

        // Translation to original center
        for (int i = 0; i < xPoints.length; i++) {
            resultMatrix[0][i] += xCenter;
            resultMatrix[1][i] += yCenter;
        }

        return resultMatrix;
    }

    public static int[][] scale3D(int[] xPoints, int[] yPoints, int[] zPoints, int xc, int yc, int zc, boolean center, double xScale, double yScale, double zScale) {
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

        int[][] resultMatrix = Utils.multiplyMatrices(scaleMatrix, initialMatrix);

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

    public static int[][] rotate(int[] xPoints, int[] yPoints, int xc, int yc, boolean center, double angle) {
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

            // Posible problema
            xCenter = sumX / xPoints.length;
            yCenter = sumY / yPoints.length;
        }

        // Create initial matrix
        int[][] initialMatrix = new int[xPoints.length][xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            initialMatrix[0][i] = xPoints[i];
            initialMatrix[1][i] = yPoints[i];
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

        // Create rotator matrix
        double[][] rotatelMatrix = new double[xPoints.length][xPoints.length];
        for (int i = 0; i < xPoints.length; i++) {
            for (int j = 0; j < xPoints.length; j++) {
                if (i == j) {
                    rotatelMatrix[i][j] = 1;
                } else {
                    rotatelMatrix[i][j] = 0;
                }
            }
        }

        rotatelMatrix[0][0] = Math.cos(angle);
        rotatelMatrix[0][1] = -1 * Math.sin(angle);
        rotatelMatrix[1][0] = Math.sin(angle);
        rotatelMatrix[1][1] = Math.cos(angle);

        int[][] resultMatrix = Utils.multiplyMatrices(rotatelMatrix, initialMatrix);

        // Translation to original center
        for (int i = 0; i < xPoints.length; i++) {
            resultMatrix[0][i] += xCenter;
            resultMatrix[1][i] += yCenter;
        }

        return resultMatrix;
    }

    public static int[][] rotate3D(int[] xPoints, int[] yPoints, int[] zPoints, int xc, int yc, int zc, boolean center, double angleX, double angleY, double angleZ) {
        int[][] rotatedPoints = new int[][] {
                xPoints,
                yPoints,
                zPoints
        };

        if(angleX != 0) {
            int[][] rotated = rotate(rotatedPoints[1], rotatedPoints[2], yc, zc, center, angleX);
            rotatedPoints = new int[][] {rotatedPoints[0], rotated[0], rotated[1]};
        }
        if(angleY != 0) {
            int[][] rotated = rotate(rotatedPoints[0], rotatedPoints[2], xc, zc, center, angleY);
            rotatedPoints = new int[][] {rotated[0], rotatedPoints[1], rotated[1]};
        }
        if(angleZ != 0) {
            int[][] rotated = rotate(rotatedPoints[0], rotatedPoints[1], xc, yc, center, angleZ);
            rotatedPoints = new int[][] {rotated[0], rotated[1], rotatedPoints[2]};
        }

        return rotatedPoints;
    }

    public static int[][] rotateAroundLine(int[] xPoints, int[] yPoints, int[] zPoints, int[] pointA, int[] pointB, double angle) {
       // Make pointA the origin
       int x0 = pointA[0];
       int y0 = pointA[1];
       int z0 = pointA[2];

       // Vector along the line (pointB - pointA)
       double ux = pointB[0] - x0;
       double uy = pointB[1] - y0;
       double uz = pointB[2] - z0;
       double norm = Math.sqrt(ux * ux + uy * uy + uz * uz);
       ux /= norm;
       uy /= norm;
       uz /= norm;

       double cosTheta = Math.cos(angle);
       double sinTheta = Math.sin(angle);

       int[][] resultMatrix = new int[3][xPoints.length];

       // Rotation
       for (int i = 0; i < xPoints.length; i++) {
           // Translate point to origin
           double x = xPoints[i] - x0;
           double y = yPoints[i] - y0;
           double z = zPoints[i] - z0;

           // Rotation matrix components
           double dotProduct = x * ux + y * uy + z * uz;
           double crossProductX = uy * z - uz * y;
           double crossProductY = uz * x - ux * z;
           double crossProductZ = ux * y - uy * x;

           double rotatedX = x * cosTheta + crossProductX * sinTheta + ux * dotProduct * (1 - cosTheta);
           double rotatedY = y * cosTheta + crossProductY * sinTheta + uy * dotProduct * (1 - cosTheta);
           double rotatedZ = z * cosTheta + crossProductZ * sinTheta + uz * dotProduct * (1 - cosTheta);

           // Back to original position
           resultMatrix[0][i] = (int) (rotatedX + x0);
           resultMatrix[1][i] = (int) (rotatedY + y0);
           resultMatrix[2][i] = (int) (rotatedZ + z0);
       }

       return resultMatrix;
   }


}
