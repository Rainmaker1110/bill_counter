package facturas.main;

import facturas.BillCounter;
import facturas.exceptions.InvalidRequestException;
import org.apache.log4j.Logger;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class BillCounterMain {
    private static Logger log; // LOGGER

    static {
        log = Logger.getLogger(BillCounterMain.class.getName());
    }

    public static void main(String[] args) {
        log.debug("Argument 0: " + args[0]);
        log.debug("Argument 1: " + args[1]);

        BillCounter billCounter = new BillCounter();

        try {
            billCounter.countBills(LocalDate.parse(args[0], ISO_LOCAL_DATE), LocalDate.parse(args[1], ISO_LOCAL_DATE));

            System.out.println(billCounter);
        } catch (InvalidRequestException e) {
            log.error("Invalid request send");

            e.printStackTrace();
        }
    }
}
