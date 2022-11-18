CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/classes \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/classes:$(CLASSPATH) cs3700.project5.Main \$$@" > 3700crawler

package:
	@zip -rv Project-5 src/ target/dependency/ Makefile README.md secret_flags

clean:
	@rm -rv target/classes 3700crawler Project-5.zip || true