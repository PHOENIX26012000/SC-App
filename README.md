# Happy Share

This application was developed in the Study Project "Smart Cities" at the Institute for Geoinformatics, University of Muenster. The idea behind the project is to develop software for the cities of the future. Using the SCRUM software development method, this application was developed by a team of 7 students. It relates to the concept of opportunistic information sharing, where the inhabitants of a city may share information with other people they meet once they are in range of their mobile phones.

The developers of the project are:
- [Heinrich Löwen](https://github.com/heinrichloewen)
- [Clara Rendel](https://github.com/crend02)
- [Maurin Radtke](https://github.com/MojioMS)
- [Saad Sarfraz](https://github.com/saadsarfrazz)
- [Apurva Anil Kochar](https://github.com/apurvaakochar)
- [Shahzeib Tariq Jaswal](https://github.com/shahzeib)
- [Sruthi Ketineni](https://github.com/Sruthiketineni)

This documentation guides through the concepts of the application, the development process as well as the installation and usage of the application.

## Getting Started

### Github

![functionality](img/git branching.png)

### Git Flow

![functionality](img/git.png)

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

### User

As a user the application can be installed by downloading the `.apk` file, which is provided at the main directory. If your phone prohibits installing applications form unknown sources, you have to go to your phone settings and enable this option.

Steps for the installation:
  1. Download the `.apk` file.
  2. Enable the phone setting for installing applications from unknown sources.
  3. Click the `.apk` file and select install.
  4. Enable the application permission in order to get the application work as intended.

## Architecture

![functionality](img/architecture_model.png)

## Functionalities

![functionality](img/functionality.png)

## Limitations

## Future Work
