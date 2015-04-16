#WiFi Based Indoor Positioning System

A MVP android application which is able to estimate the position of a user within a building using WiFi technology. For better understanding this project, we recommend you to go through the docs folder of this repository.

##Setting up

* [Install](http://example.com/) Android Studio
* Checkout project from version control. Give repository url as
 http://github.com/ajnas/wifips.git

##Testing the App

* Run the app to your android phone.
* Turn on wifi
* Go to some building were at least 3 wifi access points are available.

	### Learning/Calibrating
* Choose the 'Learn' option from the first screen.
* Enter a name for the new building and press Add
* Now you need to find different distinguishable positions inside the building. For better results, choose different rooms inside the building as different positions.
* Name the position and press calibrate button.
* The scanning starts after you press start button. Time for scanning is set as 30 seconds.
* Once the scanning of one position is complete, go to another position and repeat the procedure
* Swipe left/right on a position name to remove a position from the list.
* You can re-calibrate a position by clicking on a position again.
* Once you have added enough positions inside the building, press the update button to sync this readings to server, so that this data can be used from other devices as well.

	### Locate
* Choose the 'Locate' option from the first screen.
* Choose the building where you want to locate your position.
* Press start button and complete the scanning process.
* Your position obtained from the calculations are displayed in the screen.

    ### Sync
* Use the 'Sync' button in the first screen to load the buildings from the server.		  The readings taken from other devices are also displayed. 

## Api Details

* Submit a building's calibrated readings to server

		- Method : POST
		- Url 	 : http://ajnas.in/wifips/api/submit
		- Params : 
			mac - Device's mac address
			data - Json object containing building's calibrated readings
		- Response:
			{result:"success"} or {result:"failure"}

* Fetch Entire buildings data

		- Method : GET
		- Url 	 : http://ajnas.in/wifips/api/
		- Response: JSON-Array of all building's data


* Submit Location

		- Method : POST
		- Url 	 : 
		- Response : 
			{result:"success"} or {result:"failure"}
How to Contribute
==================
* Fork the repo
* Commit changes to a branch in your fork
* Pull request with your changes
Developers
===========
[Ajnas](https://github.com/ajnas) and [Jazeem](https://github.com/jazeem). Feel free to contact us in case of any issue while setting up/testing. 
