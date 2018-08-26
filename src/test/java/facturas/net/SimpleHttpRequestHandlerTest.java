package facturas.net;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class SimpleHttpRequestHandlerTest {

    @Test
    void executeGetRequest() {
        SimpleHttpRequestHandler tester = new SimpleHttpRequestHandler();

        // assert statements
        try {
            tester.executeGetRequest("http://34.209.24.195/facturas", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(400, tester.getResponseStatus(), tester.getResponseContent());
    }

    @Test
    void executePostRequest() {
    }
}