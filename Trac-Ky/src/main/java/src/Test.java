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
	public static final String storageConnectionString ="DefaultEndpointsProtocol=https;AccountName=trackytest;AccountKey=c1AGXKlmQP0kb8wD/VX4bDM9CDkGyjCC0PgENAw7dLI6ypvScSoeNtnpjolvErWc90FHR02L22QSiDREpk9RbQ==;EndpointSuffix=core.windows.net";

		public static void main(String[] args) {
			try {
				CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
	            CloudBlobClient serviceClient = account.createCloudBlobClient();

	            // Container name must be lower case.
	            CloudBlobContainer container = serviceClient.getContainerReference("new");
	            container.createIfNotExists();

	            // Upload an image file.
	            CloudBlockBlob blob = container.getBlockBlobReference("test\\fb_ritz.jpg");
	            File sourceFile = new File("C:\\Users\\Kzo\\Pictures\\fb_ritz.jpg");
	            blob.upload(new FileInputStream(sourceFile), sourceFile.length());

	            // Download the image file.
	            File destinationFile = new File(sourceFile.getParentFile(), "fb_ritzDownload.tmp");
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
