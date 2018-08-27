package facturas.billcounters;

import facturas.RecursiveBillCounter;
import facturas.io.PropertiesReader;

public class BillCounterFactory {

    public AbstractBillCounter getBillCounter(PropertiesReader properties) {
        String billCounterType = properties.getProperty("billcounter.implementation");

        if (billCounterType == null) {
            return null;
        }

        String uri = properties.getProperty("billcounter.uri");

        if (billCounterType.equalsIgnoreCase("recursive")) {
            RecursiveBillCounter billCounter = new RecursiveBillCounter(uri);

            return billCounter;
        } else if (billCounterType.equalsIgnoreCase("fixed")) {
            FixedPeriodBillCounter billCounter = new FixedPeriodBillCounter(uri, properties.getIntegerProperty("billcounter.fixed.period"));

            return billCounter;
        }

        return null;
    }
}
