package org.apache.camel.tutorial;

import java.util.List;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeFactory;
import org.apache.camel.Body;
import org.apache.activemq.camel.tutorial.partners.invoice.ObjectFactory;
import org.apache.activemq.camel.tutorial.partners.invoice.Invoice;
import org.apache.activemq.camel.tutorial.partners.invoice.LineItemType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class converts CSV input data to JAXB beans representing the
 * canonical XML format
 */
public class CSVConverterBean {
    private final static Log log = LogFactory.getLog(CSVConverterBean.class);

    public Invoice processCSVInvoice(@Body List<List<String>> data) {
        ObjectFactory factory = new ObjectFactory();
        Invoice invoice = factory.createInvoice();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        boolean headersFound = false;
        try {
            DatatypeFactory dateFactory = DatatypeFactory.newInstance();
            for (List<String> row : data) {
                if(!headersFound) { // Skip the header row
                    headersFound = true;
                    continue;
                }
                LineItemType item = factory.createLineItemType();
                for (int col = 0; col < row.size(); col++) {
                    String cell = row.get(col);
                    switch(col) {
                        case 0: // Quantity
                            item.setQuantity(Integer.parseInt(cell));
                            break;
                        case 1: // Price
                            item.setItemPrice(new BigDecimal(cell).setScale(2, BigDecimal.ROUND_HALF_UP));
                            break;
                        case 2: // Total
                            BigDecimal total = new BigDecimal(cell).setScale(2, BigDecimal.ROUND_HALF_UP);
                            BigDecimal computed = item.getItemPrice().multiply(new BigDecimal(item.getQuantity()));
                            if(!total.equals(computed)) {
                                log.warn("ERROR: COMPUTED TOTAL INVALID ("+item.getItemPrice()+"x"+item.getQuantity()+" <> "+total);
                            }
                            break;
                        case 3: // Date
                            GregorianCalendar calendar = new GregorianCalendar();
                            calendar.setTime(sdf.parse(cell));
                            item.setOrderDate(dateFactory.newXMLGregorianCalendar(calendar));
                            break;
                        case 4: // ID
                            item.setProductId(Long.parseLong(cell));
                            break;
                        case 5: // Name
                            item.setDescription(cell);
                            break;
                    }
                }
                invoice.getLineItem().add(item);
            }
        } catch (Exception e) {
            log.error("Unable to import CSV invoice", e);
            throw new RuntimeException("Unable to import CSV invoice", e);
        }
        return invoice;
    }
}
