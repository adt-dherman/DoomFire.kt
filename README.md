# DoomFire.kt
A simple Doom Fire renderer using Kotlin+TornadoFX

Inspired by https://fabiensanglard.net/doom_fire_psx/

![Doom Fire GIF](assets/doomfire.gif)

Kotlin is one of my favorite languages, and I heard a lot of nice things about [TornadoFX](https://tornadofx.io/),
so after being nudged by Fabien himself, I decided to give porting the doom fire effect over to Kotlin a shot.

Ideally, you should just be able to run this using gradle:

```shell
$ ./gradlew run
```

Once running, you can press `space` to toggle the fire on and off.

The code is relatively lean and written with the intention of being readable, so read Fabien's blog post linked
above first, and then jump into the source file and take a look!

## Troubleshooting

JavaFX requires at least JDK11 (see also: [JavaFX getting started](https://openjfx.io/openjfx-docs/#install-java)).
In order to avoid wasting too much time worrying about minimal versions, I just downloaded the latest OpenJDK
([JDK14](https://jdk.java.net/14/) at the time I wrote this), but you may be able to get away with older
versions.

If you see an error like this:

```
* What went wrong:
java.lang.UnsupportedClassVersionError: org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
> org/openjfx/gradle/JavaFXPlugin has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0
```

it means you need to ensure that your `JAVA_HOME` environment variable is pointing at a newer version of the JDK.

