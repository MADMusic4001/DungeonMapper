package com.madmusic4001.dungeonmapper.data.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import com.madmusic4001.dungeonmapper.R;
import com.madmusic4001.dungeonmapper.data.exceptions.DataException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility methods for working with files in the Android file system.
 */
@Singleton
public class FileUtils {
    private Context context;
	private String documentsDirName = null;

	@Inject
	public FileUtils(Context context) {
		this.context = context;
	}

	/**
	 * Writes a {@link android.graphics.Bitmap} to a file as a byte array
	 *
	 * @param stream the {@link java.io.FileOutputStream} to write to.
	 * @param bitmap the Bitmap to be written.
	 * @throws java.io.IOException
	 */
    @SuppressWarnings("unused")
    //TODO: remove method or remove SuppressWarning
	public void writeBitmap(DataOutputStream stream, Bitmap bitmap)
	throws IOException {
		ByteArrayOutputStream bytesStream;
		byte[] bytes;

		if(bitmap != null) {
			bytesStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytesStream);
			bytesStream.flush();
			bytes = bytesStream.toByteArray();
			stream.writeInt(bytes.length);
			stream.write(bytes);
		}
		else {
			stream.writeInt(0);
		}
	}

	/**
	 * Reads a byte array from a file and creates a {@link android.graphics.Bitmap}.
	 *
	 * @param input the {@link java.io.FileInputStream} to read from.
	 * @return a Bitmap or null if the length of the byte array is 0 or
	 * 			{@link android.graphics.BitmapFactory} fails to decode the byte array.
	 * @throws java.io.IOException
	 */
    @SuppressWarnings("unused")
    //TODO: remove method or remove SuppressWarning
	public Bitmap readBitmap(InputStream input) throws IOException {
		Bitmap bitmap = null;
		int bytesLength;
		byte[] bytes;
		int bytesRead;
		ByteArrayInputStream bytesStream;
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		bytesLength = Integer.parseInt(reader.readLine());
		if(bytesLength > 0) {
			bytes = new byte[bytesLength];
			bytesRead = input.read(bytes);
			if(bytesRead != bytesLength) {
				throw new IOException();
			}
			bytesStream = new ByteArrayInputStream(bytes);
			bitmap = BitmapFactory.decodeStream(bytesStream);
			if(bitmap == null) {
				throw new IOException();
			}
		}
		return bitmap;
	}

	/**
	 * Gets a File object for the specified file in the path relative to the desired app storage root.
	 *
	 * @param useExternalStorage true to get the path to external storage or false to get the path to internal storage.
	 * @param relativePath directory path name to search relative to the application directory. null or 0 length to search
	 *                     the root application directory.
	 * @param fileName the name of the desired file.
	 * @return a File instance for the requested path and file name.
	 */
	public File getFile(boolean useExternalStorage, String relativePath, String fileName) {
		File targetDir = getTargetDir(useExternalStorage, relativePath);
		return new File(targetDir, fileName);
	}

	/**
	 * Gets the app`s root directory path.
	 *
	 * @param useExternalStorage true to get the path to external storage or false to get the path to internal storage.
	 * @return a File representing the app`s root directory.
	 */
    @SuppressWarnings("unused")
    //TODO: remove method or remove SuppressWarning
	public File getAppDir(boolean useExternalStorage) {
		return getTargetDir(useExternalStorage, null);
	}

	/**
	 * Gets a FileOuputStream for the specified file name in the apps external storage directory.
	 *
	 * @param publicDirType the Environment defined public directory type string where the file should be written or null if
	 *                      the file should be written to the apps external storage folder.
	 * @param filename the name of the file
	 * @return a FileOutputStream
	 * @throws java.io.FileNotFoundException if the file could not be opened for writing.
	 */
    @SuppressWarnings("unused")
    //TODO: remove method or remove SuppressWarning
	public FileOutputStream getOutputStream(String publicDirType, String filename)
			throws FileNotFoundException {
		File dir, appDir;

		if(!isExternalStorageWritable()) {
			throw new DataException(R.string.exception_externalStorageNotAvailable, filename);
		}
		if(publicDirType != null) {
			dir = Environment.getExternalStoragePublicDirectory(publicDirType);
		}
		else {
			dir = getExternalFilesDir();
		}
		appDir = new File(dir.getPath() + "/Android/data/" + context.getPackageName());
		if(!appDir.mkdirs() && !appDir.exists()) {
            throw new DataException(R.string.exception_dirNotCreated,
									appDir.getAbsoluteFile().getName());
        }
		
		return new FileOutputStream(
				new File(appDir.getAbsolutePath() + File.separator + filename));
	}

	private File getExternalFilesDir() throws DataException {
		File dir = context.getExternalFilesDir(getDocumentsDir());
		if(dir == null) {
			throw new DataException(R.string.exception_externalStorageNotAvailable);
		}
		return dir;
	}	

	/**
	 * Gets all files with file names matching the matchString regular expression from the
     * application directory in either
	 * internal storage or external storage.
	 * 
	 * @param fromExternalStorage true to search the application's external storage directory, false to search the
	 *                            application's internal storage directory.
	 * @param relativePath directory path name to search relative to the application directory. null or 0 length to search
	 *                     the root application directory.
	 * @param matchString a regular expression that the file name must match
	 * @return a Collection of File instances.
	 * @throws DataException
	 */
	public Collection<File> getFiles(boolean fromExternalStorage, String relativePath, final String matchString) throws DataException {
		File targetDir;
		File[] files;
		Collection<File> objects = new ArrayList<>();

		targetDir = getTargetDir(fromExternalStorage, relativePath);
		files = targetDir.listFiles(new FilenameFilter() {
			Pattern regex = null;

			@Override
			public boolean accept(File dir, String filename) {
				if(matchString != null && regex == null) {
					regex = Pattern.compile(matchString);
				}
                return matchString == null || regex.matcher(filename).matches();
            }
		});

		if(files != null) {
            objects.addAll(Arrays.asList(files));
		}

		return objects;
	}

	private boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
	}

	/**
	 * Gets the Android Environment string for the directory to save documents to based on the SDK version on the device.
	 * 
	 * @return the documents directory name.
	 */
	public String getDocumentsDir() {
		if(documentsDirName == null) {
			if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
				documentsDirName = Environment.DIRECTORY_DCIM;
			}
			else {
				documentsDirName = Environment.DIRECTORY_DOCUMENTS;
			}
		}
		return documentsDirName;
	}

	/**
	 * Gets a FileOutputStream that can be used to write to a file.
	 * 
	 * @param useExternalStorage true if the file should be stored in external storage or false if it should be in internal storage
	 * @param relativePath the path relative to the applications 'home' directory where the file should be written to.
	 * @param fileName the name of the file to be opened for writing.
	 * @return a FileOutputStream instance.
	 */
    @SuppressWarnings("unused")
    //TODO: remove method or remove SuppressWarning
	public FileOutputStream getFileOutputStream(boolean useExternalStorage, String relativePath, String fileName)
			throws FileNotFoundException {
		File targetDir, targetFile;

		targetDir = getTargetDir(useExternalStorage, relativePath);
		targetFile = new File(targetDir, fileName);
		try {
			return new FileOutputStream(targetFile);
		}
		catch(FileNotFoundException ex) {
			throw new DataException(R.string.exception_errorOpeningForWrite);
		}
	}

	private File getTargetDir(boolean useExternalStorage, String relativePath) {
		File appDir, targetDir;

		if(!useExternalStorage) {
			appDir = context.getFilesDir();
		}
		else {
			appDir = getExternalFilesDir();
		}

		if(relativePath != null && relativePath.length() > 0) {
			targetDir = new File(appDir, relativePath);
		}
		else {
			targetDir = appDir;
		}
		if(!targetDir.mkdirs() && !targetDir.exists()) {
            throw new DataException(R.string.exception_dirNotCreated,
									targetDir.getAbsoluteFile().getName());
        }
		return targetDir;
	}
}
