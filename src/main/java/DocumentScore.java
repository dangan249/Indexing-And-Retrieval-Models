public class DocumentScore implements Comparable {
	String docId;
	double score;

	public DocumentScore(String docId, double score) {
		this.docId = docId;
		this.score = score;
	}

	public int compareTo(Object o) {
		DocumentScore other = (DocumentScore) o;
		return Double.compare(other.score, this.score);
	}

	public String toString() {
		return "(" + docId + "," + score + ")";
	}
}
