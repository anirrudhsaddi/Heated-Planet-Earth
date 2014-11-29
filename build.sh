# build.sh
find src -name "*.java" > sources.txt

# include the embedded neo4j
javac -target 1.6 -d bin -cp "resource/neo4j/*.jar;bin" @sources.txt