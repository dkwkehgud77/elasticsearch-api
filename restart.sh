pkill -9 -f 'search-0.0.1-SNAPSHOT.jar'
./gradlew clean
./gradlew build
java -jar ./build/libs/search-0.0.1-SNAPSHOT.jar