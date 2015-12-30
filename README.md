This repository contains the source code for the adapter used to connect to https://github.com/perandersson/everstore-server . It also contains examples on how to use it.

## Projects

### core

This project contains the Java API for using Everstore.

### vanilla

This is the default data-storage implementation

### java8-adapter

This is the java8 specific adapter. Instanciate the core adapter using configuration backed by this project. The examples included in this repository uses the java8-adapter project.

## Examples

### example-console

This is a simple console application where we try to save the same type of event to the same journal at the same time. 
This shows us what happens when a conflict happens. The example itself do not try to resolve the conflict.

### example-grizzly-rest

Example where we start a simple a simple REST HTTP server using Grizzly and Jersey2. This example makes full use of the
custom `Optional<T>` type that exists in this project. The reason for it is that all requests are handled asynchronously
and the interface `CompletableFuture<T>` is simply not good enough.