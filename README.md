# Happy Share

This application was developed in the Study Project "Smart Cities" at the Institute for Geoinformatics, University of Muenster. The idea behind the project is to develop software for the cities of the future. Using the SCRUM software development method, this application was developed by a team of 7 students. It relates to the concept of opportunistic information sharing, where the inhabitants of a city may share information with other people they meet once they are in range of their mobile phones.

The developers of the project are:
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Heinrich Löwen](https://github.com/heinrichloewen)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Clara Rendel](https://github.com/crend02)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Maurin Radtke](https://github.com/MojioMS)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Saad Sarfraz](https://github.com/saadsarfrazz)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Apurva Anil Kochar](https://github.com/apurvaakochar)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Shahzeib Tariq Jaswal](https://github.com/shahzeib)
##### ![alt GitHub](http://i.imgur.com/0o48UoR.png") [Sruthi Ketineni](https://github.com/Sruthiketineni)

This documentation guides through the concepts of the application, the development process as well as the installation and usage of the application.

## Getting Started

The developers of the project came with different programming backgrounds and knowledge, so the initial phase of the project was to some degree used to get familiar with the tools and technologies.

### Github

The decision was made to use a [Github](https://github.com) Repository as version control and code sharing platform. The following graphic was used to explain the concept of git to the developers that had not been working with it before.

![functionality](img/git.png)

### Git Flow

Git Flow is a powerfull branching concept for git, which is easy to understand and to learn, and has proofed itself during the project to be indispensabel. [This Graph](https://github.com/heinrichloewen/SC-App/network) visualizes how the concept was implemented according to the following simplified graphic.

![functionality](img/git branching.png)

## Installation

This application was mainly developed for the means of the study project, so that it is currently not released into the Google Play Store. There are to ways of installing the application: A developer may clone this repository and deploy the application via Android Studio, whereas a normal user may install the application via a pre-build `.apk` file.  

### Developer

The application was developed with [Android Studio](https://developer.android.com/studio/index.html). The Target Sdk Version was set to *API 23: Android 6.0 (Marshmallow)*, whereas the Min Sdk Version was set to *API 21: Android 5.0 (Lollipop)*. In order to build the application on your own, please make sure to install Android Studio, preferably the latest version, and install the required Android Sdks (Software Development Kits).
After this step, there are two ways of installing the application, whereas both require a preliminary step.

**Preliminary Step**: The application uses external google services that require an API_KEY.
  1. Go to your [Google Developer Console](https://console.developers.google.com/) and generate an API_KEY.
  2. Activate the services **Google Maps Android API** and **Nearby Messages API** for this API_KEY.
  3. Go to your `AndroidManifest.xml` and insert your API_KEY as follows:

  ```
  <meta-data
        android:name="com.google.android.nearby.messages.API_KEY"
        android:value="[Your API_KEY]" />
  <meta-data
        android:name="com.google.android.geo.API_KEY"
        android:value="[Your API_KEY]" />
  ```

**Option 1**: Connect your Smartphone to your PC and run the application onto your phone.

**Option 2**: Select `Build` > `Build APK`. Navigate to the *apk* directory on your file system, select the `app-debug.apk` file and transfer it to your Smartphone. Follow the steps in the next section to install the application.

**Note**: If you want to communicate with others all Apps need to be signed with the same API-Key. Apps that are signed with different API-Keys will no be able to communicate among each other. So make sure to generate a `.apk` file and deploy it to everyone you want to communicate with.

### User

As a user the application can be installed by downloading the `happy_share.apk` file, which is provided at the main directory. If your phone prohibits installing applications form unknown sources, you have to go to your phone settings and enable this option.

Steps for the installation:
  1. Download the `happy_share.apk` file.
  2. Enable the phone setting for installing applications from unknown sources.
  3. Click the `happy_share.apk` file and select install.
  4. Enable the application permission in order to get the application work as intended.

## Architecture

![functionality](img/architecture_model.png)

The main three components of the application are the application itself, the connection to the server and the connection to other peers that are using the same application.

### Application

The application is dividet into 5 main components that internally connect and interact: The user interface, the messenger (message manager), the peer-to-peer connection manager, the server connection manager and the zone manager.

The user interface is responsible for displaying the application with all its subtasks (writing messages, displaying incomming messages, etc.). It shares the written messages with the messenger and receives messages to display from the messenger. Moreover, it shows the zones that are retrieved from the server and displays the locations that have been attached to the messages.
The Messenger is the heart of the messanging that is possible with the application. Every incomming and outgoing message in evaluated by the Messenger in terms of the zone it belongs to, the expiration date of the message, and if the message has already been received from someone else before. Depending on the decisions of the Messenger messages are then shared with peer, with the server, stored in the database and/or shown in the user interface.
The peer-to-peer connection manager is responsible for sharing messages with other peers that are in range and receiving messages from peers that are in range.
The server connection manager uses the API of the server application in order to connect of the server for requestion messages and zones and for sharing messages with the server.
The zone manager is responsible for handling the zone and the events that may happen with respect to zones, e.g. leaving a zone, entering a zone, selecting a zone.

### Server Connection

As already mentioned the server connection is responsible for exchanging messages and zones with the server. This connections are tiggered only when the application is stared or closed. On the start of the application the application requests zones and messages from the server. When closing the application the application sends the messages that have been written and received in the meantime to the server.

### Peer-to-Peer Connection

The peer-to-peer connection is dependent on the lifecycle of the activity of the application and is able to share and receive messages with peers as long as the activity is active.
This is required by the library which is used, which is [Android Nearby](https://developers.google.com/nearby/).
The messages are shared anonymous, so that it is not known to the users to whom messages are send or from whom messages are received. When the activity is active messages that have not been expired will be published until they expired. Then the peer-to-peer connection manager will stop sharing the messages.  

## Functionalities

![functionality](img/functionality.png)

#### Select and show zones on a map

#### Select and show interested topics

#### Receive and write messages

##### Reshare messages

##### Share message to server

#### specify message settings

## Limitations & Future Work

The main drawback of the application is the dependency on the android nearby library, which required to be handled by an activity. This makes it impossible to receive messages from nearyby peers, when the device screen is off and the device is in the pocket. The service gets only active when the application does.
However, in the time when the application is active the battery is drained a lot faster than usual, which is obivously because of the Android Nearby library, which constantly publishes and receives messages when active. Futher investigations have to be made in this regards to find a solution that lowers the battery consumption.

By now all existing topics are open for everyone. In future it could be further investigated to create private topics that can only be entered by invited people. In such a case the concept of end-to-end encryption would have to be considered in order to avoid that people from 'outside' could incept and read the messages.  
