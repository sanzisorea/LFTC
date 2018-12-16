import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ll(1)
// parsing string
public class Parser {

	Map<String, Set<String>> first(Grammar grammar)
	{
		Map<String, Set<String>> first = new HashMap<>();
		grammar.getTerminals().forEach((terminal) -> {
			first.put(terminal, new HashSet<>());
			first.get(terminal).add(terminal);
		});

		grammar.getNonTerminals().forEach((nonTerminal) ->
		{
			first.put(nonTerminal, new HashSet<>());
			grammar.getProductions().get(nonTerminal).forEach((production) ->
			{
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
			for (String nonTerminal : grammar.getNonTerminals())
			{
				int previousSize = first.get(nonTerminal).size();
				grammar.getProductions().get(nonTerminal).forEach((production) ->
				{
					String[] symbolsInProduction = grammar.splitProductionIntoSymbols(production.getValue());
					String firstSymbolInProduction = symbolsInProduction[0];

					if (firstSymbolInProduction.matches(Grammar.getNonTerminalRegex()))
					{
						List<Set<String>> previousFirstForAllProductionSymbols =  Stream.of(symbolsInProduction)
								.map(previousFirst::get).collect(Collectors.toList());
						Set<String> firstSymbolsFromNonTerminal = previousFirstForAllProductionSymbols.stream()
								.reduce(new HashSet<>(Collections.singletonList(Grammar.getEmptySymbol())),
										this::additionOfFirstElements);
						first.get(nonTerminal).addAll(firstSymbolsFromNonTerminal);
					}
				});

				if (previousSize != first.get(nonTerminal).size())
				{
					changed= true;
				}
			}
		} while (changed);
		return first;
	}

	Map<String, Set<String>> follow(Grammar grammar, Map<String, Set<String>> first)
	{
		return new HashMap<>();
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


	public Map<Pair<String, String>, Pair<Integer, String>> generateTable(Grammar grammar, Map<String, Set<String>> first, Map<String, Set<String>> follow)
	{
		Map<String, Set<Pair<Integer, String>>> productions = grammar.getProductions();
		Set<String> terminals = grammar.getTerminals();
		Set<String> nonTerminals = grammar.getNonTerminals();

		Map<Pair<String, String>, Pair<Integer, String>> table = new HashMap<>();

		for(String nonTerminal : nonTerminals)
		{
			for(Pair<Integer, String> production : productions.get(nonTerminal))
			{
				String[] firstTerminal = grammar.splitProductionIntoSymbols(production.getValue());
				Set<Set<String>> firstTerminals = Arrays.stream(firstTerminal).map(first::get).collect(Collectors.toSet());
				Set<String> firsts = firstTerminals.stream().reduce(new HashSet<>(Collections.singletonList(Grammar.getEmptySymbol())), this::additionOfFirstElements);

				for(String currentTerminal : firsts)
				{
					if(!currentTerminal.equals(Grammar.getEmptySymbol()))
					{
						table.put(new Pair<>(nonTerminal, currentTerminal), production);
					}
				}

				if(first.get(production.getValue()).contains(Grammar.getEmptySymbol()))
				{
					follow.get(nonTerminal).forEach((elem) -> table.put(new Pair<>(nonTerminal, elem), production));
				}
			}
		}

		for(String terminal : terminals)
		{
			table.put(new Pair<>(terminal, terminal), new Pair<>(-1, "pop"));
		}

		table.put(new Pair<>("$", "$"), new Pair<>(0, "acc"));


		return table;
	}

	List<String> parse(String inputString, Grammar grammar)
	{
		Map<String, Set<String>> first = first(grammar);
		Map<String, Set<String>> follow = follow(grammar, first);
		Map<Pair<String, String>, Pair<Integer, String>> ll1Table = generateTable(grammar, first, follow);

		List<String> inputList = new ArrayList<>(Arrays.asList(inputString.split("")));
		Collections.reverse(inputList);
		Stack<String> inputStack = new Stack<>();
		inputStack.push("$");
		inputStack.addAll(inputList);

		Stack<String> workingStack = new Stack<>();
		workingStack.push("$");
		workingStack.push(grammar.getStartingSymbol());


		List<String> outputStack = new ArrayList<>();
		outputStack.add(Grammar.getEmptySymbol());

		while(true)
		{
			String workingStackTop = workingStack.peek();
			String inputStackTop = inputStack.peek();
			Pair<String, String> currentTableCell = new Pair<>(workingStackTop, inputStackTop);

			if(workingStackTop.matches(Grammar.getNonTerminalRegex()) && ll1Table.containsKey(currentTableCell))
			{
				workingStack.pop();
				workingStack.push(ll1Table.get(currentTableCell).getValue());
				outputStack.add(ll1Table.get(currentTableCell).getKey().toString());
			}
			else if (workingStackTop.matches(Grammar.getTerminalRegex()) && ll1Table.containsKey(currentTableCell) && ll1Table.get(currentTableCell).getValue().equals("pop"))
			{
				inputStack.pop();
				workingStack.pop();
			}
			else if(workingStackTop.equals("$") && inputStackTop.equals("$"))
			{
				return outputStack;
			}
			else
			{
				return null;
			}
		}

	}
}
