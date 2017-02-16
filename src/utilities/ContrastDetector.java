package utilities;

import org.opencv.core.Mat;
import org.opencv.core.Size;

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

}
