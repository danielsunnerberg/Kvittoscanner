package utilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Created by jacobth on 2017-02-01.
 */
public class BlurDetector {

    public static double getVariance(String file) {

        Mat imageMat = Imgcodecs.imread(file);

        Mat destination = new Mat();
        Mat matGray = new Mat();

        Imgproc.cvtColor(imageMat, matGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(matGray, destination, 3);

        MatOfDouble median = new MatOfDouble();
        MatOfDouble std= new MatOfDouble();

        Core.meanStdDev(destination, median, std);

        double var = Math.pow(std.get(0,0)[0],2);

        return var;
    }

}
