package reducers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import utilities.BoxValidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PolygonReducer {

    private static final Logger logger = LogManager.getLogger(PolygonReducer.class);
    private final static int MAX_REDUCTION_DELTA = 10;

    private BoxValidation boxValidation;

    public PolygonReducer() {
        this.boxValidation = new BoxValidation();
    }

    // @todo Doc

    public MatOfPoint2f smartPolygonReduction(MatOfPoint2f approximation) {
        int tryCounter = 0;
        for (InclusionStrategy inclusionStrategy : InclusionStrategy.values()) {
            tryCounter++;

            // @todo Document, (a,b), [a,b), ..., angle validation for all examples
            MatOfPoint2f smartReduction = smartPolygonReduction(approximation, inclusionStrategy);
            if (smartReduction == null || smartReduction.total() != 4) {
                logger.info("Smart reduction (try {}) failed, discarding", tryCounter);
                continue;
            }

            // We have a reduction with 4 points, validate all corners.
            // A perfect corner has an angle of 90*.
            // Allow each corner some slack, discard the frame if it's too bad.
            if (boxValidation.validateCornerAngles(smartReduction)) {
                logger.info("Found valid smart reduction in {} tries", tryCounter);
                return smartReduction;
            } else {
                logger.info("Smart reduction has invalid corners, discarding");
            }
        }

        logger.info("Smart polygon reduction failed");
        return null;
    }

    public MatOfPoint2f smartPolygonReduction(MatOfPoint2f approximation, InclusionStrategy inclusionStrategy) {
        Point[] points = approximation.toArray();

        // Find the first point with a too wide/narrow angle, a
        Integer a = null;
        for (int i = 0; i < points.length; i++) {
            Point p1 = points[i];
            Point p2 = points[Math.floorMod(i - 1, points.length)];
            Point p3 = points[Math.floorMod(i + 1, points.length)];

            if (Math.abs(boxValidation.angle(p1, p2, p3) - 90) > MAX_REDUCTION_DELTA) {
                logger.info("Found first extreme point, {}", i);
                a = i;
                break;
            }
        }

        if (a == null) {
            logger.info("Failed to get first extreme point.");
            return null;
        }

        // Walking backwards, find the next point with a too wide/narrow angle, b
        Integer b = null;
        for (int i = points.length - 1; i >= 0; i--) {
            Point p1 = points[i];
            Point p2 = points[Math.floorMod(i - 1, points.length)];
            Point p3 = points[Math.floorMod(i + 1, points.length)];

            if (Math.abs(boxValidation.angle(p1, p2, p3) - 90) > MAX_REDUCTION_DELTA) {
                logger.info("Found second extreme point, {}", i);
                b = i;
                break;
            }
        }

        if (b == null) {
            logger.info("Failed to get second extreme point.");
            return null;
        }

        if (Objects.equals(a, b)) {
            logger.info("Found extreme points are identical, removing single point");
            List<Point> reduced = new ArrayList<>();
            for (int i = 0; i < points.length; i++) {
                if (i == a) {
                    continue;
                }
                reduced.add(points[i]);
            }

            return new MatOfPoint2f(
                reduced.toArray(new Point[reduced.size()])
            );
        }

        // Create a region of points in [a..b]
        List<Point> _aRegion = new ArrayList<>();
        _aRegion.addAll(Arrays.asList(points).subList(a, b + 1)); // [a..b]
        MatOfPoint2f aAreaRegion = extractAreaRegion(_aRegion, points[a], points[b]);

        // Create a region of points in (points \ aRegion)
        List<Point> _bRegion = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            if (i > a && i < b) { // [a..b]
                continue;
            }

            _bRegion.add(points[i]);
        }
        MatOfPoint2f bAreaRegion = extractAreaRegion(_bRegion, points[a], points[b]);

        if (_aRegion.isEmpty() || _bRegion.isEmpty()) {
            logger.info("Polygon reduction failed, one area is empty");
            return null;
        }

        // When creating aRegion/bRegion, include both a and b.
        // When calculating the area, remove both.
        // When returning, let inclusion strategy decide
        // @todo ^

        // We now have two regions, aRegion and bRegion. One of these (hopefully)
        // contains glare and the other the receipt without the glare.
        // A simple, but not always correct filter is simply choosing the one with the
        // biggest area. In the event that we choose wrong, the angle detector
        // will discard the frame anyways.
        if (Imgproc.contourArea(aAreaRegion) > Imgproc.contourArea(bAreaRegion)) {
            return inclusionStrategy.include(_aRegion, points[a], points[b]);
        } else {
            return inclusionStrategy.include(_bRegion, points[a], points[b]);
        }
    }

    private MatOfPoint2f extractAreaRegion(List<Point> bRegion, Point a, Point b) {
        List<Point> areaRegion = new ArrayList<>(bRegion);
        areaRegion.remove(a);
        areaRegion.remove(b);
        return new MatOfPoint2f(
            areaRegion.stream().toArray(Point[]::new)
        );
    }

}
