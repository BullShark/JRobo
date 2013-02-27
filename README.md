## Project Members

BinaryStroke (StompinBroknGlas on irc)
Developer

teslasolutions (Muhammad Sajid)
Developer

BullShark (Christopher Lemire)
Founder and Lead Developer

## About

JRobo is an advanced IRC bot that uses its own IRC framework. It was written from scratch.

# Features
 * //TODO Write me

## Install and run

# Linux

Requirements
 * Java Apache Ant (Recommended but optional)
 * OpenJDK or Oracle/Sun JRE/JDK

### Debian/Linux Mint/Ubuntu
    $ sudo apt-get install openjdk-7-jre ant
    $ git clone https://github.com/BullShark/JRobo.git
    $ cd JRobo
    $ ant run
    $ # Or
    $ java -classpath lib/gson-2.2.2.jar:. -jar dist/JRobo.jar




# Windows

You're on your own.

## Develop

Contact me (BullShark) if you would like to contribute to this project, or you can fork on github as open source.

This works best under Netbeans, but you can choose a different development environment. It is recommended to use my custom build.xml to include Google's GSON for JSON library and other jar libraries bundled with JRobo.jar.

    $ cd ~/NetbeansProjects
    $ git clone https://github.com/BullShark/JRobo.git

OR:

From Netbean, use the Git plugin to clone this Netbeans project.