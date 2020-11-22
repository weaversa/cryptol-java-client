CLASSES = $(SOURCES:org/CaaS/%.java=org/CaaS/%.class)

JAVAC = javac
JAVA  = java
JFLAGS =
CLASSPATH = -cp org/Caas/:lib/json-java.jar

default:
	killall -v cryptol-remote-api || true
	~/unix-roots/cryptol-2.9.1.99-2020-10-20/bin/cryptol-remote-api --port 65521 --public &
	rm -f CaaSTest.class
	javac -cp ../src:. JSONFlavoredCaaSTest.java
	javac -cp ../src:. CryptolValueFlavoredCaaSTest.java
	javac -cp ../src:. StringFlavoredCaaSTest.java
	#java -cp ../src:. JSONFlavoredCaaSTest localhost 65521
	#java -cp ../src:. CryptolValueFlavoredCaaSTest
	java -cp ../src:. StringFlavoredCaaSTest
