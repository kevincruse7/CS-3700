CLASSPATH = $(subst $(subst ,, ),:,$(wildcard target/dependency/*))

build:
	@javac -verbose \
		-d target/classes \
		-cp $(CLASSPATH) \
		$(shell find src/main/java -name "*.java")
	@echo "#!/bin/sh\njava -cp target/classes:$(CLASSPATH) cs3700.project4.SenderMain \$$@" > 3700send
	@echo "#!/bin/sh\njava -cp target/classes:$(CLASSPATH) cs3700.project4.ReceiverMain \$$@" > 3700recv

package:
	@zip -rv Project-4 src/ target/dependency/ Makefile README.md

clean:
	@rm -rv target/classes 3700send 3700recv output.txt Project-4.zip || true