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

						// initialize the inverted list if needed
						List<DocumentTF> documentTFs;
						if(invertedIndex.containsKey(token)) {
							documentTFs = invertedIndex.get(token);
						} else {
							documentTFs = new ArrayList<DocumentTF>();
							invertedIndex.put(token, documentTFs);
						}


						// TODO: check if we can speed this up by implement searching better
						// check if we already have a DocumentTF entry for this token (== check if we have seen this token already in this doc)
						DocumentTF documentTFForCurrentToken = null;
						for(DocumentTF documentTF : documentTFs) {
							if(documentTF.docId.equals(currentDocId)) {
								documentTFForCurrentToken = documentTF;
							}
						}

						if(documentTFForCurrentToken == null) {
							documentTFs.add(new DocumentTF(currentDocId, 1));
						} else {
							documentTFForCurrentToken.increaseFreqCount();
						}
					}
				}
			}
		}

		writeResults();
	}

	private void writeResults() {
		// write index
		StringBuilder stringBuilder = new StringBuilder();
		for(String word : dictionary) {
			System.out.println(word);
			stringBuilder.append(word);
			stringBuilder.append(" ");
			List<DocumentTF> documentTFs = invertedIndex.get(word);
			Collections.sort(documentTFs);
			for(DocumentTF documentTF : documentTFs) {
				stringBuilder.append(documentTF);
				stringBuilder.append(" ");
			}
			stringBuilder.append("\n");
		}
		Utils.saveStringToFile(stringBuilder.toString(), outputPath);
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
