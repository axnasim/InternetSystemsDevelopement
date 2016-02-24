# System Description

Our project will be used to develop spdx documents that have relationships built on a given pom.xml file.  

# Development Environment

OS: Ubuntu 14.04

Programming Language: Java

Used for Development: Maven, DoSOCS, SQLite3, and Python

# Communication Management Plan

We will use Github Issues to communicate what is done and what needs to be done. We will use email as needed for communication outside of Github.

# Copyright

Joseph Eley

Cooper Pendleton

# Database Schema of the System

We will use is the SPDX spec that DoSOCS uses.


# How to Use

Need to have installed:

Java, Maven, DoSOCS, SQLite3, Python

1. Download/clone source
2. Run "mvn clean install" from the root of the project
3. run the jar that is created in the target/ directory with the location of the pom.xml that you want to analyze as an argument. 