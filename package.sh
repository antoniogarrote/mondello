#!/bin/bash
sbt electronMain
rm -rf Mondello*
rm -rf electon-app/lib/mondello-*.js
cp -f target/scala-2.11/mondello-fastopt.js electron-app/lib/mondello.js
cp -f target/scala-2.11/mondello-jsdeps.js electron-app/lib/mondello-jsdeps.js
cp assets/index.prod.html electron-app/index.html
electron-packager electron-app Mondello --platform=darwin --arch=x64 --version=0.36.0 --icon=graphs/Mondello.icns
cp assets/index.dev.html  electron-app/index.html
