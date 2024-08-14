// Get the DataLakeFileClient
        val fileSystemClient = serviceClient.getFileSystemClient(fileSystemName)
        val directoryClient = fileSystemClient.getDirectoryClient(directoryName)
        val fileClient = directoryClient.getFileClient(fileName)

        // Sample BufferedReader (replace with your own BufferedReader source)
        val reader = new BufferedReader(new InputStreamReader(System.in)) // Reading from standard input for demonstration

        try {
            // Create or open the file
            val outputStream: OutputStream = fileClient.appendData(true) // Use append mode if you want to append
            // Create a byte buffer to hold the data
            val buffer = new Array 
            var bytesRead: Int = 0

            // Read data from BufferedReader and write it to the OutputStream
            while ({bytesRead = reader.read(buffer); bytesRead} != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            // Close the OutputStream
            outputStream.close()

            println(s"Data successfully written to $fileName in Azure Data Lake Storage.")
        } catch {
            case e: Exception => e.printStackTrace()
        } finally {
            reader.close() // Ensure BufferedReader is closed
        }
