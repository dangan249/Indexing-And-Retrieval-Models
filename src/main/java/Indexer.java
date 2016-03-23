import java.io.*;
import java.util.*;

public class Indexer {
	private String corpusFilePath;
	private String outputPath;
	private Set<String> dictionary;
	private Map<String, List<DocumentTF>> invertedIndex;

	public Indexer(String corpusFilePath, String outputPath) {
		this.corpusFilePath = corpusFilePath;
		this.outputPath = outputPath;
		this.dictionary = new TreeSet<String>();
		this.invertedIndex = new HashMap<String, List<DocumentTF>>();
	}

	public void index() throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(corpusFilePath));
		String currentDocId = null;

		// read and generate index
		while ((line = br.readLine()) != null) {
			if(isDocummentHeader(line)) {
				currentDocId = line.split(" ")[1];
			} else {
				String[] tokens = line.split(" ");
				for(String token : tokens) {
					if(isValidToken(token)) {
						dictionary.add(token);
					Utils.updateInvertedIndex(invertedIndex, token, currentDocId);
					}
				}
			}
		}

		Utils.writeInvertedIndex(dictionary, invertedIndex, outputPath);
	}

	private boolean isValidToken(String word) {
		return !word.matches("[0-9]+");
	}

	private boolean isDocummentHeader(String line) {
		return line.contains("#");
	}

	public static void main(String args[]) {
		Indexer indexer = new Indexer(args[0], args[1]);
		try {
			indexer.index();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
