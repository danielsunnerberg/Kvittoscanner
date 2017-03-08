package utilities;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * Created by jacobth on 2017-02-01.
 */
public class BlurDetector {

    /**
     * Calculate the variance for a specific image to determine how blurry it is.
     *
     * @param imageMat the image in for of a mat
     * @return the variance of the image
     */
    public static double getVariance(Mat imageMat) {

        Mat matOut = new Mat();
        Mat matGray = new Mat();

        //Convert the mat to grey scale.
        Imgproc.cvtColor(imageMat, matGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(matGray, matOut, 3);

        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();

        Core.meanStdDev(matOut, median, std);

        //The variance is the median^2
        double var = Math.pow(std.get(0, 0)[0], 2);

        return var;
    }

    /**
     * Split a image into smaller subsection and add each subsection to a map.
     *
     * @param file the name of the image
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    public static List<MatPos> getVarianceListCols(String file, int divide) {

        Mat imageMat = Imgcodecs.imread(file);

        List<MatPos> list = getVarianceListCols(imageMat, divide);

        return list;
    }

    /**
     * Split a image into smaller subsection and add each subsection to a map.
     *
     * @param imageMat the mat from the image
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    public static List<MatPos> getVarianceListCols(Mat imageMat, int divide) {

        List<MatPos> list = new LinkedList<>();

        int rows = imageMat.rows();
        int cols = imageMat.cols();

        for(int i = 0; i < divide; i++) {
            //Split the mat in a row
            Mat rowMat = imageMat.rowRange(i * rows / divide, (i + 1) * rows / divide);

            for(int j = 0; j < divide; j++) {
                //Split the current row in to columns
                Mat colMat = rowMat.colRange(j * cols / divide, (j + 1) * cols / divide);
                double var = getVariance(colMat);

                //Add the new smaller mat with the position and its variance to the list
                list.add(new MatPos(colMat, i, j, var));
            }
        }

        return list;
    }

    /**
     * Split a image into smaller subsection and add each subsection to a map.
     *
     * @param imageMat the mat from the image
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    public static List<MatPos> getVarianceListRows(Mat imageMat, int divide) {

        List<MatPos> list = new LinkedList<>();

        int rows = imageMat.rows();

        for(int i = 0; i < divide; i++) {
            //Split the mat in a row
            Mat rowMat = imageMat.rowRange(i * rows / divide, (i + 1) * rows / divide);

            double var = getVariance(rowMat);

                //Add the new smaller mat with the position and its variance to the list
            list.add(new MatPos(rowMat, i, var));

        }

        return list;
    }

    /**
     * Creates a Mat from a list of mats and select the best part from each mat and put it to one picture with all
     * the best parts.
     *
     * @param frames the list containing the mats.
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a Mat with all the best parts for all different frames
     */
    public static Mat createImageCols(List<Mat> frames, int divide) {

        //A list that will contain all the other frames with their splitted parts.
        List<List<MatPos>> varianceLists = new LinkedList<>();

        for(Mat mat : frames) {
            varianceLists.add(getVarianceListCols(mat, divide));
        }

        MatPos[][] list = new MatPos[divide][divide];

        for(List<MatPos> l : varianceLists) {

            for (MatPos matPos : l) {

                //System.out.println(matPos.getVar());

                double variance = matPos.getVar();

                //Is the position empty?
                if(list[matPos.getY()][matPos.getX()] == null) {
                    list[matPos.getY()][matPos.getX()] = matPos;
                }

                //Is the position containing a submat with a lower variance than the current submat?
                else if(list[matPos.getY()][matPos.getX()].getVar() < variance) {

                    list[matPos.getY()][matPos.getX()] = matPos;
                }
            }
        }

        Mat outMat = mergeMats(list, divide);

        return outMat;
    }

    /**
     * Creates a Mat from a list of mats and select the best part from each mat and put it to one picture with all
     * the best parts.
     *
     * @param frames the list containing the mats.
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a Mat with all the best parts for all different frames
     */
    public static Mat createImageRows(List<Mat> frames, int divide) {

        //A list that will contain all the other frames with their splitted parts.
        List<List<MatPos>> varianceLists = new LinkedList<>();

        for(Mat mat : frames) {
            varianceLists.add(getVarianceListRows(mat, divide));
        }

        MatPos[] list = new MatPos[divide];

        for(List<MatPos> l : varianceLists) {

            for (MatPos matPos : l) {

                //System.out.println(matPos.getVar());

                double variance = matPos.getVar();

                //Is the position empty?
                if(list[matPos.getY()] == null) {
                    list[matPos.getY()] = matPos;
                }

                //Is the position containing a submat with a lower variance than the current submat?
                else if(list[matPos.getY()].getVar() < variance) {

                    list[matPos.getY()] = matPos;
                }
            }
        }

        Mat outMat = mergeMats(list, divide);

        return outMat;
    }

    /**
     * Merge the subsections of the original image back to a big image again.
     *
     * @param mats the list containing the mats with the highest variance.
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    private static Mat mergeMats(MatPos[][] mats, int divide) {

        //A list that will contain all the columns
        List<Mat> colList = new LinkedList<>();
        Mat res = new Mat();

        for(int i = 0; i < divide; i++) {

            Mat row = new Mat();
            //A list that will contain all the rows
            List<Mat> rowList = new LinkedList<>();

            for(int j = 0; j < divide; j++) {

                Mat m = mats[i][j].getMat();
                rowList.add(m);
            }

            //Merge this row together
            Core.vconcat(rowList, row);
            colList.add(row);
        }

        //Merge all the columns from the rows tigheter
        Core.hconcat(colList, res);

        return res;
    }

    /**
     * Merge the subsections of the original image back to a big image again.
     *
     * @param mats the list containing the mats with the highest variance.
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    private static Mat mergeMats(MatPos[] mats, int divide) {

        //A list that will contain all the columns
        Mat res = new Mat();
        List<Mat> rowList = new LinkedList<>();

        for(int i = 0; i < divide; i++) {

            Mat m = mats[i].getMat();
            rowList.add(m);
        }

        Core.vconcat(rowList, res);

        return res;
    }

    /**
     * Create a black and white version of a image.
     *
     * @param imageMat the image in for of a mat
     * @return the same mat but in black and white
     */
    public static Mat greyScale(Mat imageMat) {

        Mat matGray = new Mat();

        Imgproc.cvtColor(imageMat, matGray, Imgproc.COLOR_BGR2GRAY);

        return matGray;
    }

    /**
     * A method the get the best least blurry mats.
     *
     * @param frames the list with the different mats
     * @param size the number of mats the list will contain
     * @return the list that contains the least blurry mats
     */
    public static List<Mat> getBestFrames(List<Mat> frames, int size) {

        List<Mat> bestFrames = new LinkedList<>();
        Queue<MatPos> pq = new PriorityQueue<>();

        //Add all mats to the priority queue
        for(Mat mat : frames) {
            double var = getVariance(mat);
            pq.add(new MatPos(mat, var));
        }

        //Take out size elemtns that have the highest variance
        for(int i = 0; i < size; i++) {
            Mat mat = pq.poll().getMat();
            bestFrames.add(mat);
        }

        return bestFrames;
    }

    static class MatPos implements Comparable<MatPos> {

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

        public MatPos(Mat mat, int y, double var) {
            this.mat = mat;
            this.x = -1;
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
            return Double.compare(o.getVar(), getVar());
        }
    }
}
