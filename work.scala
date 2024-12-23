import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.BlobItem
import scala.jdk.CollectionConverters._

// Replace with your Azure Storage details
val connectionString = "<your_connection_string>"
val containerName = "<your_container_name>"
val basePath = "<path_in_container>" // Example: "data/", or "" for root

// Create a BlobServiceClient
val blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient()
val blobContainerClient = blobServiceClient.getBlobContainerClient(containerName)

// List all folders under the given path
val blobs = blobContainerClient.listBlobsByHierarchy(basePath + "/").iterator().asScala

// Collect folder prefixes (folders end with "/")
val folders = blobs.filter(_.isPrefix).map(_.getName).toList

// Print folders
println("Folders under the given path:")
folders.foreach(println)
