package receiptMergers;

import org.opencv.core.Mat;

import java.util.List;

public interface SectionFinder {

    /**
     * Finds mergeable sections in the given source.
     *
     * @param source source to be analyzed
     * @return found sections
     */
    List<Section> findSections(Mat source);

}
