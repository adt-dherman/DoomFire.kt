# DoomFire.kt
An app that demonstrates the "Doom Fire renderer" using Kotlin+TornadoFX

Inspired by Fabien's blog post: https://fabiensanglard.net/doom_fire_psx/

![Doom Fire GIF](assets/doomfire.gif)
_The actual app runs much faster than this. I just don't know how to gif._

Ideally, you should just be able to run this using gradle:

```shell
$ ./gradlew run
```

Once running, you can press `space` to toggle the fire source on and off.

The code is relatively lean and written first and foremost with the intention of being readable.
Check out Fabien's blog post linked above first, and then jump into the source file and take a look!
The whole thing is less than 200 lines, with a fair bit of it being UI setup a fair bit of which you
can just skip over.

The meat of the algorithm is in `DoomFireView#step` which is driven by an `AnimatedTimer` which is
a class provided by JavaFX.

## Troubleshooting

JavaFX requires at least JDK11 (see also: [JavaFX getting started](https://openjfx.io/openjfx-docs/#install-java)).
In order to avoid wasting too much time worrying about minimal versions, I just downloaded the latest OpenJDK
([JDK14](https://jdk.java.net/14/) at the time of writing this), but you may be able to get away with older
versions.

If you see an error like this:

```
* What went wrong:
java.lang.UnsupportedClassVersionError: org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
> org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
```

it means you need to update your `JAVA_HOME` environment variable to point at a newer version of the JDK.
