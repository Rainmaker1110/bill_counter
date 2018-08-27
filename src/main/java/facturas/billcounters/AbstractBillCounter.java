package facturas.billcounters;

import facturas.exceptions.InvalidRequestException;
import facturas.net.SimpleHttpRequestHandler;
import org.apache.log4j.Logger;

import java.time.LocalDate;

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

    @Override
    public String toString() {
        String message = "For ID \"" + id + "\" are: ";

        message += totalBills + " bills\n";
        message += "Between: " + start.format(ISO_LOCAL_DATE) + " - " + finish.format(ISO_LOCAL_DATE);
        message += "\nRequests made: " + totalRequests;

        return message;
    }
}
