# dependency-graph

Takes a file of dependencies and prints out its dependency graph

The input file should be in the form "X->Y", stating that X depends on Y

To run you need Java8 installed
In the root directory just run
```
./gradlew run
```
By default it will take the graph.txt in the root directory as input
To give a different file as input use the --args, for example:
```
./gradlew run --args "tests/smallGraph.txt"
```
There are a few test files under the directory tests
All output is printed to standard out