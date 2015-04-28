import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.axis.AxisFault;

import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Field;
import com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2;
import com.sugarcrm.www.sugarcrm.Get_entry_result_version2;
import com.sugarcrm.www.sugarcrm.Link_field;
import com.sugarcrm.www.sugarcrm.Link_name_to_fields_array;
import com.sugarcrm.www.sugarcrm.Module_list;
import com.sugarcrm.www.sugarcrm.Module_list_entry;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.New_module_fields;
import com.sugarcrm.www.sugarcrm.New_set_entry_result;
import com.sugarcrm.www.sugarcrm.SugarsoapBindingStub;
import com.sugarcrm.www.sugarcrm.SugarsoapLocator;
import com.sugarcrm.www.sugarcrm.User_auth;

/**
 * @author lukasz bonio
 */
public class Sugar2 {
	
	private static final String END_POINT_URL = "http://ip/SugarCRM/service/v4_1/soap.php?wsdl";
	private static final String USER_NAME = "user";
	private static final String USER_PASSWORD = "pass";
	private static final String APPLICATION_NAME = Class.class.getName();
	private static final Integer TIMEOUT = 6000;
	

	/**
	 * Main Program
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String sessionID = null;
		SugarsoapBindingStub binding = null;
		
		try {
			
			binding = connectSugar();
			sessionID = loginSugar(binding);	

			retreiveModules(sessionID, binding);
			retreiveEntriesByModule(sessionID, binding, "EmailMarketing",new String[]{},0,20);
			retreiveModuleFields(sessionID, binding, "CampaignLog");

			// campaing creation
			//createAndRetreiveCampaign(sessionID,binding);
			// mail creation
			// createAndRetreiveMail(sessionID, binding);
			// contact creation
			//createAndRetreiveContact(sessionID, binding);
			
		  //getModuleFieldsByParam(sessionID, binding,"CampaignAudit","campaigns_audit.parent_id='id'");
			
			//createAndRetreiveCampaignLog(sessionID, binding);
			
			logoutSugar(binding, sessionID);
			
		}  catch (AxisFault ex) {
			System.out.println("AxisFaultsage: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Sample to show how to do creation operation using webservice
	 * 
	 * @param sessionID
	 * @param binding
	 */
	private static void createAndRetreiveContact(String sessionID,
			SugarsoapBindingStub binding) {
		/*
			Setting up parameters by creating a name value list array from a hash map.
		*/		
		HashMap<String, String> nameValueMap = retreiveModuleFields(sessionID, binding, "Contacts");
		nameValueMap.put("first_name", "Lukasz");
		nameValueMap.put("last_name", "Bonio");
		nameValueMap.put("title", "Java Junior");
		nameValueMap.put("description", "Java Developer");
		nameValueMap.put("email1", "lukasz.bonio.pl@gmail.com");
		
		String id =setModuleFieldsByParam(sessionID, binding, "Contacts", nameValueMap);

		/**
		 * Getting an Contacts Entry (the one we just set)
		 */
		
		getModuleFieldsByParam(sessionID, binding, "Contacts", "id='"+id+"'");
	}

	/**
	 * 
	 * TO get list of values /rows for a given modules based on selection
	 * criteria
	 * 
	 * @param sessionID
	 * @param binding
	 * @param moduleName
	 * @param select_fields
	 * @param l
	 * @param offset
	 */
	private static void retreiveEntriesByModule(String sessionID,
			SugarsoapBindingStub binding, String moduleName,
			String[] select_fields, int offset, int rowCount) {
		Link_name_to_fields_array[] link_name_to_fields_array = null;

		Get_entry_result_version2 getEntryResponse = null;
		Get_entry_list_result_version2 entryListResultVersion2 = null;

		// Trying to get entry
		try {
			/*
			 * getEntryResponse = binding.get_entry_list(sessionID,moduleName,
			 * null, select_fields, link_name_to_fields_array,favorites);
			 * favorites //If only records marked as favorites should be
			 * returned.
			 */
			entryListResultVersion2 = binding.get_entry_list(sessionID,
					moduleName, "", "", offset, select_fields,
					link_name_to_fields_array, rowCount, 0, false);
			// getEntryResponse = binding.get_entries(sessionID, moduleName,
			// null, new String[]{"name","description"},
			// link_name_to_fields_array);

		} catch (RemoteException e) {
			System.out.println("Gety failed. Message: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Gety was successful! Response: ");

		// Getting the fields for entry we got.
		Entry_value[] entryList = entryListResultVersion2.getEntry_list();
		for (int k = 0; k < entryList.length; k++) {
			Entry_value entry = entryList[k];
			Name_value[] entryNameValueList = entry.getName_value_list();
			System.out.println();
			for (int j = 0; j < entryNameValueList.length; j++) {
				Name_value entryNameValue = entryNameValueList[j];
				// Outputting only non empty fields
				if (!entryNameValue.getValue().isEmpty()) {
					System.out.println(entryNameValue.getName() + ":"
							+ entryNameValue.getValue() + "    ;   ");
				}
			}
		}
	}

	/**
	 * To list out all fields of a given module
	 * @param sessionID
	 * @param binding
	 * @param moduleName
	 */
	private static HashMap<String, String> retreiveModuleFields(String sessionID,
			SugarsoapBindingStub binding, String moduleName) {
		/**
		 * Create a new Contact
		 * 
		 * 1) Setting a new entry 2) Setting up parameters for set_entry call 3)
		 * Creating a name value list array from a hash map. This is not
		 * necessary just more elegant way to initialize and add name values to
		 * an array
		 */

		/**
		 * Getting an Contacts Entry (the one we just set)
		 */
		Link_name_to_fields_array[] link_name_to_fields_array = null;
		String[] select_fields = null;
		HashMap<String, String> hm = new HashMap<String, String>();

		Get_entry_result_version2 getEntryResponse = null;
		New_module_fields moduleFields = null;
		// Trying to get entry
		try {
			moduleFields = binding.get_module_fields(sessionID, moduleName,
					select_fields);
		} catch (RemoteException e) {
			System.out.println("Gety failed. Message: " + e.getMessage());
			e.printStackTrace();
		}
		//System.out.println("Gety was successful! Response: ");

		if (moduleFields != null) {
			//System.out.println("<div>");
			for (Field field : moduleFields.getModule_fields()) {
				System.out.println("\"" + field.getName() + "\",");				
				hm.put(field.getName(), "");				
			}
			//System.out.println("</div>");
		//	System.out.println("--Linkds");
			/**
			 * Now get Link Fields
			 */

//			for (Link_field linkField : moduleFields.getLink_fields()) {
//				System.out.println("[" + linkField.getName() + " : "
//						+ linkField.getBean_name() + " : "
//						+ linkField.getRelationship() + "],");
//			}
		}
		return hm;
	}

	/**
	 * To list out all modules available to given user
	 * 
	 * @param sessionID
	 * @param binding
	 */
	private static void retreiveModules(String sessionID,
			SugarsoapBindingStub binding) {
		/**
		 * Create a new Contact
		 * 
		 * 1) Setting a new entry 2) Setting up parameters for set_entry call 3)
		 * Creating a name value list array from a hash map. This is not
		 * necessary just more elegant way to initialize and add name values to
		 * an array
		 */

		/**
		 * Getting an Contacts Entry (the one we just set)
		 */
		Link_name_to_fields_array[] link_name_to_fields_array = null;
		String[] select_fields = null;

		Get_entry_result_version2 getEntryResponse = null;
		Module_list moduleList = null;
		// Trying to get entry
		try {

			// get_available_modules(sessionID,filter) filter -possible values
			// are 'default', 'mobile', 'all'.
			moduleList = binding.get_available_modules(sessionID, "all");
		} catch (RemoteException e) {
			System.out.println("Gety failed. Message: " + e.getMessage());
			e.printStackTrace();
		}
		// System.out.println("Gety was successful! Response: ");

		for (Module_list_entry moduleName : moduleList.getModules()) {
			System.out.println(moduleName.getModule_key() + "{");
			// retreiveModuleFields(sessionID, binding,
			// moduleName.getModule_key());
			System.out.println("}");
		}

	}

	private static void createAndRetreiveEmail(String from, String mailto,
			String subject, String msg) {
		/**
		 * Create a new Contact
		 * 
		 * 1) Setting a new entry 2) Setting up parameters for set_entry call 3)
		 * Creating a name value list array from a hash map. This is not
		 * necessary just more elegant way to initialize and add name values to
		 * an array
		 */
		final String username = "mail@mail.com";
		final String password = "pass";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.checkserveridentity", "false");
		props.put("mail.smtp.ssl.trust", "*");

		try {
			Session session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(mailto));
			message.setSubject(subject);
			message.setText("Dear Mail Crawler,"
					+ "\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");
		} catch (NoSuchProviderException e2) {

			e2.printStackTrace();

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

	private static void createAndRetreiveMail(String sessionID,
			SugarsoapBindingStub binding) {

		HashMap<String, String> nameValueMap = retreiveModuleFields(sessionID, binding, "Emails");
		nameValueMap.put("from_addr_name", "mail@mail.com");
		nameValueMap.put("name", "Subject");
		nameValueMap.put("to_addrs_names", "mail2@mail2.com");
		nameValueMap.put("assigned_user_name", "Lukasz");
		nameValueMap.put("description", "msg text");
		nameValueMap.put("assigned_user_id", "1");
		nameValueMap.put("parent_type", "Users");
		nameValueMap.put("parent_id", "id");
		nameValueMap.put("status", "read");
		nameValueMap.put("date_sent", new Date().toString());
		
		String id =setModuleFieldsByParam(sessionID, binding, "Emails", nameValueMap);
		
		getModuleFieldsByParam(sessionID, binding, "Emails", "id='"+id+"'");
		
	}
	
	private static void createAndRetreiveCampaignLog(String sessionID,
			SugarsoapBindingStub binding) {

		HashMap<String, String> nameValueMap2 = new HashMap<String, String>();
		nameValueMap2.put("target_id", "86d11106-dcba-b86b-1717-5536646e8a4b");
		nameValueMap2.put("campaign_id", "7859863d-41f8-6acd-aac7-5538a737f2ee");
		nameValueMap2.put("target_type", "Prospects");
		nameValueMap2.put("target_tracker_key", "c693543a-2828-b87b-6184-55265b3e7888");
		nameValueMap2.put("activity_type", "targeted");
		nameValueMap2.put("activity_date", "2015-04-09 10:43:00");
		nameValueMap2.put("list_id", "a26c0c84-be02-a32d-f474-552627a45881");
		nameValueMap2.put("marketing_id", "6ddb62b4-ecb9-8bcf-4314-5526572c8ed9");
		nameValueMap2.put("id", "f0ba8025-5475-658f-724c-5538a382bd17");
		
		

		String id =setModuleFieldsByParam(sessionID, binding, "CampaignLog", nameValueMap2);
		
		getModuleFieldsByParam(sessionID, binding, "CampaignLog", "campaign_log.id='"+id+"'");
	}
	
	
	private static void createAndRetreiveCampaign(String sessionID,
			SugarsoapBindingStub binding) {

		HashMap<String, String> nameValueMap = new HashMap<String, String>();
		nameValueMap.put("id", "7859863d-41f8-6acd-aac7-5538a737f2ee");
		nameValueMap.put("name", "campaign 2");
		nameValueMap.put("end_date", "2015-03-25");
		nameValueMap.put("status", "Planning");
		nameValueMap.put("campaign_type", "Email");
		nameValueMap.put("assigned_user_id", "1");

		String id =setModuleFieldsByParam(sessionID, binding, "Campaigns", nameValueMap);
		
		getModuleFieldsByParam(sessionID, binding, "Campaigns", "campaigns.id='"+id+"'");
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	public static String setModuleFieldsByParam(String sessionID,
			SugarsoapBindingStub binding, String module, HashMap<String, String> hm){
		
		Name_value nameValueListSetEntry[] = new Name_value[hm.size()];
		int i = 0;
		for (Entry<String, String> entry : hm.entrySet()) {
			Name_value nameValue = new Name_value();
			nameValue.setName(entry.getKey());
			nameValue.setValue(entry.getValue());
			nameValueListSetEntry[i] = nameValue;
			i++;
		}

		// Trying to set a new entry
		New_set_entry_result setEntryResponse = null;
		try {
			setEntryResponse = binding.set_entry(sessionID,module,
					nameValueListSetEntry);
		} catch (RemoteException e) {
			System.out.println("Sety failed. Message: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Sety was successful! Entry ID: "
				+ setEntryResponse.getId());
		return setEntryResponse.getId();
	}
	
	public static void getModuleFieldsByParam(String sessionID,
			SugarsoapBindingStub binding, String module, String query) {
		
		Link_name_to_fields_array[] link_name_to_fields_array = null;
		String[] select_fields = null;

		Get_entry_list_result_version2 getEntryResponse = null;

		// Trying to get entry
		try {
			getEntryResponse = binding.get_entry_list(sessionID, module, query, "" ,0, select_fields, link_name_to_fields_array,20 ,0, false);
		} catch (RemoteException e) {
			System.out.println("Gety failed. Message: " + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Gety was successful! Response: ");

		// Getting the fields for entry we got.
		Entry_value[] entryList = getEntryResponse.getEntry_list();
		for (int k = 0; k < entryList.length; k++) {
			Entry_value entry = entryList[k];
			Name_value[] entryNameValueList = entry.getName_value_list();
			for (int j = 0; j < entryNameValueList.length; j++) {
				Name_value entryNameValue = entryNameValueList[j];
				// Outputting only non empty fields
				if (!entryNameValue.getValue().isEmpty()) {
					System.out.println("Attribute: '"
							+ entryNameValue.getName() + "' Attribute Value: '"
							+ entryNameValue.getValue() + "'");
				}
			}
		}
		
	}
	
	public static SugarsoapBindingStub connectSugar() {
		
		SugarsoapBindingStub binding2 = null;
		
		try {
			URL wsdlUrl = null;
			if (END_POINT_URL.isEmpty()) {
				wsdlUrl = new URL(
						new SugarsoapLocator().getsugarsoapPortAddress()
								+ "?wsdl");

			} else {
				wsdlUrl = new URL(END_POINT_URL);
			}

			System.out.println("URLoint created successfully!");

			// Create Service for test configuration
			ServiceFactory serviceFactory = ServiceFactory.newInstance();
			Service service = serviceFactory.createService(wsdlUrl,
					new SugarsoapLocator().getServiceName());

			System.out.println("Serviceted successfully");
			System.out
					.println("Service:" + service.getServiceName().toString());
			System.out.println("Service:"
					+ service.getWSDLDocumentLocation().toString());

			// Trying to create a stub
			binding2 = new SugarsoapBindingStub(wsdlUrl,
					service);
			binding2.setTimeout(TIMEOUT);
			System.out.println("Stubted successfully!");
			
		} catch (MalformedURLException ex) {
			System.out.println("URLoing creation failed. Message: "
					+ ex.getMessage());
			ex.printStackTrace();
		} catch (ServiceException ex) {
			System.out.println("Servicetion failed. Message: "
					+ ex.getMessage());
			ex.printStackTrace();
		} catch (AxisFault ex) {
			System.out.println("AxisFaultsage: " + ex.getMessage());
			ex.printStackTrace();
		}
		return binding2;
	}
	
	public static String loginSugar(SugarsoapBindingStub binding) throws Exception{
		
		String loginID= null;

			// 1. Prepare a MD5 hash password
			MessageDigest messageDiget = MessageDigest.getInstance("MD5");
			messageDiget.update(USER_PASSWORD.getBytes());

			// 2. Prepare a User Auth object
			User_auth userAuthInfo = new User_auth();
			userAuthInfo.setUser_name(USER_NAME);
			userAuthInfo.setPassword((new BigInteger(1, messageDiget.digest()))
					.toString(16));

			try {
				// 3. Execute login
				Entry_value loginResult = binding.login(userAuthInfo,
						APPLICATION_NAME, null);
				System.out.println("Loginessfully for " + USER_NAME);
				System.out.println("Yourion Id: " + loginResult.getId());
				loginID = loginResult.getId();
			} catch (RemoteException ex) {
				System.out.println("Logined. Message: " + ex.getMessage());
				ex.printStackTrace();
			}
			
		return loginID;
	}
	
	public static void logoutSugar(SugarsoapBindingStub binding,String sessionID) {
		try {
			binding.logout(sessionID);
			System.out.println("Logoutessfully for " + USER_NAME);
			sessionID = null;
		} catch (RemoteException ex) {
			System.out.println("Logined. Message: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void jsonTest() {
		
		 try {
			JSONObject json=readJsonFromUrl("http://ip/SugarCRM/upload/temp_json/accounts");
			JSONObject json2 = new JSONObject("{\"id\":\"663871001\"}");
			System.out.println(json);
			System.out.println((json.get("link")));
			System.out.println((json.get("name")));
			System.out.println((json.getJSONObject("id")));
			System.out.println(json.getJSONArray("link").getJSONObject(0).get("categoryId"));
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
