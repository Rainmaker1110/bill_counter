package facturas.billcounters;

import facturas.exceptions.InvalidRequestException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Implements a fixed period algorithm for making request.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class FixedPeriodBillCounter extends AbstractBillCounter {
    private int fixedPeriod;

    static {
        log = Logger.getLogger(FixedPeriodBillCounter.class.getName());
    }

    /**
     * Stablish properties to invalid state.
     */
    public FixedPeriodBillCounter() {
        uri = null;

        fixedPeriod = 0;
    }

    /**
     * Sets uri and fixed period from parameters.
     *
     * @param uri         the uri to make the requests
     * @param fixedPeriod the fixed period range
     */
    public FixedPeriodBillCounter(String uri, int fixedPeriod) {
        this.uri = uri;
        this.fixedPeriod = fixedPeriod;
    }

    @Override
    public void countBills(String id, LocalDate start, LocalDate finish) throws InvalidRequestException {
        if (uri == null || fixedPeriod == 0) {
            throw new IllegalStateException("URI is null or fixedPeriod is 0");
        }

        if (start.isAfter(finish)) {
            throw new IllegalStateException("start date is after finish date");
        }

        this.id = id;

        this.start = start;
        this.finish = finish;

        totalRequests = 0;

        LocalDate relativeStart = start;
        LocalDate relativeFinish = start.plusDays(fixedPeriod);

        while (relativeFinish.isBefore(finish)) {
            log.debug("relativeFinish: " + relativeFinish.format(ISO_LOCAL_DATE));

            totalBills = getBills(relativeStart, relativeFinish);

            relativeStart = relativeFinish;
            relativeFinish = relativeFinish.plusDays(fixedPeriod);
        }
    }

    /**
     * It makes request based on fixed period. When a request exceeds 100 bills
     * it recursively splits period in half.
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
