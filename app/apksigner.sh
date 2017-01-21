#!/bin/sh

## 输入加密apk目录
jarsigner -verbose -keystore ./interestbar.keystore -signedjar ./onebuy.apk $1 interestbar.keystore
