CS6310P3
========

GA Tech CS6310 Fall 14 Project 3, Team 22

http://gooftroop.github.io/CS6310P3
https://github.com/gooftroop/CS6310P3

github check

REQUIREMENTS:

This project uses Neo4j as it's embedded Database. The jar files are located under /resource/neo4j and should be included in the compile classpath.

NOTE: If neo4j does not run on your system's Java JRE 1.6, JRE 1.7 will be needed. Follow these steps to install an additional JRE on your 
Ubuntu System.

1. Download the JRE 1.7 from http://apt.ubuntu.com/p/openjdk-7-jre
2. Download the JDK 1.7 from http://apt.ubuntu.com/p/openjdk-7-jdk (optional)

Choosing the default Java to use

If your system has more than one version of Java, configure which one your system uses by entering the following command in a terminal window

3. sudo update-alternatives --config java

"""
	This will present you with a selection that looks similar to the following (the details may differ for you):

	There are 2 choices for the alternative java (providing /usr/bin/java).  
	Selection Path Priority Status 
	———————————————————— 
	* 0 /usr/lib/jvm/java-6-openjdk/jre/bin/java 1061 auto mode 
	1 /usr/lib/jvm/jre1.7.0/jre/bin/java 3 manual mode  

	Press enter to keep the current choice[*], or type selection number: 1
"""

Once Java 7 has been selected, compile the project again. If you encounter any errors, please e-mail:

webb.c.brandon@gmail.com

INSTALLATION:

sudo bash build.sh

Or, alternatively, copy and paste the commands found in build.sh by hand.

RUN:

java PlanetSim.Demo [-p #] [-g #] [-t #]
