package utilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.io.File;

public class ComputerVision {

    public static String executePostBinary(String fileName){

        File fi = new File(fileName);
        byte [] fileContent = null;
        try {
            fileContent = Files.readAllBytes(fi.toPath());
        } catch(java.io.IOException ex){
            ex.printStackTrace();
        }

        HttpURLConnection connection = null;
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")) {

            // load a properties file containing apikey
            prop.load(input);

            //Create connection and set headers
            URL url = new URL("https://westus.api.cognitive.microsoft.com/vision/v1.0/ocr?language=sv&detectOrientation=true");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/octet-stream");

            connection.setRequestProperty("Host",
                    "westus.api.cognitive.microsoft.com");

            connection.setRequestProperty("Ocp-Apim-Subscription-Key",
                    prop.getProperty("computerVisionKeyWorking"));

            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.write(fileContent);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

}
