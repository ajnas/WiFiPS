<?php
require_once "eloquent_db.php";

class AccessPoint extends \Illuminate\Database\Eloquent\Model
{
	protected $table = 'access_points';
    public $timestamps = false;

    public function readings(){
    	return $this->hasMany('Reading', 'building_id', 'building_id');
    }
}