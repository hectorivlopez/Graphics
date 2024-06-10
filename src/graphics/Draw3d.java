package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.Arrays;
import static graphics.Transformations.*;
import static graphics.Draw.*;

public class Draw3d {
    public static int[][] projection(int[][] points, int[] director, String projection) {
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

    public static void cube(int x0, int y0, int z0, int width, int depth, int height, int[] director, double scale, String projection, Color color, BufferedImage buffer) {
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

    public static void prism(int[][] points, int height, int[] director, double scale, int[] p0, String projection, int direction, Color color, BufferedImage buffer) {
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
        double[] heightVector = Utils.calculatePerpendicularVector(points[0], points[1], points[2], height);

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

    public static void biPyramid(int[][] points, int height, int[] director, double scale, double[] angles, int[] p0, String projection, Boolean onlyFront, Color borderColor, Color color, BufferedImage buffer) {
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
        double[] heightVector1 = Utils.calculatePerpendicularVector(xPoints, yPoints, zPoints, height);
        double[] heightVector2 = Utils.calculatePerpendicularVector(xPoints, yPoints, zPoints, -height);

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

            // Rotate
            int[][] rotatedAllPoints = rotate3D(scaledAllPoints[0], scaledAllPoints[1], scaledAllPoints[2], p0[0], p0[1], p0[2], false, angles[0], angles[1], angles[2]);

            // Projection
            int[][] projectedPoints = projection(rotatedAllPoints, director, projection);

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
                    int[] faceXPoints = {rotatedAllPoints[0][face[0]], rotatedAllPoints[0][face[1]], rotatedAllPoints[0][face[2]]};
                    int[] faceYPoints = {rotatedAllPoints[1][face[0]], rotatedAllPoints[1][face[1]], rotatedAllPoints[1][face[2]]};
                    int[] faceZPoints = {rotatedAllPoints[2][face[0]], rotatedAllPoints[2][face[1]], rotatedAllPoints[2][face[2]]};

                    double[] perpendicularVector = Utils.calculatePerpendicularVector(faceXPoints, faceYPoints, faceZPoints, 10);

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

                    double dotProduct = Utils.calculateDotProduct(perpendicularVector, directionVector);

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
        }
        else {
            int[][] heightsPoints = new int[][]{
                    new int[]{xc + (int) heightVector1[0], xc + (int) heightVector2[0], 1},
                    new int[]{yc + (int) heightVector1[1], yc + (int) heightVector2[1], 1},
                    new int[]{zc + (int) heightVector1[2], zc + (int) heightVector2[2], 1},
            };

            // Scale
            int[][] scaledPoints = scale3D(points[0], points[1], points[2], p0[0], p0[1], p0[2], false, scale, scale, scale);
            int[][] scaledHeightsPoints = scale3D(heightsPoints[0], heightsPoints[1], heightsPoints[2], p0[0], p0[1], p0[2], false, scale, scale, scale);

            // Rotate
            int[][] rotatedPoints = rotate3D(scaledPoints[0], scaledPoints[1], scaledPoints[2], p0[0], p0[1], p0[2], false, angles[0], angles[1], angles[2]);
            int[][] rotatedHeightsPoints = rotate3D(scaledHeightsPoints[0], scaledHeightsPoints[1], scaledHeightsPoints[2], p0[0], p0[1], p0[2], false, angles[0], angles[1], angles[2]);

            // Projection
            int[][] projectedPoints = projection(rotatedPoints, director, projection);
            int[][] projectedHeightsPoints = projection(rotatedHeightsPoints, director, projection);

            // Drawing
            if (projectedPoints != null && projectedHeightsPoints != null) {
                drawPolygon(
                        projectedPoints[0],
                        projectedPoints[1],
                        borderColor,
                        buffer
                );

                for (int i = 0; i < projectedPoints[0].length; i++) {
                    drawLine(projectedPoints[0][i], projectedPoints[1][i], projectedHeightsPoints[0][0], projectedHeightsPoints[1][0], borderColor, buffer);
                    drawLine(projectedPoints[0][i], projectedPoints[1][i], projectedHeightsPoints[0][1], projectedHeightsPoints[1][1], borderColor, buffer);
                }
            }
        }
    }

    public static void surface(int[][] points, double angle, int[] p1, int[] p2, int[] director, String projection, Color color, BufferedImage buffer) {
        int[][] rotated = rotateAroundLine(
                points[0],
                points[1],
                points[2],
                p1,
                p2,
                angle
        );
        //System.out.println(rotated[0][2]);

        int[][] projectedPoints = projection(rotated, director, projection);

        double[] perpendicularVector = Utils.calculatePerpendicularVector(rotated[0], rotated[1], rotated[2], 10);

        double[] directionVector = new double[]{
                director[0],
                director[1],
                director[2]
        };

        if (projection.equals("orthogonal")) {
            directionVector = new double[]{0, 0, 1};
        }

        if (projection.equals("perspective")) {
            // Calculate the centroid
            int[] xPoints = rotated[0];
            int[] yPoints = rotated[1];
            int[] zPoints = rotated[2];

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

            directionVector = new double[]{
                    director[0] - xc,
                    director[1] - yc,
                    director[2] - zc
            };
        }

        double dotProduct = Utils.calculateDotProduct(perpendicularVector, directionVector);

        // Drawing
        if (dotProduct < 0  ) {

        }
            Draw.drawPolygon(projectedPoints[0], projectedPoints[1], color, buffer);
    }

}
