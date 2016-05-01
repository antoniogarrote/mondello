![Mondello logo](https://github.com/antoniogarrote/mondello/blob/master/graphs/mondello_small.png?raw=true)

## About

Mondello is a free graphical client for Docker, Docker Machine, Docker Compose for OSX.

[Installer for version 0.2.1](https://github.com/antoniogarrote/mondello/releases/download/v0.2.1/Mondello.dmg)

![Mondello screenshot](http://antoniogarrote.github.io/mondello/images/screenshot1.png)

Read more [here](http://antoniogarrote.github.io/mondello/).

## Developing

The application is written in ScalaJS and it uses Electron for the user interface.

To build the application run

```bash
sbt electronMain
```

You can leave sbt running in the background and looking for changes using the command:

```bash
sbt ~electronMain
```

At the same time you can start the application to see the result of the changes in your code using the binary Electron distribution, you will also need to pass the `ENV=development` environment variable to see traces and the development tools:

```bash
ENV=development /path/to/Electron.app/Contents/MacOS/Electron /path/to/mondello/electron-app/main.js 
```
Once Electron opens with the application you can just select `reload` from the menu every time you make changes.

To package the OSX app use the `package.sh` script:

```bash
./package.sh
```

After building the app you can create a dmg package using the `build.sh` script:

```bash
./build.sh
```

## License

Release under the MIT license.

(c) 2016 Antonio Garrote
