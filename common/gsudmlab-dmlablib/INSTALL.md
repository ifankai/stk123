#CLONE REPOSITORY
Assuming that the prerequisites are installed, the repository needs to be cloned to your local machine.

### Step 1: Go Home ###
Navigate to your home (~) directory.

	$ cd ~

### Step 2: Make a place to store repos ###
	
	$ mkdir repos

Or whatever you want to name it. This is just a folder to hold your git repositories, it won't be the final folder of the repository.

	$ cd ~/repos

Go to the place we will store the repository.

### Step 3: Clone the repo ###
Clone the repository to your local machine with the following command. 

	git clone https://bitbucket.org/gsudmlab/dmlablib.git

You should then see a dmlablib directory in your repos directory. Navigate into that directory

	cd ./dmlablib

Next will be to compile and install.

#INSTALL LIBRARY
In the compilation of the project we need to be in the main directory of the project (where we left off in the last section).

You should see the following files and directories:

	* src (directory)
	* tests (directory)
	* LICENSE.txt (file)
	* pom.xml (file)
	* README.MD (file)

From here we will package the program into a single .jar file and install it into the local Maven repository.

	mvn clean install

You should see various displays of things downloading and when done there should be a display of success like the following.
 
	------------------------------------------------------------------------
	[INFO] Reactor Summary for DMLab Library 0.0.4-SNAPSHOT:
	[INFO] 
	[INFO] DMLab Library ...................................... SUCCESS [  0.366 s]
	[INFO] GSU DMLab Core Types and Classes Module ............ SUCCESS [ 14.076 s]
	[INFO] GSU DMLab Database Connectors Module ............... SUCCESS [  9.378 s]
	[INFO] GSU DMLab Image Processing Module .................. SUCCESS [ 27.465 s]
	[INFO] GSU DMLab Tracking Module .......................... SUCCESS [ 30.464 s]
	[INFO] GSU DMLab Solgrind Module .......................... SUCCESS [  8.202 s]
	[INFO] GSU DMLab Interpolation Module ..................... SUCCESS [  9.857 s]
	[INFO] GSU DMLab Library .................................. SUCCESS [ 35.613 s]
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time:  02:15 min
	[INFO] Finished at: 2019-08-14T08:37:27-04:00
	[INFO] ------------------------------------------------------------------------

[RETURN TO README](./README.md)