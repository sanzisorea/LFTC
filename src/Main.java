public class Main {
	public static void main(String[] args) {
		Grammar grammar = new Grammar();
		try {
			grammar.readFromFile("src/files/grammar.txt");
			System.out.println(grammar.getTerminals());
			System.out.println(grammar.getNonTerminals());
			grammar.getProductions().forEach((key, value) -> System.out.println(key + " : " + value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
