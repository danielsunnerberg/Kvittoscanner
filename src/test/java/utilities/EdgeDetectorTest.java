package utilities;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Rect bounds = edgeDetector.findBoundingRect(source);
        int boundsArea = bounds.width * bounds.height;

        assertEquals(300700, boundsArea, 300);
        assertTrue(boundsArea < (originalSize.width * originalSize.height));

        Mat extracted = edgeDetector.extractBiggestObject(source);
        int extractedArea = extracted.width() * extracted.height();

        assertEquals(boundsArea, extractedArea);
    }

    @Test
    public void testFindBoundingRectMidContrast() {
        Mat source = getMatFromFile("mid-contrast.jpg");
        Size originalSize = source.size();

        // Area of found object should be smaller than source
        Rect bounds = edgeDetector.findBoundingRect(source);
        int boundsArea = bounds.width * bounds.height;

        assertEquals(198100, boundsArea, 300);
        assertTrue(boundsArea < (originalSize.width * originalSize.height));

        Mat extracted = edgeDetector.extractBiggestObject(source);
        int extractedArea = extracted.width() * extracted.height();

        assertEquals(boundsArea, extractedArea);
    }

}