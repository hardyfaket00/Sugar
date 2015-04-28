package sugar;

package sugar;
import java.io.*;
import java.net.*;
public class SOAPClient {

    public static void main(String[] args) throws Exception {

//        if (args.length  < 2) {
//            System.err.println("Usage:  java SOAPClient4XG " +
//                               "http://soapURL soapEnvelopefile.xml" +
//                               " [SOAPAction]");
//				System.err.println("SOAPAction is optional.");
//            System.exit(1);
//        }
//
//        String SOAPUrl      = args[0];
//        String xmlFile2Send = args[1];
//
//		  String SOAPAction = "";
//        if (args.length  > 2) 
//				SOAPAction = args[2];
				
        // Create the connection where we're going to send the file.
        
    	//URL url = new URL(SOAPUrl);
    	URL url = new URL("http://Cristian-PC:8080/SUGARCRMWeb/AccountServiceService");
    	URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;

        // Open the input file. After we copy it to a byte array, we can see
        // how big it is so that we can set the HTTP Cotent-Length
        // property. (See complete e-mail below for more on this.)
        String xmlFile2Send = "c:/Users/Cristian/workspaceLukasz/SUGARCRMWeb/WebContent/file.xml";
        
        FileInputStream fin = new FileInputStream(xmlFile2Send);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
    
        // Copy the SOAP file to the open connection.
        copy(fin,bout);
        fin.close();

        byte[] b = bout.toByteArray();

        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty( "Content-Length",
                                     String.valueOf( b.length ) );
        httpConn.setRequestProperty("Content-Type","text/xml; charset=utf-8");
		httpConn.setRequestProperty("SOAPAction","setName");
        httpConn.setRequestMethod( "POST" );
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);

        // Everything's set up; send the XML that was read in to b.
        OutputStream out = httpConn.getOutputStream();
        out.write( b );    
        out.close();

        // Read the response and write it to standard out.

        InputStreamReader isr =
            new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String inputLine;

        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);

        in.close();
    }

  // copy method from From E.R. Harold's book "Java I/O"
  public static void copy(InputStream in, OutputStream out) 
   throws IOException {

    // do not allow other threads to read from the
    // input or write to the output while copying is
    // taking place

    synchronized (in) {
      synchronized (out) {

        byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = in.read(buffer);
          if (bytesRead == -1) break;
          out.write(buffer, 0, bytesRead);
        }
      }
    }
  } 
}
