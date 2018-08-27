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
 * Implements a recursive algorithm splitting the dates in halves.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class RecursiveBillCounter extends BaseHalfDateBillCounter {
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
     * Resets the total requests to 0 and executes recursive algorithm.
     *
     * @param id     the id to retrieve the bill count
     * @param start  the start date for search
     * @param finish the end date for search
     */
    @Override
    public void countBills(String uri, String id, LocalDate start, LocalDate finish) throws InvalidRequestException {
        super.countBills(uri, id, start, finish);

        totalBills = getBills(start, finish);
    }
}
