# Adventure Emulation

[![Build Status](https://travis-ci.com/CoolONEOfficial/adventure-simulation.svg?token=wfz5f29VzkAUQiKYXcZQ&branch=master)](https://travis-ci.com/CoolONEOfficial/adventure-simulation)
[![codecov](https://codecov.io/gh/CoolONEOfficial/adventure-simulation/branch/master/graph/badge.svg?token=W88P95Fwo1)](https://codecov.io/gh/CoolONEOfficial/adventure-simulation)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae2245672f4a4e72b532c290e7595b78)](https://www.codacy.com/app/CoolONEOfficial/adventure-simulation?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=CoolONEOfficial/adventure-simulation&amp;utm_campaign=Badge_Grade)

2D platformer game


A [LibGDX](http://libgdx.badlogicgames.com/) project generated with [gdx-setup](https://github.com/czyzby/gdx-setup).

Project template included simple launchers and an empty `Game` extension.
Project uses `Overlap2d`

## Gradle

This project uses [Gradle](http://gradle.org/) to manage dependencies. Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands. Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `desktop:jar`: builds application's runnable jar, which can be found at `desktop/build/libs`.
- `desktop:run`: starts the application.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.