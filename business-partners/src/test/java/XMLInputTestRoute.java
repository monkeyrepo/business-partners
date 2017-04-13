

import java.io.InputStream;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

// This is my version that does not use the spring dependency injection
/**
 * Java DSL Route definition for the XMLInputTest test class
 */

public class XMLInputTestRoute extends RouteBuilder {
	
    public void configure() throws Exception {
        from("direct:start").pipeline("xslt:XMLConverter.xsl", "mock:finish");

    }
    

    public static void main(String args[]) throws Exception {

    	XMLInputTestRoute xmlTestRoute = new XMLInputTestRoute();
    	CamelContext context = new DefaultCamelContext();
    	context.addRoutes(xmlTestRoute);
    	context.start();
    	
    	MockEndpoint finish = MockEndpoint.resolve(context, "mock:finish");
    	finish.setExpectedMessageCount(1);
    	
    	InputStream in = XMLInputTest.class.getResourceAsStream("/input-customer1.xml");
    	
    	ProducerTemplate template = context.createProducerTemplate();
    	template.sendBody("direct:start",in);
    	MockEndpoint.assertIsSatisfied(context);
    	System.err.println(finish.getExchanges().get(0).getIn().getBody());
    	
    	
    	context.stop();  
    	
    	Thread.sleep(10000);
    	
    }
}
