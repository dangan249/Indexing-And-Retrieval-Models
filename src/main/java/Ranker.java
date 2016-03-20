import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public abstract class Ranker {
	private String invertedIndexPath;
	private String queriesPath;
	private int numDocumentLimit;
	protected double avgDocumentLength;
	private String OUTPUT_FILE = "resultsVSM1.eval";

	protected Map<String, List<DocumentTF>> invertedIndex;
	protected Map<String, Integer> documentLengths;

	Set<String> docIds = new HashSet<String>();

	public Ranker(String invertedIndexPath, String queriesPath, int numDocumentLimit) throws IOException {
		this.invertedIndexPath = invertedIndexPath;
		this.queriesPath = queriesPath;
		this.numDocumentLimit = numDocumentLimit;
		this.invertedIndex = new HashMap<String, List<DocumentTF>>();
		this.documentLengths = new HashMap<String, Integer>();

		populateInvertedIndex();
		avgDocumentLength = 0.0;
		for(String docId : documentLengths.keySet()) {
			avgDocumentLength += documentLengths.get(docId);
		}

		avgDocumentLength = avgDocumentLength / documentLengths.keySet().size();
	}

	public void processQueries() throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(queriesPath));
		// process queries
		while ((line = br.readLine()) != null) {
			String[] words = line.split("\\s+");
			String[] query = Arrays.copyOfRange(words, 1, words.length);
			printMatches(generateQueryString(query), processQuery(query));
		}
	}

	protected abstract DocumentScore generateDocumentScore(String docId, String[] query);

	protected double tfIdf(String docId, String word) {
		return okapiTF(docId, word) * idf(word);
	}

	protected double idf(String word) {
		return Math.log(docIds.size() / (double) invertedIndex.get(word).size());
	}

	protected int termFrequencyInQuery(String term, String[] query) {
		int count = 0;
		for(String word : query) {
			if (word.equals(term)) count++;
		}
		return count;
	}

	protected int termFrequencyInDoc(String term, String docId) {
		List<DocumentTF> documentTFs = invertedIndex.get(term);
		DocumentTF documentTFForWord = null;
		for(DocumentTF documentTF : documentTFs) {
			if(documentTF.docId.equals(docId)) {
				documentTFForWord = documentTF;
			}
		}

		if(documentTFForWord == null) {
			return 0;
		}

		return documentTFForWord.frequency;
	}

	private double okapiTF(String docId, String word) {
		int frequency = termFrequencyInDoc(word, docId);

		return frequency / (frequency + 0.5 + 1.5 * (documentLengths.get(docId) / avgDocumentLength));
	}

	private String generateQueryString(String[] query) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < query.length; i++) {
			builder.append(query[i]);
			builder.append(" ");
		}
		return builder.toString();
	}

	private void printMatches(String query, List<DocumentScore> scores) {
		System.out.println("************************************************");
		System.out.println("Matches Documents for query: " + query);
		StringBuilder builder = new StringBuilder();
		for(DocumentScore score : scores) {
			System.out.println(score);
		}
		System.out.println("************************************************");
	}


	private List<DocumentScore> processQuery(String[] query) {
		// generate scores
		List<DocumentScore> scores = generateDocumentScores(query);

		// return top numDocumentLimit documents
		Collections.sort(scores);
		return scores.subList(0, numDocumentLimit);
	}

	private List<DocumentScore> generateDocumentScores(String[] query) {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		for(String docId : docIds) {
			scores.add(generateDocumentScore(docId, query));
		}

		return scores;
	}

	private void populateInvertedIndex() throws IOException {
		String line;
		BufferedReader br = new BufferedReader(new FileReader(invertedIndexPath));

		// process queries
		while ((line = br.readLine()) != null) {
			String[] termAndDocumentTFs = line.split(" ");
			String term = termAndDocumentTFs[0];
			List<DocumentTF> documentTFs = new ArrayList<DocumentTF>();

			for(int i = 1; i < termAndDocumentTFs.length; i++) {
				String documentTF[] = termAndDocumentTFs[i].replace("(", "").replace(")", "").split(",");

				String documentID = documentTF[0];
				int frequency = Integer.parseInt(documentTF[1]);

				docIds.add(documentID);
				documentTFs.add(new DocumentTF(documentID, frequency));

				if(documentLengths.containsKey(documentID)) {
					documentLengths.put(documentID, documentLengths.get(documentID) + frequency);
				} else {
					documentLengths.put(documentID, frequency);
				}
			}
			invertedIndex.put(term, documentTFs);
		}
	}
}
