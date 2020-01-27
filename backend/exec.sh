echo "Building .jar executable"
gradle fatJar
mv build/libs/Conference\ Management\ System-all-0.1.jar CMT.jar
java -jar CMT.jar test normal-persistent
