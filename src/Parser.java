import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				String firstSymbolInProduction = grammar.splitProductionIntoSymbols(production.getValue())[0];
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
					String[] symbolsInProduction = grammar.splitProductionIntoSymbols(production.getValue());
					String firstSymbolInProduction = symbolsInProduction[0];
					if (firstSymbolInProduction.matches(Grammar.getNonTerminalRegex())) {
						List<Set<String>> previousFirstForAllProductionSymbols =  Stream.of(symbolsInProduction)
								.map(previousFirst::get).collect(Collectors.toList());
						Set<String> firstSymbolsFromNonTerminal = previousFirstForAllProductionSymbols.stream()
								.reduce(new HashSet<>(Collections.singletonList(Grammar.getEmptySymbol())),
										this::additionOfFirstElements);
						first.get(nonTerminal).addAll(firstSymbolsFromNonTerminal);
					}
				});
				if (previousSize != first.get(nonTerminal).size()) {
					changed= true;
				}
			}
		} while (changed);
		return first;
	}

	private Set<String> additionOfFirstElements(Set<String> firstSet, Set<String> secondSet) {
		Set<String> result = new HashSet<>();
		firstSet.forEach((firstSetElement) -> {
			if (firstSetElement.equals(Grammar.getEmptySymbol())) {
				result.addAll(secondSet);
			} else {
				result.add(firstSetElement);
			}
		});
		return result;
	}

//	public Map<String, Set<String>> follow(Grammar grammar) {
//
//	}

	public String parseSequence(Grammar contextFreeGrammar, String inputSequence) {
		return "";
	}
}
