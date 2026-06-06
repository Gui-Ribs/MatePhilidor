GRADLE := ./gradlew
GRADLW_WINDOWS := .\gradlew.bat

.PHONY: run format check build clean help

## run: compila e executa a aplicação
run:
	$(GRADLE) run

## run-windows: compila e executa a aplicação no Windows
run-windows:
	$(GRADLW_WINDOWS) run

## format: aplica o formatador de código (google-java-format)
format:
	$(GRADLE) spotlessApply

## check: verifica formatação sem alterar arquivos
check:
	$(GRADLE) spotlessCheck

## build: compila e empacota o projeto
build:
	$(GRADLE) build

## clean: remove artefatos de build
clean:
	$(GRADLE) clean

## jar: gera o arquivo .jar
jar:
	$(GRADLE) jar

## help: lista os alvos disponíveis
help:
	@grep -E '^## ' $(MAKEFILE_LIST) | sed 's/## //'
