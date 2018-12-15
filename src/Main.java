public class Main {
	public static void main(String[] args) {
		Grammar grammar = new Grammar();
		Parser parser = new Parser();
		try {
			grammar.readFromFile("src/files/grammarWithoutIdAndConst.txt");
//			System.out.println(grammar.getTerminals());
//			System.out.println(grammar.getNonTerminals());
//			grammar.getProductions().forEach((key, value) -> System.out.println(key + " : " + value));
//			System.out.println();
			System.out.println(parser.first(grammar));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
