package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 *
 * @author Jon Cook, Ph.D.
 *
 **/

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.*;

public class WebWorker implements Runnable
{

	private Socket socket;

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		String request = null;
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			System.out.println("input stream read: " + is);
			request = readHTTPRequest(is);
			if(request.equals("favicon.ico"))
				System.out.println("Looking for url icon image");
			String extension = request.substring(request.lastIndexOf(".") + 1);
			System.out.println("File Extension: " + extension);
			if(extension.equals("html")){
				writeHTTPHeader(os, "text/html", request);
				writeContentText(os,request);//, "text/html");
			}else if(extension.equals("png")){
				writeHTTPHeader(os, "image/png", request);
				writeContentImage(os,request);//, "image/jpeg");
			}else if(extension.equals("jpeg")){
				writeHTTPHeader(os, "image/jpeg", request);
				writeContentImage(os,request);//, "image/jpeg");
			}else if(extension.equals("gif")){
				writeHTTPHeader(os, "image/gif", request);
				writeContentImage(os,request);//, "image/jpeg");
			}else{
				writeHTTPHeader(os, "text/html", request);
				writeContentText(os,request);
			}
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private String readHTTPRequest(InputStream is)
	{
		String line;
		String request;
		String retRequest = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		try{
			request = r.readLine();
			System.out.println("request = " + request);
			retRequest = request.substring(5,(request.length() - 9));
			System.out.println("retRequest = " + retRequest);
		}catch(Exception e){
			System.err.println("Request error: " + e);
		}

		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return retRequest;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 *
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, String request) throws Exception
	{
		if(request != null){
			String name;//used to hold the absolute pathname
			File path = new File(request);
			name = path.getAbsolutePath();
			System.out.println("Absolute Path: " + name);
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			if(!path.isDirectory() && path.exists()){
				os.write("HTTP/1.1 200 OK\n".getBytes());
				os.write("Date: ".getBytes());
				os.write((df.format(d)).getBytes());
				os.write("\n".getBytes());
				os.write("Server: Navarre's very own server\n".getBytes());
				// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
				// os.write("Content-Length: 438\n".getBytes());
				os.write("Connection: close\n".getBytes());
				os.write("Content-Type: ".getBytes());
				os.write(contentType.getBytes());
				os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			}else{
				os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
				os.write("Date: ".getBytes());
				os.write((df.format(d)).getBytes());
				os.write("\n".getBytes());
				os.write("Server: Navarre's very own server\n".getBytes());
				// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
				// os.write("Content-Length: 438\n".getBytes());
				os.write("Connection: close\n".getBytes());
				os.write("Content-Type: ".getBytes());
				os.write(contentType.getBytes());
				os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
			}//end if else
		}else{
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			os.write("HTTP/1.1 404 NOT FOUND\n".getBytes());
			os.write("Date: ".getBytes());
			os.write((df.format(d)).getBytes());
			os.write("\n".getBytes());
			os.write("Server: Navarre's very own server\n".getBytes());
			// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
			// os.write("Content-Length: 438\n".getBytes());
			os.write("Connection: close\n".getBytes());
			os.write("Content-Type: ".getBytes());
			os.write(contentType.getBytes());
			os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		}//end if else
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 *
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContentText(OutputStream os, String request) throws Exception
	{
		File name = new File(request);//create a file for the request
	 	if(!name.isDirectory() && name.exists()){//if file exits open it and print content, else print 404 NOT FOUND
			BufferedReader in = new BufferedReader(new FileReader(name));
			String line = "";//used to hold the tokenized text
			String text = "";//used to hold the content of each line of file
			Date d = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();

			while((line = in.readLine()) != null ){//read each line of the file until there is no more content
				text = text + "\n" + line;
			}//end while

			text = text.replaceAll("<cs371date>", df.format(d));
			text = text.replaceAll("<cs371server>", "Navarre's Server");

			os.write(text.getBytes());
			in.close();//close bufferedReader
		}else{
			String text = "404 NOT FOUND!";
			os.write(text.getBytes());
		}//end if else
	}//end  writeContentText

	private void writeContentImage(OutputStream os, String request) throws Exception
	{
		int offset = 0;
		int bytesRead = 0;
		File file = new File(request);
		long len = file.length();
		System.out.println("len: " + len);
		byte[] buff = new byte[(int)len + 1];
		FileInputStream name = new FileInputStream(file);//create a file for the request
	 	if(!file.isDirectory() && file.exists()){//if file exits open it and print content, else print 404 NOT FOUND
			bytesRead = name.read(buff, offset, buff.length - 1);
			os.write(buff, 0, bytesRead);
		}//end if
	}//end writeContentImage
} // end class
