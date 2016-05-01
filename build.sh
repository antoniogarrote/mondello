#!/bin/bash
rm -rf dist/osx
electron-builder Mondello-darwin-x64/Mondello.app --platform=osx --config=builder.json --out=dist/osx
