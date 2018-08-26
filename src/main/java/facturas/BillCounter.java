package facturas;

import facturas.exceptions.InvalidRequestException;
import facturas.io.PropertiesReader;
import facturas.net.SimpleHttpRequestHandler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * Implements a recursive algorithm splitting the dates in halfs
 * when there are more than 100 bills.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class BillCounter {
    private static final int STATUS_OK = 200;

    private static final String PROPERTIES_FILE = "billcounter.properties";

    private static Logger log; // LOGGER

    private static SimpleHttpRequestHandler requestHandler;

    private int totalRequests;
    private int totalBills;

    private String uri;
    private String id;

    private LocalDate start;
    private LocalDate finish;

    static {
        log = Logger.getLogger(BillCounter.class.getName());

        requestHandler = new SimpleHttpRequestHandler();
    }

    /**
     * Tries to get uri and id from config file.
     */
    public BillCounter() {
        PropertiesReader properties = new PropertiesReader();

        if (properties.readFromFile(PROPERTIES_FILE)) {
            uri = properties.getProperty("uri");
        } else {
            log.info("Setting default properties");

            uri = null;
        }
    }

    /**
     * Sets uri and id from parameters.
     *
     * @param uri the uri to make the requests
     * @param id  the id for search its bills
     */
    public BillCounter(String uri, String id) {
        this.uri = uri;
        this.id = id;
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

    /**
     * Resets the total requests to 0 and executes recursive algorithm.
     *
     * @param id     the id to retrieve the bill count
     * @param start  the start date for search
     * @param finish the end date for search
     */
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

        // A succeful request where made
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

    /**
     * Splits a date range in half.
     * Uses the {@link java.time.Period} between the {@link LocalDate} start and end parameters.
     *
     * @param start the start date for search
     * @param end   the end date for search
     */
    private LocalDate dateSlicer(LocalDate start, LocalDate end) {
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
