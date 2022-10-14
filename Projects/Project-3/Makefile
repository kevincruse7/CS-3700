CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/classes \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/classes:$(CLASSPATH) cs3700.project3.Main \$$@" > 3700router

package:
	@zip -rv Project-3 src/ target/dependency/ Makefile README.md

clean:
	@rm -rv target/classes 3700router Project-3.zip || true