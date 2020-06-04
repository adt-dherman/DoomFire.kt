# DoomFire.kt
An application that demonstrates the "Doom Fire effect" using Kotlin+TornadoFX

Inspired by Fabien's blog post: https://fabiensanglard.net/doom_fire_psx/

![Doom Fire GIF](assets/doomfire.gif)
_The actual program runs much faster than this. I just don't know how to gif._

Ideally, after cloning, you should just be able to navigate into the root folder
and run the application using gradle:

```shell
$ ./gradlew run
```

Once running, you can press `space` to toggle the fire source on and off and `esc` to quit.

[The code](src/DoomFireApp.kt) all lives in a single Kotlin file. It's relatively lean and
written first and foremost with the intention of being readable. My recommendation: check
out Fabien's blog post linked above first, and then jump into the source file and take a look!
The whole thing is less than 200 lines, with a fair bit of it being UI setup that you can just
gloss over.

The meat of the algorithm is in `DoomFireView#step()` which is driven by an
[`AnimationTimer`](https://docs.oracle.com/javase/8/javafx/api/javafx/animation/AnimationTimer.html),
a class provided by JavaFX.

`step()` updates a buffer which is a 2D array of palette indices (0 being black, 36 being white,
and intermediate indices representing a smooth, fiery gradient that passes through orange/red).
Every time `step()` is called, each pixel checks the row below it and takes its value, often
decaying into a slightly darker color index (based on a bit of randomness). `updateCanvas()`
converts those pallete indices into actual pixels on the canvas.

## Troubleshooting

JavaFX requires at least JDK11 (see also: [JavaFX getting started](https://openjfx.io/openjfx-docs/#install-java)).
In order to avoid wasting too much time worrying about minimal versions, I just downloaded the latest OpenJDK version
([JDK14](https://jdk.java.net/14/) at the time of writing this), but you may be able to get away with older
versions.

If you see an error like this:

```
* What went wrong:
java.lang.UnsupportedClassVersionError: org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
> org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
```

it means you need to update your `JAVA_HOME` environment variable to point at a newer version of the JDK.
