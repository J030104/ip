#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]; then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]; then
    rm ACTUAL.TXT
fi

if [ -e "./data" ]; then
    rm -rf ./data
fi

# Compile the code, terminate on error
if ! javac -cp ../src/main/java -Xlint:none -d ../bin $(find ../src/main/java -name "*.java"); then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ../bin eggo.Eggo < input.txt > ACTUAL.TXT

# Ensure EXPECTED.TXT exists before copying
if [ ! -f EXPECTED.TXT ]; then
    echo "Error: EXPECTED.TXT not found!"
    exit 1
fi

# convert to UNIX format (ensure dos2unix is installed)
if command -v dos2unix &> /dev/null; then
    cp EXPECTED.TXT EXPECTED-UNIX.TXT
    dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT
else
    echo "Warning: dos2unix not installed, skipping conversion."
fi

# Remove trailing spaces from both files
sed -i 's/[ \t]*$//' ACTUAL.TXT
sed -i 's/[ \t]*$//' EXPECTED-UNIX.TXT

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi