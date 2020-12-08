JAVAC = javac -Werror -deprecation -source 1.6 -target 1.6

JAVAC = javac -Werror -deprecation

CAASETCJAR = ../lib/caas-etc.jar

default:
	find . -type f -name '*.java' | xargs $(JAVAC)
	find . -name '*.class' | xargs jar cf $(CAASETCJAR)  

clean:
	rm -f $(CAASETCJAR)
	find . -name '*.class' -execdir rm -- {} \;




# javac na/na/na/cryptol/CryptolValue.java
# javac org/json/*.java
# javac -Xlint:unchecked na/na/na/*.java
# javac na/na/na/cryptol/caas/*.java
