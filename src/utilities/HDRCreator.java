package utilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.photo.*;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.photo.Photo.*;

public class HDRCreator {

    private static final Scalar MULTIPLIER = new Scalar(255, 255, 255);

    private static final float GAMMA_CORRECTION_DEFAULT = 2.2f;
    private static final float GAMMA_CORRECTION_ROBERTSON = 1.3f;

    private static final String DEBEVEC = "debevec";
    private static final String ROBERTSON = "robertson";
    private static final String MERTENS = "mertens";

    public static List<Mat> createHDR(String directory){
        List<String>  imagePaths = ImageReader.readImages(directory);
        List<Mat> imageMats = new ArrayList<>();

        for (String imagePath : imagePaths) {
            imageMats.add(Imgcodecs.imread(imagePath));
        }

        List<Float> exposureTimes = ImageReader.getExposureTimes(imagePaths);

        List<Mat> mergedImages = new ArrayList<>();

        mergedImages.add(createHDRDebevec(exposureTimes , imageMats));

        mergedImages.add(createHDRRobertson(exposureTimes , imageMats));

        mergedImages.add(createMertensFusion(imageMats));

        return mergedImages;
    }

    public static Mat createHDR(String directory, String method){
        List<String>  imagePaths = ImageReader.readImages(directory);
        List<Mat> imageMats = new ArrayList<>();

        for (String imagePath : imagePaths) {
            imageMats.add(Imgcodecs.imread(imagePath));
        }

        if(method.equals(MERTENS)) {
            return createMertensFusion(imageMats);
        }

        List<Float> exposureTimes = ImageReader.getExposureTimes(imagePaths);

        if(method.equals(DEBEVEC)) {
            return createHDRDebevec(exposureTimes , imageMats);
        }

        if(method.equals(ROBERTSON)) {
            return createHDRRobertson(exposureTimes , imageMats);
        }

        return new Mat();
    }

    private static Mat createHDRDebevec(List<Float> times, List<Mat> src) {

        Mat timeMat = floatToMat(times);

        Mat crf = new Mat();
        CalibrateDebevec calibrateDebevec = createCalibrateDebevec();
        calibrateDebevec.process(src, crf, timeMat);

        Mat hdr = new Mat();
        MergeDebevec merge_debevec = createMergeDebevec();
        merge_debevec.process(src, hdr, timeMat, crf);

        return toneMapImage(hdr, GAMMA_CORRECTION_DEFAULT);
    }

    private static Mat createHDRRobertson(List<Float> times, List<Mat> src) {

        Mat timeMat = floatToMat(times);

        Mat crf = new Mat();
        CalibrateRobertson calibrateRobertson = createCalibrateRobertson();
        calibrateRobertson.process(src, crf, timeMat);

        Mat hdr = new Mat();
        MergeRobertson merge_robertson = createMergeRobertson();
        merge_robertson.process(src, hdr, timeMat, crf);

        return toneMapImage(hdr, GAMMA_CORRECTION_ROBERTSON);
    }

    private static Mat floatToMat(List<Float> list) {
        return Converters.vector_float_to_Mat(list);
    }

    private static Mat createMertensFusion(List<Mat> images){
        Mat fusion = new Mat();
        MergeMertens mergeMertens = createMergeMertens();
        mergeMertens.process(images, fusion);

        Mat result = new Mat();
        Core.multiply(fusion, MULTIPLIER, result);

        return result;
    }

    private static Mat toneMapImage(Mat src, float gammaCorrection){
        Mat toneMap = new Mat();
        TonemapDurand tonemapDurand = createTonemapDurand(gammaCorrection, 4.0f, 1.0f, 2.0f, 2.0f);
        tonemapDurand.process(src, toneMap);
        Mat result = new Mat();
        Core.multiply(toneMap, MULTIPLIER, result);

        return result;
    }
}
