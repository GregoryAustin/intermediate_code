import java.io.*;
import java.nio.file.*;
import java.util.*;

class optimiser {
	public static void main(String[] args) {
		optimiser op = new optimiser();
	}

	ArrayList<String> lines;

	public optimiser() {
		System.out.println("Starting optimisation...");
		lines = new ArrayList<String>();
		try 
		{
			for (String line : Files.readAllLines(Paths.get("../spl.basic"))) {
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		} catch (Exception e) {}
		
		whileLoops(0);

		for (String line : lines) {
			System.out.println(line);
		}

	}

	public void whileLoops(int start) {
		System.out.println("Entering while loop optimisation....");
		//int count = start;

		for (int i = start; i < lines.size(); ++i) {
			String line = lines.get(i);
			if (line.indexOf("while") == 0) {
				//System.out.println("FOUND A LOOP");
				String bool = line.substring(6, line.length());
				lines.add(i++, "if " + bool + " then");
				String block = "";

				int temp = i + 1;
				line = lines.get(temp++);
				int find = 1;
				while (find != 0) {
					if (!line.equals("wend")){
						if (line.indexOf("while") == 0) {
							++find;
						}
						block += line + "\n";
						line = lines.get(temp++);
					} else if (line.equals("wend")) {
						--find;
					}
				}

				lines.add(i++, block);
				line = lines.get(i);
				while (!line.equals("wend")) {
					line = lines.get(i++);	
					//System.out.println("LINE FOUND: " + line);
				}
				lines.add(i++, "end if");
			}
			//System.out.println("EXITED");
			//i++;
		}
	}

	public String returnNewLines() {
		String ret = "";
		for (String line : lines) {
			ret += line + "\n";
		}

		return ret;
	}
}