package utilities;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import static org.junit.Assert.*;

public class ContrastDetectorTest {

    private ContrastDetector contrastDetector;

    @Before
    public void setUp() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        contrastDetector = new ContrastDetector();
    }

    private Mat getMatFromFile(String file) {
        String path = ContrastDetectorTest.class.getResource(String.format("/images/contrastTest/%s", file))
                .getFile()
                .substring(1);

        return Imgcodecs.imread(path);
    }

    @Test
    public void testPlainContrast() {
        Mat black = getMatFromFile("black.png");
        Mat white = getMatFromFile("white.png");
        Mat mix = getMatFromFile("mix.png");

        double blackContrast = contrastDetector.calculateContrast(black);
        assertEquals(0, blackContrast, 0);
        assertEquals(0, contrastDetector.calculateContrast(white), 0);
        assertTrue(contrastDetector.calculateContrast(mix) > blackContrast);
    }

    @Test
    public void testContrast() {
        Mat lowContrast = getMatFromFile("low-contrast.png");
        Mat midContrast1 = getMatFromFile("mid-contrast.jpg");
        Mat midContrast2 = getMatFromFile("mid-contrast2.jpg");

        assertTrue(contrastDetector.calculateContrast(lowContrast) < contrastDetector.calculateContrast(midContrast1));
        assertEquals(contrastDetector.calculateContrast(midContrast1), contrastDetector.calculateContrast(midContrast2), .5);
    }
}