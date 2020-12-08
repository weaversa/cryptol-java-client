
HOST = localhost

# PORT = 65521

PORT = 65519

CLASSPATH = -cp ../lib/caas-etc.jar:../lib/binarystring.jar:.

SYSPROPS = -Dna.na.na.cryptol.caas.hostOrIP=$(HOST) -Dna.na.na.cryptol.caas.port=$(PORT)

JAVAC = javac $(CLASSPATH) -Werror -deprecation -source 1.6 -target 1.6

JAVAC = javac $(CLASSPATH) -Werror -deprecation

JAVA = java $(CLASSPATH) $(SYSPROPS)


default:
	$(JAVAC) CryptolSequenceTest.java
	$(JAVA) CryptolSequenceTest.java
	$(JAVAC) sha224.java
	$(JAVA) sha224
	$(JAVAC) ModuleSpecificCaaSTest.java
	$(JAVA) ModuleSpecificCaaSTest
	$(JAVAC) PrimitiveCaaSTest.java
	$(JAVA) PrimitiveCaaSTest

clean:
	killall -v cryptol-remote-api || true
	find . -name '*.class' -execdir rm -- {} \;

server:
	killall -v cryptol-remote-api || true
	~/unix-roots/cryptol-2.9.1.99-2020-10-20/bin/cryptol-remote-api --port $(PORT) --public &

netstring:
	$(JAVAC) NetstringTest.java
	java $(CLASSPATH) NetstringTest
	
