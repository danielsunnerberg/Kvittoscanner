package utilities;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        Imgproc.cvtColor(imageMat, matGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Laplacian(matGray, matOut, 3);

        MatOfDouble median = new MatOfDouble();
        MatOfDouble std = new MatOfDouble();

        Core.meanStdDev(matOut, median, std);

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
    public static List<MatPos> getVarianceList(String file, int divide) {

        Mat imageMat = Imgcodecs.imread(file);

        List<MatPos> list = getVarianceList(imageMat, divide);

        return list;
    }

    /**
     * Split a image into smaller subsection and add each subsection to a map.
     *
     * @param imageMat the mat from the image
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a map containing utilities.MatPos and variance value for each subsection.
     */
    public static List<MatPos> getVarianceList(Mat imageMat, int divide) {

        List<MatPos> list = new LinkedList<>();

        int rows = imageMat.rows();
        int cols = imageMat.cols();

        for(int i = 0; i < divide; i++) {

            Mat rowMat = imageMat.rowRange(i * rows / divide, (i + 1) * rows / divide);

            for(int j = 0; j < divide; j++) {

                Mat colMat = rowMat.colRange(j * cols / divide, (j + 1) * cols / divide);
                double var = getVariance(colMat);

                list.add(new MatPos(colMat, i, j, var));
            }
        }

        return list;
    }

    /**
     * This method is only used to create a image too look at to check results for yourself.
     *
     * @param frame the list containing the mats with the highest variance.
     * @void
     */
    private static void mat2Image(Mat frame) throws IOException {

        MatOfByte buffer = new MatOfByte();

        Imgcodecs.imencode(".png", frame, buffer);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.toArray());

        File newFile = new File("test.png");
        FileOutputStream fos = new FileOutputStream(newFile);
        int data;

        while((data = byteArrayInputStream.read()) != -1)
        {
            char ch = (char)data;
            try {
                fos.write(ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fos.flush();
        fos.close();
    }

    /**
     * Creates a Mat from a list of mats and select the best part from each mat and put it to one picture with all
     * the best parts.
     *
     * @param frames the list containing the mats.
     * @param divide the number of wanted subsections, divide = 4 gives 4 rows and 4 columns
     * @return a Mat with all the best parts for all different frames
     */
    public static Mat createImage(List<Mat> frames, int divide) {

        List<List<MatPos>> varianceLists = new LinkedList<>();

        for(Mat mat : frames) {
            varianceLists.add(getVarianceList(mat, divide));
        }

        MatPos[][] list = new MatPos[divide][divide];

        for(List<MatPos> l : varianceLists) {

            for (MatPos matPos : l) {

                System.out.println(matPos.getVar());

                double variance = matPos.getVar();

                if(list[matPos.getY()][matPos.getX()] == null) {
                    list[matPos.getY()][matPos.getX()] = matPos;
                }

                else if(list[matPos.getY()][matPos.getX()].getVar() < variance) {

                    list[matPos.getY()][matPos.getX()] = matPos;
                }
            }
        }

        Mat outMat = mergeMats(list, divide);

        try {
            mat2Image(outMat);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        List<Mat> colList = new LinkedList<>();
        Mat res = new Mat();

        for(int i = 0; i < divide; i++) {

            Mat row = new Mat();
            List<Mat> rowList = new LinkedList<>();

            for(int j = 0; j < divide; j++) {

                Mat m = mats[i][j].getMat();

             //   System.out.println("rows: " + m.rows() + " cols: " + m.cols() + " type: " + m.type() + " variance: " + mats[i][j].getVar());
                rowList.add(m);
            }

            Core.vconcat(rowList, row);
            colList.add(row);
        }

        Core.hconcat(colList, res);

        return res;
    }

    /**
     * Create a black and white version of a image.
     *
     * @param imageMat the image in for of a mat
     * @return the same mat but in black and white
     */
    public static Mat makeBlackAndWhite(Mat imageMat) {

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

        for(Mat mat : frames) {
            double var = getVariance(mat);
            pq.add(new MatPos(mat, var));
        }

        for(int i = 0; i < size; i++) {
            Mat mat = pq.poll().getMat();
            bestFrames.add(mat);
        }

        return bestFrames;
    }
}
