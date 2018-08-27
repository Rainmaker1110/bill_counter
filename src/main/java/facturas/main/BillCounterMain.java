package facturas.main;

import facturas.billcounters.AbstractBillCounter;
import facturas.billcounters.BillCounterFactory;

import facturas.exceptions.InvalidRequestException;

import facturas.io.PropertiesReader;

import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class BillCounterMain {
    public static final String PROPERTIES_FILE = "billcounter.properties";

    private static Logger log; // LOGGER

    static {
        log = Logger.getLogger(BillCounterMain.class.getName());
    }

    public static void main(String[] args) {
        // Needs 4 parameters
        if (args.length != 4) {
            System.err.println("Use: java BillCounterMain <uri> <id> <start> <finish>");

            System.exit(-1);
        }

        log.debug("Argument 0: " + args[0]);
        log.debug("Argument 1: " + args[1]);
        log.debug("Argument 2: " + args[2]);
        log.debug("Argument 3: " + args[3]);

        String uri = args[0];
        String id = args[1];
        String startDate = args[2];
        String finishDate = args[3];

        try {
            PropertiesReader properties = new PropertiesReader();

            if (!properties.readFromFile(PROPERTIES_FILE)) {
                System.err.println("No billcounter.properties file found.");

                System.exit(-1);
            }

            // Gets the correct BillCounter
            BillCounterFactory counterFactory = new BillCounterFactory();

            AbstractBillCounter billCounter = counterFactory.getBillCounter(properties);

            if (billCounter == null) {
                System.err.println("Unknown implementation");

                System.exit(-1);
            }

            LocalDate start = LocalDate.parse(startDate, ISO_LOCAL_DATE);
            LocalDate finish = LocalDate.parse(finishDate, ISO_LOCAL_DATE);

            billCounter.countBills(uri, id, start, finish);

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
