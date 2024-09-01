package prog11;

import java.io.*;
import java.util.*;


public class HardDisk extends TreeMap<Long, InfoFile> {
    public Long newFile () {
	Long index = nextIndex;
	nextIndex += 1 + random.nextInt(4);
	return index;
    }

    public boolean write (String fileName) {
	try {
	    // Create PrintWriter for the file.
	    PrintWriter out = new PrintWriter(new FileWriter(fileName));

	    for (Map.Entry entry : entrySet()) {
		out.println(entry.getKey());
		InfoFile file = (InfoFile) entry.getValue();
		out.println(file.data);
		out.println(file.impact);
		out.println(file.indices.size());
		for (Long index : file.indices)
		    out.println(index);
	    }

	    out.close();
	} catch (Exception ex) {
	    System.err.println("Could not write to " + fileName);
	    return false;
	}
	
	return true;
    }

    public boolean read (String fileName) {
	clear();
	try {
	    Scanner in = new Scanner(new File(fileName));

	    while (in.hasNextLong()) {
		Long index = in.nextLong();
		String data = in.nextLine();
		data = in.nextLine();
		// System.out.println("data " + data);
		InfoFile file = new InfoFile(data);
		file.impact = in.nextDouble();
		int n = in.nextInt();
		for (int i = 0; i < n; i++)
		    file.indices.add(in.nextLong());
		put(index, file);
	    }

	    // Close the file.
	    in.close();
	} catch (FileNotFoundException ex) {
	    // Do nothing: no data to load.
	    System.out.println("Could not read " + fileName);
	    return false;
	}

	return true;
    }

    private Long nextIndex = 0L;
    private Random random = new Random(0);
}
