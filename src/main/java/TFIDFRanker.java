import java.io.IOException;

public class TFIDFRanker extends Ranker {
	public TFIDFRanker(String invertedIndexPath, String queriesPath, int numDocumentLimit) throws IOException {
		super(invertedIndexPath, queriesPath, numDocumentLimit);
	}

	@Override
	protected DocumentScore generateDocumentScore(String docId, String[] query) {
		double score = 0;
		for(int i = 0; i < query.length; i++) {
			score += tfIdf(docId, query[i]);
		}
		return new DocumentScore(docId, score);
	}

	public static void main(String[] args) {
		try {
			Ranker ranker = new TFIDFRanker(args[0], args[1], Integer.parseInt(args[2]));
			ranker.processQueries();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
