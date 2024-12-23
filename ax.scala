import org.apache.spark.sql.SparkSession

// Initialize Spark Session
val spark = SparkSession.builder.appName("Azure Storage File Scanner").getOrCreate()

// Replace with your ADLS account and container name
val storageAccountName = "<your_storage_account_name>"
val containerName = "<your_container_name>"

// Base path for ADLS
val basePath = s"abfss://$containerName@$storageAccountName.dfs.core.windows.net/"

// Regular expressions for file patterns
val successPattern = "_SUCCESS_RTG_\\d+".r
val excludePattern = "93005_SGAS_\\d{8}_SGAS0RTG_OrderEvents_000001\\.csv".r

// Function to find folders with specific patterns
def scanFolder(folderPath: String): Option[String] = {
  try {
    val filesDF = spark.read.format("binaryFile").load(folderPath + "/*") // Load all files in the folder
    val filePaths = filesDF.select("path").collect().map(_.getString(0))  // Get all file paths as an array

    val containsSuccess = filePaths.exists(path => successPattern.findFirstIn(path).isDefined)
    val containsExcluded = filePaths.exists(path => excludePattern.findFirstIn(path).isDefined)

    if (containsSuccess && !containsExcluded) Some(folderPath) else None
  } catch {
    case e: Exception =>
      println(s"Error scanning folder $folderPath: ${e.getMessage}")
      None
  }
}

// List all folders at the root level
val rootFoldersDF = spark.read.format("binaryFile").load(basePath + "/*")
val rootFolders = rootFoldersDF.select("path").collect().map(_.getString(0)).filter(_.endsWith("/"))

// Scan each folder for the required patterns
val matchingFolders = rootFolders.flatMap(scanFolder)

// Print the results
println("Folders containing _SUCCESS_RTG_ files but not 93005_SGAS_ files:")
matchingFolders.foreach(println)
