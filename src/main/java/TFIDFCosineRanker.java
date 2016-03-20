import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TFIDFCosineRanker extends Ranker {
	private Map<String, Map<String, Double>> documentVectors;

	public TFIDFCosineRanker(String invertedIndexPath, String queriesPath, int numDocumentLimit) throws IOException {
		super(invertedIndexPath, queriesPath, numDocumentLimit);
		this.documentVectors = generateDocumentVectors();
	}

	@Override
	protected DocumentScore generateDocumentScore(String docId, String[] query) {
		Map<String, Double> documentVector = documentVectors.get(docId);
		Map<String, Double> queryVector = generateQueryVector(query);
		return new DocumentScore(docId, calculateCosine(documentVector, queryVector));
	}

	private double calculateCosine(Map<String, Double> documentVector, Map<String, Double> queryVector) {
		double numerator = 0.0;
		double tfIdfSumForDoc = 0.0;
		double tfidSumForQuery = 0.0;

		for(String word : queryVector.keySet()) {
			tfidSumForQuery += queryVector.get(word);
			if(documentVector.containsKey(word)) {
				tfIdfSumForDoc += documentVector.get(word);
				numerator += queryVector.get(word) * documentVector.get(word);
			}
		}

		if (tfIdfSumForDoc == 0) return 0;

		return numerator / Math.sqrt(tfIdfSumForDoc * tfidSumForQuery);
	}

	private Map<String, Double> generateQueryVector(String[] query) {
		Map<String, Double> queryVector = new HashMap<String, Double>();

		for(String word : query) {
			queryVector.put(word, termFrequencyInQuery(word, query) * idf(word));
		}

		return queryVector;
	}

	private Map<String, Map<String, Double>> generateDocumentVectors() {
		Map<String, Map<String, Double>> documentVectors = new HashMap<String, Map<String, Double>>();
		for(String term : invertedIndex.keySet()) {
			// For computational efficiency concern, we do not record the weight of the
			// terms not appearing in the document.
			// Going in through the inverted list for each term will do that for us.
			List<DocumentTF> documentTFs = invertedIndex.get(term);
			for(DocumentTF documentTF : documentTFs) {
				String docId = documentTF.docId;
				Map<String, Double> documentVector = null;
				if(documentVectors.containsKey(docId)) {
					documentVector = documentVectors.get(docId);
				} else {
					documentVector = new HashMap<String, Double>();
					documentVectors.put(docId, documentVector);
				}

				documentVector.put(term, tfIdf(docId, term));
			}

		}
		return documentVectors;
	}

	public static void main(String[] args) {
		try {
			Ranker ranker = new TFIDFCosineRanker(args[0], args[1], Integer.parseInt(args[2]));
			ranker.processQueries();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
