#!/bin/sh

## 输入加密apk目录
jarsigner -verbose -keystore onebuy.keystore -signedjar onebuy.apk $1 diamond
