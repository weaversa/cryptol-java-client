
CLASSPATH = ../lib/caas-etc.jar:.

default:
	# killall -v cryptol-remote-api || true
	# ~/unix-roots/cryptol-2.9.1.99-2020-10-20/bin/cryptol-remote-api --port 65521 --public &
	javac -cp $(CLASSPATH) NetstringTest.java
	java  -cp $(CLASSPATH) NetstringTest
	javac -cp $(CLASSPATH) sha224.java
	java  -cp $(CLASSPATH) sha224
	javac -cp $(CLASSPATH) ModuleSpecificCaaSTest.java
	java  -cp $(CLASSPATH) ModuleSpecificCaaSTest
	javac -cp $(CLASSPATH) PrimitiveCaaSTest.java
	java  -cp $(CLASSPATH) PrimitiveCaaSTest localhost 65521
