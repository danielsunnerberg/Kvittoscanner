package utilities;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlurDetectorTest {

    @Before
    public void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void testGetVariance() {

        String path = BlurDetectorTest.class.getResource("/images/example.png").getFile().substring(1);
        String path2 = BlurDetectorTest.class.getResource("/images/example2.png").getFile().substring(1);

        Mat imageMat = Imgcodecs.imread(path);
        Mat imageMat2 = Imgcodecs.imread(path2);

        assertTrue("Make sure image added cols.", imageMat.cols() > 0);
        assertTrue("Make sure image added rows.", imageMat.rows() > 0);

        // Variance should always be a positive value.
        double var = BlurDetector.getVariance(imageMat);
        assertTrue(var > 0);

        double var2 = BlurDetector.getVariance(imageMat2);
        assertTrue(var2 > 0);

        //imageMat is a more blurry image than imageMat2 therefore imageMat2 should return a larger variance value
        assertTrue(var2 > var);
    }

    @Test
    public void testGetVarianceMap() {

        String path = BlurDetectorTest.class.getResource("/images/example.png").getFile().substring(1);

        Mat imageMat = Imgcodecs.imread(path);

        assertTrue("Make sure image added cols.", imageMat.cols() > 0);
        assertTrue("Make sure image added rows.", imageMat.rows() > 0);

        int divide = 4;

        List<BlurDetector.MatPos> list = BlurDetector.getVarianceListCols(path, divide);

        // Should contain 4 rows and 4 cols, total 16 elements
        assertEquals(divide * divide, list.size());

        for (BlurDetector.MatPos matPos : list) {

            assertTrue(matPos.getMat() != null);
            assertTrue(matPos.getX() >= 0 && matPos.getY() >= 0);
        }
    }

    @Test
    public void testCreateImage() {

        String path = BlurDetectorTest.class.getResource("/images/example.png").getFile().substring(1);
        String path2 = BlurDetectorTest.class.getResource("/images/example2.png").getFile().substring(1);

        Mat imageMat = Imgcodecs.imread(path);
        Mat imageMat2 = Imgcodecs.imread(path2);

        int rows = imageMat.rows();
        int cols = imageMat.cols();

        int rows2 = imageMat2.rows();
        int cols2 = imageMat2.cols();

        List<Mat> frames = Arrays.asList(imageMat, imageMat2);

        assertTrue("Make sure image added cols.", imageMat.cols() > 0);
        assertTrue("Make sure image added rows.", imageMat.rows() > 0);

        int divide = 3;

        Mat mat = BlurDetector.createImageCols(frames, divide);

        //Make sure the new image is a large as imageMat or imageMat2.
        assertTrue(mat.cols() == cols || mat.cols() == cols2);
        assertTrue(mat.rows() == rows || mat.rows() == rows2);
    }
}
