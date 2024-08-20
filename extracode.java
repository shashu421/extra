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


--------
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.BlobClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;

public class HttpToAzureBlobUploader {

    public static void main(String[] args) {
        String httpUrl = "http://example.com/file.txt";
        String connectionString = "your_azure_blob_storage_connection_string";
        String containerName = "your-container-name";
        String blobName = "file.txt";

        // Proxy settings
        String proxyHost = "your_proxy_host";
        int proxyPort = 8080; // Replace with your proxy port

        try {
            // Fetch content from HTTP URL
            byte[] fileContent = fetchContentFromHttpUrl(httpUrl, proxyHost, proxyPort);

            // Upload content to Azure Blob Storage
            uploadToAzureBlob(fileContent, connectionString, containerName, blobName);

            System.out.println("File uploaded to Azure Blob Storage successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] fetchContentFromHttpUrl(String httpUrl, String proxyHost, int proxyPort) throws IOException {
        // Configure the proxy
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
        
        // Create a HttpClient with proxy configuration
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();

        try {
            HttpGet httpGet = new HttpGet(httpUrl);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toByteArray(entity);
            } else {
                throw new IOException("Failed to fetch content from HTTP URL.");
            }
        } finally {
            httpClient.close();
        }
    }

    private static void uploadToAzureBlob(byte[] content, String connectionString, String containerName, String blobName) {
        // Create a BlobServiceClient
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        // Get a BlobClient
        BlobClient blobClient = blobServiceClient.getBlobContainerClient(containerName)
                .getBlobClient(blobName);

        // Upload the file content
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            blobClient.upload(inputStream, content.length, true);
        }
    }
}



------()()

            
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnectionToByteArray {

    public static void main(String[] args) {
        String httpUrl = "http://example.com/file.txt"; // Replace with your URL

        try {
            // Fetch content from HTTP URL
            byte[] fileContent = fetchContentAsByteArray(httpUrl);

            // For demonstration: print the content as a string
            System.out.println(new String(fileContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] fetchContentAsByteArray(String httpUrl) throws IOException {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try (InputStream inputStream = connection.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            
            // Buffer to read data
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            // Read the input stream into the byte array output stream
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            
            // Convert to byte array
            return byteArrayOutputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
}





            
