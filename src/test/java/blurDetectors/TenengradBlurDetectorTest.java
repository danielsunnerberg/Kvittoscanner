package blurDetectors;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.Assert.assertTrue;

public class TenengradBlurDetectorTest {

    private BlurDetector blurDetector;

    @Before
    public void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        blurDetector = new TenengradBlurDetector();
    }

    @Test
    public void testGetVariance() {

        String path = TenengradBlurDetectorTest.class.getResource("/images/example.png").getFile().substring(1);
        String path2 = TenengradBlurDetectorTest.class.getResource("/images/example2.png").getFile().substring(1);

        Mat imageMat = Imgcodecs.imread(path);
        Mat imageMat2 = Imgcodecs.imread(path2);

        assertTrue("Make sure image added cols.", imageMat.cols() > 0);
        assertTrue("Make sure image added rows.", imageMat.rows() > 0);

        // Variance should always be a positive value.
        double var = blurDetector.getVariance(imageMat);
        assertTrue(var > 0);

        double var2 = blurDetector.getVariance(imageMat2);
        assertTrue(var2 > 0);

        //imageMat is a more blurry image than imageMat2 therefore imageMat2 should return a larger variance value
        assertTrue(var2 > var);
    }
}
