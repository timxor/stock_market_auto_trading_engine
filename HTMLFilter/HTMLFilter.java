import java.io.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.ArrayList;
public class HTMLFilter
{
    private static Path sourcePath;
    private static Path logPath;

    private static String log = "";

    public static void main(String args[])
    {
        if(args.length!=1)
            System.out.println("Usage: java HTMLCleaner <Directory of HTMLs>");
        sourcePath = Paths.get(args[0]);
        logPath = Paths.get("/home/simon/Desktop/log.txt");


        try (
        		DirectoryStream<Path> stream = Files.newDirectoryStream(sourcePath);
        		BufferedWriter writer = Files.newBufferedWriter(logPath, StandardCharsets.UTF_8);
        )
        {
        	ArrayList<Double> EPSs = new ArrayList<>();
            for (Path HTML: stream)
            {
            	log += "Filtering " + HTML.toString() + "\n";
                System.out.println("Filtering " + HTML.toString());
            	try(BufferedReader reader = Files.newBufferedReader(HTML, StandardCharsets.UTF_8))
            	{
                    String content = "";
                    String line = "";
                    while((line = reader.readLine()) != null)
                    	content+=line;
                    String EPS = "";
                    String[] words = clean(content).split(" ");
                    log += "\tClean words count: " + words.length + "\n";
                     System.out.println(words.length);
                    for(int c = 50;c < words.length;c++)
                    	if(words[c].equals("EPS"))
                    	{
                    		EPS = words[c+1];
                    		break;
                    	}
                    EPSs.add(Double.parseDouble(EPS));
                    log += "\tEPS: " + EPS + "\n";
            	}

            }
            writer.write(log);
        } catch (IOException | DirectoryIteratorException x)
        {
            x.printStackTrace();
        }
    }


    public static String clean(String html)
    {
    	long startTime = System.currentTimeMillis();
    	html = stripComments(html);
    	html = stripElement(html, "head");
		html = stripElement(html, "style");
		html = stripElement(html, "script");
    	html = stripTags(html);
    	html = stripEntities(html);
		html = stripSpaces(html);
		long stopTime = System.currentTimeMillis();
		log += "\tCleaning time took: " + (stopTime - startTime)/1000.0 + " seconds.\n";
		return html;
    }
    public static String stripEntities(String html)
	{return html.replaceAll("&[^\\s]+?;", " ");}

	public static String stripComments(String html)
	{return html.replaceAll("(?s)<!-.*?->", " ");}

	public static String stripTags(String html)
	{return html.replaceAll("<[^>]*?>", " ");}

	public static String stripElement(String html, String name)
	{return html.replaceAll("(?s)(?i)<"+name+".*?</"+name+".*?>", " ");}

	public static String stripSpaces(String html)
	{return html.replaceAll("\\p{Space}+", " ");}




}
