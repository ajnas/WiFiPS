<?php
require_once "eloquent_db.php";

class Reading extends \Illuminate\Database\Eloquent\Model
{
	protected $table = 'readings';
    public $timestamps = false;

}