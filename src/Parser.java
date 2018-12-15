import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// ll(1)
// parsing string
public class Parser {
	public Map<String, Set<String>> first(Grammar grammar) {
		Map<String, Set<String>> first = new HashMap<>();
		grammar.getTerminals().forEach((terminal) -> {
			first.put(terminal, new HashSet<>());
			first.get(terminal).add(terminal);
		});
		grammar.getNonTerminals().forEach((nonTerminal) -> {
			first.put(nonTerminal, new HashSet<>());
			grammar.getProductions().get(nonTerminal).forEach((production) -> {
				String firstSymbolInProduction = grammar.splitProductionIntoSymbols(production)[0];
				if (firstSymbolInProduction.matches(Grammar.getTerminalRegex() + "|" + Grammar.getEmptySymbol())) {
					first.get(nonTerminal).add(firstSymbolInProduction);
				}
			});
		});
		boolean changed;
		do {
			changed = false;
			Map<String, Set<String>> previousFirst = new HashMap<>(first);
			for (String nonTerminal : grammar.getNonTerminals()) {
				int previousSize = first.get(nonTerminal).size();
				grammar.getProductions().get(nonTerminal).forEach((production) -> {
					String firstSymbolInProduction = grammar.splitProductionIntoSymbols(production)[0];
					if (firstSymbolInProduction.matches(Grammar.getNonTerminalRegex())) {
						first.get(nonTerminal).addAll(previousFirst.get(firstSymbolInProduction));
					}
				});
				if (previousSize != first.get(nonTerminal).size()) {
					changed= true;
				}
			}
		} while (changed);
		return first;
	}

	public String parseSequence(Grammar contextFreeGrammar, String inputSequence) {
		return "";
	}
}
