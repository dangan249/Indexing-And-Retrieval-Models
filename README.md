Goal: Indexing, Retrieval Models and Text Transformation

### Project Description:
* Indexer.java read in the document collection provided in the file tccorpus.txt and generate an inverted index and save save the file
to indexTC.out

 indexer tccorpus.txt indexTC.out

 * The project implement three different retrieval models: Vector Space Model/TF-IDF, Vector Space Model/TF-IDF and Cosine Similarity, and Okapi_BM25
 * FIDFRanker.java, TFIDFCosineRanker.java, OkapiBM25.java process queries in `queries.txt` and print out related documents together with their ranking scores
 vsm1 indexTC.out queries.txt 100 > resultsVSM1.eval
 vsm2 indexTC.out queries.txt 100 > resultsVSM2.eval
 bm25 indexTC.out queries.txt 100 > resultsBM25.eval

### Instruction to run the project:

This is a maven based project.  You can import it into any IDE that support Java development with Maven and run the main methods in these classes:
Indexer.java, TFIDFRanker.java, TFIDFCosineRanker.java, OkapiBM25.java, KendallTauRankCorrelationCalculator.java

Otherwise, follow these instructions to build the jar with all dependencies and run the program:

-- To build this project, `cd` into the project directory and type the below command:
   mvn package
-- The result of a build will be stored in the `target` directory

RUN INSTRUCTIONS:
java -jar {path to a jar} arguments

