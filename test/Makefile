CLASSES = $(SOURCES:org/CaaS/%.java=org/CaaS/%.class)

JAVAC = javac
JAVA  = java
JFLAGS =
CLASSPATH = -cp org/Caas/:lib/json-java.jar

default:
	# ~/unix-roots/cryptol-2.9.1.99-2020-10-20/bin/cryptol-remote-api --port 65521 --public &
	rm -f CaaSTest.class
	javac -cp ../src:. CaaSTest.java
	java -cp ../src:. CaaSTest localhost 65521
