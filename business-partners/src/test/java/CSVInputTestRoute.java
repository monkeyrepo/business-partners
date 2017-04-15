

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.tutorial.CSVConverterBean;
//import org.apache.camel.tutorial.business_partners.XMLInputTest;

/**
 * Java DSL Route definition for the CSVInputTest test class
 */
public class CSVInputTestRoute extends RouteBuilder {
    public void configure() throws Exception {
        from("direct:CSVstart")
                .unmarshal().csv()
                .pipeline("bean:CSVConverter", "mock:finish");
    }
    public static void main(String args[]) throws Exception {

    	SimpleRegistry reg = new SimpleRegistry();
    	CSVConverterBean converterBean = new CSVConverterBean();
    	reg.put("CSVConverter", converterBean);
    	
    	CSVInputTestRoute csvTestRoute = new CSVInputTestRoute();
    	CamelContext context = new DefaultCamelContext(reg);    	
    	context.addRoutes(csvTestRoute);
    	context.start();
    	
    	MockEndpoint finish = MockEndpoint.resolve(context, "mock:finish");
    	finish.setExpectedMessageCount(1);
    	
    	InputStream in = XMLInputTest.class.getResourceAsStream("/input-customer2.csv");
    	
    	ProducerTemplate template = context.createProducerTemplate();
    	template.sendBody("direct:CSVstart",in);
    	MockEndpoint.assertIsSatisfied(context);
    	System.err.println("Result => " + finish.getExchanges().get(0).getIn().getBody());
    	
    	
    	context.stop();  
    	
    	Thread.sleep(10000);
    	
    }
}