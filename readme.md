# Sudoku
Minimalistic Sudoku implementation intended for a command line interface. Made
for an AP Computer Science project.

## Building
You must use the `jar` task, because using Gradle's default `run` task causes
issues with processing system input.
- On Linux: `$ ./gradlew jar`
- On Windows: `$ ./gradlew.bat jar`

### Build requirements
You need a system running Java version at least 15. This is because language
level 15 introduces text blocks, which are used in `Board.java` to represent
the default gameboard.

## Running
There's two ways to run the application. The first is to use the script:
```
$ ./sudoku.sh
```
If there's any issues with the script's execution and you're using a Linux
device, you need to mark the script as an executable.
```
$ chmod +x sudoku.sh
```
The second is to use Java's `-jar` flag.
```
$ java -jar ./build/libs/sudoku-1.0.0.jar
```

## Licensing and credits
This project is licensed under the MIT license - basically, I could care less
what you do with the code - it's not going to be very useful anyways.

The solving portion of this project is ported from 
[this tutorial](https://www.baeldung.com/java-sudoku). The rest of the code
is my own.
