import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weka.core.stemmers.SnowballStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadedHTMLIndexBuilder {
	private String HTML_FOLDER= "downloaded";
	private String STOPWORD_FILE = "stopwords.txt";
	private String OUPUT_PATH = "wiki_indexTC.out";
	private Map<String, List<DocumentTF>> invertedIndex;
	private Set<String> dictionary;
	private Set<String> stopWords;
	private Pattern pattern;
	SnowballStemmer stemmer;

	public DownloadedHTMLIndexBuilder() throws IOException {
		this.invertedIndex = new HashMap<String, List<DocumentTF>>();
		this.pattern = Pattern.compile("\\w+(\\.?\\w+)*");
		this.stemmer = new SnowballStemmer();
		this.stemmer.setStemmer("porter");
		this.stopWords = loadStopWords();
		this.dictionary = new TreeSet<String>();
	}

	private Set<String> loadStopWords() throws IOException {
		Set<String> stopWords = new HashSet<String>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(STOPWORD_FILE));
		while ((line = br.readLine()) != null) {
			stopWords.add(line.trim());
		}
		return stopWords;
	}

	public void build() throws IOException {
		File folder = new File(HTML_FOLDER);

		for (final File fileEntry : folder.listFiles()) {
			String documentId = fileEntry.getName();

			if (fileEntry.isDirectory()) {
				continue;
			} else {
				Document doc = Jsoup.parse(fileEntry, "UTF-8");
				Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");
				Elements pTags = doc.select("p");
				Elements aTags = doc.select("a");
				List<String> tokens = new ArrayList<String>();

				for(Element hTag : hTags) {
					tokens.addAll(processText(hTag.text()));
				}

				for(Element pTag : pTags) {
					tokens.addAll(processText(pTag.text()));
				}

				for(Element aTag : aTags) {
					tokens.addAll(processText(aTag.text()));
				}

				for(String token : tokens) {
					dictionary.add(token);
					Utils.updateInvertedIndex(invertedIndex, token, documentId);
				}
			}
		}

		Utils.writeInvertedIndex(dictionary, invertedIndex, OUPUT_PATH);
	}

	private List<String> processText(String str) {
		return removeStopWords(stem(tokenize(str)));
	}

	private List<String> removeStopWords(List<String> stemmedTokens) {
		List<String> tokens = new ArrayList<String>();
		for(String token : stemmedTokens) {
			if(!stopWords.contains(token)) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	private List<String> stem(List<String> tokens) {
		List<String> stemmedTokens = new ArrayList<String>();
		for(String token : tokens) {
			String stemmedWord = stemmer.stem(token);
			if (!stemmedWord.isEmpty()) {
				stemmedTokens.add(stemmedWord);
			}
		}
		return stemmedTokens;
	}

	private List<String> tokenize(String str) {
		List<String> tokens = new ArrayList<String>();
		for(String token : str.split("\\s+")) {
			Matcher m = pattern.matcher(token);
			if(m.matches() && !token.isEmpty()) {
				tokens.add(token.toLowerCase());
			}
		}
		return tokens;
	}

	public static void main(String args[]) {
		try {
			DownloadedHTMLIndexBuilder indexBuilder = new DownloadedHTMLIndexBuilder();
			indexBuilder.build();
		} catch (IOException e) {
			System.out.println("invalid file");
		}
	}
}
