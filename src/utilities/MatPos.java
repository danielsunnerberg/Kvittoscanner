package utilities;

import org.opencv.core.Mat;

/**
 * Created by jacobth on 2017-02-10.
 */
public class MatPos {

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

}
