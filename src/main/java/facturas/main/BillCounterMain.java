package facturas.main;

import facturas.BillCounter;
import facturas.exceptions.InvalidRequestException;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class BillCounterMain {
    private static Logger log; // LOGGER

    static {
        log = Logger.getLogger(BillCounterMain.class.getName());
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Use: java BillCounterMain <id> <start> <finish>");

           System.exit(-1);
        }

        log.debug("Argument 0: " + args[0]);
        log.debug("Argument 1: " + args[1]);
        log.debug("Argument 2: " + args[1]);

        try {
            BillCounter billCounter = new BillCounter();

            LocalDate start = LocalDate.parse(args[1], ISO_LOCAL_DATE);
            LocalDate finish = LocalDate.parse(args[2], ISO_LOCAL_DATE);

            billCounter.countBills(args[0], start, finish);

            System.out.println(billCounter);
        } catch (DateTimeParseException e) {
            log.error("An console argument cannot be parsed to a date");

            System.err.println("One of the arguments it's not a valid date");
        } catch (InvalidRequestException e) {
            log.error("Invalid request send");

            e.printStackTrace();
        }
    }
}
