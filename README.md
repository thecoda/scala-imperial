scala-imperial
==============

A [minty fresh](http://en.wikipedia.org/wiki/Mint_(candy)#Mint_imperials) scala wrapper for [Coda Hale's Metrics](https://github.com/codahale/metrics), with a core focus on improved Akka support.

It's so-named because "[imperial](http://en.wikipedia.org/wiki/Imperial_units)" is the natural counterpoint to "metric" _(this may come as something of a surprise to anyone used to speaking of "American" units... Sorry folks, you didn't invent them, you're just the last hold-out for an old British system)_

This project started as a fork from [metrics-scala](https://github.com/erikvanoosten/metrics-scala) and still retains the core wrapper types.  The code has been cleaned up though, and the following changes made:

* methods have been "unitized", and all use of procedure syntax removed
* trivial doc comments collapsed to one-liners
* Removal of deprecated methods
* Wrapper types extend `AnyVal` to reduce runtime overhead
* Implicit conversions provided (in the package object) from raw codahale types to the wrapper types
* Anonymous/Structural classes given a name and refactored to singletons where possible
* Minor renaming to make the code better self-documenting

###Akka Support

By far the biggest rewrite is around actor instrumentation.  By moving this logic into a sub-package of `akka` (specifically, to `akka.imperial`) we gain access to the package-protected  `aroundReceive` method.

This allows instrumentation traits to be mixed in as actors are defined, there's no need to retrofit the traits any more.

It also allows for instrumentation to work even after an Actor's `receive` block has been exchanged via the `become` method.

Another change is that metric naming for instrumented actors is now based on the Actor's [path](http://doc.akka.io/api/akka/2.3.4/#akka.actor.ActorPath) and **not** on the class.  The class name is instead re-exposed via a `Gauge`.

###Versioning

As with metrics-scala, the version number of imperial also embeds the version of Akka being targeted. 


