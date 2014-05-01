package search
import java.io._;
class SearchWriter(val fileName: String) {
  val writer = new BufferedWriter(new FileWriter(fileName))
  def write(toWrite: String) {
    writer.write(toWrite)
    writer.flush
  }
  def writeLine(toWrite:String) {
    writer.write(toWrite + "\n")
    writer.flush
  }
  def close() {
    writer.close
  }
}
