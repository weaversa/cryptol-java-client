JAVAC = javac -Werror

CASSETCJAR = ../lib/caas-etc.jar

default:
	find . -type f -name '*.java' | xargs $(JAVAC)
	find . -name '*.class' | xargs jar cf $(CASSETCJAR)  

clean:
	rm -f $(CASSETCJAR)
	find . -name '*.class' -execdir rm -- {} \;




# javac na/na/na/cryptol/CryptolValue.java
# javac org/json/*.java
# javac -Xlint:unchecked na/na/na/*.java
# javac na/na/na/cryptol/caas/*.java
