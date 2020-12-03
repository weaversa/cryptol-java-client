default:
	# find . -name '*.class' -execdir rm -- {} +
	find . -type f -name '*.java' | xargs javac
	find . -name '*.class' | xargs jar cvf ../lib/caas-etc.jar  
	# javac na/na/na/cryptol/CryptolValue.java
	#javac org/json/*.java
	#javac -Xlint:unchecked na/na/na/*.java
	#javac na/na/na/cryptol/caas/*.java
