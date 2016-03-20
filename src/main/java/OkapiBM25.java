import java.io.IOException;

public class OkapiBM25 extends Ranker {
	private double k1 = 1.2, b = 0.75, k2 = 100;

	public OkapiBM25(String invertedIndexPath, String queriesPath, int numDocumentLimit) throws IOException {
		super(invertedIndexPath, queriesPath, numDocumentLimit);
	}

	@Override
	protected DocumentScore generateDocumentScore(String docId, String[] query) {
		double score = 0.0;
		for(String word : query) {
			int termFrequencyInDoc = termFrequencyInDoc(word, docId);
			int termFrequencyInQuery = termFrequencyInQuery(word, query);
			score += Math.log((docIds.size() + 0.5) / (invertedIndex.get(word).size() + 0.5))
					* ((termFrequencyInDoc + k1 * termFrequencyInDoc) / (termFrequencyInDoc + k1 * ((1 - b) + b * documentLengths.get(docId) / avgDocumentLength)))
			    * ((termFrequencyInQuery + k2 * termFrequencyInQuery) / (termFrequencyInQuery + k2));
		}
		return new DocumentScore(docId, score);
	}

	public static void main(String[] args) {
		try {
			Ranker ranker = new OkapiBM25(args[0], args[1], Integer.parseInt(args[2]));
			ranker.processQueries();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
