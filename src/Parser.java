import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// ll(1)
// parsing string
public class Parser {
	private static final String PRODUCTION_REGEX = "";
	public Map<String, Set<String>> first(Grammar grammar) {
		Map<String, Set<String>> first = new HashMap<>();
		grammar.getTerminals().forEach((terminal) -> {
			first.put(terminal, new HashSet<>());
			first.get(terminal).add(terminal);
		});
		grammar.getNonTerminals().forEach((nonTerminal) -> {
			first.put(nonTerminal, new HashSet<>());
			grammar.getProductions().get(nonTerminal).forEach((production) -> {
//				if (production.matches())
			});
		});
		return first;
	}

	public String parseSequence(Grammar contextFreeGrammar, String inputSequence) {
		return "";
	}
}
