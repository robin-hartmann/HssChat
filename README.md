# HssChat
Chat client and server for Android

<img src="docs/images/client-chat-activity.png" height="700"> <img src="docs/images/server-main-activity.png" height="700">

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

* [Android Studio](https://developer.android.com/studio/index.html) - The Official IDE for Android

### Setup

To use the PC Client, you need to add a Run Configuration of type `Application` with the following details:

* Name: `client_pc`
* Main class: `de.hss.sae.sue.chat.client_pc.PcClient`
* Program arguments: `<server-address>:<server-port>`
* Use classpath of module: `client_pc`

### Troubleshooting

#### Q: How do I get the app to run? It just opens the "Edit configuration" dialog with "Error: Please select Android SDK". 
Just sync gradle by clicking <img src="docs/images/toolbar-sync-gradle.png" height="24"> in the toolbar and it should work.

## Deployment

### Prerequisites

* [Android 4.0.3+](https://developer.android.com/about/versions/android-4.0.3.html)

### Installation

Just copy over the generated apk and install it.

## Authors

* **Alexander Berndt**
  * *Client - Design, Interface*
* **Robin Hartmann** - [RobinHartmann](https://github.com/RobinHartmann)
  * *Client - Communication, Threading*
  * *Server*
  * *PC Client*
* **Kevin Landsberg** - [KevinLandsberg](https://github.com/KevinLandsberg)
  * *Client - Design, Interface*
* **Frederik Ried**
  * *Client - Local Strorage, Database*

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* Special thanks to Alex, [Kevin](https://github.com/KevinLandsberg) and Freddy for letting me publish this project
