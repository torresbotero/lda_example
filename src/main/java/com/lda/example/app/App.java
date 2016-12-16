package com.lda.example.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * LDA Example
 *
 */
public class App 
{
	private static final String[] VOCAB = {"money",
	                                       "loan",
	                                       "bank",
	                                       "river",
	                                       "stream"};
	
	private static final double[] Z1 = {1/3, 1/3, 1/3, .0, .0};
	private static final double[] Z2 = {.0, .0,1/3, 1/3, 1/3};
	
	//number of documents
	private static int D = 16;
	
	//mean word length of documents
	private static int MEAN_LENGTH = 10;
	
	//fix number of topics
	private static int T = 2;
	
	
    public static void main( String[] args ) {
        System.out.println("Start");
        
        //Data generation
        
        //sample a length for each document using Poisson
        List<Integer> lenDoc = new ArrayList<Integer>();
        for(int i = 0; i < D; i++){
        	int rand = ThreadLocalRandom.current().nextInt(8, 16);
        	lenDoc.add(rand);
        }
        
        System.out.println("lenDoc.toString()" + lenDoc.toString());
        
        List<List<String>> docs = new ArrayList<List<String>>();
        List<Integer> origTopics = new ArrayList<Integer>();
        
        for(int i = 0; i < D; i++){
        	int z = ThreadLocalRandom.current().nextInt(0, 2);
        	List<String> words = new ArrayList<String>();
        	if(z == 0){
        		for(int j = 0; j < lenDoc.get(i); j++){
        			int z1 = ThreadLocalRandom.current().nextInt(0, 3);
        			words.add(VOCAB[z1]);
        		}
        	} else {
        		for(int j = 0; j < lenDoc.get(i); j++){
        			int z2 = ThreadLocalRandom.current().nextInt(2, 5);
        			words.add(VOCAB[z2]);
        		}
        	}
        	origTopics.add(z);
        	docs.add(words);
        }
        System.out.println("origTopics.size(): " + origTopics.size());
        System.out.println("docs.size(): " + docs.size());
        System.out.println("origTopics.toString(): " + origTopics.toString());
        System.out.println("docs.get(0): " + docs.get(0));
        System.out.println("docs.get(5): " + docs.get(5));
        
        
        //Initialize pointers
        List<String> wi = new ArrayList<String>();
        List<Integer> index = new ArrayList<Integer>();
        List<Integer> di = new ArrayList<Integer>();
        List<Integer> zi = new ArrayList<Integer>();
        int counter = 0;
        
        //go through each document
        for(int i = 0; i < docs.size(); i++){
        	//go through each word in doc
        	for(int j = 0; j < docs.get(i).size(); j++){
        		//append word
        		wi.add(docs.get(i).get(j));
        		index.add(counter);
        		di.add(i);
        		zi.add(ThreadLocalRandom.current().nextInt(0, T));
        		counter++;
        	}
        }
        
        System.out.println("wi.size(): " + wi.size());
        System.out.println("wi.toString(): " + wi.toString());
        System.out.println("index.size(): " + index.size());
        System.out.println("index.toString(): " + index.toString());
        System.out.println("di.size(): " + di.size());
        System.out.println("di.toString(): " + di.toString());
        System.out.println("zi.size(): " + zi.size());
        System.out.println("zi.toString(): " + zi.toString());
        
        int[][] wt = new int[VOCAB.length][T];
        for(int i = 0; i < VOCAB.length; i++){
        	List<Integer> ziIndexes = new ArrayList<Integer>();
        	for(int w = 0; w < wi.size(); w++){
        		if(VOCAB[i].equals(wi.get(w))){
        			ziIndexes.add(w);
        		}
        	}
        	for(int t = 0; t < T; t++){
        		int countTopicWord = 0;
        		for(int z = 0; z < ziIndexes.size(); z++){
        			if(t == zi.get(z)){
        				countTopicWord++;
        			}
        		}
        		wt[i][t] = countTopicWord;
        	}
        }
        
        System.out.println("WT:");
        for(int i = 0; i < wt.length; i++){
        	for(int j = 0; j < wt[i].length; j++){
        		System.out.print(wt[i][j] + ", ");
        	}
        	System.out.println("");
        }
        
        int[][] dt = new int[D][T];
        
        for(int i = 0; i < D; i++){
        	List<Integer> ziIndexes = new ArrayList<Integer>();
        	for(int d = 0; d < di.size(); d++){
        		if(i == di.get(d)){
        			ziIndexes.add(d);
        		}
        	}
        	
        	for(int t = 0; t < T; t++){
        		int countTopicDoc = 0;
        		for(int z = 0; z < ziIndexes.size(); z++){
        			if(t == zi.get(z)){
        				countTopicDoc++;
        			}
        		}
        		dt[i][t] = countTopicDoc;
        	}
        }
        
        System.out.println("DT:");
        for(int i = 0; i < dt.length; i++){
        	for(int j = 0; j < dt[i].length; j++){
        		System.out.print(dt[i][j] + ", ");
        	}
        	System.out.println("");
        }
        
        System.out.println("test something");
        int[] test1 = dt[15];
        for(int i = 0; i < test1.length; i++){
        	System.out.println("test1[i]: " + test1[i]);
        }
        System.out.println("dt[i]: " + dt[15] + 1);
        
        //runnings the Gibbs sampler
        
        //These two variables will keep track of the topic assignments
        //for each word. They are only useful for illustrating purposes.
        int[][] phi1 = new int[VOCAB.length][100];
        int[][] phi2 = new int[VOCAB.length][100];
        
        //How many iterations you want to run
        int iters = 100;
        
        //The Dirichlet priors
        //Setting them to 1 essentially means they won't do anything
        int beta = 1;
        int alpha = 1;
        
        for(int step = 0; step < iters; step++){
        	//for each word token
        	for(Integer current : index){
        		//get document id and word id
        		int docIdx = di.get(current);
        		int wIdx = 0;
        		for(int i = 0; i < VOCAB.length; i++){
        			if(VOCAB[i].equals(wi.get(current))){
        				wIdx = i;
        			}
        		}
        		
        		dt[docIdx][zi.get(current)] = dt[docIdx][zi.get(current)] - 1;
        		wt[wIdx][zi.get(current)] = wt[wIdx][zi.get(current)] - 1;
        		
        		//calculate new assignment
        		//according to the formuala
        		
        		
        		
        	}
        }
        
        
        System.out.println("End");
        
    }
}
