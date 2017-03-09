package utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoxValidation {

    private static final Logger logger = LogManager.getLogger(EdgeDetector.class);

    private final static int CORNER_ANGLE_SLACK = 15;
    private final static int ANGLE_ALPHA = 5;

    /**
     * Validates the given box, ensuring that no corner has an invalid
     * corner.
     *
     * @param boundingPolygon box to be validated
     * @return Whether the box is valid
     */
    public boolean validateCornerAngles(MatOfPoint2f boundingPolygon) {
        Point[] points = boundingPolygon.toArray();
        if (points.length != 4) {
            logger.error("Invalid polygon supplied. Fix me."); // @todo
            return false;
        }

        int[][] triangles = {
            new int[]{0, 3, 1}, // top left
            new int[]{1, 0, 2}, // bottom left
            new int[]{2, 1, 3}, // bottom right
            new int[]{3, 0, 2}, // top right
        };

        for (int[] corners : triangles) {
            Point p1 = points[corners[0]];
            Point p2 = points[corners[1]];
            Point p3 = points[corners[2]];
            double angle = angle(p1, p2, p3);
            if (Math.abs(angle - 90) > CORNER_ANGLE_SLACK) {
                logger.info("Illegal corner detected: {} - {}", Arrays.toString(corners), angle);
                return false;
            }
        }

        return true;
    }

    // @todo Use this instead if we're allowing different perspectives
    private boolean validateCornerAngleSum(MatOfPoint2f boundingPolygon){
        double angleSumPrimary;
        double angleSumSecondary;
        boolean status = true;

        List<Double> angleList = new ArrayList<Double>();
        Point[] points = boundingPolygon.toArray();
        int[][] triangles = {
                new int[]{ 0, 3, 1 }, // top left
                new int[]{ 1, 0, 2 }, // bottom left
                new int[]{ 2, 1, 3 }, // bottom right
                new int[]{ 3, 0, 2 }, // top right
        };

        for (int[] corners : triangles) {
            Point p1 = points[corners[0]];
            Point p2 = points[corners[1]];
            Point p3 = points[corners[2]];
            double angle = angle(p1, p2, p3);
            angleList.add(angle);

        }

        for (int i=0; i<4; i++){
            switch(i){
                case 0:
                    angleSumPrimary = angleList.get(0)+angleList.get(3);
                    angleSumSecondary = angleList.get(0)+angleList.get(1);

                    status = checkAngleSum(angleSumPrimary, angleSumSecondary);

                    if (!status){
                        return false;
                    }
                    break;

                case 3:
                    angleSumPrimary = angleList.get(3)+angleList.get(0);
                    angleSumSecondary = angleList.get(3)+angleList.get(2);

                    status = checkAngleSum(angleSumPrimary, angleSumSecondary);

                    if (!status){
                        return false;
                    }
                    break;

                default:
                    angleSumPrimary = angleList.get(i)+angleList.get(i+1);
                    angleSumSecondary = angleList.get(i)+angleList.get(i-1);

                    status = checkAngleSum(angleSumPrimary, angleSumSecondary);

                    if (!status){
                        return false;
                    }
                    break;
            }
        }

        return status;
    }

    private boolean checkAngleSum(double angleSumPrimary, double angleSumSecondary){
        boolean angleStatus = true;
        if (!(angleSumPrimary >= 180 - ANGLE_ALPHA && angleSumPrimary <= 180 + ANGLE_ALPHA)){
            angleStatus = false;
        }
        if (!(angleSumSecondary >= 180 - ANGLE_ALPHA && angleSumSecondary <= 180 + ANGLE_ALPHA)){
            angleStatus = false;
        }
        return angleStatus;
    }

    /**
     * Calculates the angle formed by the triangle formed by the
     * given three points.
     *
     * @param p1 First point
     * @param p2 Second point
     * @param p3 Third point
     * @return Angle formed by the given triangle
     */
    public double angle(Point p1, Point p2, Point p3) {
        double p12 = length(p1, p2);
        double p13 = length(p1, p3);
        double p23 = length(p2, p3);
        // Using the law of cosines
        double angle = Math.acos((Math.pow(p12, 2) + Math.pow(p13, 2) - Math.pow(p23, 2)) / (2 * p12 * p13));
        return Math.toDegrees(angle);
    }

    /**
     * Gets the length of a line formed by two points.
     *
     * @param p1 First point
     * @param p2 Second point
     * @return Length of formed line
     */
    private double length(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.x - p2.x), 2) + Math.pow((p1.y - p2.y), 2));
    }
}
