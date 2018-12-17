package compiler;

public class PIFElement<X, Y> {
	private X tokenCode;
	private Y stPosition;

	public PIFElement(X tokenCode, Y stPosition) {
		this.tokenCode = tokenCode;
		this.stPosition = stPosition;
	}

	public X getTokenCode() {
		return tokenCode;
	}

	public void setTokenCode(X tokenCode) {
		this.tokenCode = tokenCode;
	}

	public Y getStPosition() {
		return stPosition;
	}

	public void setStPosition(Y stPosition) {
		this.stPosition = stPosition;
	}

	@Override
	public String toString() {
		return tokenCode + "\t\t\t" + stPosition;
	}
}
