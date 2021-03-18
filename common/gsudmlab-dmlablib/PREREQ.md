* [Return to READEME](./README.md#markdown-header-prerequisites)

# PREREQUISITES 
__Note:__ These instructions have only been tested on Ubuntu 18.04, and are only meant as a helpful guide, not a definitive set of instructions.

### Step 1: Update your machine ###

	sudo apt-get update -y
	sudo apt-get upgrade -y

### Step 2: Install Java JRE and JDK ###
Apache Maven requires Java to be installed on your machine. So, install the latest default version of OpenJDK jre: 

	sudo apt install default-jre

Verify the Java version by running the following command:

	java -version

The reply should be something like this.

	openjdk version "11.0.4" 2019-07-16
	OpenJDK Runtime Environment (build 11.0.4+11-post-Ubuntu-1ubuntu218.04.3)
	OpenJDK 64-Bit Server VM (build 11.0.4+11-post-Ubuntu-1ubuntu218.04.3, mixed mode, sharing)

You will need the Java Development Kit (JDK) in addition to the JRE in order to compile and run some specific Java-based software. To install the JDK, execute the following command, which will also install the JRE:

	sudo apt install default-jdk

Verify that the JDK is installed by checking the version of javac, the Java compiler:

	javac -version

You'll see output similar to the following:

	javac 11.0.4

### Step 3: Install Apache Maven ###
To install maven execute the following:

	sudo apt install maven

Then test it with the command

	mvn -version
	
The reply should be something like this.

	Apache Maven 3.6.0
	Maven home: /usr/share/maven
	Java version: 11.0.4, vendor: Ubuntu, runtime: /usr/lib/jvm/java-11-openjdk-amd64
	Default locale:  en_US, platform encoding: UTF-8
	OS name:  "linux", version: "5.0.0-27-generic", arch: "amd64", family: "unix"
		
### Step 4: Install Git ###

This project is stored in a git repository, and if you don't have git installed through an IDE, you will need to install it to make copying it to your computer easier. 

To install simply do:
	
	sudo apt-get install git
	
### Step 5: Maven Setup ###
One thing that seems to have been missed by the maven and java package installations is providing the JAVA_HOME that is expected by the maven compiler plugin.  To accomplish that just for the maven program, we can specify it in a config file in the home directory.  This file is named .mavenrc and should include the line:

	export JAVA_HOME=/usr/lib/jvm/default-java

That's it, return to [README](./README.md)
