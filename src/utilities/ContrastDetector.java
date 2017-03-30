package utilities;

import enhanceImage.ImageEnhancer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

public class ContrastDetector {

    /**
     * Computes a numeric value for an images contrast.
     *
     * @param source Source to be analyzed.
     * @return Numeric contrast value. Higher = more contrast; i.e. an image with all the same color =~0.
     */
    public double calculateContrast(Mat source) {
        List<Double> pixels = new ArrayList<>();
        for (int row = 0; row < source.rows(); row++) {
            for (int col = 0; col < source.cols(); col++) {
                // For each pixel, find the "average color", that is the average
                // of all channels. This is a simplification but works good enough.
                double[] pixelChannels = source.get(row, col);
                double pixel = DoubleStream.of(pixelChannels).sum() / pixelChannels.length;
                pixels.add(pixel);
            }
        }

        double pixelSum = pixels.stream().mapToDouble(Double::doubleValue).sum();
        if (pixelSum == 0) {
            // All black image, avoid division by zero
            return 0;
        }

        Size size = source.size();
        return (Collections.max(pixels) - Collections.min(pixels)) / (pixelSum / size.width / size.height);
    }


    /**
     * Computes the minimum y-coordinate in the interval where a flash is detected.
     *
     * @param receipt Source to be analyzed.
     * @return y-coordinate of detected flash. -1 if no flash detected.
     */
    public int getFlashMinY(Mat receipt){
        int sigma = getSigma(receipt);
        List<MatOfPoint> contours = getFlashContours(receipt,sigma);
        if (contours.size()!=2)
            return -1;
        int indexOfSmallestArea = getIndexOfSmallestArea(contours);

        double minY = contours.get(indexOfSmallestArea).get(0,0)[1];
        for (int i = 0; i<contours.get(indexOfSmallestArea).rows(); i++){
            minY=Math.min(minY, contours.get(indexOfSmallestArea).get(i,0)[1]);
        }
        return (int)minY;
    }

    /**
     * Computes the maximum y-coordinate in the interval where a flash is detected.
     *
     * @param receipt Source to be analyzed.
     * @return y-coordinate of detected flash. -1 if no flash detected.
     */
    public int getFlashMaxY(Mat receipt){
        int sigma = getSigma(receipt);
        List<MatOfPoint> contours = getFlashContours(receipt,sigma);
        if (contours.size()!=2)
            return -1;
        int indexOfSmallestArea = getIndexOfSmallestArea(contours);

        double maxY = contours.get(indexOfSmallestArea).get(0,0)[1];
        for (int i = 0; i<contours.get(indexOfSmallestArea).rows(); i++){
            maxY=Math.max(maxY, contours.get(indexOfSmallestArea).get(i,0)[1]);
        }
        return (int)maxY;
    }

    private int getSigma(Mat receipt){
        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(receipt, median, std);
        return (int) std.get(0, 0)[0];
    }

    private List<MatOfPoint> getFlashContours(Mat receipt, int sigma){
        Mat out = ImageEnhancer.gaussianBlurAndThreshold(receipt,99,99,sigma,200,255,true, Imgproc.THRESH_TOZERO_INV, false);
        Mat threshOut = new Mat();
        threshOut = ImageEnhancer.onlyThreshold(out, false, Imgproc.THRESH_BINARY, 1,255,false);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(threshOut, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        for(int i=1; i<55; i++){
            if (contours.size()==2)
                break;
            System.out.println(i);
            out = ImageEnhancer.gaussianBlurAndThreshold(receipt,99,99,sigma,200+i,255,true, Imgproc.THRESH_TOZERO_INV, false);
            threshOut = ImageEnhancer.onlyThreshold(out, false, Imgproc.THRESH_BINARY, 1,255,false);
            contours = new ArrayList<>();
            Imgproc.findContours(threshOut, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        }
        return contours;
    }

    private int getIndexOfSmallestArea(List<MatOfPoint> contours) {
        int index = 0;
        double[] minArea = new double[contours.size()];
        for (MatOfPoint contour : contours) {
            minArea[index] = Imgproc.contourArea(contour);
            index++;
        }
        return findMinIdx(minArea);
    }

    private int findMinIdx(double[] numbers) {
        if (numbers == null || numbers.length == 0) return -1;
        double minVal = numbers[0];
        int minIdx = 0;
        for(int idx=1; idx<numbers.length; idx++) {
            if(numbers[idx] < minVal) {
                minVal = numbers[idx];
                minIdx = idx;
            }
        }
        return minIdx;
    }

}
