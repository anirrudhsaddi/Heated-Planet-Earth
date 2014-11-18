# build.sh
find src -name "*.java" > sources.txt

# include the embedded neo4j
javac -source 1.6 -d bin -cp bin @sources.txt