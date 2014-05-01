package search

class Indexer (url : String){
import scala.xml.Node 
//import indexer.SubNode
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedList
import scala.util.control.Breaks._
import scala.math

  //this DocScore class made it easy for us to access information about a document and its score within our 
  // for loop. It takes in a document, and an intermediate score. 
  class DocScore(doc: Int, score: Double){
    val Doc = doc
    var Score = score 
    def addone = {Score = Score + 1.0}
    override def toString : String = "\n Doc: " + Doc + " Score: " + Score
  }
  

//Creating node from XML file
val node : Node  = xml.XML.loadFile(url)

//Selects all the titles
val IDNodeTitle: Seq[Node] = (node \\ "title")
//places them into an array
val TitleArray = IDNodeTitle.toArray

//Selects all the body text
val IDNodeText: Seq[Node] = (node \\ "text")
//places into array
val TextArray = IDNodeText.toArray

//empty array for body text
val masterBArray = Array.fill[(Array[String])](TextArray.length)(null)

//map words to a list of document frequencies
val WordScoreMap = new HashMap[String, List[DocScore]]
//map document id to titles
val DocTitleMap = new HashMap[Int, String]
//making pagerank hashmap
var PageRankScore = new HashMap[Int, (Double, List[Int])]

//make method that takes in a word, and its document score, and hashes/updates WordScoreMap 
// accordingly
def docscoreupdate (word: String, score: Double, doc: Int)={
  WordScoreMap.get(word) match {
    case None => {
      WordScoreMap += ((word, List(new DocScore(doc, score)))) //add word and its score to the hashmap
    }
    case Some(m) => {
      val nextdoc = new DocScore(doc, score)
      WordScoreMap.update(word, nextdoc::m)
    }
  }
}

// making method that takes in a word and updates each word's score for every document it exists
// in using the inversedocument frequency. 

def inversedocscoreupdate(word: String) ={
  val thislist = WordScoreMap.get(word).get
  val totaldocs = TitleArray.length
  val documentfrequency = thislist.length
  val idf = scala.math.log(totaldocs/(.99+documentfrequency)) //used .99 because log of 1 is zero, we didn't want to use 0 for scores of a word for any given document.
  for (n <- thislist){
    n.Score = n.Score * idf
  }
}

//writing a method that reverses keys and values for an inputed hashmap
// this will be useful when calculating outlinks and lists of outlinks. 
def reversehashmap(hashmap: HashMap[Int, String]):HashMap[String, Int] = {
  val reversehashmap = new HashMap[String, Int]
  for(x <- hashmap.keys){
    reversehashmap += ((hashmap.get(x).get.toLowerCase().trim, x))
  }
  return reversehashmap
}

//this program will take in a document ID, and a list of outlinks found inside the document's body
//It will then internally create a list of document ids that it retrieves from the reverse hashmap created
// from DocTitleMap and hashinto PageRankScore a new instance of the documant, its score, and a unique list
// of outlinks. 
def hashmapping (doc: Int, titlelist: List[String]) = {
  val reversedhashmap = reversehashmap(DocTitleMap) //creating reversed hashmap
  var outlinklist = List[Int]()
  for(title <- titlelist){
    reversedhashmap.get(title.trim) match{
      case None => 
      case Some(m) => {
        outlinklist = m :: outlinklist
      }
    }
  }
  PageRankScore += ((doc, ((1.0/TitleArray.length), outlinklist.distinct))) //creating a new instance of doc->pagerank for the inputted doc
}


//this program takes as input an array of strings and out puts a list of strings after certain changes have
// been made to each element in the array. This process is useful when creating a list of outlinks to feed
// to the program above (hashmapping).
def outlinkarraytolist(a: Array[String]): List[String] ={
  var outlinklist = List[String]()
  for (x <- a){
    if (x.contains("|")){
      val keep = x.slice((x.indexOf("|")+1), x.length) //we want to keep the strings before "|" and after the "|"
      val alsokeep = x.slice(0,(x.indexOf("|")))
      outlinklist = alsokeep::keep :: outlinklist //we'll add these to the list of outlinks we have to check
    }
    else {outlinklist = x :: outlinklist}
  }
  return outlinklist
}

//creating the DocTitleHashMap
for (i <- 0 until TitleArray.length){
    var c = TitleArray(i).text
    DocTitleMap += ((i, c))
    
}

// ++++++++++++++++++++++++ BIG FOR LOOP - HEART OF OUR PROGRAM ++++++++++++++++++++++++++++++++++++++


//for every document
for (i <- 0 until masterBArray.length){
  var document = i
  var a = TextArray(i).text.trim.split("[^A-Za-z0-9]+") //this will be used to store all the words
  var b = TextArray(i).text.trim //this will be used to find outlinks in the body
  val regExp = """(?<=\[\[)[^\]]+(?=\]\])""".r //for matching outlinks
  var outlinks = (regExp findAllMatchIn b).mkString(",").trim.toLowerCase().split(",")
  val euclidmap = new HashMap[String, Int] //internal hashmap
  
  
  //for every word in a document
  for (j <- 0 until a.length){
    
    //make every word in text lower case
    a(j) = a(j).toLowerCase()
    
    //make everyword in title stemmed
    a(j) = PorterStemmer.stem(a(j)).trim()
    
    //storing the word
    var word = a(j)
    
    //creating the euclidean value for each word in the document
    euclidmap.get(word) match{
      case Some(x) => euclidmap.update(word,(scala.math.pow((scala.math.sqrt(x)+1),2).toInt))
      case None => euclidmap += ((word,1))
    }
    }
  
  //updating the score for each unique word in the document in Doc Scores
  for(uniqueword <- euclidmap.keys){
    //we get the frequency of the word by square-rooting the value in the euclidmap hashmap
    val frequency = scala.math.sqrt(euclidmap.get(uniqueword).get)
    val score = frequency/(scala.math.sqrt(euclidmap.values.sum))
    docscoreupdate(uniqueword, score, document)
  }
  euclidmap.clear
  
  //hashmapping the document's current pagerank to the PageRankMap
  hashmapping(document, outlinkarraytolist(outlinks))
  
  }

//+++++++++++++++++++++++++++ END OF GINORMOUS FOR LOOP ++++++++++++++++++++++++++++

//taking all the words that ever occur in the entire corpus, and multiplying by the inversedocfrequency
// this is helpful because then we don't have to hashmap an Id score in addition to a Sf score in Query - thus
// it saves MEMORY and TIME. 
for (words <- WordScoreMap.keys){
  inversedocscoreupdate(words)
}

//this is the program that propogates the PageRank hashmap. This is necessary when taking into account the 
// number of inbound links to a page. We propagate 5 times. 
for (m <- 0 until 5){
  val runninghashmap = new HashMap[Int, (Double, List[Int])] //creating a temporary hashmap to store new scores once they have been tampered with
  for(x <- DocTitleMap.keys.toList){
    // for all the documents
    PageRankScore.apply(x) match {
      case (score, list) => {runninghashmap.put(x,(0.85 / (DocTitleMap.size.toDouble),list))} //use a dampening factor for their score
    }}
    //for all the documents
    for (document <- DocTitleMap.keys.toList){
      PageRankScore.apply(document) match {
        case(pscore, plist) => {
          //for all the documents that the particular document we are working on has an outlink to
          for (docs <- plist){
          //we up that document's score
          var upscore : Double = 0.85 * pscore/(plist.length.toDouble)
          runninghashmap.apply(docs) match{
            //we store this new score in the temporary hashmap. 
            case(rscore, rlist) => runninghashmap.update(docs, (rscore + upscore, rlist))
          }
        }
      }
      }
    
  }
  //after each iteration, the PageRankScore HashMap is set equal to runninghashmap.
  PageRankScore = runninghashmap
}
}



object Indexer{

  
  def main(args : Array[String]):Unit = {
   //creates indexer for the inputed corpus
   val thingy = new Indexer(args(0))
   //makes a writer for the 2nd argument -> this writes a wordScoreMap
   val writer2 = new SearchWriter(args(1))
   writer2.writeLine(" " + thingy.WordScoreMap.toString.substring(4))
   writer2.close
   //makes a writer for the 3rd argument -> this writes a DocTitleMap
   val writer3 = new SearchWriter(args(2))
   writer3.writeLine(", " + thingy.DocTitleMap.toString.substring(4))
   writer3.close
   //makes a writer for the 4th argument -> this writes a PageRankScore 
   val prHashMapWriter = new SearchWriter(args(3))
   thingy.PageRankScore.foreach(x => prHashMapWriter.writeLine(x.toString.split("[^A-Za-z0-9.'?-]").filter(x => x != "").slice(0,2).mkString(" "))) //using this splitting will make it easier to access information from this doc in query
   prHashMapWriter.close
	}

}