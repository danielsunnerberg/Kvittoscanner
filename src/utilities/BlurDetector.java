package utilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by jacobth on 2017-02-01.
 */
public class BlurDetector {

    private static double getVariance(Mat imageMat) {

        Mat matOut = new Mat();
        Mat matGray = new Mat();

        Imgproc.cvtColor(imageMat, matGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(matGray, matOut, 3);

        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();

        Core.meanStdDev(matOut, median, std);

        double var = Math.pow(std.get(0,0)[0],2);

        return var;
    }

    public static HashMap<Point, Double> getVarianceMap(String file, int divide) {

        HashMap<Point, Double> map = new HashMap<Point, Double>();

        Mat imageMat = Imgcodecs.imread(file);

        int rows = imageMat.rows();
        int cols = imageMat.cols();

        for(int i = 0; i < divide; i++) {

            Mat rowMat = imageMat.rowRange(i * rows / divide, (i + 1) * rows / divide);

            for(int j = 0; j < divide; j++) {

                Mat colMat = rowMat.colRange(j * cols / divide, (j + 1) * cols / divide);
                map.put(new Point(i, j), getVariance(colMat));
            }
        }

        return map;
    }

}
