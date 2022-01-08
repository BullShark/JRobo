## Project Members (Developers)

[BullShark](https://github.com/BullShark "Core Developer") (Christopher Lemire)

[BinaryStroke](https://github.com/BinaryStroke "Developer") (StompinBroknGlas on irc)

[projektile](https://github.com/projektile "Developer") (Tyler Pollard)

[teslasolutions](https://github.com/teslasolution "Developer") (Kamal Sajid)

[n0tme](https://github.com/thatsn0tmysite) (thatsn0tmysite on matrix)

## About

JRobo is an advanced IRC bot that uses its own IRC framework. It was written from scratch.

# Features
 * Colored and formatted output
 * Ability to respond to actions such as a user who joined
 * Auto reponds to its name with a random joke
 * Uses its own IRC framework
 * Configuration file in plain text JSON, just modify it's values
 * Authenticates with nickserv
 
## Provides many commands
* google|g &lt;search query&gt;
* wakeroom|wr
  * Brings a dead channel back to life!
* weather|w &lt;location, zip, etc.&gt;
  * Queries the wunderground api for a weather summary
* urbandict|ud &lt;search query&gt;
  * Returns the top 3 results of the search query from urbandictionary.com
  * Sorts by thumbs up, and shows the number of thumbs up and thumbs down for each result
  * Nicely formatted and colored output
* list|l
  * Provides a list of commands
  * Uses a Linux Man page type Syntax
* help [cmd]
  * Provides help output on a particular command
* leet <Category> <search query>
  * Queries 1337x.to with formatted and colored output
  * Category is one of the following: Movies, TV, Games, Music, Apps, Documentaries, Anime, Other, XXX, All
* pirate [-s|-l|-d] &lt;search query&gt;
  * Queries pirate bay with formatted and colored output
  * -s sort by seeds (default)
  * -l sort by leachers
  * -d sort by total times downloaded
  * Limits output to 3 lines to avoid flooding the channel
* epic
  * Gets a list of free Epic Games with links
* isup &lt;url&gt;
  * Checks if the url is really up or down
* version
* quit|q
  * JRobo will only respond to this and other commands/features sent from the master or (Not implemented fully yet)
  * One of the list of masters read in from Config.json
* And others, download and use the bots in-channel help for more

# Features to come
* RSS Feed Support

## Install and run

# Linux

Requirements
 * Gradle
 * OpenJDK or Oracle/Sun JDK version 16 or newer
 * Git of course

### Manjaro / Arch Linux
    $ yay -Syy aur/jrobo

    Edit the Config.json in /usr/lib/JRobo.jar

    $ jrobo

### Debian/Linux Mint/Ubuntu
    $ sudo apt-get install openjdk-17-jdk git
    $ git clone https://github.com/BullShark/JRobo.git
    $ cd JRobo
    $ mv src/jrobo/Config.json-example src/JRobo/Config.json
    $ nano src/jrobo/Config.json
    $ ./gradlew clean
    $ ./gradlew build
    $ ./gradlew run

### RHEL/Fedora/CentOS
    $ yum install java-latest-openjdk-devel git
    $ # The rest is the same as above

### Compile and run without Gradle
    $ # Download gson jar to the project root where it's easy to find
    $ wget https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.8/gson-2.8.8.jar
    $ cd src/main/java
    $ javac -classpath ../../../gson-2.8.8.jar:. jrobo/expectusafterlun/ch/JRobo.java
    $ java -classpath ../../../gson-2.8.8.jar:. jrobo.expectusafterlun.ch.JRobo

# Windows

Tested, and it works.

## Develop

Contact me (BullShark) if you would like to contribute to this project, and you might be able to become a collaborator.

This works best under Netbeans, but you can choose a different development environment. It is recommended to use my custom build.xml to include Google's GSON for JSON library and other jar libraries bundled with JRobo.jar.

    $ cd ~/NetbeansProjects
    $ git clone https://github.com/BullShark/JRobo.git
    
Now go into Netbeans and click File->Open Project. Browse to ~/NetbeansProjects and choose JRobo.

OR:

From Netbeans, use the Git plugin to clone this Netbeans project.
/main/java

### Developer Documentation

http://expectusafterlun.ch/JRobo/docs/javadoc/jrobo/expectusafterlun/ch/package-summary.html
