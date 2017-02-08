package utilities;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComputerVisionTest {

    Map<String,String> result;
    String expectedDate;
    String expectedSum;
    String expectedVat;
    String receiptName = "kvitto2";

    @Before
    public void setUp(){
        String fileName = ComputerVision.class.getResource("/images/"+receiptName+".gif").getFile();
        String resourceFile = ComputerVision.class.getResource("/imageProperties/"+receiptName+".properties").getFile();

        result = ComputerVision.getRelevantEntries(fileName);
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(resourceFile)) {
            // load a properties file containing expected values
            prop.load(input);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        expectedDate = prop.getProperty("expectedDate");
        expectedSum = prop.getProperty("expectedSum");
        expectedVat = prop.getProperty("expectedVat");
    }

    @Test
    public void testSumIsCorrect() {

        String totalSum = result.get("TotalSum");
        Assert.assertEquals(expectedSum, totalSum);
    }

    @Test
    public void testDateIsCorrect(){
        String date = result.get("Date");
        Assert.assertEquals(expectedDate, date);
    }

    @Test
    public void testVatIsCorrect(){
        String vat = result.get("Vat");
        Assert.assertEquals(expectedVat, vat);
    }
}
