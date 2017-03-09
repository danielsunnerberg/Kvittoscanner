package reducers;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.List;

/**
 * Makes it possible to enumerate the inclusion strategies:
 * (a,b)
 * [a,b)
 * (a,b]
 * [a,b]
 */
enum InclusionStrategy {
    NOT_A_NOT_B((region, a, b) -> { /* Do not include either */ }),
    A_NOT_B((region, a, b) -> { region.add(a); }),
    NOT_A_B((region, a, b) -> { region.add(b); }),
    A_AND_B((region, a, b) -> { region.add(a); region.add(b); });

    private final IncludeOperator includeOperator;

    InclusionStrategy(IncludeOperator includeOperator) {
        this.includeOperator = includeOperator;
    }

    /**
     * Apply the inclusion strategy.
     *
     * @param region
     * @param a
     * @param b
     */
    public MatOfPoint2f include(List<Point> region, Point a, Point b) {
        includeOperator.apply(region, a, b);
        return new MatOfPoint2f(
            region.stream().toArray(Point[]::new)
        );

    }

    private interface IncludeOperator {
        void apply(List<Point> region, Point a, Point b);
    }
}
