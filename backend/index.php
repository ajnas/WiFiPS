<?php
require 'vendor/autoload.php';

$app = new \Slim\Slim();

$app->response->headers->set('Content-Type', 'application/json');

$app->post('/submit/?', function () {

	$data = isset($_REQUEST['data'])?$_REQUEST['data']:null;

	if($data){
		$data = json_decode($data);

		Reading::where('building_id','=', $data->building_id)->delete();
		AccessPoint::where('building_id','=', $data->building_id)->delete();

		foreach ($data->readings as $reading) {
			foreach ($reading->values as $mac => $rssi) {
				$new_reading = new Reading;
				$new_reading->building_id = $data->building_id;
				$new_reading->position_id = $reading->name;
				$new_reading->ssid = $reading->routers->$mac;
				$new_reading->mac_id = $mac;
				$new_reading->rssi = $rssi;
				$new_reading->save();
			}
		}

		foreach ($data->friendly_wifis as $wifi) {
			$new_access_point = new AccessPoint;
			$new_access_point->building_id = $data->building_id;
			$new_access_point->ssid = $wifi->SSID;
			$new_access_point->mac_id = $wifi->BSSID;
			$new_access_point->save();
		}

		echo json_encode(['result'=>'success'], JSON_PRETTY_PRINT);
	}else{
		echo json_encode(['result'=>'fail'], JSON_PRETTY_PRINT);
	}
});


$app->get('/?', function () {

	$buildings = Reading::groupBy('building_id')->get(['building_id']);

	$responses = array();

	foreach ($buildings as $building) {
		$building_id = $building->building_id;

		$readings = Reading::where('building_id','=',$building_id)->get();
		$access_points = AccessPoint::where('building_id','=',$building_id)->get();

		
		
		foreach($readings as $reading){
			$positions[$reading->position_id]['values'][$reading->mac_id]=$reading->rssi;
			$positions[$reading->position_id]['routers'][$reading->mac_id]=$reading->ssid;		
			
		}

		$positions_list= array();
		foreach ($positions as $name => $data) {
			$new_position=array(
				'name'=>(string)$name,
				'routers'=>$data['routers'],
				'values'=>$data['values'],
				
				);

		 array_push($positions_list,$new_position);

		}

		$access_points_list=array();
		foreach ($access_points as $access_point) {
			$new_access_point=array(
				'BSSID'=>$access_point['mac_id'],
				'SSID'=>$access_point['ssid'],
		
				);
			array_push($access_points_list,$new_access_point);
		}
		

		$building_response = array(
			'building_id' => $building_id, 
			'readings' => $positions_list,
			'friendly_wifis' => $access_points_list,
			);

		array_push($responses,$building_response);

	}
	echo json_encode($responses);
	return;

});



$app->run();
