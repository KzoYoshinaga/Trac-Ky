package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

public class Test {
	public static final String storageConnectionString ="";

		public static void main(String[] args) {
			try {
				CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
	            CloudBlobClient serviceClient = account.createCloudBlobClient();

	            // Container name must be lower case.
	            CloudBlobContainer container = serviceClient.getContainerReference("containerName");
	            container.createIfNotExists();

	            // Upload an image file.
	            CloudBlockBlob blob = container.getBlockBlobReference("targetFilePath");
	            File sourceFile = new File("filePath");
	            blob.upload(new FileInputStream(sourceFile), sourceFile.length());

	            // Download the image file.
	            File destinationFile = new File(sourceFile.getParentFile(), "Filename");
	            blob.downloadToFile(destinationFile.getAbsolutePath());
	        }
	        catch (FileNotFoundException fileNotFoundException) {
	            System.out.print("FileNotFoundException encountered: ");
	            System.out.println(fileNotFoundException.getMessage());
	            System.exit(-1);
	        }
	        catch (StorageException storageException) {
	            System.out.print("StorageException encountered: ");
	            System.out.println(storageException.getMessage());
	            System.exit(-1);
	        }
	        catch (Exception e) {
	            System.out.print("Exception encountered: ");
	            System.out.println(e.getMessage());
	            System.exit(-1);
	        }
		}
}
