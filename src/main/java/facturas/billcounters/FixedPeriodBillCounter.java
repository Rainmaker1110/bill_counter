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
public class FixedPeriodBillCounter extends BaseHalfDateBillCounter {
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
        super.countBills(uri, id, start, finish);

        if (fixedPeriod == 0 || fixedPeriod < 0) {
            throw new IllegalStateException("fixedPeriod <= 0");
        }

        LocalDate relativeStart = start;
        LocalDate relativeFinish = start.plusDays(fixedPeriod);

        while (relativeFinish.isBefore(finish)) {
            totalBills += getBills(relativeStart, relativeFinish);

            relativeStart = relativeFinish.plusDays(1);
            relativeFinish = relativeFinish.plusDays(fixedPeriod);
        }

        totalBills += getBills(relativeStart, finish);
    }

}
