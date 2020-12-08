JAVAC = javac -Werror -deprecation -source 1.6 -target 1.6

JAVAC = javac -Werror -deprecation

CAASETCJAR = ../lib/caas-etc.jar

BINARYSTRINGJAR = ../lib/binarystring.jar

default:
	$(JAVAC) na/na/na/BinaryString.java
	jar cf $(BINARYSTRINGJAR) na/na/na/BinaryString.class
	rm na/na/na/BinaryString.class
	find . \! -name BinaryString.java -and -type f -name '*.java' | xargs $(JAVAC) -cp $(BINARYSTRINGJAR)
	find . -name '*.class' | xargs jar cf $(CAASETCJAR)  

clean:
	rm -f $(CAASETCJAR)
	find . -name '*.class' -execdir rm -- {} \;




# javac na/na/na/cryptol/CryptolValue.java
# javac org/json/*.java
# javac -Xlint:unchecked na/na/na/*.java
# javac na/na/na/cryptol/caas/*.java
