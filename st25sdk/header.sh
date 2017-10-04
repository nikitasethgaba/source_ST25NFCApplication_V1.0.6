#!/bin/bash

IFS=$'\012'
if [ -z "$1" ]
  then
    echo "No argument supplied"
	echo "you must provide directory name where to apply header.txt"
	exit 0;
fi

fileList=$(find $1 -name "*.cpp" -or -name "*.c" -or -name "*.h" -or -name "*.java")
for file in $fileList; do
    cat header.txt "$file" > "$file.new"
	mv $file.new $file
	echo "$file => OK"
done

#SED 
#
#for file in $fileList; do
#	sed -i "s/COPYRIGHT 2015 STMicroelectronics/COPYRIGHT 2017 STMicroelectronics/g" $file
#	sed -i "s/Author                    :  MMY Application Team/@author STMicroelectronics MMY Application team/g" $file
#done