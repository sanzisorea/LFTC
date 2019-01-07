import compiler.Scanner;
import grammar.Grammar;
import parser.Parser;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		Grammar grammar = new Grammar();
		Parser parser = new Parser();
		Scanner scanner = new Scanner();
		try {
//			grammar.readFromFile("src/files/exampleGrammar.txt");
			grammar.readFromFile("src/files/grammarWithoutIdAndConst.txt");
			List<String> parsingString = parser.parseFromScanner(grammar, scanner, "src/files/program1.txt");
			if (parsingString != null) {
				System.out.println(parsingString);
				parsingString.forEach((production) -> System.out.println(grammar.getProductionByNumber(Integer.parseInt(production))));
			}
//			System.out.println(parser.parse("<a><*><(><a><+><a><)>", grammar));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
