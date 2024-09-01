package prog11;

import prog08.ExternalSort;
import prog08.TestExternalSort;
import prog09.BTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class NotGPT implements SearchEngine{
    public HardDisk pageDisk = new HardDisk();
    public Map<String,String> indexOfURL = new BTree(100);
    public HardDisk wordDisk = new HardDisk();
    public HashMap<String, Long> indexOfWord = new HashMap<>();
    //Put a HardDisk variable pageDisk inside NotGPT to store theinformation about web pages.  Initialize pageDisk.

   // Put a Map<String,String> variable  indexOfURL into NotGPT.  Initialize to a new prog09.BTree(100).

    Long indexPage(String url){
        Long index = pageDisk.newFile();
        InfoFile infofile = new InfoFile(url);
        pageDisk.put(index,infofile);
        indexOfURL.put(url,index.toString());
        System.out.println("indexing url " + url + " index " + index + " file " + infofile);
        return index;

        //It gets the index of a new file from pageDisk,
        // creates a new InfoFile,
        // and stores it in pageDisk under that index.
        // Then it tells the Map
        //indexOfURL to map url to that index
        // and returns the index.
        //THIS IS WRONG
    }
    Long indexWord(String word){
        Long index;
        if(indexOfWord.containsKey(word)){
            index = indexOfWord.get(word);
        }
        else{
        index= wordDisk.newFile();
        InfoFile infoFile = new InfoFile(word);
        wordDisk.put(index, infoFile);
        indexOfWord.put(word, index);
            System.out.println("indexing word " + word + " index " + index + " file " + infoFile);
        }
        return index;}
    public void collect(Browser browser, List<String> startingURLs) {
        Queue<Long> IndexofpageIndices = new ArrayDeque<Long>();
        System.out.println("starting pages " + startingURLs);
        for(String starting_URL : startingURLs){
            if(indexOfURL.get(starting_URL)==null){
                Long x = indexPage(starting_URL);
                IndexofpageIndices.offer(x);
            }}
            while(!IndexofpageIndices.isEmpty()){
                System.out.println("queue " + IndexofpageIndices);
               Long y = IndexofpageIndices.poll();
               if(browser.loadPage(pageDisk.get(y).data)){
                   System.out.println("dequeued " + pageDisk.get(y).data + "[]0.0");
                   Set<String> setOfURLs = new HashSet<>();
                   List<String> urls = browser.getURLs();
                   System.out.println("urls " + urls);
                   for(String h: urls){
                       if(!setOfURLs.contains(h)){
                               setOfURLs.add(h);
                               Long index = null;


                       if(indexOfURL.get(h)!=null){
                           index = Long.parseLong(indexOfURL.get(h));}
                       if(index == null){
                           index = indexPage(h);
                           IndexofpageIndices.offer(index);
                       }

                       pageDisk.get(y).indices.add(index);}}
                   System.out.println("updated page file " + pageDisk.get(y).data + pageDisk.get(y).indices + "0.0" );


                       List <String> words = browser.getWords();
                   System.out.println("words " + words);
                       for (String word: words){
                               Long index = indexWord(word);
                           if(!wordDisk.get(index).indices.contains(y)){
                               wordDisk.get(index).indices.add(y);
                               System.out.println("updated word " + word + " index " + index + " file " + wordDisk.get(index) );
                           }





                   }

               }

            }



    }

    @Override
    public void rank(boolean fast) {
        for (Map.Entry<Long,InfoFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            InfoFile file = entry.getValue();
            file.impact = 1.0;
            file.impactTemp = 0.0;



    }
        if(fast){
            for(int i = 0; i<20;i++){
                rankFast();
            }
        }
        if(!fast){
            for(int i = 0; i<20;i++){
                rankSlow();
            }
        }}
   
    /** Check if all elements in an array of long are equal.
     @param array an array of numbers
     @return true if all are equal, false otherwise
     */
    private boolean allEqual (long[] array) {
        for(int i = 1 ; i< array.length; i++){
            if(array[i-1] != array[i]){
                return false;
            }
        }
        return true;
    }

        /** Get the largest element of an array of long.
         @param array an array of numbers
         @return largest element
         */
        private long getLargest (long[] array) {
            long largest = 0;
            for(int i = 0 ; i< array.length; i++){
                if (array[i]> largest){
                    largest = array[i];
                }
            }
            return largest;
        }

            void rankSlow () {
                double zeroLinkImpact = 0.0;
                for (Map.Entry<Long, InfoFile> entry : pageDisk.entrySet()) {
                    long index = entry.getKey();
                    InfoFile file = entry.getValue();
                    if (file.indices.isEmpty()) {
                        zeroLinkImpact = file.impact + zeroLinkImpact;
                    }
                }
                zeroLinkImpact = zeroLinkImpact / pageDisk.entrySet().size();
                for (Map.Entry<Long, InfoFile> entry : pageDisk.entrySet()) {
                    long index = entry.getKey();
                    InfoFile file = entry.getValue();
                    double impactPerIndex = file.impact / file.indices.size();
                    for (Long page_index : file.indices) {
                        pageDisk.get(page_index).impactTemp += impactPerIndex;
                    }

                }
                for (Map.Entry<Long, InfoFile> entry : pageDisk.entrySet()) {
                    InfoFile file = entry.getValue();
                    file.impact = file.impactTemp + zeroLinkImpact;
                    file.impactTemp = 0.0;


                }




            }

    /** If all the elements of currentPageIndices are equal,
     set each one to the next() of its Iterator,
     but if any Iterator hasNext() is false, just return false.

     Otherwise, do that for every element not equal to the largest element.

     Return true.

     @param currentPageIndices array of current page indices
     @param pageIndexIterators array of iterators with next page indices
     @return true if all page indices are updated, false otherwise
     */
    private boolean getNextPageIndices(long[] currentPageIndices, Iterator<Long>[] pageIndexIterators) {
        if(allEqual(currentPageIndices)){
            for(int i = 0; i<pageIndexIterators.length;i++){
                if(!pageIndexIterators[i].hasNext()){return false;}
              else{
                    currentPageIndices[i] = pageIndexIterators[i].next();
                }


            }
        }
        long Largest = getLargest(currentPageIndices);
        for(int i = 0; i<pageIndexIterators.length;i++){
            if(currentPageIndices[i]<Largest){
                if(!pageIndexIterators[i].hasNext()){return false;}
                currentPageIndices[i] = pageIndexIterators[i].next();}

        }
        return true;
    }

        
    void rankFast (){

        double zeroLinkImpact = 0.0;

        for (Map.Entry<Long,InfoFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            InfoFile file = entry.getValue();
            if(file.indices.isEmpty()){
                zeroLinkImpact = file.impact + zeroLinkImpact;}
        }

        try{
            zeroLinkImpact = zeroLinkImpact/pageDisk.entrySet().size();
            PrintWriter out = new PrintWriter("unsorted-votes.txt");
            for (Map.Entry<Long,InfoFile> entry : pageDisk.entrySet()) {
                long index = entry.getKey();
                InfoFile file = entry.getValue();
                double impactPerIndex = file.impact/ file.indices.size();
                for(Long page_index: file.indices){
                    Vote newesrvote = new Vote(page_index,impactPerIndex);
                    out.println(newesrvote);
                }}
                out.close();
                VoteScanner newscanner = new VoteScanner();
                ExternalSort<Vote> externalSort = new ExternalSort<>(newscanner);
                externalSort.sort("unsorted-votes.txt","sorted-votes.txt");



        }catch (FileNotFoundException e) {
            System.out.println(e);
        }

        VoteScanner sortedVoteScanner = new VoteScanner();
        Iterator<Vote> sortedVoteIterator = sortedVoteScanner.iterator("sorted-votes.txt");
        Vote nextVote = sortedVoteIterator.next();
        for (Map.Entry<Long,InfoFile> entry : pageDisk.entrySet()) {
            long index = entry.getKey();
            InfoFile file = entry.getValue();
            file.impact = zeroLinkImpact;
            while(nextVote.index == index){
                file.impact += nextVote.vote;
                if(sortedVoteIterator.hasNext())
                    nextVote = sortedVoteIterator.next();
                else
                    break;




            }


    }}


    @Override
    public String[] search(List<String> searchWords, int numResults) {
        Iterator<Long>[] pageIndexIterators = (Iterator<Long>[]) new Iterator[searchWords.size()];
        long[] currentPageIndices = new long[searchWords.size()];
        for(int i = 0; i < searchWords.size(); i++){
            Iterator<Long> iter = wordDisk.get(indexOfWord.get((searchWords.get(i)))).indices.iterator();
            pageIndexIterators[i] = iter;
        }
        PriorityQueue<Long> bestPageIndices = new PriorityQueue<>(new PageIndexComparator());
        while(getNextPageIndices(currentPageIndices, pageIndexIterators)){
            if(allEqual(currentPageIndices)) {
                System.out.println(pageDisk.get(currentPageIndices[0]).data);
                if(bestPageIndices.size() < numResults)
                    bestPageIndices.offer(currentPageIndices[0]);
                else if(pageDisk.get(currentPageIndices[0]).impact > pageDisk.get(bestPageIndices.peek()).impact){
                    bestPageIndices.poll();
                    bestPageIndices.offer(currentPageIndices[0]);
                }
            }
        }
        String[] result = new String[bestPageIndices.size()];
        for(int i = result.length-1; i >= 0; i--)
            result[i] = pageDisk.get(bestPageIndices.poll()).data;
        return result;
    }

    public class Vote implements Comparable<Vote>{
        Long index;
        double vote;
        public Vote(long x, double y){
            this.index = x;
            this.vote = y;
        }

        @Override
        public int compareTo(Vote o) {
            if(!Objects.equals(index, o.index)){
                return index.compareTo(o.index)
;            }
            return Double.compare(vote,o.vote);
        }

        @Override
        public String toString() {
            return index + " "+ vote;
        }}

        class VoteScanner implements ExternalSort.EScanner<Vote> {
            @Override
            public Iterator<Vote> iterator(String fileName) {
                return new Iter(fileName);
            }

            class Iter implements Iterator<Vote> {
                Scanner in;

                Iter (String fileName) {
                    try {
                        in = new Scanner(new File(fileName));
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                public boolean hasNext () {
                    return in.hasNext();
                }

                public Vote next () {
                    long x = in.nextLong();
                    double y = in.nextDouble();
                    return new Vote(x,y);

                }

            }


        }
        class PageIndexComparator implements Comparator<Long>{
            @Override
            public int compare(Long o1, Long o2) {
                return Double.compare(pageDisk.get(o1).impact,pageDisk.get(o2).impact);
            }
        }


}
