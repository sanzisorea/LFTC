import compiler.Scanner;
import grammar.Grammar;
import parser.Parser;

public class Main {
	public static void main(String[] args) {
		Grammar grammar = new Grammar();
		Parser parser = new Parser();
		Scanner scanner = new Scanner();
		try {
//			grammar.readFromFile("src/files/exampleGrammar.txt");
			grammar.readFromFile("src/files/grammarWithoutIdAndConst.txt");
			System.out.println(parser.parseFromScanner(grammar, scanner, "src/files/program1.txt"));
//			System.out.println(parser.parse("<a><*><(><a><+><a><)>", grammar));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
