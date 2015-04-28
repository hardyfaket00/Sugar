package sugar;


import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class SOAPSAAJ {

    /**
     * Starting point for the SAAJ - SOAP Client Testing
     */
    public static void main(String args[]) {
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            String url = "http://ip:port/SUGARCRMWeb/AccountServiceService";
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
            System.out.println("done");
            // Process the SOAP Response
            printSOAPResponse(soapResponse);
            

            
          //  soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending SOAP Request to Server");
            e.printStackTrace();
        }
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        
        //soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "UTF-8");
        soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION ,"true");
        SOAPPart soapPart = soapMessage.getSOAPPart();  
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix("S");
        soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("SOAP-ENV");
        //envelope.setEncodingStyle("http://www.w3.org/2001/12/soap-encoding");
        SOAPHeader sh = envelope.getHeader();
        sh.setPrefix("S");
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("S");
        SOAPElement soapBodyElem = soapBody.addChildElement("setName");
        String serverURI = "http://sugar/";
        soapBodyElem.addNamespaceDeclaration("ns2", serverURI);
        soapBodyElem.setPrefix("ns2");
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("arg0");
        soapBodyElem1.addTextNode("lukasz");

        soapMessage.saveChanges();

        /* Print the request message */
        System.out.print("Request SOAP Message = ");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }

    /**
     * Method used to print the SOAP Response
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.print("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);

        transformer.transform(sourceContent, result);
    }

}
