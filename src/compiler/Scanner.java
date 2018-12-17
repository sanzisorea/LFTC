package compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Scanner {

    private List<String> symbolTable = new ArrayList<>();
    private List<PIFElement<Integer, Integer>> programInternalForm = new ArrayList<>();
    private List<String> separatorsOperators = new ArrayList<>(Arrays.asList("[", "]", "(", ")", "{", "}", ";", ",",
            "+", "-", "*", "/", "%", "==", "!=", "<=", ">=", "=", "<", ">"));
    private final String delimiter = "((?<=%1$s)|(?=%1$s))";
    private Map<String, Integer> tokenTypes = new HashMap<>();
    private String identifierRegex = "[A-Za-z][A-Za-z_0-9]*";
    private String constantRegex = "([+-])?[1-9][0-9]*|0|\'[a-zA-Z]\'|\'[0-9]\'|\"[A-Za-z_0-9]*\"";

    public Scanner() {
        tokenTypes.put("function", 2);
        tokenTypes.put("int", 3);
        tokenTypes.put("char", 4);
        tokenTypes.put("string", 5);
        tokenTypes.put("array", 6);
        tokenTypes.put("const", 7);
        tokenTypes.put("if", 8);
        tokenTypes.put("else", 9);
        tokenTypes.put("while", 10);
        tokenTypes.put("read", 11);
        tokenTypes.put("write", 12);
        tokenTypes.put("of", 13);
        tokenTypes.put("+", 14);
        tokenTypes.put("-", 15);
        tokenTypes.put("*", 16);
        tokenTypes.put("/", 17);
        tokenTypes.put("%", 18);
        tokenTypes.put("=", 19);
        tokenTypes.put("==", 20);
        tokenTypes.put("!=", 21);
        tokenTypes.put("<", 22);
        tokenTypes.put(">", 23);
        tokenTypes.put("<=", 24);
        tokenTypes.put(">=", 25);
        tokenTypes.put("[", 26);
        tokenTypes.put("]", 27);
        tokenTypes.put("(", 28);
        tokenTypes.put(")", 29);
        tokenTypes.put("{", 30);
        tokenTypes.put("}", 31);
        tokenTypes.put(";", 32);
        tokenTypes.put(",", 33);
    }

    public List<PIFElement<Integer, Integer>> scanProgram(String filename) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineCounter = 0;
            while ((line = bufferedReader.readLine()) != null) {
            	lineCounter++;
                List<String> tokens = splitTokens(line);

                for (String token : tokens) {
                	if (tokenTypes.containsKey(token)) {
                		programInternalForm.add(new PIFElement<>(tokenTypes.get(token), -1));
                	} else if (token.matches(identifierRegex) && token.length() <= 250) {
						addToSTandPIF(0, token);
					} else if (token.matches(constantRegex)) {
						addToSTandPIF(1, token);
					} else {
                		throw new LexicalError(LexicalErrorType.INVALID_IDENTIFIER, lineCounter, 0);
					}
                }
            }

//			System.out.println("Symbol Table: " + symbolTable);
//			System.out.println("Program Internal Form:\nCode\tSymbol Table position");
//			programInternalForm.forEach(System.out::println);
			return programInternalForm;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (LexicalError l) {
			System.out.println("Error encountered on line: " + l.getLine() + " : " + l.getLexicalErrorType());
		}
		return null;
	}

    private void addToSTandPIF(int tokenCode, String token) {
		int tokenPosition = findTokenPosition(token);
		if ((tokenPosition > 0) || (tokenPosition == 0 && !symbolTable.isEmpty() && token.equals(symbolTable.get(0)))) {
			programInternalForm.add(new PIFElement<>(tokenCode, tokenPosition));
		} else {
			symbolTable.add("");
			tokenPosition = -tokenPosition;
			for (int i = symbolTable.size() - 1; i > tokenPosition; i--) {
				symbolTable.set(i, symbolTable.get(i - 1));
			}
			symbolTable.set(tokenPosition, token);
			for (PIFElement<Integer, Integer> pifElement : programInternalForm) {
				int oldPosition = pifElement.getStPosition();
				if (oldPosition >= tokenPosition) {
					pifElement.setStPosition(oldPosition + 1);
				}
			}
			programInternalForm.add(new PIFElement<>(tokenCode, tokenPosition));
		}
	}

    private int findTokenPosition(String token) {
		int left = 0, right = symbolTable.size() - 1, middle;
		while (left <= right) {
			middle = (left + right)/2;
			int compared = token.compareTo(symbolTable.get(middle));
			if (compared == 0) {
				return middle;
			} else if (compared < 0) {
				right = middle - 1;
			} else {
				left = middle + 1;
			}
		}
		return -left;
	}

    private List<String> splitTokens(String line) {

		// Split by " " and "   " and get rid of the "" that may form
		String[] spaceSplit = line.split("[ \t]");
		List<String> tokens = new ArrayList<>(Arrays.asList(spaceSplit));
		tokens.removeAll(Collections.singleton(""));

		// Split every token by each separator and operator, building the list of tokens for each line
		// Special attention to double operators
		for (String separatorOperator : separatorsOperators) {
			List<String> temporaryTokens = new ArrayList<>();
			for (String token : tokens) {
				if (!token.equals("==") && !token.equals("!=") && !token.equals("<=") && !token.equals(">=")) {
					String[] splitWords = token.split(String.format(delimiter, "\\" + separatorOperator));
					temporaryTokens.addAll(Arrays.asList(splitWords));
				} else {
					temporaryTokens.add(token);
				}
			}
			tokens = temporaryTokens;
		}

		return tokens;
	}
}
