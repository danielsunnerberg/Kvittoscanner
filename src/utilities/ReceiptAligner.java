package utilities;

import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.calib3d.Calib3d.findHomography;
import static org.opencv.imgproc.Imgproc.warpPerspective;

public class ReceiptAligner {

    private static final int MAX_AXIS_DELTA = 25;

    private final FeatureDetector featureDetector;
    private final DescriptorExtractor descriptorExtractor;
    private final DescriptorMatcher descriptorMatcher;

    public ReceiptAligner() {
        featureDetector = FeatureDetector.create(FeatureDetector.ORB);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    }

    /**
     * Aligns a receipt after the specified reference.
     * This is done through feature detection/matching.
     *
     * @param _reference reference receipt, image which we want to imitate
     * @param _receipt receipt to be aligned
     * @return aligned receipt
     */
    public Mat align(Mat _reference, Mat _receipt)
    {
        Mat reference = _reference.clone(); // @todo Performance: Do we really need to clone?
        Mat receipt = _receipt.clone();

        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        featureDetector.detect(reference, keypoints1);

        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        featureDetector.detect(receipt, keypoints2);

        Mat descriptor1 = new Mat();
        descriptorExtractor.compute(reference, keypoints1, descriptor1);

        Mat descriptor2 = new Mat();
        descriptorExtractor.compute(receipt, keypoints2, descriptor2);

        List<MatOfDMatch> matches = new ArrayList<>();
        descriptorMatcher.knnMatch(descriptor1, descriptor2, matches, 1);

        KeyPoint[] _keyPoints1 = keypoints1.toArray();
        KeyPoint[] _keyPoints2 = keypoints2.toArray();

        List<Point> _source = new ArrayList<>();
        List<Point> _destination = new ArrayList<>();

        for (MatOfDMatch match : matches) {
            DMatch[] dMatches = match.toArray();
            if (dMatches.length != 1) {
                // Should not happen as k = 1
                continue;
            }
            // queryIdx => keypoints1 index
            // trainIdx => keypoints2 index

            // p1 is the point in reference and p2 is where it was
            // found in the other source
            Point p1 = _keyPoints1[dMatches[0].queryIdx].pt;
            Point p2 = _keyPoints2[dMatches[0].trainIdx].pt;

            // Only select the best matches, e.g. the one where the matched points
            // do not have too much x/y-difference.
            if (Math.abs(p1.y - p2.y) > MAX_AXIS_DELTA || Math.abs(p1.x - p2.x) > MAX_AXIS_DELTA) {
                continue;
            }

            _source.add(p1);
            _destination.add(p2);
        }

        MatOfPoint2f destination = new MatOfPoint2f();
        destination.fromList(_destination);

        MatOfPoint2f source = new MatOfPoint2f();
        source.fromList(_source);

        Mat aligned = new Mat(reference.size(), CvType.CV_8UC1);

        // Find homography and warp the receipt
        Mat homography = findHomography(destination, source);
        warpPerspective(receipt, aligned, homography, receipt.size());
        return aligned;
    }
}
