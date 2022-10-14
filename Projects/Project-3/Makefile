CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/out \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/out:$(CLASSPATH) cs3700.project3.Main \$$@" > router

package:
	@zip -rv Project-3 src/ target/dependency/ Makefile README.md

clean:
	@rm -rv target/out/ router Project-3.zip || true