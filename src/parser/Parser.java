package parser;

import grammar.Grammar;
import compiler.Scanner;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// ll(1)
// parsing string
public class Parser {

	Map<String, Set<String>> first(Grammar grammar) {
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
			if (firstSetElement.equals(Grammar.getEmptySymbol()) && secondSet != null) {
				result.addAll(secondSet);
			} else {
				result.add(firstSetElement);
			}
		});
		return result;
	}

	public Map<String, Set<String>> follow(Grammar grammar, Map<String, Set<String>> first) {
		Map<String, Set<String>> follow = new HashMap<>();
		grammar.getNonTerminals().forEach((nonTerminal) -> follow.put(nonTerminal, new HashSet<>()));
		follow.get(grammar.getStartingSymbol()).add(Grammar.getEmptySymbol());
		boolean changed;
		do {
			changed = false;
			for(String nonTerminal : grammar.getNonTerminals()) {
				int previousFollowSize = follow.get(nonTerminal).size();
				grammar.getProductions().forEach((nonTerminalInProduction, productions) -> {
					for (Pair<Integer, String> production : productions) {
						String productionRightHandSide = production.getValue();

						if (productionRightHandSide.contains(nonTerminal)) {
							if (productionRightHandSide.endsWith(nonTerminal)) {
								follow.get(nonTerminal).addAll(follow.get(nonTerminalInProduction));
							}
							String[] productionWithoutNonTerminal = productionRightHandSide.split(nonTerminal);
							for (int i = 1; i < productionWithoutNonTerminal.length; i++) {
								Set<String> firstAfterNonTerminal = Stream
										.of(grammar.splitProductionIntoSymbols(productionWithoutNonTerminal[i]))
										.map(first::get)
										.reduce(new HashSet<>(Collections.singletonList(Grammar.getEmptySymbol())),
												this::additionOfFirstElements);

								if (firstAfterNonTerminal.contains(Grammar.getEmptySymbol())) {
									follow.get(nonTerminal).addAll(follow.get(nonTerminalInProduction));
								}
								follow.get(nonTerminal).addAll(firstAfterNonTerminal);
							}

						}
					}
				});
				if (previousFollowSize != follow.get(nonTerminal).size()) {
					changed = true;
				}
			}
		} while (changed);
		return follow;
	}

	private Map<Pair<String, String>, Pair<Integer, String>> generateTable(
			Grammar grammar, Map<String, Set<String>> first, Map<String, Set<String>> follow) {
		Map<String, Set<Pair<Integer, String>>> productions = grammar.getProductions();
		Set<String> terminals = grammar.getTerminals();
		Set<String> nonTerminals = grammar.getNonTerminals();

		Map<Pair<String, String>, Pair<Integer, String>> table = new HashMap<>();

		for(String nonTerminal : nonTerminals) {
			for(Pair<Integer, String> production : productions.get(nonTerminal)) {
				String[] firstTerminal = grammar.splitProductionIntoSymbols(production.getValue());
				List<Set<String>> firstTerminals = Arrays.stream(firstTerminal).map(first::get).collect(Collectors.toList());
				Set<String> firsts = firstTerminals.stream().reduce(new HashSet<>(Collections.
						singletonList(Grammar.getEmptySymbol())), this::additionOfFirstElements);
//				System.out.println("production " + production.getValue());
//				System.out.print("first Terminal ");
//				for (String t : firstTerminal) {
//					System.out.print(t + " ");
//				}
//				System.out.println();
//				System.out.println("first terminals " + firstTerminals + " firsts " + firsts);
				for(String currentTerminal : firsts) {
					if(!currentTerminal.equals(Grammar.getEmptySymbol())) {
						table.put(new Pair<>(nonTerminal, currentTerminal), production);
					}
				}

				if(firsts.contains(Grammar.getEmptySymbol())) {
					follow.get(nonTerminal).forEach((elem) -> {
						if (elem.equals(Grammar.getEmptySymbol())) {
							table.put(new Pair<>(nonTerminal, "$"), production);
						} else {
							table.put(new Pair<>(nonTerminal, elem), production);
						}
					});
				}
			}
		}
		for(String terminal : terminals) {
			table.put(new Pair<>(terminal, terminal), new Pair<>(-1, "pop"));
		}
		table.put(new Pair<>("$", "$"), new Pair<>(0, "acc"));
		return table;
	}

	public List<String> parse(String inputString, Grammar grammar) {
		Map<String, Set<String>> first = first(grammar);
		Map<String, Set<String>> follow = follow(grammar, first);
		Map<Pair<String, String>, Pair<Integer, String>> ll1Table = generateTable(grammar, first, follow);

//		System.out.println("First");
//		first.forEach((key, value) -> System.out.println(key + " : " + value));
//		System.out.println("Follow");
//		follow.forEach((key, value) -> System.out.println(key + " : " + value));
//		System.out.println("Table");
//		ll1Table.forEach((key, value) -> System.out.println(key + " : " + value));


		List<String> inputList = new ArrayList<>(Arrays.asList(inputString.split("(?<=>)")));
		System.out.println(inputList);
		Collections.reverse(inputList);
		Stack<String> inputStack = new Stack<>();
		inputStack.push("$");
		inputStack.addAll(inputList);

		Stack<String> workingStack = new Stack<>();
		workingStack.push("$");
		workingStack.push(grammar.getStartingSymbol());

		List<String> outputStack = new ArrayList<>();
		outputStack.add(Grammar.getEmptySymbol());

		while(true) {
//			System.out.println("Input stack: " + inputStack);
//			System.out.println("Working stack: " + workingStack);
//			System.out.println("Output stack: " + outputStack);
			String workingStackTop = workingStack.peek();
			String inputStackTop = inputStack.peek();
			Pair<String, String> currentTableCell = new Pair<>(workingStackTop, inputStackTop);

			if(workingStackTop.matches(Grammar.getNonTerminalRegex()) && ll1Table.containsKey(currentTableCell)) {
				workingStack.pop();
				String currentCellValue = ll1Table.get(currentTableCell).getValue();
				List<String> symbolsInCurrentCellValue = Arrays.asList(currentCellValue.split("(?<=>)"));
				Collections.reverse(symbolsInCurrentCellValue);
				symbolsInCurrentCellValue.forEach((symbol) -> {
					if (!symbol.equals(Grammar.getEmptySymbol())) {
						workingStack.push(symbol);
					}
				});
				if (outputStack.get(outputStack.size() - 1).equals(Grammar.getEmptySymbol())) {
					outputStack.remove(Grammar.getEmptySymbol());
				}
				outputStack.add(ll1Table.get(currentTableCell).getKey().toString());
			} else if (workingStackTop.matches(Grammar.getTerminalRegex()) &&
					ll1Table.containsKey(currentTableCell) && ll1Table.get(currentTableCell).getValue().equals("pop")) {
				inputStack.pop();
				workingStack.pop();
			} else if(workingStackTop.equals("$") && inputStackTop.equals("$")) {
				return outputStack;
			} else {
				StringBuilder input = new StringBuilder();
				while (!inputStack.peek().equals("$")) {
					input.append(inputStack.pop());
				}
				return new ArrayList<>(Arrays.asList("input does not match when parsing", input.toString()));
			}
		}
	}

	public List<String> parseFromScanner(Grammar grammar, Scanner scanner, String programFilename) {
		StringBuilder inputSequence = new StringBuilder();
		scanner.scanProgram(programFilename).forEach((pifElement) -> {
		 	inputSequence.append("<").append(pifElement.getTokenCode()).append(">");
		 });
		return parse(inputSequence.toString(), grammar);
	}
}
