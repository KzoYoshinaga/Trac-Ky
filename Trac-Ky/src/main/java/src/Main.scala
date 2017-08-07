
package src



// storage

trait Storage {
  def upload
  def download
}

class AzureBolb extends Storage {
  import com.microsoft.azure.storage._
  import com.microsoft.azure.storage.blob._
  override def upload = ???
  override def download = ???
}

class LocalStrage extends Storage {
  override def upload = ???
  override def download = ???
}

class Directory {
  val storage : Storage = ???
  val root : String = ???
  val path : String = ???
}

class File {
  val directory : Directory = ???
  val file : String = ???
}


// storage actors

class Duplicator {
  def copy = ???
}

class Terminator {
  def delete = ???
}

class Transporter {
  def move = ???
}

class Fetcher {
  def getFile = ???
}

class Scouter {
  def isExist = ???
  def getAttr = ???
  def getFileList = ???
}


// worker counter, countAccumulator
class Trigger
class Event
class Worker

// text file
class Text
class IISLog extends Text
class AccessCount extends Text



