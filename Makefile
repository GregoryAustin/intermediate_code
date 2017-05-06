ifeq (run,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

main:
	javac -cp .:lib/junit-4.12.jar src/*.java src/lexer/*.java src/parser/*.java src/semantics/*.java src/codegen/*.java -d "out/"

run:
	java -cp "out/" Main $(RUN_ARGS)

clean:
	rm -R out/* lexeroutput ParseTree PrunedParseTree SymbolTables spl.basic