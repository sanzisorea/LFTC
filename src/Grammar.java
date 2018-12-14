import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Grammar {
	private Set<String> terminals;
	private Set<String> nonTerminals;
	private String startingSymbol;
	private Map<String, List<String>> productions;
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
	private static final String TERMINAL_REGEX = "(\"[a-zA-Z0-9_'\"]\"|[2-9]|[1-2][0-9]|3[0-3])";
	private static final String NON_TERMINAL_LINE_REGEX = NON_TERMINAL_REGEX + "(," + NON_TERMINAL_REGEX + ")*";
	private static final String TERMINAL_LINE_REGEX = TERMINAL_REGEX + "(," + TERMINAL_REGEX + ")*";
	private static final String PRODUCTION = "(( ?(" + NON_TERMINAL_REGEX + "|" + TERMINAL_REGEX +
			" ?))+|" + EMPTY_SYMBOL + ")";

	private static final String PRODUCTION_REGEX = NON_TERMINAL_REGEX + " ?-> ?" + PRODUCTION + "( ?\\| ?" + PRODUCTION + ")*";
	private static final String INVALID_FORMAT = "Invalid format";


	public void readFromKeyboard() throws Exception {
		initializeFields();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Input the non-terminals:");
		String line = scanner.nextLine();
		symbolsToSet(line, nonTerminals, NON_TERMINAL_LINE_REGEX, NON_TERMINAL_REGEX);

		System.out.println("Input the terminals:");
		line = scanner.nextLine();
		symbolsToSet(line, terminals, TERMINAL_LINE_REGEX, TERMINAL_REGEX);

		System.out.println("Input the starting symbol:");
		line = scanner.nextLine();
		if (line == null || !line.matches(NON_TERMINAL_REGEX) || !nonTerminals.contains(line)) {
			throw new Exception(INVALID_FORMAT);
		}
		startingSymbol = line;

		System.out.println("Input the productions on each row, press Enter again when you want to stop:");
		while (!(line = scanner.nextLine()).equals("")) {
			addProductionToNonTerminal(line);
		}
	}

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
		if (!productions.containsKey(nonTerminal)) {
			productions.put(nonTerminal, new ArrayList<>());
		}
		productions.get(nonTerminal).addAll(Arrays.asList(rightHandSide));
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

	public boolean isRegular() {
		for (String nonTerminal : productions.keySet()) {
			for (String rightHandSide : productions.get(nonTerminal)) {
				if (!rightHandSide.matches("(" + TERMINAL_REGEX + "(" + NON_TERMINAL_REGEX + ")" + "?)|" + EMPTY_SYMBOL))
				{
					return false;
				}
				if (rightHandSide.equals(EMPTY_SYMBOL) && !nonTerminal.equals(startingSymbol)) {
					return false;
				}
				if (rightHandSide.contains(startingSymbol) && productions.get(startingSymbol).contains(EMPTY_SYMBOL)) {
					return false;
				}
			}
		}
		return true;
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

	public Map<String, List<String>> getProductions() {
		return productions;
	}

	public void setProductions(Map<String, List<String>> productions) {
		this.productions = productions;
	}
}
