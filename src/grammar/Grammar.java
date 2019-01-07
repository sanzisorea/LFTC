package grammar;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
	private Set<String> terminals;
	private Set<String> nonTerminals;
	private String startingSymbol;
	private Map<String, Set<Pair<Integer, String>>> productions;
	//	private static final String NON_TERMINAL_REGEX = "[A-Z]";
//	private static final String TERMINAL_REGEX = "[a-z]";
	/*
	<S>,<A>,<B>
	"a","b"
	<S>
	<S>->"a"<A>|~
	<A>->"a"|"a"<B>
	<B>->"b"
	<B>->"b"<B>
	*/
	private static final String EMPTY_SYMBOL = "~";
	private static final String NON_TERMINAL_REGEX = "<[A-Z_]+>";
	private static final String TERMINAL_REGEX = "(<([0-9]|[1-2][0-9]|3[0-3])>)";
	//for example
//	private static final String TERMINAL_REGEX = "(<[+*()a]>)";
	private static final String NON_TERMINAL_LINE_REGEX = NON_TERMINAL_REGEX + "(," + NON_TERMINAL_REGEX + ")*";
	private static final String TERMINAL_LINE_REGEX = TERMINAL_REGEX + "(," + TERMINAL_REGEX + ")*";
	private static final String PRODUCTION = "(( ?(" + NON_TERMINAL_REGEX + "|" + TERMINAL_REGEX +
			" ?))+|" + EMPTY_SYMBOL + ")";

	private static final String PRODUCTION_REGEX = NON_TERMINAL_REGEX + " ?-> ?" + PRODUCTION + "( ?\\| ?" + PRODUCTION + ")*";
	private static final String INVALID_FORMAT = "Invalid format";
	private Integer nextProductionNumber = 1;

	private void initializeFields() {
		nonTerminals = new HashSet<>();
		terminals = new HashSet<>();
		productions = new HashMap<>();
	}

	public void readFromFile(String filename) throws Exception {
		initializeFields();
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
			String line = bufferedReader.readLine();
			symbolsToSet(line, nonTerminals, NON_TERMINAL_LINE_REGEX, NON_TERMINAL_REGEX);

			line = bufferedReader.readLine();
			symbolsToSet(line, terminals, TERMINAL_LINE_REGEX, TERMINAL_REGEX);

			line = bufferedReader.readLine();
			if (line == null || !line.matches(NON_TERMINAL_REGEX) || !nonTerminals.contains(line)) {
				throw new Exception(INVALID_FORMAT);
			}
			startingSymbol = line;

			while ((line = bufferedReader.readLine()) != null) {
				addProductionToNonTerminal(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addProductionToNonTerminal(String line) throws Exception {
		if (!line.matches(PRODUCTION_REGEX)) {
			throw new Exception(INVALID_FORMAT);
		}
		String[] production = line.split(" ?-> ?");
		String nonTerminal = production[0];
		String[] rightHandSide = production[1].split(" ?\\| ?");
		if (!nonTerminals.contains(nonTerminal)) {
			throw new Exception(INVALID_FORMAT);
		}

		for (String productionSymbols : rightHandSide) {
			for (String symbol : splitProductionIntoSymbols(productionSymbols)) {
				if (!terminals.contains(symbol) && !nonTerminals.contains(symbol) && !symbol.equals(EMPTY_SYMBOL)) {
					throw new Exception(INVALID_FORMAT);
				}
			}
		}

		if (!productions.containsKey(nonTerminal)) {
			productions.put(nonTerminal, new HashSet<>());
		}
		for (String currentProduction : rightHandSide) {
			productions.get(nonTerminal).add(new Pair<>(nextProductionNumber++, currentProduction));
		}
	}

	public String[] splitProductionIntoSymbols(String production) {
		return production.split("(?<=>)");
	}

	private void symbolsToSet(String line, Set<String> symbolList, String lineRegex, String symbolRegex) throws Exception {
		if (line == null || !line.matches(lineRegex)) {
			throw new Exception(INVALID_FORMAT);
		}
		String[] symbols = line.split(",");
		for (int i = 0; i < symbols.length; i++) {
			if (symbols[i].equals("\"")) {
				symbols[i] = "\",\"";
			}
			if (!symbols[i].matches(symbolRegex)) {
				throw new Exception(INVALID_FORMAT);
			}
		}
		symbolList.addAll(Arrays.asList(symbols));
	}

	public String getProductionByNumber(int productionNumber) {
		StringBuilder production = new StringBuilder();
		for (Map.Entry<String, Set<Pair<Integer, String>>> nonTerminal : productions.entrySet()) {
			for (Pair<Integer, String> nonTerminalProduction : nonTerminal.getValue()) {
				if (nonTerminalProduction.getKey() == productionNumber) {
					production.append(productionNumber)
							.append(": ")
							.append(nonTerminal.getKey())
							.append(" -> ")
							.append(nonTerminalProduction.getValue());
					return production.toString();
				}
			}
		}
		return production.toString();
	}

	public Set<String> getTerminals() {
		return terminals;
	}

	public void setTerminals(Set<String> terminals) {
		this.terminals = terminals;
	}

	public Set<String> getNonTerminals() {
		return nonTerminals;
	}

	public void setNonTerminals(Set<String> nonTerminals) {
		this.nonTerminals = nonTerminals;
	}

	public String getStartingSymbol() {
		return startingSymbol;
	}

	public void setStartingSymbol(String startingSymbol) {
		this.startingSymbol = startingSymbol;
	}

	public Map<String, Set<Pair<Integer, String>>> getProductions() {
		return productions;
	}

	public void setProductions(Map<String, Set<Pair<Integer, String>>> productions) {
		this.productions = productions;
	}

	public static String getEmptySymbol() {
		return EMPTY_SYMBOL;
	}

	public static String getNonTerminalRegex() {
		return NON_TERMINAL_REGEX;
	}

	public static String getTerminalRegex() {
		return TERMINAL_REGEX;
	}
}
