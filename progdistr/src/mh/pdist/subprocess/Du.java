package mh.pdist.subprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab1 ( procese externe ): Sa se determine spatiul ocupat de un director
 * precizat ca parametru. Se va folosi comanda du. Se vor detecta toate cazurile
 * de eroare: lipsa unui parametru in linia de comanda, specificarea unui
 * parametru care nu este director, lipsa drepturilor de parcurgere a
 * continutului directorului specificat.
 * 
 * 
 */
public class Du {
	/**
	 * Starts a subprocess with the given command, waits for it to finish and buffers the output 
	 * into a list which it returns
	 */
	static List<String> invoke_synchron(String... args) throws IOException, InterruptedException {
		ArrayList<String> output = new ArrayList<String>(3);
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Process proc = pb.start();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				proc.getInputStream()));
		String line;
		while ((line = bufreader.readLine()) != null) {// atentie blocheaza
			output.add(line);
		}
		proc.waitFor();// i oligatoriu sa fac asta , sau procesu i terminat daca
						// o inchis sysoutu?
		bufreader.close();
		// detctez erorile procesand outputul mai jos
		// if(proc.exitValue()!= 0 ){
		// System.err.println("subprocess terminated with nonzero status "+
		// proc.exitValue());
		// System.exit(proc.exitValue());
		// }
		return output;
	}

	public static void main(String[] args) {
		// ProcessBuilder pb = new ProcessBuilder("du");

		// check precond
		String os = System.getProperty("os.name");
		if (os == null || !os.contains("Linux")) {
			System.err.println("this tool is linux specific");
			System.exit(-1);
		}

		// check arg
		if (args.length != 1) {
			System.err.println("first argument must be a directory");
			System.exit(-2);
		}
		
		String dir = args[0];
		List<String> output=null;
		try {
			output = invoke_synchron("du","-sc",dir);
		} catch (IOException e) {
			System.err.println("could not open subprocess");
			System.exit(-3);
			e.printStackTrace();
		} catch (InterruptedException e) {
       	    System.err.println("unexpected thread interuption");
			System.exit(-3);
			e.printStackTrace();
		}
		// process output

		// du poate raporta ca nu gaseste un director chiar daca nu-i cel
		// specificat de utilizator
		// se intampla daca imediat dupa citirea directorului parinte copilul a
		// fost sters
		// Se intampla mai ales in /proc pentru ca-i foarte volatil
		// Deci singurul mod sigur de a detecta ca argumentul e gresit e cel de
		// jos
		// `' necesare ca de nu pt "du /proc" /proc/3115 contine /proc

		if (output.get(0).contains("No such file or directory")
				&& output.get(0).contains("`" + dir + "'")) {
			System.err.println("first argument is not a directory");
			System.exit(-3);
		}

		if (output.get(0).contains("Permission denied")
				&& output.get(0).contains("`" + dir + "'")) {
			System.err.println("do not have enough permissions to traverse "
					+ dir);
			System.exit(-4);
		}

		// toate liniile in afara de ultima si penultima sunt erori , le afisez
		// as putea sa le parsez ca mai inainte dar fara mare utilitate

		for (int i = 0; i <= output.size() - 3; i++) {
			System.out.println(output.get(i));
		}

		if (output.size() > 2) {
			System.err.println("some errors occured");
			System.exit(33);
		}

		// ultima linie e totalul
		String total = output.get(output.size() - 1).split("\\s")[0];
		System.out.println("total space ocupied by directory: " + total);
	}
}
