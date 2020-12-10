
HOST = localhost

# PORT = 65521

PORT = 65519

CLASSPATH = -cp ../lib/caas-etc.jar:../lib/binarystring.jar:.

SYSPROPS = -Dna.na.na.cryptol.caas.hostOrIP=$(HOST) -Dna.na.na.cryptol.caas.port=$(PORT)

JAVAC = javac $(CLASSPATH) -Werror -deprecation -source 1.6 -target 1.6

JAVAC = javac $(CLASSPATH) -Werror -deprecation

JAVA = java $(CLASSPATH) $(SYSPROPS)


default:
	$(JAVAC) Hammer.java
	$(JAVA) Hammer
	$(JAVAC) CryptolSequenceTest.java
	$(JAVA) CryptolSequenceTest
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
	../server/macOS/cryptol-remote-api socket --host 0.0.0.0 --port $(PORT) &

netstring:
	$(JAVAC) NetstringTest.java
	java $(CLASSPATH) NetstringTest
	
