scala-imperial
==============

A [minty fresh](http://en.wikipedia.org/wiki/Mint_(candy)#Mint_imperials) provider of metrics, served by the Pint!

This is a scala wrapper for [Coda Hale's Metrics](https://github.com/codahale/metrics), and started as a fork from [metrics-scala](https://github.com/erikvanoosten/metrics-scala).
It's so-named because "[imperial](http://en.wikipedia.org/wiki/Imperial_units)" is the natural counterpoint to "metric" _(this may come as something of a surprise to anyone used to speaking of "American" units... Sorry folks, you didn't invent them, you're just the last hold-out for an old British system)_

The API has undergone a significant change + cleanup since the fork, with the goal of improving maintainability and providing far better Akka integration.
Some of the changes made include:

* methods have been "unitized", and all use of procedure syntax removed
* trivial doc comments collapsed to one-liners
* Removal of deprecated methods
* Core logic lifted to interfaces and mocks provided that don't delegate to codahale-metrics.
* Anonymous/Structural classes given a name and refactored to singletons where possible
* Renaming to make the code better self-documenting
* The `Registry` functionality is being folded into the `Builder` classes
* classes have been grouped into sub-packages.

###Akka Support

By far the biggest rewrite is around actor instrumentation.
By moving this logic into a sub-package of `akka` (specifically, to `akka.imperial`) we gain access to the package-protected `aroundReceive` method that arrived with Akka 2.3.

This allows instrumentation traits to be mixed in as actors are defined, there's no need to retrofit the traits any more.

It also allows for instrumentation to work even after an Actor's `receive` block has been exchanged via the `become` method.

Another change is that metric naming for instrumented actors is now based on the Actor's [path](http://doc.akka.io/api/akka/2.3.4/#akka.actor.ActorPath) and **not** on the class.  The class name is instead re-exposed via a `Gauge`.

###Versioning

There are two variants of this project released:

- scala-imperial-ci is an alternative to using snapshots, and is numbered after successful travis-ci builds.
- scala-imperial uses semantic versioning.

Owing to the new mechanism used, only Akka 2.3.x is targeted at this point.


