import org.apache.spark.sql.SparkSession
import java.util.regex.Pattern

// Initialize Spark Session (In Synapse, this is usually pre-configured)
val spark = SparkSession.builder.appName("Azure Storage File Scanner").getOrCreate()

// Replace with your ADLS account name and container name
val storageAccountName = "<your_storage_account_name>"
val containerName = "<your_container_name>"

// Base URL for accessing ADLS
def adlsUrl(path: String): String = s"abfss://$containerName@$storageAccountName.dfs.core.windows.net/$path"

// Regular expressions for file patterns
val successPattern = "_SUCCESS_RTG_\\d+".r
val excludePattern = "93005_SGAS_\\d{8}_SGAS0RTG_OrderEvents_000001\\.csv".r

// Function to list folders and files recursively
def listFiles(folderPath: String): List[String] = {
  try {
    val files = dbutils.fs.ls(folderPath)
    files.flatMap(file => {
      if (file.isDir) listFiles(file.path)
      else List(file.path)
    }).toList
  } catch {
    case _: Exception => List.empty[String] // Handle inaccessible paths
  }
}

// Fetch all folders from the root of the container
val rootPath = adlsUrl("")
val folders = dbutils.fs.ls(rootPath).filter(_.isDir).map(_.path)

// Process each folder and check for file patterns
val result = folders.flatMap(folder => {
  val files = listFiles(folder)
  val containsSuccess = files.exists(file => successPattern.findFirstIn(file).isDefined)
  val containsExcluded = files.exists(file => excludePattern.findFirstIn(file).isDefined)

  if (containsSuccess && !containsExcluded) Some(folder) else None
})

// Print the folders meeting the criteria
println("Folders containing _SUCCESS_RTG_ files but not 93005_SGAS_ files:")
result.foreach(println)
