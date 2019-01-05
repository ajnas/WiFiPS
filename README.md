# WiFi Based Indoor Positioning System

A MVP android application which is able to estimate the position of a user within a building using WiFi technology. For better understanding this project, we recommend you to go through the docs folder of this repository.

## Setting up

* [Install](http://example.com/) Android Studio
* Clone or Download project from this url http://github.com/ajnas/wifips
* From Android Studio, click on Open project and select the android-client folder inside the downloaded project.

## Testing the App

* Download [Demo App](demo.apk) to your android phone or run the imported code from Android Studio to your device.
* Turn on WiFi
* Go to some building were at least 3 wifi access points are available.

### Learn/Calibrate
* Choose the 'Learn' option from the first screen.
* Enter a name for the new building and press Add
* Select wifi access points that are permanent inside the building using the 'Friendly Wifis' button. Do not forget to save changes after adding all such access points.
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

## Syncing data to server [OPTIONAL]
* Create MYSQL database with tables to store readings and list of access points. Please refer  [this file](backend/schema.txt) for necessary schema details
* Setup a php server with backend code included in this project and host it on your own server. It uses [Slim framework](https://www.slimframework.com/).
* Change the **BASE_URL** in android code to your server url in **Config.java** file
* Run app to your Android device from Android Studio
* Use the 'Sync' button in the first screen to load the buildings from the server.		  The readings taken from other devices are also displayed. 

## How to Contribute
* Fork the repo
* Commit changes to a branch in your fork
* Pull request with your changes

## Developers

[Ajnas](https://github.com/ajnas) and [Jazeem](https://github.com/jazeem). Feel free to contact us in case of any issue while setting up/testing. 
