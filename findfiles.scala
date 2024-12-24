import org.apache.hadoop.fs._

// Define the root path of the directory in Azure Blob Storage
val rootPath = "abfss://<container>@<storage-account>.dfs.core.windows.net/<directory>/"

// Get the Hadoop FileSystem object
val fs = FileSystem.get(spark.sparkContext.hadoopConfiguration)

// List all files in the directory recursively
val fileStatusList = fs.listStatus(new Path(rootPath))

// Filter files based on the pattern '_SUCCESS_RTG_*'
val pattern = "_SUCCESS_RTG_.*".r
val matchedFiles = fileStatusList.filter(file => pattern.pattern.matcher(file.getPath.getName).matches)

// Extract the file names and their parent directories
val fileAndParentDirs = matchedFiles.map { file =>
  val filePath = file.getPath.toString
  val parentDir = file.getPath.getParent.toString
  (filePath, parentDir)
}

// Print the results
println("Matched files and their parent directories:")
fileAndParentDirs.foreach { case (file, parent) =>
  println(s"File: $file, Parent Folder: $parent")
}
