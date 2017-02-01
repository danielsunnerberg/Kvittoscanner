import org.opencv.core.*;
import utilities.ComputerVision;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.load(System.getProperty("java.library.path"));
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat m  = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("m = " + m.dump());

        //String response = ComputerVision.executePost("{\"url\":\"http://support.quadriga.nu/hc/sv/article_attachments/200038862/Kassa_-_Plus_och_minus_-_12.PNG\"}");
        //System.out.println(response);


    }
}
