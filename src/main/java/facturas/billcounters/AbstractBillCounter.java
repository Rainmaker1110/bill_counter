package facturas.billcounters;

import facturas.exceptions.InvalidRequestException;
import facturas.net.SimpleHttpRequestHandler;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Common implementation of BIllCounter.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public abstract class AbstractBillCounter {
    protected static final int STATUS_OK = 200;

    protected static Logger log; // LOGGER

    protected static SimpleHttpRequestHandler requestHandler;

    protected int totalRequests;
    protected int totalBills;

    protected String uri;
    protected String id;

    protected LocalDate start;
    protected LocalDate finish;

    static {
        requestHandler = new SimpleHttpRequestHandler();
    }

    // Getters and setters
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getTotalBills() {
        return totalBills;
    }

    public abstract void countBills(String uri, String id, LocalDate start, LocalDate finish) throws InvalidRequestException;

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

    @Override
    public String toString() {
        String message = "For ID \"" + id + "\" are: ";

        message += totalBills + " bills\n";
        message += "Between: " + start.format(ISO_LOCAL_DATE) + " - " + finish.format(ISO_LOCAL_DATE);
        message += "\nRequests made: " + totalRequests;

        return message;
    }
}
