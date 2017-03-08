package utilities;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


import static org.junit.Assert.*;

public class BoxValidationTest {

    @Before
    public void setUp() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test(expected=NullPointerException.class)
    public void testValidateAngleSumThrowsErrorIfNoBoundingbox(){
        EdgeDetector edgeDetector = new EdgeDetector();

        //Image with glare where the boundingbox formed does not validate
        String path = "D:\\Kvittoscanner\\src\\test\\resources\\images\\receipt-glare-left.png";

        Mat frame = Imgcodecs.imread(path);

        boolean detectGlare = false;
        MatOfPoint fc2 = new MatOfPoint(edgeDetector.findBoundingBox(frame, detectGlare).toArray());

    }

    @Test
    public void testValidateAngleSum(){
        EdgeDetector edgeDetector = new EdgeDetector();

        //Image without reflection where a good boundingbox is formed
        String path = "D:\\Kvittoscanner\\src\\test\\resources\\images\\withoutreflection.jpg";

        Mat frame = Imgcodecs.imread(path);

        boolean detectGlare = false;
        MatOfPoint fc2 = new MatOfPoint(edgeDetector.findBoundingBox(frame, detectGlare).toArray());

        assertNotNull(fc2);
    }
}
