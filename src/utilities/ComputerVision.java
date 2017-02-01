package utilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class ComputerVision {
    public static String executePost(String urlParameters) {
        HttpURLConnection connection = null;
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("config.properties")){

            // load a properties file
            prop.load(input);

            //Create connection
            URL url = new URL("https://westus.api.cognitive.microsoft.com/vision/v1.0/ocr?language=sv&detectOrientation=true");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setRequestProperty("Host",
                    "westus.api.cognitive.microsoft.com");

            connection.setRequestProperty("Ocp-Apim-Subscription-Key",
                    prop.getProperty("computerVisionKeyWorking"));

            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
