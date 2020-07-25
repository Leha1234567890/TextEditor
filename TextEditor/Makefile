.PHONY: run build

.SUFFIXES:

NAME=TextEditor

JAR=build/libs/$(NAME).jar

SRCS=$(wildcard src/*) $(wildcard src/**/*)

run: $(JAR)
	java -jar $^

build: $(JAR)
	@echo Built $(JAR).

$(JAR): $(SRCS)
	gradlew jar
