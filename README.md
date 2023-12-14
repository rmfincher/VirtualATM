# GeoMoney
### By: Jacob Hall, Yohan Sanchez, Ryan Fincher, Brian Granados Cuellar

## Description:
#### GEOMoney is a money-transferring app that can be used on Android devices. GEOMoney makes it easy to transfer money safely and securely to anyone you know. With a simple-to-use UI design, you will have no problems navigating and using GEOMoney. GEOMoney also takes extra steps to verify and secure your transfers with a GEO location feature that uses your device location to receive transactions at a specified location set by the sender. This gives the sender an extra layer of trust that strengthens security and ensures that the money transferred goes to the correct user. This also protects against fraudulent or deceptive actions by scammers and hackers who try and steal your hard-earned money. 

## Usage:

GEOMoney uses Amplify SDK by AWS to securely handle data. For development, you need to download the Amplify CLI.

### Windows:
curl -sL https://aws-amplify.github.io/amplify-cli/install-win -o install.cmd && install.cmd

### cURL (Mac and Linux):
curl -sL https://aws-amplify.github.io/amplify-cli/install | bash && $SHELL

### NPM:
npm install -g @aws-amplify/cli
### Once Amplify is installed you will need to pull the data:
cd <project-dir>
amplify init
amplify pull --appId d2dg0hd0px3svb --envName dev

#### Access key and Secret key will be needed to pull 

### Once data is pulled AndroidStudio IDE will need to be installed:
https://developer.android.com/studio/install
Once AndroidStudio is installed and running connect github account and pull repository.

## Commits and Updates:

### Version 1.0
Groundwork for application development setup. The main application file was completed along with base fragments for ui view models.

### Version 1.1
View model fragments are continued along with drawables and layouts for the application ui. Values are set up for application theme, colors, and strings. 

### Version 1.2
The app menu is integrated into the main function and view models along with fragments are implemented with simple functionality.

### Version 1.3 
Ui models and drawables are refined for application theme and functionality. Menu item fragments "deposit", "home", "send", and "withdraw" are functional and can be accessed by the menu.

### Version 1.4 
Amplify SDK is set up and configured for the application. Auto-generated configuration files are added and Amplify packages are imported. Amplify configuration is set up with AWS console along with a database for storing username, password, funds, and email.

### Version 1.5
App login and account creation is implemented. App asks user for email, username, and password to store and keep track of account information such as user funds. Email verification feature is also added with a secure one time code being sent to the users email upon sign up.

### Version 1.6
Send functionality is implemented into app. Send fragment asks user for the name of the recipient and the amount they want to send. User data is stored and computed with money being subtracted from the sender and added to the receiver.

## Member Onboarding
https://docs.google.com/document/d/1LrTm0KhTUUCn_jdvhE4MD72ykRmrL_FHJRNwCLWNzXQ/edit?usp=sharing







