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
     * @param fixedPeriod the fixed period range
     */
    public FixedPeriodBillCounter(int fixedPeriod) {
        this.fixedPeriod = fixedPeriod;
    }

    @Override
    public void countBills(String uri, String id, LocalDate start, LocalDate finish) throws InvalidRequestException {
        if (uri == null) {
            throw new IllegalArgumentException("URI is null");
        }

        if (start == null) {
            throw new IllegalArgumentException("start is null");
        }

        if (finish == null) {
            throw new IllegalArgumentException("finish is null");
        }

        if (start.isAfter(finish)) {
            throw new IllegalArgumentException("start date is after finish date");
        }

        if (fixedPeriod == 0 || fixedPeriod < 0) {
            throw new IllegalStateException("fixedPeriod <= 0");
        }

        this.uri = uri;

        this.id = id;

        this.start = start;
        this.finish = finish;

        totalRequests = 0;
        totalBills = 0;

        LocalDate relativeStart = start;
        LocalDate relativeFinish = start.plusDays(fixedPeriod);

        while (relativeFinish.isBefore(finish)) {
            totalBills += getBills(relativeStart, relativeFinish);

            relativeStart = relativeFinish.plusDays(1);
            relativeFinish = relativeFinish.plusDays(fixedPeriod);
        }

        totalBills += getBills(relativeStart, finish);
    }

    /**
     * It makes request based on fixed period. When a request exceeds 100 bills
     * it recursively splits period in half.
     *
     * @param start  the start date for search
     * @param finish the end date for search
     * @return int the bills between the dates
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
            log.debug("Integer result");

            return Integer.parseInt(content);
        } else { // Split dates in half
            log.debug("More than 100 bills");

            LocalDate half = dateSlicer(start, finish);

            log.debug("Half of date: " + half.format(ISO_LOCAL_DATE));

            // Subtract a day in half date because we are already counting 1 day in the "start" date.
            return getBills(start, half.minusDays(1)) + getBills(half, finish);
        }
    }
}
