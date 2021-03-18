# README #


* [Summary](#markdown-header-summary)
* [Prerequisites](#markdown-header-prerequisites)
* [Clone Repository](#markdown-header-clone-repository)
* [Usage](#markdown-header-usage)
* [Acknowledgment](#markdown-header-acknowledgment)


# Summary
This project is a library that is utilized by the [Data Mining Lab](http://dmlab.cs.gsu.edu/) of [Georgia State University](http://www.gsu.edu/).

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by the Free Software
Foundation version 3.
 
This program is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
details.

The javadocs for the classes in this library are located at:

* [DMLabLib JavaDocs](http://dmlab.cs.gsu.edu/docs/dmlablib/)


# PREREQUISITES 

This project is a Java based project which utilizes Maven for dependency management.  
To that end, both Java and Maven need to be installed on your machine prior to proceeding 
with any utilizing this project. The following are some helpful instructions for the 
prerequisites for this project.  

* [Prerequisites](./PREREQ.md)

If the prerequisites are already installed, you can skip forward to the next section. 

# USAGE
There are two ways to utilize this library, it can either be installed from the source 
using Maven or it can be pulled from a repository as a dependency defined in the 
pom.xml file of a project that utilizes Maven as its dependecy magement system.

If installation from source is desired see the installation instructions [INSTALL](./INSTALL.md)

If using the repository version, then the repository needs to be added to your pom.xml file.
To do that, the following repository should be in your pom.xml file in order to utilize the latest snapshot.

	<repositories>
		<repository>
			<id>dmlab-snapshots</id>
			<name>DMLab Snapshots Repository</name>
			<url>http://dmlab.cs.gsu.edu/repos/maven-snapshots</url>
		</repository>
	</repositories>

Now that the library has been installed you can utilize it in a maven enabled project by placing the following in your pom.xml file.

	<dependency>
		<groupId>edu.gsu.cs.dmlab</groupId>
		<artifactId>lib</artifactId>
		<version>0.0.4-SNAPSHOT</version>
	</dependency>
	
If you are utilizing the HelioviewerPullingAIAImageDBConnection or HelioviewerImageFileDatasource classes 
of the db-connectors module, you will also need to add a library for reading JP2 files.  One such 
library would be the following in your pom.xml file.

	<dependency>
		<groupId>com.github.jai-imageio</groupId>
		<artifactId>jai-imageio-jpeg2000</artifactId>
		<version>1.3.0</version>
	</dependency>
	
If you only want to use portions of the library and don't want the others imported into your project, 
there are several different module choices for this. Instead of lib in the artifactId, you could utilize
the following modules. 

	* core -- This contains the core classes of the DMLab Library that are used in various different modules.
	
	* db-connectors -- This contains the database connector and creator classes used for the storage of and retrieval of events, image parameters, and images form a MySql database.
	
	* imageproc -- This contains several classes used to process images by computing parameters, applying color maps to images, drawing polygons on images.
	
	* solgrind -- This contains several classes that are used in solar event frequent pattern datamining among others.
	
	* tracking -- This contains several classes that are used in solar event tracking.



# Acknowledgment

 This work was supported in part by two NASA Grant Awards (No. NNX11AM13A, and No. NNX15AF39G),
 and one NSF Grant Award (No. AC1443061). The NSF Grant Award has been supported by funding 
 from the Division of Advanced Cyberinfrastructure within the Directorate for Computer and 
 Information Science and Engineering, the Division of Astronomical Sciences within the 
 Directorate for Mathematical and Physical Sciences, and the Division of Atmospheric and 
 Geospace Sciences within the Directorate for Geosciences.
 
 This software is distributed using the [GNU General Public License, Version 3](./LICENSE.txt)  
 ![alt text](./images/gplv3-88x31.png)
 
***
 
© 2019 Dustin Kempton, Azim Ahmadzadeh, Berkay Aydin, Surabhi Priya, Michael Tinglof, Ahmet Kucuk, Soukaina Filali, Thaddeus Gholston, Rafal Angryk
 
[Data Mining Lab](http://dmlab.cs.gsu.edu/), 
[Georgia State University](http://www.gsu.edu/)
 	