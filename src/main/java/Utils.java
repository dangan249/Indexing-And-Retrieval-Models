import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Utils {

	public static void writeInvertedIndex(Set<String> dictionary, Map<String, List<DocumentTF>> invertedIndex, String outputPath) {
		StringBuilder stringBuilder = new StringBuilder();

		for(String word : dictionary) {
			System.out.println(word);
			stringBuilder.append(word);
			stringBuilder.append(" -> ");
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

	public static void updateInvertedIndex(Map<String, List<DocumentTF>> invertedIndex, String token, String documentId) {
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
			if(documentTF.docId.equals(documentId)) {
				documentTFForCurrentToken = documentTF;
			}
		}

		if(documentTFForCurrentToken == null) {
			documentTFs.add(new DocumentTF(documentId, 1));
		} else {
			documentTFForCurrentToken.increaseFreqCount();
		}
	}

	public static void saveStringToFile(String str, String filePath) {
		FileOutputStream fop;
		try {
			File file = new File(filePath);
			fop = new FileOutputStream(file);
			file.createNewFile();

			// get the content in bytes
			byte[] contentInBytes = str.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
