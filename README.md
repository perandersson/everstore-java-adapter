This repository contains the source code for both the adapter and various examples.

## Projects

## Examples

### Example1

This is a simple console application where we try to save the same type of event to the same journal at the same time. 
This shows us what happens when a conflict happens. The example itself do not try to resolve the conflict.

### Example2

A more practical example on how to use Eventsourcing. This example initializes a journal with a basic event, which is 
later used to recreate the state of our data.
