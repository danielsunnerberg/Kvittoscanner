package utilities;

import org.opencv.core.Mat;

import java.util.Comparator;

/**
 * Created by jacobth on 2017-02-10.
 */
public class MatPos implements Comparable<MatPos> {

    private Mat mat;
    private int x;
    private int y;
    private double var;

    public MatPos(Mat mat, int x, int y, double var) {
        this.mat = mat;
        this.x = x;
        this.y = y;
        this.var = var;
    }

    public MatPos(Mat mat, double var) {
        this.mat = mat;
        this.var = var;
    }

    public Mat getMat() {
        return mat;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getVar() {
        return var;
    }

    @Override
    public int compareTo(MatPos o) {
        return Double.compare(getVar(), o.getVar());
    }
}
