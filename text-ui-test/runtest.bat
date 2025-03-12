@ECHO OFF

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM Delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM Compile all Java files recursively
for /r ..\src\main\java %%f in (*.java) do (
    javac -cp ..\src\main\java -Xlint:none -d ..\bin "%%f"
)
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)
REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
java -classpath ..\bin eggo.Eggo < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT /W

IF ERRORLEVEL 1 (
    echo Test result: FAILED
    exit /b 1
) ELSE (
    echo Test result: PASSED
    exit /b 0
)