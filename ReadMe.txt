- instructions for use, describing how the user will interact with your program

	-> In order to run our program, our user will first have to compile and run our indexer program. This can be done using the following input:
				 scalac *.scala
				 scala search.Indexer <nameofWikipediaCorpus> <name of file with words->scores> <name of file with docID -> titles> <name of file with docID -> pgrank>
				 ex: time scala search.Indexer SmallWiki.xml WordScoreMap.txt DocTitleMap.txt PageRankScores.txt
				 
		this program will produce 3 textfiles that will contain information that will be used by query. 
	
	-> Once Indexer has been compiled, we must run Query. Query takes in the names of the 3 documents in the following order
				 scala search.Query <name of file with docID -> titles>  <name of file with words->scores>
				 ex: scala search.Query DocTitleMap.txt  WordScoreMap.txt
				 
	   NOTE: To run the Smart version of our program, you would type in the following command in the terminal:
	   			 scala search.Query --smart <name of file with docID -> titles>  <name of file with words->scores> <name of file with docID -> pagerank>
	   			 ex: scala search.Query --smart DocTitleMap.txt WordScoreMap.txt PageRankScores.txt
	
	-> Once the Query Program has been called, it will prompt the user for input for something to search. The program then returns a list of the top 10 documents that best relate to the inputted search.
	-> When the user is prompted for input, the next step would be for the user to input something he or she would like to search. As said above, the program will output the top 10 documents relating to each inputted search. 
	-> The user may choose to exit the program by typing in :quit.
                                                    

- a brief overview of your design

Our design is split into two different programs: Indexer and Query. 
	
	Our design for Indexer is as follows:
		-> we have created a class (DocScores) -> this makes it easier to store and access information about individual documents and a word's score for that document.
		-> we have 3 main hashmaps: 
				(1) Doc ID -> Titles
				(2) Words -> List(DocScores)
				(3) Doc ID -> PageRank score
		-> There are 4 main forloops in our program
		-> The first forloop creates the Doc->title hashmap so that it may be accessed for the other for loops.
		-> The heart of our program is 1 large for loop which updates two of our main hashmaps (the one containing information about every unique word in the entire 
		corpus and it's final score for each document it occurs in, and the one containing pagerank scores for each document)
		-> Once our program has collected all the information it needs from the body text of the entire corpus, it improves upon the information by assigning a final
		score to each word for each document (it multiplies the initial score by the inverse document frequency). This is done in a for loop - in which we loop through
		every element in our WordScoreMap and multiply the score by the Idf
		-> Our last main forloop propogates our pagerank hashmap to make it more accurate - it also helps us account for Inlinks, not just outlinks. 
	


   

- an overview of how your program functions, including how all of the pieces fit together
	
	As said before, our program consists of 2 "sub programs" -> Indexer and Query. 
	
		OVERVIEW OF INDEXER
		
		DocTitle For Loop -> Fills in Hashmap for DocTitleMap
		
		DocScore class: takes in a document, and a temporary score when a new docscore is created.
						-> used in the main for loop.  
		
		docscoreupdate: takes in a word, a particular document, and a new document score for that word in that document, and hashes/updates WordScoreMap accordingly
						-> used when updating the WordScoreMap with euclidean normalized scores. 
		
		inversedocscoreupdate: takes in a word and updates each word's score for every document it exists in using the inversedocument frequency.
						-> used this in the 3rd main forloop that creates the final scores for each unique word in the entire corpus.
		
		reversehashmap: takes in a hashmap and reverses the keys and values of that hashmap.  
						-> this was useful in caclulating outlinks and lists of outlinks. 
		
		hashmapping: takes in a document ID, and a list of outlinks found inside the particular document's body
					It will then internally create a list of document ids that it retrieves from the reverse hashmap created
					from DocTitleMap and hashinto PageRankScore a new instance of the documant, its score, and a unique list
					of outlinks. 
					
		outlinkarraytolist: takes as input an array of strings and out puts a list of strings after certain changes have
					  	 been made to each element in the array. This process is useful when creating a list of outlinks to feed
						 to the program above (hashmapping).
						 
		GIANT FOR LOOP -> goes through all the text in the body portions of the entire corpus and retrieves and stores information it needs from each document's body
		
		Inverse frequency For lopp -> multiplies all the euclidean values for a word by the inverse frequency. 
		
		PageRank propogation For loop -> this is the program that propogates the PageRank hashmap. This is necessary when taking into account the 
										number of inbound links to a page. We propagate 5 times.


                                                    

- a description of any possible bugs or problems with your program (rest assured, you will lose fewer points for documented bugs than undocumented ones)                                            

	-> Bugs for our program include all the instances in which we use "hashmap".apply(x) or "hashmap".get(x).get when there could possibly be no result. But the way our
	   for loops have been designed, we should never come accross this case. An example of this is in how our page rank works. 

- a list of the people with whom you collaborated
	"Alex" :: "Karishma" :: List[String]()


- a description of any extra features you chose to implement     

	We did not chose to implement any extra features. 
