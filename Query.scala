package search

import scala.xml.Node 
//import indexer.SubNode
import scala.collection.mutable.HashMap
import scala.collection.mutable.LinkedList
import scala.util.control.Breaks._
import scala.math
import scala.io.Source

class Query (DocTitleMap: String, freqIndexMap: String, EuclidVals: String, titlefreqMap: String, BodyPositionMap: String) {

  val doctitlemap = new HashMap[Int, String]
  
  var idtitles = Source.fromFile(DocTitleMap).getLines.toArray
  
  for (line <- 0 until idtitles.length-1 by 2){
    var doc = idtitles(line).split("[^0-9]+")(1).toInt
    var title = idtitles(line+1)
    doctitlemap += ((doc, title))
    
  }
  
  val doceuclidvals = new HashMap[Int, Double]
  
  var euclids = Source.fromFile(EuclidVals).getLines
  
  for (line <- euclids){
    var data = line.split(",")
    doceuclidvals += ((data(0).substring(1).toInt, data(1).dropRight(1).toDouble))
  }
  
  val docwordfreqmap = new HashMap[(String,Int),Int]
  
  val freqs = Source.fromFile(freqIndexMap).mkString
  
  val freqarray = freqs.split("[))]+")
  //println(freqarray(0))
  
  for (i <- 0 until freqarray.length-1){
    val wordlist = freqarray(i).substring(2).split("List")
    val datalist = wordlist(1).split(",")

    for (j<-0 until datalist.length){
      val numlist = datalist(j).split(" Freq: ")
      if(j==0) docwordfreqmap += (((wordlist(0).dropRight(1),numlist(0).substring(8).toInt),numlist(1).toInt))
      else docwordfreqmap += (((wordlist(0).dropRight(1),numlist(0).substring(8).toInt),numlist(1).toInt))
    }
  }

  
  
}

object Query{

  
  def main(args : Array[String]):Unit = {
    val thing = new Query(args(0), args(1), args(2), args(3), args(4))
    println(thing.docwordfreqmap.get(("is",98)))

  }
}