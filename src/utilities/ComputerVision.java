package utilities;

import com.google.gson.*;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.io.File;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class ComputerVision {


    /*
    Map<String,Integer> trialResult = ComputerVision.getRelevantEntriesWithoutFiltration("kvitto.png");
        trialResult = ComputerVision.sortByValue(trialResult);
        for (HashMap.Entry<String,Integer> entry : trialResult.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
     */
    /*
    Returns a Map with the best guess according to Bokios system for TotalSum, Vat and Date.
    Can easily be extended to include more guesses.
     */
    public static Map<String, String> getRelevantEntries(String fileName){
        String jsonResult = executePostBinary(fileName);
        String bokioResult = callBokioJsonInterpreterEndpoint(jsonResult);
        return filterInterpretedOutput(bokioResult);
    }

    /*
    Returns a Map with all words and the number of times it occurs in the json response
     */
    public static Map<String,Integer> getRelevantEntriesWithoutFiltration(String fileName){
        String jsonResult = executePostBinary(fileName);
        String bokioResult = callBokioJsonInterpreterEndpoint(jsonResult);
        return processWholeString(bokioResult);
    }

    private static String executePostBinary(String fileName){
        HttpClient httpclient = HttpClients.createDefault();

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {

            // load byte data from fileName
            byte[] fileContent = getByteArrayFromFile(fileName);

            // load a properties file containing apikey
            prop.load(input);

            URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/vision/v1.0/ocr?language=sv&detectOrientation=true");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Host", "westus.api.cognitive.microsoft.com");
            request.setHeader("Ocp-Apim-Subscription-Key",
                    prop.getProperty("computerVisionKeyWorking"));


            // Request body
            ByteArrayEntity reqEntity = new ByteArrayEntity(fileContent);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                return EntityUtils.toString(entity);
            }
        }
        catch (Exception e)
        {
            System.out.println("catch: "+e.getMessage());
        }

        return null;
    }

    private static String callBokioJsonInterpreterEndpoint(String jsonResult){
        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("http://bokio-staging.azurewebsites.net/api/v1/ocr");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");

            // Request body
            StringEntity reqEntity = new StringEntity(jsonResult, ContentType.APPLICATION_JSON);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                return EntityUtils.toString(entity);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private static Map<String,String> filterInterpretedOutput(String bokioResult){
        JsonObject jsonObject = new JsonParser().parse(bokioResult).getAsJsonObject();
        JsonElement el = jsonObject.get("InterpreterResult");
        JsonObject innerJsonObject = new JsonParser().parse(el.toString()).getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entries = innerJsonObject.entrySet();
        Map<String, String> resultMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> innerEntry: entries) {
            String keyVal = innerEntry.getKey();

            if (keyVal.equals("TotalSum") || keyVal.equals("Vat") || keyVal.equals("Date")) {
                JsonObject jsonChild = new JsonParser().parse(innerEntry.getValue().toString()).getAsJsonObject();
                JsonElement childEl = jsonChild.get("Text");
                String childVal = childEl.getAsString();

                switch (keyVal) {
                    case "TotalSum":
                        resultMap.put(keyVal, childVal);
                        break;
                    case "Vat":
                        resultMap.put(keyVal, childVal);
                        break;
                    case "Date":
                        resultMap.put(keyVal, childVal);
                        break;
                }
            }
        }
        return resultMap;
    }

    private static Map<String,Integer> processWholeString(String bokioResult){
        JsonObject jsonObject = new JsonParser().parse(bokioResult).getAsJsonObject();
        JsonElement el = jsonObject.get("InterpreterResult");
        JsonObject innerJsonObject = new JsonParser().parse(el.toString()).getAsJsonObject();

        Set<Map.Entry<String, JsonElement>> entries = innerJsonObject.entrySet();
        Map<String, Integer> resultMap = new HashMap<>();

        for (Map.Entry<String, JsonElement> innerEntry: entries) {
            String keyVal = innerEntry.getKey();
            if (keyVal.equals("All")) {
                JsonArray jsonChild = new JsonParser().parse(innerEntry.getValue().toString()).getAsJsonArray();
                JsonElement childEl = jsonChild.get(0);
                int length = jsonChild.size();

                for (JsonElement element: jsonChild) {
                    JsonObject jsonObj = new JsonParser().parse(element.toString()).getAsJsonObject();
                    String childVal = jsonObj.get("Text").getAsString();
                    if (resultMap.containsKey(childVal)) {
                        int temp = resultMap.get(childVal).intValue();
                        resultMap.put(childVal, temp + 1);
                    } else {
                        resultMap.put(childVal, 1);
                    }
                }
            }
        }
        return resultMap;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private static byte[] getByteArrayFromFile(String fileName){
        byte[] fileContent = null;
        if (fileName != null) {
            File fi = new File(fileName);
            try {
                fileContent = Files.readAllBytes(fi.toPath());
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
        return fileContent;
    }

}
