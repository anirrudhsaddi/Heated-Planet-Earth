# build.sh
find src -name "*.java" > sources.txt
javac -source 1.6 -d bin -cp bin @sources.txt

