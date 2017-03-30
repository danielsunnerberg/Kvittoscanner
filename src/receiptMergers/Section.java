package receiptMergers;

import org.opencv.core.Mat;
import org.opencv.core.Range;


/**
 * Represents a mergeable section.
 */
class Section {
    private final int start;
    private final int stop;
    private final int padding;
    private final int sourceHeight;

    Section(int start, int stop, int padding, Mat source) {
        this.start = start;
        this.stop = stop;
        this.padding = padding;
        this.sourceHeight = source.height();
    }

    int getStart() {
        return start;
    }

    int getStop() {
        return stop;
    }

    /**
     * @return a range containing the start and stop, including padding
     */
    Range getRangeWithPadding() {
        return new Range(Math.max(0, start - padding), Math.min(stop + padding, sourceHeight));
    }
}
