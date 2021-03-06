package receiptMergers;

import blurDetectors.BlurDetector;
import blurDetectors.TenengradBlurDetector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utilities.EdgeDetector;

import java.util.*;

import static org.opencv.core.Core.FONT_HERSHEY_COMPLEX_SMALL;
import static org.opencv.core.CvType.CV_8UC3;

public class ReceiptMerger {

    private static final Logger logger = LogManager.getLogger(EdgeDetector.class);

    private static final int SECTION_PADDING = 8;

    private final SectionFinder sectionFinder;
    private final BlurDetector blurDetector;

    private final boolean debug;

    public ReceiptMerger() {
        this(false);
    }

    public ReceiptMerger(boolean debug) {
        this.debug = debug;
        this.sectionFinder = new TextSectionFinder(SECTION_PADDING);
        this.blurDetector = new TenengradBlurDetector();
    }

    /**
     * Creates a Mat from a list of mats and select the best part from each mat and put it to one picture with all
     * the best parts.
     *
     * @param frames the list containing the mats.
     * @return a Mat with all the best parts for all different frames
     */
    public Mat createImageRows(List<Mat> frames) {
        // A list that will contain all the other frames with their splitted parts.
        List<List<MatPos>> varianceLists = new LinkedList<>();

        if (frames.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract rows from empty list");
        }

        logger.info("Using first extracted frame as section reference.");
        List<Section> sections = sectionFinder.findSections(frames.get(0));
        logger.info("Found {} sections.", sections.size());

        for(Mat mat : frames) {
            varianceLists.add(getVarianceListRows(mat, sections, frames.indexOf(mat)));
        }

        MatPos[] list = new MatPos[sections.size()];
        for(List<MatPos> l : varianceLists) {
            for (MatPos matPos : l) {
                double variance = matPos.getVar();

                // Is the position empty?
                if(list[matPos.getY()] == null) {
                    list[matPos.getY()] = matPos;
                }

                // Is the position containing a submat with a lower variance than the current submat?
                else if(list[matPos.getY()].getVar() < variance) {
                    list[matPos.getY()] = matPos;
                }
            }
        }

        return mergeMats(new ArrayList<>(Arrays.asList(list)), frames);
    }

    /**
     * Split a image into smaller subsection and add each subsection to a map.
     *
     * @param imageMat the mat from the image
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    private List<MatPos> getVarianceListRows(Mat imageMat, List<Section> sections, int referenceIndex) {
        List<MatPos> list = new LinkedList<>();

        int index = 0;
        for (Section section : sections) {
            Mat rowMat = imageMat.rowRange(section.getRangeWithPadding());

            double var = blurDetector.getVariance(rowMat);

            // Add the new smaller mat with the position and its variance to the list
            list.add(new MatPos(rowMat, index, var, referenceIndex, section));
            index++;
        }

        return list;
    }

    /**
     * Merge the subsections of the original image back to a big image again.
     *
     * @param mats the list containing the mats with the highest variance.
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    private Mat mergeMats(List<MatPos> mats, List<Mat> frames) {
        //A list that will contain all the columns
        Mat res = new Mat();
        List<Mat> rowList = new LinkedList<>();

        List<MatPos> removeQueue = new ArrayList<>();
        int maxStop = 0;
        for (int i = 0; i < mats.size(); i++) {
            if (i + 1 > mats.size() - 1) {
                break;
            }

            if (i < maxStop) {
                continue;
            }

            MatPos matPos = mats.get(i);
            int referenceIndex = matPos.referenceIndex;
            if (referenceIndex == mats.get(i + 1).referenceIndex) {
                int stop = getEqualsStopIndex(mats, i + 1, referenceIndex);
                maxStop = stop;

                int yStart = matPos.section.getStart();
                int yStop = mats.get(stop).section.getStop();
                logger.info("Combine sections [{}, {}] (y-range: [{}, {}])", i, stop, yStart, yStop);

                Mat source = frames.get(referenceIndex);
                Section section = new Section(yStart, yStop, SECTION_PADDING, source);
                matPos.mat = source.rowRange(section.getRangeWithPadding());

                for (int j = i + 1; j <= stop; j++) {
                    removeQueue.add(mats.get(j));
                }
            }
        }

        mats.removeAll(removeQueue);

        Mat padding = createPaddingMat(mats.get(0).mat.size());
        int i = 0;
        for (MatPos section : mats) {
            Mat m = section.getMat();

            if (debug) {
                displayDebugOnSection(section, m);
            }
            rowList.add(m);

            if (i < mats.size() - 1) {
                // For all but last, add bottom padding
                rowList.add(padding);
            }
            i++;
        }

        // Combine all the best sections to a single matrix
        Core.vconcat(rowList, res);

        return res;
    }

    private void displayDebugOnSection(MatPos matPos, Mat m) {
        Imgproc.putText(
                m,
                String.valueOf(matPos.referenceIndex),
                new Point(10, 15),
                FONT_HERSHEY_COMPLEX_SMALL,
                0.8,
                new Scalar(0,0,255)
        );
    }

    private int getEqualsStopIndex(List<MatPos> mats, int start, int referenceId) {
        // Find the next element which is not equal to the reference
        for (int i = start; i < mats.size(); i++) {
            if (mats.get(i).referenceIndex != referenceId) {
                // i - 1 is always assumed to be in range and equal to the reference
                return i - 1;
            }
        }

        return mats.size() - 1;
    }


    private Mat createPaddingMat(Size mat) {
        final int PADDING_HEIGHT = 20;
        Mat padding = Mat.ones(PADDING_HEIGHT, (int) mat.width, CV_8UC3);
        padding.setTo(new Scalar(255, 255, 255));
        return padding;
    }

    /**
     * A method the get the best least blurry mats.
     *
     * @param frames the list with the different mats
     * @param size the number of mats the list will contain
     * @return the list that contains the least blurry mats
     */
    public List<Mat> getBestFrames(List<Mat> frames, int size) {

        List<Mat> bestFrames = new LinkedList<>();
        Queue<MatPos> pq = new PriorityQueue<>();

        // Add all mats to the priority queue
        for(Mat mat : frames) {
            double var = blurDetector.getVariance(mat);
            pq.add(new MatPos(mat, var));
        }

        // Take out size elements that have the highest variance
        for(int i = 0; i < size; i++) {
            Mat mat = pq.poll().getMat();
            bestFrames.add(mat);
        }

        return bestFrames;
    }

    static class MatPos implements Comparable<MatPos> {

        private Section section;
        private int referenceIndex;
        private Mat mat;
        private int x;
        private int y;
        private double var;

        MatPos(Mat mat, int y, double var, int referenceIndex, Section section) {
            this.mat = mat;
            this.x = -1;
            this.y = y;
            this.var = var;
            this.referenceIndex = referenceIndex;
            this.section = section;
        }

        MatPos(Mat mat, double var) {
            this.mat = mat;
            this.var = var;
        }

        public Mat getMat() {
            return mat;
        }

        public int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        double getVar() {
            return var;
        }

        @Override
        public int compareTo(MatPos o) {
            return Double.compare(o.getVar(), getVar());
        }
    }
}
