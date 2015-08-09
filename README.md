## Project Members

[BinaryStroke](https://github.com/BinaryStroke "Developer") (StompinBroknGlas on irc)

Developer


[projektile](https://github.com/projektile "Developer") (Tyler Pollard)

Developer


[teslasolutions](https://github.com/teslasolution "Developer") (Kamal Sajid)

Developer


[BullShark](https://github.com/BullShark "Core Developer") (Christopher Lemire)

Founder and Lead Developer


## About

JRobo is an advanced IRC bot that uses its own IRC framework. It was written from scratch.

# Features
 * Colored and formatted output
 * Ability to respond to actions such as a user who joined
 * Auto reponds to its name with a random joke
 * Uses its own IRC framework
 * Configuration file in plain text JSON, just modify it's values
 * Authenticates with nickserv
  * You have it logged in under one of your groupped nicks
 
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
 * pirate [-s|-l|-d] &lt;search query&gt;
  * Queries pirate bay with formatted and colored output
  * -s sort by seeds (default)
  * -l sort by leachers
  * -d sort by total times downloaded
  * Limits output to 3 lines to avoid flooding the channel
 * isup &lt;url&gt;
  * Checks if the url is really up or down
 * version
 * quit|q
  * JRobo will only respond to this and other commands/features sent from the master or
  * One of the list of masters read in from Config.json
 * And others, download and use the bots in-channel help for more

# Features to come
 * RSS Feed Support
  * Works differently and better than all other RSS Feed bots I've used
 * Quizzes
  * Practice Certification Exam Questions
  * And others read in by files (Add your own as well)
 * More

## Install and run

# Linux

Requirements
 * Java Apache Ant 1.8 (Recommended but optional)
 * OpenJDK or Oracle/Sun JDK version 7 or newer
 * Git of course

### Debian/Linux Mint/Ubuntu
    $ sudo apt-get install openjdk-7-jdk ant git
    $ git clone https://github.com/BullShark/JRobo.git
    $ cd JRobo
    $ ant clean
    $ ant jar
    $ ant run
    $ ant <tab><tab> for a full listing (Useful if you want to develop for JRobo)
    $ # Or build and run without ant
    $ cd src
    $ javac -classpath ../lib/gson-2.2.2.jar:. jrobo/JRobo.java
    $ java -classpath ../lib/gson-2.2.2.jar:. jrobo.JRobo

### RHEL/Fedora/CentOS
    $ yum install java-1.7.0-openjdk-devel ant git
    $ # The rest is the same as above

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
