class DocumentTF implements Comparable {
	String docId;
	int frequency;

	public DocumentTF(String docId, int frequency) {
		this.docId = docId;
		this.frequency = frequency;
	}

	public void increaseFreqCount() {
		this.frequency++;
	}

	public int compareTo(Object o) {
		DocumentTF other = (DocumentTF) o;
		try {
			return Integer.compare(Integer.parseInt(this.docId), Integer.parseInt(other.docId));
		} catch (NumberFormatException ex) {
			return this.docId.compareTo(other.docId);
		}
	}

	public String toString() {
		return "(" + docId + "," + frequency + ")";
	}
}
