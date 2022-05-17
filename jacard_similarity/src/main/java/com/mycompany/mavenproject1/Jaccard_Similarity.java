/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject1;

/**
 *
 * @author Eman Mohamed
 */



import java.io.*;
import java.util.*;

//=====================================================================



class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    // term ,doc_freq,term_freq >===> postingslist
    // eman ,3       ,5         >===> 1,5,8 
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();// take first [term,dictionry] from *index* hashmap 
            DictEntry2 dd = (DictEntry2) pair.getValue();// take the postings list from pair, cast to dictionary
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }
    //-----------------------------------------------
    
    
    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        for (K key: map.keySet())
        {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }
    
    public boolean indexHasWord(String word)
    {
        for (String key: index.keySet())
        {
            if (word.toLowerCase().equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    

    // query inverted index logic search
    public float j(String phrase,String docName) {
        String[] words = phrase.split("\\W+");
        
        float intersect=0, union=0;
        intersect= intersect(words,docName);
        union= union(words,docName);
        //System.out.println("intersect : "+intersect+" , union : "+ union);
        if (union>0)    
            return intersect/union;
        return 0;
    }
    //--------------------------------------------------------------------------
    
    
    //--------------------------------------------------------------------------
    public void jaccard_similarity(String phrase) {
        
        for (int key: sources.keySet())
        {
            if(j(phrase, sources.get(key))>0)
                 System.out.println("doc_"+key+ " : " + j(phrase, sources.get(key)));
            else
                System.out.println("doc_"+key+ " : " + "0");
        }
        
    }
    //--------------------------------------------------------------------------
    
    
    //----------------------------------------------------------------------------  
    int intersect(String[] words, String docName) {
        int doc_id = getKey(sources, docName);
        
        HashSet<Integer> post_01=new HashSet<>();
        
        HashSet<String> intersectWords=new HashSet<>();
       
        for(int i=0; i<words.length; i++)
        {
            if( indexHasWord(words[i])){
                post_01 = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                if (post_01.contains(doc_id))
                {
                    intersectWords.add(words[i]);
                }
            }
            
        }
            
        
         return intersectWords.size();

    }
    //-----------------------------------------------------------------------   
    
    //----------------------------------------------------------------------------  
    float union(String[] words, String docName) {
        int doc_id = getKey(sources, docName);
        int unionCounter=0;
        
        HashSet<Integer> post_01=new HashSet<>();//posting list of each word 
        HashSet<String> intersectWords=new HashSet<>();//to prevent count same word
        
        float doc_words=0;
        //number of words in docName
        
        for (String key: index.keySet())
        {
            post_01 = new HashSet<Integer>(index.get(key.toLowerCase()).postingList);
            if (post_01.contains(doc_id)) {
                doc_words++;
            }
        }
       
        for(int i=0; i<words.length; i++)
        {
            if( indexHasWord(words[i])){
                post_01 = new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
                if (post_01.contains(doc_id))
                {
                    intersectWords.add(words[i]);
                }
            }
        }
            
        
         return doc_words+words.length-intersectWords.size() ;

    }
    //-----------------------------------------------------------------------   
    
    
    
}






public class Jaccard_Similarity {
    
    public static void main(String args[]) throws IOException {
        Index2 index = new Index2();
        String phrase = "";

        index.buildIndex(new String[]{
            //put your own files' path here..
            "C:\\Users\\Eman Mohamed\\Downloads\\6th semester\\IR\\20190120_Eman_S5\\eman.txt",
            "C:\\Users\\Eman Mohamed\\Downloads\\6th semester\\IR\\20190120_Eman_S5\\hassan.txt",
            "C:\\Users\\Eman Mohamed\\Downloads\\6th semester\\IR\\20190120_Eman_S5\\eandhandm.txt"
        });

        // hassan =[0,1,2]
        // eman = [0,2]
        // mohsen=[2]
        index.jaccard_similarity("eman went to the hospital");
    }
    
}
