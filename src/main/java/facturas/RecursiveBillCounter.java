package facturas;

import facturas.billcounters.AbstractBillCounter;
import facturas.exceptions.InvalidRequestException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Implements a recursive algorithm splitting the dates in halves.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class RecursiveBillCounter extends AbstractBillCounter {
    static {
        log = Logger.getLogger(RecursiveBillCounter.class.getName());
    }

    /**
     * Stablish properties to invalid state.
     */
    public RecursiveBillCounter() {
        uri = null;
    }

    /**
     * Sets uri from parameter.
     *
     * @param uri the uri to make the requests
     */
    public RecursiveBillCounter(String uri) {
        this.uri = uri;
    }

    /**
     * Resets the total requests to 0 and executes recursive algorithm.
     *
     * @param id     the id to retrieve the bill count
     * @param start  the start date for search
     * @param finish the end date for search
     */
    @Override
    public void countBills(String id, LocalDate start, LocalDate finish) throws InvalidRequestException {
        if (uri == null) {
            throw new IllegalStateException("URI is null");
        }

        this.id = id;

        this.start = start;
        this.finish = finish;

        totalRequests = 0;

        totalBills = getBills(start, finish);
    }

    /**
     * Recursive algorithm. Splits the dates in half when there are more than 100 bills.
     * The base case is when there are a specific number of bills.
     *
     * @param start  the start date for search
     * @param finish the end date for search
     */
    private int getBills(LocalDate start, LocalDate finish) throws InvalidRequestException {
        // Put the parameters into a map
        Map<String, String> parameters = new Hashtable<>();

        parameters.put("id", id);
        parameters.put("start", start.format(ISO_LOCAL_DATE));
        parameters.put("finish", finish.format(ISO_LOCAL_DATE));

        log.info("Getting bills");

        log.debug("From: " + start.format(ISO_LOCAL_DATE));
        log.debug("To: " + finish.format(ISO_LOCAL_DATE));

        try {
            requestHandler.executeGetRequest(uri, parameters);
        } catch (URISyntaxException e) {
            log.error("Malformed URI");

            e.printStackTrace();
        } catch (IOException e) {
            log.error("Error while executing the request");

            e.printStackTrace();
        }

        if (requestHandler.getResponseStatus() != STATUS_OK) {
            log.error("Request failed");

            throw new InvalidRequestException(requestHandler.getResponseContent());
        }

        String content = requestHandler.getResponseContent();

        // A successful request where made
        totalRequests++;

        log.debug("Requests made: " + totalRequests);

        // The base case, when the response content is a number
        if (content.chars().allMatch(Character::isDigit)) {
            return Integer.parseInt(content);
        } else { // Split dates in half
            LocalDate half = dateSlicer(start, finish);

            log.debug("Half of date: " + half.format(ISO_LOCAL_DATE));

            // Subtract a day in half date because we are already counting 1 day in the "start" date.
            return getBills(start, half.minusDays(1)) + getBills(half, finish);
        }
    }

}