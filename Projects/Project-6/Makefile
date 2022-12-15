CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/classes \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/classes:$(CLASSPATH) cs3700.project6.Main \$$@" > 3700kvstore

package:
	@zip -rv Project-6 src/ target/dependency/ Makefile README.md

clean:
	@rm -rv target/classes/ 3700kvstore Project-6.zip || true