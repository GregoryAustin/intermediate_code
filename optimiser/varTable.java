class varTable {
	public class variable() {
		public String var;
		public String expression;
	}

	private variable head;

	public varTable() {
		head = null;
	}

	public addExpression(String line) {
		String variable = line.split("=")[0];
		String expressionOfLine = line.split("=")[1];
	}
}