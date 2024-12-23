import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

// Initialize Spark session
val spark = SparkSession.builder.appName("List Folders in Azure Storage").getOrCreate()

// Replace with your Azure Storage account and container name
val storageAccountName = "<your_storage_account_name>"
val containerName = "<your_container_name>"

// Base URL for accessing Azure Storage
val basePath = s"abfss://$containerName@$storageAccountName.dfs.core.windows.net/"

// Initialize Hadoop FileSystem
val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

// Function to list all folders under a given path
def listFolders(path: String): List[String] = {
  val directoryPath = new Path(path)
  val statusList = fs.listStatus(directoryPath)
  statusList
    .filter(_.isDirectory)   // Filter directories
    .map(_.getPath.toString) // Get folder paths
    .toList
}

// List folders under the root path
val folders = listFolders(basePath)

// Print folders
println("Folders under the given path:")
folders.foreach(println)
