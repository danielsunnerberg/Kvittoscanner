package utilities;

import org.opencv.core.Mat;
import org.opencv.photo.CalibrateDebevec;
import org.opencv.photo.MergeDebevec;
import org.opencv.photo.MergeMertens;
import org.opencv.photo.TonemapDurand;
import org.opencv.utils.Converters;

import java.util.List;

import static org.opencv.photo.Photo.*;

/**
 * Created by jacobth on 2017-03-09.
 */
public class HDRCreator {


    public Mat createHDR(List<Float> times, List<Mat> src) {

        Mat response = new Mat();
        Mat timeMat = floatToMat(times);

        CalibrateDebevec calibrate = createCalibrateDebevec();
        calibrate.process(src, response, timeMat);

        Mat hdr = new Mat();
        MergeDebevec merge_debevec = createMergeDebevec();
        merge_debevec.process(src, hdr, timeMat, response);

        Mat ldr = new Mat();
        TonemapDurand tonemap = createTonemapDurand();
        tonemap.process(hdr, ldr);

        Mat fusion = new Mat();
        MergeMertens merge_mertens = createMergeMertens();
        merge_mertens.process(src, fusion);

        return ldr;
    }

    private Mat floatToMat(List<Float> list) {
        return Converters.vector_float_to_Mat(list);
    }
}
