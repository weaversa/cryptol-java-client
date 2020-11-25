CLASSES = $(SOURCES:org/CaaS/%.java=org/CaaS/%.class)

JAVAC = javac
JAVA  = java
JFLAGS =
CLASSPATH = -cp org/Caas/:lib/json-java.jar

default:
	# killall -v cryptol-remote-api || true
	# ~/unix-roots/cryptol-2.9.1.99-2020-10-20/bin/cryptol-remote-api --port 65521 --public &
	rm -f CaaSTest.class
	javac -cp ../src:. ModuleSpecificCaaSTest.java
	java -cp ../src:. ModuleSpecificCaaSTest
	javac -cp ../src:. PrimitiveCaaSTest.java
	java -cp ../src:. PrimitiveCaaSTest localhost 65521
