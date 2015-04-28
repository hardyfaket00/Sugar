<?php

class SugarCRMSocket {
	
	public function after_save_account2($bean, $event, $arguments) {
		
		$host    = "ip";
		$port    = "port";
		
		$socket = stream_socket_client($host.":".$port, $errno, $errstr,3);
		
		//create data msg for selected fields
		// $data = array();
		// $data['id'] = $bean->id;
		// $data['name'] = $bean->name;
   	    // $data['account_type'] = $bean->account_type;
		// $data['date_modified'] = $bean->date_modified;
		// $data['email'] = $bean->email;
						
		//json encode the whole bean which contains all info about module
		$message = json_encode($bean);
	
		fwrite($socket, $message);
		fclose($socket);
	}
}
?>
