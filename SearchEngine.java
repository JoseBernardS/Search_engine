package prog11;

import java.util.List;

public interface SearchEngine {
    
    void collect (Browser browser, List<String> startingURLs);

    
    void rank (boolean fast);

    
    String[] search (List<String> searchWords, int numResults);
}

