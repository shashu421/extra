import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession
import scala.util.matching.Regex

// Initialize Spark Session
val spark = SparkSession.builder.appName("Azure Blob Storage Scanner").getOrCreate()

// Replace with your Azure Storage account and container details
val storageAccountName = "<your_storage_account_name>"
val containerName = "<your_container_name>"

// Base path for Azure Blob Storage
val basePath = s"abfss://$containerName@$storageAccountName.dfs.core.windows.net/"

// Regular expressions for file patterns
val successPattern: Regex = "_SUCCESS_RTG_\\d+".r
val excludePattern: Regex = "93005_SGAS_\\d{8}_SGAS0RTG_OrderEvents_000001\\.csv".r

// Function to list all files and directories recursively in a given folder
def listFiles(fs: FileSystem, folderPath: Path): List[String] = {
  val files = fs.listStatus(folderPath)
  files.flatMap(status => {
    if (status.isDirectory) listFiles(fs, status.getPath) // Recurse into subdirectories
    else List(status.getPath.toString) // Add file path
  }).toList
}

// Initialize Hadoop FileSystem
val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

// List all folders at the root level
val rootPath = new Path(basePath)
val folders = fs.listStatus(rootPath).filter(_.isDirectory).map(_.getPath)

// Process each folder to check for patterns
val matchingFolders = folders.flatMap(folder => {
  val files = listFiles(fs, folder)
  val containsSuccess = files.exists(file => successPattern.findFirstIn(file).isDefined)
  val containsExcluded = files.exists(file => excludePattern.findFirstIn(file).isDefined)

  if (containsSuccess && !containsExcluded) Some(folder.toString) else None
})

// Print the folders that match the criteria
println("Folders containing _SUCCESS_RTG_ files but not 93005_SGAS_ files:")
matchingFolders.foreach(println)
