package facturas.billcounters;

import facturas.exceptions.InvalidRequestException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Implements base.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public abstract class BaseHalfDateBillCounter extends AbstractBillCounter {
    static {
        log = Logger.getLogger(RecursiveBillCounter.class.getName());
    }

    /**
     * Resets the total requests to 0 and executes recursive algorithm.
     *
     * @param id     the id to retrieve the bill count
     * @param start  the start date for search
     * @param finish the end date for search
     */
    @Override
    public void countBills(String uri, String id, LocalDate start, LocalDate finish) throws InvalidRequestException {
        if (uri == null) {
            throw new IllegalArgumentException("URI is null");
        }

        if (id == null) {
            throw new IllegalArgumentException("id is null");
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

        this.uri = uri;

        this.id = id;

        this.start = start;
        this.finish = finish;

        totalRequests = 0;
        totalBills = 0;
    }

    /**
     * Recursive algorithm. Splits the dates in half when there are more than 100 bills.
     * The base case is when there are a specific number of bills.
     *
     * @param start  the start date for search
     * @param finish the end date for search
     * @return int bills between the dates
     */
    protected int getBills(LocalDate start, LocalDate finish) throws InvalidRequestException {
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

    /**
     * Splits a date range in half.
     * Uses the {@link java.time.Period} between the {@link LocalDate} start and end parameters.
     *
     * @param start the start date for search
     * @param end   the end date for search
     * @return LocalDate the half date between the two dates
     */
    protected LocalDate dateSlicer(LocalDate start, LocalDate end) {
        /* Adds a day to end date because ChronoUnit.DAYS.between method is inclusive for the first parameter
        and exclusive for the second parameter. */
        long halfOfDays = ChronoUnit.DAYS.between(start, end.plusDays(1)) / 2;

        log.debug("Half of days: " + halfOfDays);

        return start.plusDays(halfOfDays);
    }
}
