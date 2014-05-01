package search
object Test {
  def main(args:Array[String]) {
val writer = new SearchWriter("file")
writer.write("Hello World!")
writer.close
  }}
