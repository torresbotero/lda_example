package com.lda.example.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hello world!
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
        
        //sample a length for each document using Poisson
        List<Integer> lenDoc = new ArrayList<Integer>();
        for(int i = 0; i < D; i++){
        	int rand = ThreadLocalRandom.current().nextInt(8, 16);
        	lenDoc.add(rand);
        }
        
        List<String> docs = new ArrayList<String>();
        List<String> origTopics = new ArrayList<String>();
        
        for(int i = 0; i < D; i++){
        	int z = ThreadLocalRandom.current().nextInt(0, 2);
        	if(z == 0){
        		
        	} else {
        		
        	}
        }
        
    }
}
