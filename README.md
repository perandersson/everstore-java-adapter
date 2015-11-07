This repository contains the source code for the adapter used to connect to https://github.com/perandersson/everstore-server . It also contains examples on how to use it.

## Projects

### core

This project contains the Java API for using Everstore.

### vanilla

This is the default data-storage implementation

### java8-adapter

This is the java8 specific adapter. Instanciate the core adapter using configuration backed by this project. The examples included in this repository uses the java8-adapter project.

## Examples

### Example1

This is a simple console application where we try to save the same type of event to the same journal at the same time. 
This shows us what happens when a conflict happens. The example itself do not try to resolve the conflict.

### Example2

A more practical example on how to use Eventsourcing. This example initializes a journal with a basic event, which is 
later used to recreate the state of our data.
