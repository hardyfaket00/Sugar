<?php

ini_set('display_errors','1');
error_reporting(E_ALL);

class mySoap {
	
	public $xmlRequest;
    public $header;
	private $url = 'http://ip:port/SUGARCRMWeb/AccountServiceService?WSDL';
	
	public function set_header() {
		
		$this->header = array(
            "Content-type: text/xml;charset=\"utf-8\"",
            "Accept: text/xml",
            "Cache-Control: no-cache",
            "Pragma: no-cache",
            "SOAPAction: \"{this->url}\"",
            "Content-length: ".strlen($this->xmlRequest)
        );	
	}
	
	
	public function send() {
		
		$this->set_header();
	    $soapCURL = curl_init();
        curl_setopt($soapCURL, CURLOPT_URL, "http:/ip:port/SUGARCRMWeb/AccountServiceService?WSDL" );
        curl_setopt($soapCURL, CURLOPT_CONNECTTIMEOUT, 100);
        curl_setopt($soapCURL, CURLOPT_TIMEOUT,        1000);
        curl_setopt($soapCURL, CURLOPT_RETURNTRANSFER, true );
        curl_setopt($soapCURL, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($soapCURL, CURLOPT_SSL_VERIFYHOST, false);
        curl_setopt($soapCURL, CURLOPT_POST,           true );
        curl_setopt($soapCURL, CURLOPT_POSTFIELDS,     $this->xmlRequest);
        curl_setopt($soapCURL, CURLOPT_HTTPHEADER,     $this->header);
        //Executing Curl Here.
        $result = curl_exec($soapCURL);
        if($result === false) {
          $err = 'Curl error: ' . curl_error($soapCURL);
          $result = $err;
          //echo "This is text".$err;
        }
        curl_close($soapCURL);
        return $result;

	}
	
}

class Lukasz_ws_call {
	
	public $temp;
	private $url = 'http://ip:port/SUGARCRMWeb/AccountServiceService?WSDL';
	
	public function after_save_account($bean, $event, $arguments) {	
		
		
		//echo "Message To server :".$message;
		$host    = "ip";
		$port    = port;
		$timeout = array('sec'=>3,'usec'=>0);
		// create socket
		$socket = socket_create(AF_INET, SOCK_STREAM, 0); //or die();
			if ($socket ===false) break;
				else {
				// connect to server
				//default_socket_timeout(5);
				$bool = socket_set_option($socket,SOL_SOCKET,SO_SNDTIMEO,$timeout);
				$result = socket_connect($socket, $host, $port);// or die();
				 
				if ($result === true) {
					if ($bool === true) {
						$data = array();
						$data['id'] = $bean->id;
						$data['name'] = $bean->name;
						$data['account_type'] = $bean->account_type;
						$data['date_modified'] = $bean->date_modified;
						$data['email'] = $bean->email;
						$message = json_encode($bean);
					
					// send string to server
					socket_write($socket, $message, strlen($message)) or die("Could not send data to server\n");
				}}}
		socket_close($socket);
			//self::create_soap($data);
			//die();	
			//}
	}
	
	private function create_soap($datas) {
			
			$soapxml = '<?xml version="1.0" encoding="UTF-8"?>
			<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
				<S:Header/>
				<S:Body>
				<ns2:setName xmlns:ns2="http://sugar/">
					<arg0>'.$datas['name'].'</arg0>
				</ns2:setName>
				</S:Body>
				</S:Envelope>';
			 
			$obj = new mySoap();
			$obj->xmlRequest = $soapxml;
			print_r($obj->send());
					
	}
}
?>
