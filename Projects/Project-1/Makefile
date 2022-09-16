SPACE := $(subst ,, )

SOURCE_FILES := $(shell find src/main/java -name '*.java')
LIBRARY_FILES := $(wildcard target/dependency/*)
CLASSPATH := $(subst $(SPACE),:,$(LIBRARY_FILES))

build:
	javac -d target/out -cp "$(CLASSPATH)" $(SOURCE_FILES)

clean:
	rm -r target/out project1.zip || true

package: clean
	zip -r project1.zip src target client Makefile README.md secret_flags