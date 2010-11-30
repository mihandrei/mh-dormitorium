package mh.pdist.auction.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * data stored in a file nice to do : make this into a mini database engine 1)
 * make access to file rows fast (store seek jumps at the beginning of the
 * file?) seekable files? java nio memory mapped files? 2) b-trees 4) locking or multi
 * versipon concurrency control
 * 
 * For now dump the memory to the file on every write
 * cars can be O(1) retrieved by id;
 * in o(1) we can retrieve all cars of the same model
 * other queries should just iterate the whole dataset or the model sub-datasets
 */

public class CarDataSource {
	//indexes the cars by id
	public HashMap<Integer, Car> carindex = new HashMap<Integer, Car>();
	//index by model
	HashMap<String, Set<Integer>> model2id_index = new HashMap<String, Set<Integer>>();
	
	private String filename;

	public CarDataSource(String filename) throws IOException {
		this.filename = filename;
		if (new File(filename).exists()) {
			readfrom(new FileInputStream(filename));
		}		
	}

	public Car get(int id) {
		return carindex.get(id);
	}
	
	/**
	 * a o(1) operation that retrieves all the cars with the same model
	 */
	public Set<Integer> getByModel(String model) {
		return model2id_index.get(model);
	}

	/**
	 * update or insert
	 */
	public void put(Car c) throws IOException {
		carindex.put(c.id, c);
		
		if(!model2id_index.containsKey(c.model)){
			model2id_index.put(c.model,new HashSet<Integer>());
		}
		model2id_index.get(c.model).add(c.id);
	}

	public void remove(Car c) throws IOException {
		carindex.remove(c.id);		
		model2id_index.get(c.model).remove(c.id);
	}
	
	/**
	 * writes the changes to the file
	 * @throws IOException
	 */
	public void commit() throws IOException{
		dumpTo(new FileOutputStream(filename));
	}

	private void dumpTo(OutputStream os) throws IOException {
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(new OutputStreamWriter(os, Charset
					.forName("UTF-8")));

			for (Car c : carindex.values()) {
				writer.println(c.asString());				
			}

		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private void readfrom(InputStream is) throws IOException {
		carindex.clear();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(is, Charset
					.forName("UTF-8")));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Car c = Car.fromStr(line);
				carindex.put(c.id, c);
			}

		} finally {
			if (reader != null)
				reader.close();
		}
	}

}
