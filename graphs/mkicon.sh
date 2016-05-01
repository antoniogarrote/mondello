#!/bin/bash
mkdir Mondello.iconset
sips -z 16 16     mondello.png --out Mondello.iconset/icon_16x16.png
sips -z 32 32     mondello.png --out Mondello.iconset/icon_16x16@2x.png
sips -z 32 32     mondello.png --out Mondello.iconset/icon_32x32.png
sips -z 64 64     mondello.png --out Mondello.iconset/icon_32x32@2x.png
sips -z 128 128   mondello.png --out Mondello.iconset/icon_128x128.png
sips -z 256 256   mondello.png --out Mondello.iconset/icon_128x128@2x.png
sips -z 256 256   mondello.png --out Mondello.iconset/icon_256x256.png
sips -z 512 512   mondello.png --out Mondello.iconset/icon_256x256@2x.png
sips -z 512 512   mondello.png --out Mondello.iconset/icon_512x512.png
cp mondello.png Mondello.iconset/icon_512x512@2x.png
iconutil -c icns Mondello.iconset
rm -R Mondello.iconset
