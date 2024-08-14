DataLakeFileClient fileClient = serviceClient.getFileSystemClient(fileSystemName)
            .getDirectoryClient(directoryName)
            .getFileClient(fileName);

        // Sample BufferedReader (replace with your own BufferedReader source)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) { // Reading from standard input for demonstration
            // Create or open the file
            try (OutputStream outputStream = fileClient.appendData(true)) { // Use append mode if you want to append
                String line;
                while ((line = reader.readLine()) != null) {
                    outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                    outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
                }
            }

            System.out.println(String.format("Data successfully written to %s in Azure Data Lake Storage.", fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
