package com.sun.imageloader.core;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

import com.sun.imageloader.imagedecoder.utils.L;

public class ImageWriter {
	private static final String TAG = ImageWriter.class.getName();
	private static final String THREAD_NAME = Thread.currentThread().getName();
	private File _fileSaveDirectory;

	/**
	 * Writes the {@link Bitmap} objects to a specified directory, used later for retrieveing {@link Bitmap} objects without performing
	 * a network call.
	 * 
	 * @param fileSaveDirectory_
	 * 				{@link File} object containing a references to the write directory
	 */
	public ImageWriter(String fileSaveDirectory_) {
		_fileSaveDirectory = new File(fileSaveDirectory_);
	}

	public ImageWriter(File fileSaveDirectory_) {
		_fileSaveDirectory = fileSaveDirectory_;
	}

	/**
	 * Write the specified {@link Bitmap} to the directory location
	 * 
	 * @param imageSettings_
	 * 				which identifies the image that will be stored on the disk and cached on external storage
	 * @param bitmap_
	 * 				to write to disk
	 */
	public void writeBitmapToDisk(ImageSettings imageSettings_, Bitmap bitmap_) {
		File imageFileLocation = new File(_fileSaveDirectory, imageSettings_.getFinalFileName());
		L.v(TAG, THREAD_NAME + ": Attempting to decode and save image file with name: "
				+ imageSettings_);

		if (!imageFileLocation.exists()) {
			
			L.v(TAG, THREAD_NAME + " :File doesn't exist, so creating file with path: "
					+ imageFileLocation.getAbsolutePath());
			
			boolean createdDir = imageFileLocation.getParentFile().mkdirs();
			
			L.v(TAG, THREAD_NAME + " :Created dir with status: "
					+ createdDir);

			if (imageFileLocation.getParentFile().exists()) {
				BufferedOutputStream fileOs = null;
				try {
					L.v(TAG, THREAD_NAME + " :Writing bitmap to disk nao: " + imageSettings_.getFinalFileName());

					fileOs = new BufferedOutputStream(new FileOutputStream(
							imageFileLocation));
					if(bitmap_ != null){
						bitmap_.compress(imageSettings_.getCompressformat(), imageSettings_.getImageQuality(), fileOs);
						fileOs.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					closeOutputStream(fileOs);
				}

			}
		} else {
			L.v(TAG, THREAD_NAME + " :File already exsists so not creating new file: "
					+ imageSettings_.getFinalFileName());
		}
	}

	private void closeOutputStream(Closeable closeable_) {
		if (closeable_ != null) {
			try {
				closeable_.close();
			} catch (IOException e) {
				e.printStackTrace();
				closeable_ = null;
			}
		}
	}
	
	public File getFileSaveDirectory(){
		return new File(_fileSaveDirectory.getAbsolutePath());
	}
	
	public String getFileSaveDirectoryPath(){
		return _fileSaveDirectory.getAbsolutePath();
	}
}
