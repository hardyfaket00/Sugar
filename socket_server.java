package sugar;

import java.io.*;
import java.net.*;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerListener {

	public static void main(String args[]) {

		try {
			// declate and try to open a server socket on port 25003
			ServerSocket echoServer = new ServerSocket(25003);

			while (true) {

				// Create a socket object from the ServerSocket
				// to listen and accept connections.
				Socket clientSocket = echoServer.accept();

				// Open input stream
				DataInputStream is = new DataInputStream(
						clientSocket.getInputStream());

				String line = is.readLine();
				if (line != null) {

					// create JSON from input String
					JSONObject json = new JSONObject(line);

					// show selected data from JSON
					System.out.println("Module: " + json.get("module_name")
							+ "\nentry: " + json.get("name")
							+ "\nmodified by: " + json.get("modified_by_name"));
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		} catch (JSONException e) {
		}
		;
	}
}
