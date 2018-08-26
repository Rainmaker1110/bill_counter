package facturas;

import facturas.exceptions.InvalidRequestException;
import facturas.net.HttpRequestHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorCode;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class BillCounter {
    private static Logger log; // LOGGER

    private static HttpRequestHandler requestHandler;

    private String uri;
    private String id;

    static {
        log = Logger.getLogger(BillCounter.class.getName());

        requestHandler = new HttpRequestHandler();
    }

    public BillCounter(String uri, String id) {
        this.uri = uri;
        this.id = id;
    }

    public int getBills(LocalDate start, LocalDate finish) throws InvalidRequestException {
        String content;

        Map<String, String> parameters = new Hashtable<>();

        log.info("Getting bills");

        log.debug("From: " + start.format(ISO_LOCAL_DATE));
        log.debug("To: " + finish.format(ISO_LOCAL_DATE));

        parameters.put("id", id);
        parameters.put("start", start.format(ISO_LOCAL_DATE));
        parameters.put("finish", finish.format(ISO_LOCAL_DATE));

        try {
            requestHandler.executeGetRequest(uri, parameters);
        } catch (URISyntaxException e) {
            log.error("Malformed URI");

            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error while executing the request");

            e.printStackTrace();
        }

        if (requestHandler.getResponseStatus() != 200) {
            log.error("Request failed");

            throw new InvalidRequestException(requestHandler.getResponseContent());
        }

        content = requestHandler.getResponseContent();

        if (content.chars().allMatch(Character::isDigit)) {
            return Integer.parseInt(content);
        } else {
            LocalDate half = dateSlicer(start, finish);

            return getBills(start, half.minusDays(1)) + getBills(half, finish);
        }
    }

    private LocalDate dateSlicer(LocalDate start, LocalDate end) {
        long halfOfDays = ChronoUnit.DAYS.between(start, end.plusDays(1)) / 2;

        log.debug("Half of days: " + halfOfDays);

        return start.plusDays(halfOfDays);
    }
}
