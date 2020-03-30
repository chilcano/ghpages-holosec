package uk.gov.hmrc.wso2.rabbitmq.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.task.Task;
import org.apache.synapse.util.PayloadHelper;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;

public class PlaceStockOrderTask implements Task, ManagedLifecycle {
	private Log log = LogFactory.getLog(PlaceStockOrderTask.class);
	private String to;
	private String stockFile;
	private SynapseEnvironment synapseEnvironment;

	public void execute() {
		log.debug("PlaceStockOrderTask begin");

		if (synapseEnvironment == null) {
			log.error("Synapse Environment not set");
			return;    }

		if (to == null) {
			log.error("to not set");
			return;    }

		File existFile = new File(stockFile);

		if(!existFile.exists()) {
			log.debug("waiting for stock file");
			return;    }

		try {

			// file format IBM,100,120.50

			BufferedReader reader = new BufferedReader(new FileReader(stockFile));
			String line = null;

			while( (line = reader.readLine()) != null){
				line = line.trim();

				if(line == "") {
					continue;
				}

				String[] split = line.split(",");
				String symbol = split[0];
				String quantity = split[1];
				String price = split[2];
				MessageContext mc = synapseEnvironment.createMessageContext();
				mc.setTo(new EndpointReference(to));
				mc.setSoapAction("urn:placeOrder");
				mc.setProperty("OUT_ONLY", "true");
				OMElement placeOrderRequest = createPlaceOrderRequest(symbol, quantity, price);
				PayloadHelper.setXMLPayload(mc, placeOrderRequest);
				synapseEnvironment.injectMessage(mc);
				log.info("placed order symbol:" + symbol + " quantity:" + quantity + " price:" + price);
			}

			reader.close();
		}

		catch (IOException e) {
			throw new SynapseException("error reading file", e);
		}

		File renamefile = new File(stockFile);
		renamefile.renameTo(new File(stockFile + "." + System.currentTimeMillis()));
		log.debug("PlaceStockOrderTask end");  }

	public static OMElement createPlaceOrderRequest(String symbol, String qty, String purchPrice) {
		OMFactory factory   = OMAbstractFactory.getOMFactory();
		OMNamespace ns      = factory.createOMNamespace("http://services.samples/xsd", "m0");
		OMElement placeOrder= factory.createOMElement("placeOrder", ns);
		OMElement order     = factory.createOMElement("order", ns);
		OMElement price     = factory.createOMElement("price", ns);
		OMElement quantity  = factory.createOMElement("quantity", ns);
		OMElement symb      = factory.createOMElement("symbol", ns);
		price.setText(purchPrice);
		quantity.setText(qty);
		symb.setText(symbol);
		order.addChild(price);
		order.addChild(quantity);
		order.addChild(symb);
		placeOrder.addChild(order);
		return placeOrder;
	}

	public void destroy() {
	}

	public void init(SynapseEnvironment synapseEnvironment) {
		this.synapseEnvironment = synapseEnvironment;
	}

	public SynapseEnvironment getSynapseEnvironment() {
		return synapseEnvironment;
	}

	public void setSynapseEnvironment(SynapseEnvironment synapseEnvironment) {
		this.synapseEnvironment = synapseEnvironment;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getStockFile() {
		return stockFile;
	}

	public void setStockFile(String stockFile) {
		this.stockFile = stockFile;
	}
	
}