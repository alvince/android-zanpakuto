#!/usr/bin/env bash

module=$1

echo "prepare to publish module: $module"

sed -i -c "/release.offline=/ s/=.*/=false/" $module/gradle.properties

./gradlew $module:clean $module:build
./gradlew $module:publish

sed -i -c "/release.offline=/ s/=.*/=true/" $module/gradle.properties
rm $module/gradle.properties-c
