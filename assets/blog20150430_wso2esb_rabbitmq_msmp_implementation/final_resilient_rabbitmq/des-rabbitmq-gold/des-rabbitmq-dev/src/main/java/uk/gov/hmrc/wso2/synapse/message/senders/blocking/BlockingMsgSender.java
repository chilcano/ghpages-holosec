package uk.gov.hmrc.wso2.synapse.message.senders.blocking;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.ServiceGroupContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.SynapseException;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.AnonymousServiceFactory;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.endpoints.AbstractEndpoint;
import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.endpoints.EndpointDefinition;
import org.apache.synapse.transport.nhttp.NhttpConstants;
import org.apache.synapse.util.MessageHelper;

import javax.xml.namespace.QName;

public class BlockingMsgSender {
    public final static String DEFAULT_CLIENT_REPO = "./repository/deployment/client";
    public final static String DEFAULT_AXIS2_XML = "./repository/conf/axis2/axis2_blocking_client.xml";

    private static Log log = LogFactory.getLog(BlockingMsgSender.class);
    private String clientRepository = null;
    private String axis2xml = null;
    private ConfigurationContext configurationContext = null;
    boolean initClientOptions = true;

    /**
     * Initialize the sender using axis2 repo of WSO2
     */
    public BlockingMsgSender() {
    	
    	log.debug(" ~ start constructor.");
    	
        try {
            if (configurationContext == null) {
                configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                        clientRepository != null ? clientRepository : DEFAULT_CLIENT_REPO,
                        axis2xml != null ? axis2xml : DEFAULT_AXIS2_XML);
            }
        } catch (AxisFault e) {
            handleException("Error initializing BlockingMessageSender", e);
        }
    }
    
/*
    public void init() {
        try {
            if (configurationContext == null) {
                configurationContext = ConfigurationContextFactory.createConfigurationContextFromFileSystem(
                        clientRepository != null ? clientRepository : DEFAULT_CLIENT_REPO,
                        axis2xml != null ? axis2xml : DEFAULT_AXIS2_XML);
            }
        } catch (AxisFault e) {
            handleException("Error initializing BlockingMessageSender", e);
        }
    }
*/
    
    public MessageContext send(Endpoint endpoint, MessageContext synapseInMsgCtx) throws Exception {

    	log.debug("send() ~ start sending the message to Backend EP.");

        AbstractEndpoint abstractEndpoint = (AbstractEndpoint) endpoint;
        if (!abstractEndpoint.isLeafEndpoint()) {
        	log.debug("send() ~ endpoint Type not supported.");
            handleException("Endpoint Type not supported");
        }
        abstractEndpoint.executeEpTypeSpecificFunctions(synapseInMsgCtx);
        EndpointDefinition endpointDefinition = abstractEndpoint.getDefinition();

        org.apache.axis2.context.MessageContext axisInMsgCtx = ((Axis2MessageContext) synapseInMsgCtx).getAxis2MessageContext();
        org.apache.axis2.context.MessageContext axisOutMsgCtx =  new org.apache.axis2.context.MessageContext();

        String endpointReferenceValue = null;
        if (endpointDefinition.getAddress() != null) {
            endpointReferenceValue = endpointDefinition.getAddress();
        } else if (axisInMsgCtx.getTo() != null) {
            endpointReferenceValue = axisInMsgCtx.getTo().getAddress();
        } else {
        	log.debug("send() ~ service url, Endpoint or 'To' header is required.");
            handleException("Service url, Endpoint or 'To' header is required");
        }
        axisOutMsgCtx.setTo(new EndpointReference(endpointReferenceValue));

        if (endpointReferenceValue != null && endpointReferenceValue.startsWith(Constants.TRANSPORT_LOCAL)) {
            configurationContext = axisInMsgCtx.getConfigurationContext();
        }

        axisOutMsgCtx.setConfigurationContext(configurationContext);
        axisOutMsgCtx.setEnvelope(axisInMsgCtx.getEnvelope());

        // Fill MessageContext
        BlockingMsgSenderUtils.fillMessageContext(endpointDefinition, axisOutMsgCtx, synapseInMsgCtx);
        if (JsonUtil.hasAJsonPayload(axisInMsgCtx)) {
            JsonUtil.cloneJsonPayload(axisInMsgCtx, axisOutMsgCtx);
        }

        Options clientOptions;
        if (initClientOptions) {
            clientOptions = new Options();
        } else {
            clientOptions = axisInMsgCtx.getOptions();
        }
        
        // Fill Client options
        BlockingMsgSenderUtils.fillClientOptions(endpointDefinition, clientOptions, synapseInMsgCtx);

        AxisService anonymousService = AnonymousServiceFactory.getAnonymousService(
                        null,
                        configurationContext.getAxisConfiguration(),
                        endpointDefinition.isAddressingOn() | endpointDefinition.isReliableMessagingOn(),
                        endpointDefinition.isReliableMessagingOn(),
                        endpointDefinition.isSecurityOn(),
                        false);
        
        anonymousService.getParent().addParameter(SynapseConstants.HIDDEN_SERVICE_PARAM, "true");
        ServiceGroupContext serviceGroupContext = new ServiceGroupContext(configurationContext, (AxisServiceGroup) anonymousService.getParent());
        ServiceContext serviceCtx = serviceGroupContext.getServiceContext(anonymousService);
        
        axisOutMsgCtx.setServiceContext(serviceCtx);

        // Invoke
        boolean isOutOnly = isOutOnly(synapseInMsgCtx, axisOutMsgCtx);
        log.debug("send() ~ isOutOnly = " + isOutOnly);
        
        try {
        	
            if (isOutOnly) {
                
            	// send MSG to BE and does not wait for response (OUT-ONLY)
            	sendRobust(axisOutMsgCtx, clientOptions, anonymousService, serviceCtx);
            	
            } else {
            	
            	// get the response because is IN-OUT
                org.apache.axis2.context.MessageContext result = sendReceive(axisOutMsgCtx, clientOptions, anonymousService, serviceCtx);
                synapseInMsgCtx.setEnvelope(result.getEnvelope());
                if (JsonUtil.hasAJsonPayload(result)) {
                	JsonUtil.cloneJsonPayload(result, ((Axis2MessageContext) synapseInMsgCtx).getAxis2MessageContext());
                }                
                synapseInMsgCtx.setProperty(NhttpConstants.HTTP_SC, result.getProperty(SynapseConstants.HTTP_SENDER_STATUSCODE));
                synapseInMsgCtx.setProperty(SynapseConstants.BLOCKING_SENDER_ERROR, "false");
                
                return synapseInMsgCtx;
                
            } // -- if-else
            
        } catch (Exception ex) {
        	
            if (!isOutOnly) {
            	
                //axisOutMsgCtx.getTransportOut().getSender().cleanup(axisOutMsgCtx);
                synapseInMsgCtx.setProperty(SynapseConstants.BLOCKING_SENDER_ERROR, "true");
                
                if (ex instanceof AxisFault) {
                	
                    AxisFault fault = (AxisFault) ex;
                    synapseInMsgCtx.setProperty(SynapseConstants.ERROR_CODE, fault.getFaultCode() != null ?  fault.getFaultCode().getLocalPart() : "");
                    synapseInMsgCtx.setProperty(SynapseConstants.ERROR_MESSAGE, fault.getMessage());
                    synapseInMsgCtx.setProperty(SynapseConstants.ERROR_DETAIL, fault.getDetail() != null ? fault.getDetail().getText() : "");
                    synapseInMsgCtx.setProperty(SynapseConstants.ERROR_EXCEPTION, ex);
                    org.apache.axis2.context.MessageContext faultMC = fault.getFaultMessageContext();
                    if (faultMC != null) {
                        synapseInMsgCtx.setProperty(NhttpConstants.HTTP_SC, faultMC.getProperty(SynapseConstants.HTTP_SENDER_STATUSCODE));
                        synapseInMsgCtx.setEnvelope(faultMC.getEnvelope());
                    } // -- if
                    
                } // -- if
                
                return synapseInMsgCtx;
                
            } // -- if
            
            handleException("Error sending Message to url : " + ((AbstractEndpoint) endpoint).getDefinition().getAddress(), ex);
        }
        
        return null;
        
    }


    private void sendRobust(org.apache.axis2.context.MessageContext axisOutMsgCtx, Options clientOptions, AxisService anonymousService, ServiceContext serviceCtx) throws AxisFault {

    	log.debug("sendRobust() ~ start sending the message to Backend EP (OUT-ONLY).");
    	
        AxisOperation axisAnonymousOperation = anonymousService.getOperation(new QName(AnonymousServiceFactory.OUT_ONLY_OPERATION));
        OperationClient operationClient = axisAnonymousOperation.createClient(serviceCtx, clientOptions);
        operationClient.addMessageContext(axisOutMsgCtx);
        axisOutMsgCtx.setAxisMessage(axisAnonymousOperation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
        operationClient.execute(true);
        axisOutMsgCtx.getTransportOut().getSender().cleanup(axisOutMsgCtx);

    }

    
    private org.apache.axis2.context.MessageContext sendReceive(org.apache.axis2.context.MessageContext axisOutMsgCtx, Options clientOptions, AxisService anonymousService, ServiceContext serviceCtx) throws AxisFault {

    	log.debug("sendReceive() ~ start receiving the message from Backend EP (IN-OUT).");
    	
        AxisOperation axisAnonymousOperation = anonymousService.getOperation(new QName(AnonymousServiceFactory.OUT_IN_OPERATION));
        OperationClient operationClient = axisAnonymousOperation.createClient(serviceCtx, clientOptions);
        operationClient.addMessageContext(axisOutMsgCtx);
        axisOutMsgCtx.setAxisMessage(axisAnonymousOperation.getMessage(WSDLConstants.MESSAGE_LABEL_OUT_VALUE));
        operationClient.execute(true);
        org.apache.axis2.context.MessageContext resultMsgCtx = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);

        org.apache.axis2.context.MessageContext returnMsgCtx = new org.apache.axis2.context.MessageContext();
        if (resultMsgCtx.getEnvelope() != null) {
            returnMsgCtx.setEnvelope(MessageHelper.cloneSOAPEnvelope(resultMsgCtx.getEnvelope()));
        }
        if (JsonUtil.hasAJsonPayload(resultMsgCtx)) {
            JsonUtil.cloneJsonPayload(resultMsgCtx, returnMsgCtx);
        }
        returnMsgCtx.setProperty(SynapseConstants.HTTP_SENDER_STATUSCODE, resultMsgCtx.getProperty(SynapseConstants.HTTP_SENDER_STATUSCODE));
        axisOutMsgCtx.getTransportOut().getSender().cleanup(axisOutMsgCtx);

        return returnMsgCtx;
        
    }

    
    private boolean isOutOnly(MessageContext messageIn, org.apache.axis2.context.MessageContext axisOut2Ctx) {
    	
    	log.debug("isOutOnly() ~ start");
    	
    	String strMessageExchangePattern = axisOut2Ctx.getOperationContext().getAxisOperation().getMessageExchangePattern();
    	boolean booleanOutOnlyInMsg = (Boolean) messageIn.getProperty(SynapseConstants.OUT_ONLY);
    	log.debug("isOutOnly() ~ MessageExchangePattern = " + strMessageExchangePattern);
    	log.debug("isOutOnly() ~ OutOnly property in MSG = " + booleanOutOnlyInMsg);
        return "true".equals(booleanOutOnlyInMsg) || axisOut2Ctx.getOperationContext() != null && WSDL2Constants.MEP_URI_IN_ONLY.equals(strMessageExchangePattern);
        
    }

/*
    public void setClientRepository(String clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void setAxis2xml(String axis2xml) {
        this.axis2xml = axis2xml;
    }  
    
    public void setInitClientOptions(boolean initClientOptions) {
        this.initClientOptions = initClientOptions;
    }
*/  
    
    public void setConfigurationContext(ConfigurationContext configurationContext) {
    	
    	log.debug("setConfigurationContext() ~ start");
    	
        this.configurationContext = configurationContext;
    }
    
    private void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new SynapseException(msg, e);
    }

    private void handleException(String msg) {
        log.error(msg);
        throw new SynapseException(msg);
    }

}
