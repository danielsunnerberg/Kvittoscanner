package utilities;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ImageReader {

    private static final Logger logger = LogManager.getLogger(ImageReader.class);


    public static List<String> readImages(String directory){
        List<String> filePaths = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(Paths.get(directory))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    filePaths.add(filePath.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaths;
    }

    public static List<Float> getExposureTimes(List<String> images){
        List<Float> expTimes = new ArrayList<>();
        for (String img : images){
            expTimes.add(getExposureTimes(new File(img)));
        }
        return expTimes;
    }

    private static float getExposureTimes(File file){
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file);

            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

            float expTimes = directory.getFloatObject(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            logger.info("Exposure time for: " + file + " is: " + expTimes);
            return expTimes;

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }



}
