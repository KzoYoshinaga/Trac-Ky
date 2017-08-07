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

	public static void main(String...args) {
		String command = args[0];
		String storageConnectionString = args[1];
		String containerName = args[2];
		String dstFilePath = args[3];
		String srcFilePath = args[4];
		String filename = args[5];
		File dstFile = new File(dstFilePath, filename);
		File srcFile = new File(srcFilePath, filename);

		System.out.println(dstFile.toString());

		try {
			CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            CloudBlobContainer container = serviceClient.getContainerReference(containerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(dstFile.toString());

            if (command.equals("upload")) upload(blob, srcFile);
            else if(command.equals("download")) download(blob, srcFile);

        } catch (StorageException storageException) {
            System.out.print("StorageException encountered: ");
            System.out.println(storageException.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.out.print("Exception encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
	}

	public static void upload(CloudBlockBlob blob, File srcFile) {
		System.out.println("upload: src=" + srcFile.getAbsolutePath());
        try {
        	blob.upload(new FileInputStream(srcFile), srcFile.length());
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.print("FileNotFoundException encountered: ");
            System.out.println(fileNotFoundException.getMessage());
            System.exit(-1);
        } catch (StorageException storageException) {
            System.out.print("StorageException encountered: ");
            System.out.println(storageException.getMessage());
            System.exit(-1);
	    } catch (Exception e) {
            System.out.print("Exception encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
	}

	public static void download(CloudBlockBlob blob, File srcFile) {
		System.out.println("download: src=" + srcFile.getAbsolutePath());
        try {
			blob.downloadToFile(srcFile.getAbsolutePath());
        } catch (StorageException storageException) {
            System.out.print("StorageException encountered: ");
            System.out.println(storageException.getMessage());
            System.exit(-1);
	    } catch (Exception e) {
            System.out.print("Exception encountered: ");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
	}
}
