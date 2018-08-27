package facturas.billcounters;

import facturas.io.PropertiesReader;

/**
 * Instantiates the BillCounter indicated in the properties.
 *
 * @author Hector Enrique Diaz Hernandez
 */
public class BillCounterFactory {

    /**
     * Sets uri and fixed period from parameters.
     *
     * @param properties the properties read from file
     * @return AbstractBillCounter the BillCounter indicated in the properties
     */
    public AbstractBillCounter getBillCounter(PropertiesReader properties) {
        String billCounterType = properties.getProperty("billcounter.implementation");

        if (billCounterType == null) {
            return null;
        }

        if (billCounterType.equalsIgnoreCase("recursive")) {
            RecursiveBillCounter billCounter = new RecursiveBillCounter();

            return billCounter;
        } else if (billCounterType.equalsIgnoreCase("fixed")) {
            FixedPeriodBillCounter billCounter = new FixedPeriodBillCounter(properties.getIntegerProperty("billcounter.fixed.period"));

            return billCounter;
        }

        return null;
    }
}
