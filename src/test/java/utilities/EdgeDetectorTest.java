package utilities;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.opencv.imgproc.Imgproc.boundingRect;

public class EdgeDetectorTest {

    private EdgeDetector edgeDetector;

    @Before
    public void setUp() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        edgeDetector = new EdgeDetector();
    }

    private Mat getMatFromFile(String file) {
        String path = EdgeDetectorTest.class.getResource(String.format("/images/contrastTest/%s", file))
                .getFile()
                .substring(1);

        return Imgcodecs.imread(path);
    }

    @Test
    public void testExtractLowContrast() {
        Mat source = getMatFromFile("low-contrast.png");
        Size originalSize = source.size();

        // Area of found object should be smaller than source
        MatOfPoint2f mat = edgeDetector.findBoundingPolygon(source);
        Rect bounds = boundingRect(new MatOfPoint(mat.toArray()));

        int boundsArea = bounds.width * bounds.height;

        assertEquals(300000, boundsArea, 5000);
        assertTrue(boundsArea < (originalSize.width * originalSize.height));
    }

    @Test
    public void testFindBoundingRectMidContrast() {
        Mat source = getMatFromFile("mid-contrast.jpg");
        Size originalSize = source.size();

        // Area of found object should be smaller than source
        MatOfPoint2f mat = edgeDetector.findBoundingPolygon(source);
        Rect bounds = boundingRect(new MatOfPoint(mat.toArray()));

        int boundsArea = bounds.width * bounds.height;

        assertEquals(190000, boundsArea, 5000);
        assertTrue(boundsArea < (originalSize.width * originalSize.height));
    }

    @Test
    public void testExtractBiggestObject() {
        Mat source = getMatFromFile("mid-contrast.jpg");
        Mat extracted = edgeDetector.extractBiggestObject(source);
        System.out.println();

        assertNotNull(extracted);
        assertTrue(extracted.size().width > 0 && extracted.size().height > 0);
    }

}