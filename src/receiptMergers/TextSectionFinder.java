package receiptMergers;

import org.opencv.core.Mat;
import utilities.ContrastDetector;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.*;


/**
 * Finds mergeable sections by analyzing where the source has text.
 */
public class TextSectionFinder implements SectionFinder {

    /**
     * How "white" the average of the channels has to be to
     * be considered white.
     */
    private static final int WHITE_PIXEL_THRESHOLD = 250;

    /**
     * How many pixels of padding required to be considered
     * a section.
     */
    private final int padding;

    /**
     * How high a section must be to not be discarded.
     */
    private static final int MIN_ROW_HEIGHT = 10;

    private static ContrastDetector contrastDetector;

    TextSectionFinder(final int padding) {
        contrastDetector = new ContrastDetector();
        this.padding = padding;
    }

    /**
     * {@inheritDoc}
     */
    public List<Section> findSections(Mat source) {
        Mat gray = new Mat();
        cvtColor(source, gray, COLOR_BGR2GRAY);

        double thresholdValue = calculateThreshold(source);

        Mat binary = new Mat();
        threshold(gray, binary, thresholdValue, 255, THRESH_BINARY);

        List<Section> sections = new ArrayList<>();

        Integer start = null;
        for (int y = 0; y < binary.height(); y++) {
            double rowAverage = rowPixelAverage(binary, y);

            // Find first black row (going down)
            if (start == null && rowAverage <= WHITE_PIXEL_THRESHOLD) {
                start = y;
                continue;
            }

            // Continue until we find a bottom padding, then we have our stop point.
            if (start != null && rowHasBottomPadding(y, binary)) {
                if (y - start < MIN_ROW_HEIGHT) {
                    continue;
                }

                sections.add(new Section(start, y, padding, source));

                // The section has now been added, re-start from the end of that section
                start = null;
            }
        }

        return sections;
    }

    private static double calculateThreshold(Mat source) {
        // By testing a few samples, we can map a contrast value
        // to a threshold value. This is sadly very fragile if we don't
        // have a perfect sample library; but adaptive thresholds aren't an option here.
        return 368.4392032814367 - 186.2830192604969 * contrastDetector.calculateContrast(source);
    }

    private double rowPixelAverage(Mat source, int row) {
        List<Double> rowSum = new ArrayList<>();
        for (int x = 0; x < source.width(); x++) {
            double[] doubles = source.get(row, x);
            double sum = 0;
            for (double d : doubles) {
                sum += d;
            }
            rowSum.add(sum / doubles.length);
        }

        double sum = 0;
        for (double d : rowSum) {
            sum += d;
        }
        return sum / rowSum.size();
    }

    private boolean rowHasBottomPadding(int row, Mat source) {
        for (int offset = 1; offset <= padding; offset++) {
            int paddingRow = row + offset;
            if (paddingRow <= 0 || paddingRow >= source.height()) {
                // Row is outside mat bounds
                return true;
            }

            if (rowPixelAverage(source, paddingRow) < WHITE_PIXEL_THRESHOLD) {
                return false;
            }
        }

        return true;
    }
}

