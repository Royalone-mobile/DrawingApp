package com.songu.shadow.drawing.tools;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.songu.shadow.drawing.model.CustomPoint;

import android.content.Context;

public class SerializationTools {
	
	/**
	 * Serializes CustomPoints ArrayList objects
	 */
  	public static void serializePoints(Context c, ArrayList<CustomPoint> obj, String fileName) {
		FileOutputStream fos;
		try {
			fos = c.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(obj);
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  	
  	/**
	 * Retrieves CustomPoint ArrayList objects
	 * @return ArrayList<CustomPoint> object
	 */
  	@SuppressWarnings("unchecked")
	public static ArrayList<CustomPoint> loadPoints(Context c, String fileName) {
		ObjectInputStream inputStream = null;
		try {
			// Construct the ObjectInputStream object
			inputStream = new ObjectInputStream(c.openFileInput(fileName));

			Object obj = null;

			obj = inputStream.readObject();

			return (ArrayList<CustomPoint>) obj;


		} catch (EOFException ex) { // This exception will be caught when EOF is
		} catch (ClassNotFoundException ex) {
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
  	
  	public static void deletePoints(Context c, String fileName) {
		c.deleteFile(fileName);
  	}

}
