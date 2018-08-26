package facturas.net;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class handles the request execution in both GET and POST.
 * Retrieves status and content from the request.
 *
 * @author Hector Enrique Diaz Hernandez
 */

public class HttpRequestHandler {
    private static Logger log; // LOGGER

    private int responseStatus;

    private String responseContent;

    private CloseableHttpClient httpClient;

    static {
        log = Logger.getLogger(HttpRequestHandler.class.getName());
    }

    public HttpRequestHandler() {
        httpClient = HttpClients.createDefault();
    }

    // Getters and setters
    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    /**
     * Executes a GET request appending parameters into the uri.
     *
     * @param uri    the uri to make the request
     * @param params the parameters to put in the request
     */
    public void executeGetRequest(String uri, Map<String, String> params) throws URISyntaxException, IOException {
        log.info("GET request");

        HttpGet httpGet;

        URIBuilder uriBuilder;

        uriBuilder = new URIBuilder(uri);

        // Method reference suggested by IDE, that's new for me!
        params.forEach(uriBuilder::setParameter);

        httpGet = new HttpGet(uriBuilder.build());

        executeRequest(httpGet);
    }

    /**
     * Executes a POST request appending parameters into request body.
     *
     * @param uri    the uri to make the request
     * @param params the parameters to put in the request
     */
    public void executePostRequest(String uri, Map<String, String> params) throws IOException {
        log.info("POST request");

        HttpPost httpPost;

        List<NameValuePair> paramList;

        httpPost = new HttpPost(uri);

        paramList = new ArrayList<>();

        params.forEach((String key, String value) -> paramList.add(new BasicNameValuePair(key, value)));

        httpPost.setEntity(new UrlEncodedFormEntity(paramList));

        executeRequest(httpPost);
    }

    /**
     * Executes the request.
     *
     * @param httpRequest the specified request to execute
     */
    private void executeRequest(HttpRequestBase httpRequest) throws IOException {
        log.info("Executing request");

        log.debug("Request URI: " + httpRequest.getURI().toString());

        CloseableHttpResponse response = httpClient.execute(httpRequest);

        readResponse(response);

        try {
            response.close();
        } catch (IOException e) {
            log.error("Error while trying to close the response");

            e.printStackTrace();
        }
    }

    /**
     * Reads the status and content from the response.
     *
     * @param response the response from the request
     */
    private void readResponse(CloseableHttpResponse response) throws IOException {
        log.info("Reading response");

        responseStatus = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int length;

        while ((length = entity.getContent().read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        responseContent = result.toString("UTF-8");

        log.debug("Request status: " + responseStatus);
        log.debug("Request response: " + responseContent);
    }

}
