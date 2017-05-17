class varTableOpt {
	public class variableEntry {
		public String var;
		public String expression;
	}

	variableEntry head;

	public varTableOpt() {
		head = null;
	}

	public void addVar(String _var, String _expression) {
		System.out.println("ADDING " + _var + "=" + _expression);
	}
}