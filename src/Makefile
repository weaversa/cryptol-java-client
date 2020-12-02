EXTRAS = Makefile test/TestHarness.java

SOURCES = org/CaaS/CaaS.java

CLASSES = $(SOURCES:org/CaaS/%.java=org/CaaS/%.class)

JAVAC = javac
JAVA  = java
JFLAGS =
CLASSPATH = -cp org/Caas/:lib/json-java.jar

default:
	# find . -name '*.class' -execdir rm -- {} +
	find . -type f -name '*.java' | xargs javac
	# javac na/na/na/cryptol/CryptolValue.java
	#javac org/json/*.java
	#javac -Xlint:unchecked na/na/na/*.java
	#javac na/na/na/cryptol/caas/*.java
