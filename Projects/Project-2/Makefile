CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/out \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/out:$(CLASSPATH) com.cs3700.project2.Main \$$@" > 3700ftp

package:
	@zip -rv Project-2 src/ target/dependency/ Makefile README.md

clean:
	@rm -rv target/out/ 3700ftp Project-2.zip || true