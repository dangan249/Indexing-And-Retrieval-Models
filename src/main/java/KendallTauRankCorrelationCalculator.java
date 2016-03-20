import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class KendallTauRankCorrelationCalculator {
	private LinkedList<String> resultsVSM1;
	private LinkedList<String> resultsVSM2;
	private LinkedList<String> resultsBM25;

	public KendallTauRankCorrelationCalculator(String resultsVSM1File, String resultsVSM2File, String resultsBM25File) throws IOException {
		this.resultsVSM1 = parseResultFile(resultsVSM1File);
		this.resultsVSM2 = parseResultFile(resultsVSM2File);
		this.resultsBM25 = parseResultFile(resultsBM25File);
	}

	private LinkedList<String> parseResultFile(String file) throws IOException {
		LinkedList<String> results = new LinkedList<String>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			if(line.contains("*") || line.contains("Matches")) continue;
			String[] score = line.replace("(", "").replace(")", "").split(",");
			results.add(score[0]);
		}
		return results;
	}

	public void calculate() {
		System.out.println("Kendall Tau coefficient metric: resultsVSM1 VS resultsVSM2");
		printKendalTau(resultsVSM1, resultsVSM2);
		System.out.println("Kendall Tau coefficient metric: resultsVSM1 VS resultsBM25");
		printKendalTau(resultsVSM1, resultsBM25);
		System.out.println("Kendall Tau coefficient metric: resultsVSM2 VS resultsBM25");
		printKendalTau(resultsVSM2, resultsBM25);
	}

	private void printKendalTau(LinkedList<String> list1, LinkedList<String> list2) {
		int concordantCount = 0;
		int discordantCount = 0;
		for(int i = 0; i < list1.size(); i++) {
			if(list1.get(i).equals(list2.get(i))) {
				concordantCount++;
			} else {
				discordantCount++;
			}
		}

		System.out.println("concordantCount: " + concordantCount);
		System.out.println("discordantCount: " + discordantCount);
		System.out.println((concordantCount - discordantCount) / (double) list1.size());
	}

	public static void main(String[] args) {
		try {
			KendallTauRankCorrelationCalculator calculator = new KendallTauRankCorrelationCalculator(args[0], args[1], args[2]);
			calculator.calculate();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
