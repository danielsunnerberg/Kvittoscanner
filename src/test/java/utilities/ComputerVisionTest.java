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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComputerVisionTest {

    Map<String,String> result;
    Map<String,Integer> trialResult;
    String expectedDate;
    int expectedDateOccurences;
    String expectedSum;
    int expectedSumOccurences;
    String expectedVat;
    int expectedVatOccurences;
    String receiptName = "withoutreflection";
    String receiptNameSuffix = ".jpg";

    @Before
    public void setUp(){
        String fileName = ComputerVision.class.getResource("/images/"+receiptName+receiptNameSuffix).getFile();
        String resourceFile = ComputerVision.class.getResource("/imageProperties/"+receiptName+".properties").getFile();

        result = ComputerVision.getRelevantEntries(fileName);
        trialResult = ComputerVision.getRelevantEntriesWithoutFiltration(fileName);


        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(resourceFile)) {
            prop.load(input);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        expectedDate = prop.getProperty("expectedDate");
        expectedDateOccurences = Integer.parseInt(prop.getProperty("expectedDateOccurences"));
        expectedSum = prop.getProperty("expectedSum");
        expectedSumOccurences = Integer.parseInt(prop.getProperty("expectedSumOccurences"));
        expectedVat = prop.getProperty("expectedVat");
        expectedVatOccurences = Integer.parseInt(prop.getProperty("expectedVatOccurences"));
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

    @Test
    public void testVatOccurencesAreCorrect(){
        int vat = trialResult.get(expectedVat);
        Assert.assertEquals(expectedVatOccurences, vat);
    }

    @Test
    public void testDateOccurencesAreCorrect(){
        int date = trialResult.get(expectedDate);
        Assert.assertEquals(expectedDateOccurences, date);
    }

    @Test
    public void testSumOccurencesAreCorrect(){
        int sum = trialResult.get(expectedSum);
        Assert.assertEquals(expectedSumOccurences, sum);
    }
}
