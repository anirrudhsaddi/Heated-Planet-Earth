CS6310P3
========

GA Tech CS6310 Fall 14 Project 3, Team 22

http://gooftroop.github.io/CS6310P3
https://github.com/gooftroop/CS6310P3

github check

REQUIREMENTS:

This project uses Neo4j as it's embedded Database. The jar files are located under /resource/neo4j and should be included in the compile classpath.

NOTE: Java 1.6 was EOL'd two years ago, and EOL'd 1 year ago on OpenJDK. As such, many of the the tools needed to complete this project
was incompatible with Java 6. As such, we have provided Java 1.7 JDK under "resources/java" for use with this project. 

There are two options for configuration. 

1. You can set your paths to temporarily point to Java 7.

	export JAVA_HOME=<path to resource>/resource/java/jdk1.7.0_72
	export PATH=$JAVA_HOME/bin:$PATH
	
NOTE: the jdk 1.7 rpm is located alongside the jdk 1.7 source
	
2. Install the JDK 1.7 as an alternative Java under Ubuntu's "alternatives"

	Choosing the default Java to use:
	
	1. sudo update-alternative --install 
	
	3. sudo update-alternatives --config java
	
	4. select the jdk1.7

Once Java 7 has been selected, compile the project again. If you encounter any errors, please e-mail:

webb.c.brandon@gmail.com

INSTALLATION:

sudo bash build.sh

Or, alternatively, copy and paste the commands found in build.sh by hand.

RUN:

java PlanetSim.Demo [-p #] [-g #] [-t #]
