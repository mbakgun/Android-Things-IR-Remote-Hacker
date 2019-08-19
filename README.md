## Android Things IR Remote Hacker

<img src="/assets/1.jpeg" width ="61%" >


This Android Things project integrates `Nearby Communication` , `Serial Communication` and `Voice Recognition` together to
build a connected IR remote that explores the relationships between surfaces and content.

This repo contains all the app code that powers IR Remote Controled Android Things powered Raspberry Pi. 

The project is split into three modules:

- `/things` - the Android Things app
- `/mobile` - the companion mobile app
- `/arduino` - the Arduino code

<img src="/assets/1.gif" width="95%">

### Pre-requisites

- Android Things compatible board
- Android Studio 3.2+
- [Arduino](https://www.arduino.cc/) and the following individual components:
    - 1 IR Receiver
    - 1 IR Transmitter
    - Jumper wires
    - (optional) 1 MI-305 - USB Microphone
    
<br><p align="center">
<img src="/assets/2.jpeg" width ="40%">
<img src="/assets/4.jpeg" width ="41%">
</p><br>

## How it works

<br><p align="center">
<img src="/assets/3.jpeg" width="66%">
</p><br>

The IR Remote Hacker imagines that you can control all remote controled  electronic devices by voice and also with a Mobile Application. The Companion Mobile application allows you to configure the embedded software. Capture and record the Signal with the mobile app. You can now hack as many remote controls as you want. Controlling IR Remote signals over network is planned for next releases.

<br><p align="center">
<img src="/assets/5.jpeg" width="66%">
</p><br>

Android Things device and its components is set-up and controlled using the companion app for Android. They communicate using *Nearby Connections*, a protocol developed by Google to facilitate local peer-to-peer communication with nearby devices.

This application is built around the Voice controlled Raspberry Pi that capable of talking with the Arduino (which can process the data of IR Remote sensors) and control over the companion mobile application. Once your Android phone and Android Things are connected, you can take control of all the remote controls around you. In fact, the whole story consists of user interfaces that control the ability to capture and repeat infrared signals.

While accomplishing this creative idea, many features has been developed to show different connection methods and technologies with other IOT devices. Both the Android Things code and the companion app are written in Kotlin using Jetpack components and industrial best practises , which has been a joy to work with.

## Technical overview

<img src="/assets/how.png">

There are two main components to the IR Remote Hacker software - the ‘Things’ app (`/things`), which runs on Android Things on a Raspberry Pi, and the Companion app (`/mobile`) which runs on an Android phone.

The hardware is built as an voice controlled remote device, with an Arduino Uno, a Raspberry Pi, a USB Microphone, an IR Receiver & Transmitter and a few off-the-shelf wires and connectors.

## Schematics

<img src="/assets/sch.jpg">

- If you have the Arduino, just plug it onto your Raspberry Pi 3.

### Next steps
- Remote Control : Over Network communication 

### Found this project lovely :heart:
* Support by clicking the :star: button on the upper right of this page.
* Contribute and Make Android Things Great Again  :v:

### Contact me
- [Twitter](https://twitter.com/mbakguns)
- [LinkedIn](https://www.linkedin.com/in/mbakgun/)
- [Medium](https://medium.com/@mbakgun)
