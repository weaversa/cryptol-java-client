EXTRAS = Makefile test/TestHarness.java

SOURCES = org/CaaS/CaaS.java

CLASSES = $(SOURCES:org/CaaS/%.java=org/CaaS/%.class)

JAVAC = javac
JAVA  = java
JFLAGS =
CLASSPATH = -cp org/Caas/:lib/json-java.jar

default: $(CLASSES)

.SUFFIXES: .java .class
.java.class:
	$(JAVAC) $(CLASSPATH) $(JFLAGS) $*.java

test/TestHarness.class: test/TestHarness.java
	$(JAVAC) -cp lib/CaaS.jar:lib/json-java.jar test/TestHarness.java

CaaS.jar: $(CLASSES)
	jar cvfe lib/CaaS.jar CaaS $(CLASSES)

run: CaaS.jar test/TestHarness.class
	$(JAVA) -cp lib/CaaS.jar:lib/json-java.jar:test TestHarness $(IP) $(PORT)

clean:
	rm -rf *~ */*~ $(CLASSES) *.dSYM lib/CaaS.jar

edit:
	emacs -nw $(SOURCES) $(EXTRAS)
