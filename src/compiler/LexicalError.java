package compiler;

public class LexicalError extends Exception {
    private int line;
    private int column;
    private LexicalErrorType lexicalErrorType;

    public LexicalError(LexicalErrorType lexicalErrorType, int line, int column) {
		super();
		this.line = line;
    	this.column = column;
    	this.lexicalErrorType = lexicalErrorType;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public LexicalErrorType getLexicalErrorType() {
		return lexicalErrorType;
	}
}
