package facturas.net;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.junit.jupiter.api.Assertions.*;

class SimpleHttpRequestHandlerTest {

    private SimpleHttpRequestHandler tester = new SimpleHttpRequestHandler();

    @Test
    void executeGetRequest() {
        try {
            tester.executeGetRequest("http://34.209.24.195/facturas", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(400, tester.getResponseStatus(), tester.getResponseContent());

        try {
            tester.executeGetRequest("http://34.209.24.195/facturas", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(400, tester.getResponseStatus(), tester.getResponseContent());

        Map<String, String> parameters = new Hashtable<>();

        parameters.put("id", "1f1bcc03-5fa9-4e73-a150-79a569f912d9");
        parameters.put("start", "2017-01-01");
        parameters.put("finish", "2017-01-20");

        try {
            tester.executeGetRequest("http://34.209.24.195/facturas", parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, tester.getResponseStatus(), tester.getResponseContent());

        parameters.clear();

        parameters.put("id", "1f1bcc03-5fa9-4e73-a150-79a569f912d9");
        parameters.put("start", "2017-01-01");

        try {
            tester.executeGetRequest("http://34.209.24.195/facturas", parameters);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(400, tester.getResponseStatus(), tester.getResponseContent());

        parameters.clear();

        parameters.put("v", "7VykSxTeQLU");

        try {
            tester.executeGetRequest("http://www.youtube.com/watch", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, tester.getResponseStatus(), tester.getResponseContent());

        try {
            tester.executeGetRequest("http://www.google.com", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, tester.getResponseStatus(), tester.getResponseContent());
    }

    @Test
    void executePostRequest() {
        try {
            tester.executePostRequest("http://34.209.24.195/facturas", null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Las peticiones post a ese servicio devuelve 404
        assertEquals(404, tester.getResponseStatus(), tester.getResponseContent());
    }
}