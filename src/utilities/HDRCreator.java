package utilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.photo.*;
import org.opencv.utils.Converters;
import java.util.List;

import static org.opencv.photo.Photo.*;

public class HDRCreator {

    private static final Scalar MULTIPLIER = new Scalar(255, 255, 255);

    private static final float GAMMA_CORRECTION_DEFAULT = 2.2f;
    private static final float GAMMA_CORRECTION_ROBERTSON = 1.3f;

    public Mat createHDR(List<Float> times, List<Mat> src) {

        Mat timeMat = floatToMat(times);

        Mat crf = calibrateCRF(src, timeMat);

        Mat hdr = new Mat();
        MergeDebevec merge_debevec = createMergeDebevec();
        merge_debevec.process(src, hdr, timeMat, crf);

        return toneMapImage(hdr, GAMMA_CORRECTION_DEFAULT);
    }

    public Mat createHDRRobertson(List<Float> times, List<Mat> src) {

        Mat timeMat = floatToMat(times);

        Mat crf = calibrateCRF(src, timeMat);

        Mat hdr = new Mat();
        MergeRobertson merge_robertson = createMergeRobertson();
        merge_robertson.process(src, hdr, timeMat, crf);

        return toneMapImage(hdr, GAMMA_CORRECTION_ROBERTSON);
    }

    private Mat floatToMat(List<Float> list) {
        return Converters.vector_float_to_Mat(list);
    }

    public Mat getMertensFusion(List<Mat> images){
        Mat fusion = new Mat();
        MergeMertens mergeMertens = createMergeMertens();
        mergeMertens.process(images, fusion);

        Mat result = new Mat();
        Core.multiply(fusion, MULTIPLIER, result);

        return result;
    }

    private Mat toneMapImage(Mat src, float gammaCorrection){
        Mat toneMap = new Mat();
        TonemapDurand tonemapDurand = createTonemapDurand(gammaCorrection, 4.0f, 1.0f, 2.0f, 2.0f);
        tonemapDurand.process(src, toneMap);
        Mat result = new Mat();
        Core.multiply(toneMap, MULTIPLIER, result);


        return result;
    }

    private Mat calibrateCRF(List<Mat> src, Mat times){
        Mat result = new Mat();
        CalibrateDebevec calibrate = createCalibrateDebevec();
        calibrate.process(src, result, times);
        return result;
    }
}
