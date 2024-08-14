import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BlobStorageExample {

    public static void main(String[] args) {
        String connectionString = "your-azure-storage-connection-string";
        String containerName = "your-container-name";
        String blobName = "your-blob-name";

        // Create a BlobClient object
        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        // Sample BufferedReader (could be from any source)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://example.com").openStream(), StandardCharsets.UTF_8))) {
            // Upload the BufferedReader content to Azure Blob Storage
            uploadBufferedReaderToBlob(blobClient, reader);
            System.out.println("Upload complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void uploadBufferedReaderToBlob(BlobClient blobClient, BufferedReader reader) {
        try (OutputStream outputStream = blobClient.getBlockBlobClient().getBlobOutputStream()) {
            String line;
            while ((line = reader.readLine()) != null) {
                outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                outputStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BlobStorageException e) {
            e.printStackTrace();
        }
    }
}
