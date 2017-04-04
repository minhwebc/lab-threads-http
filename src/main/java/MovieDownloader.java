import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * A class for downloading movie data from the internet.
 * Code adapted from Google.
 *
 * YOUR TASK: Add comments explaining how this code works!
 * 
 * @author Joel Ross & Kyungmin Lee
 */
public class MovieDownloader {

	public static String[] downloadMovieData(String movie) {

		//construct the url for the omdbapi API
		String urlString = "";
		try { //try catch here to see if the users entering characters that cannot be UTF-8 encoded. If 
			  //they tried to do that, the method will just return null for the catch 
			urlString = "http://www.omdbapi.com/?s=" + URLEncoder.encode(movie, "UTF-8") + "&type=movie";
		}catch(UnsupportedEncodingException uee){
			return null;
		}

		HttpURLConnection urlConnection = null; //will be use to make an api request for the movies found
		BufferedReader reader = null; //will be use to read back the input stream from the http request

		String[] movies = null;

		try { //because we are dealing with a lot of stream reader and text reader, this whole block of code is put into a try/catch
		      //so that in the process of requesting or reading the input stream, there are any error occured, we want pur code to 
			  //continue executing safely without carshing the program 

			URL url = new URL(urlString);

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream(); //this is the response of the request
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				return null; //if no repsonse comming back from the sever then just return
			}
			reader = new BufferedReader(new InputStreamReader(inputStream)); 
			//pass in the input stream of the response into the bufferedreader to read it

			String line = reader.readLine(); //read first line of the response
			while (line != null) { //keep reading until there are none left
				buffer.append(line + "\n");
				line = reader.readLine();
			}

			if (buffer.length() == 0) {
				return null;
			}
			String results = buffer.toString();
			results = results.replace("{\"Search\":[","");
			results = results.replace("]}","");
			results = results.replace("},", "},\n");

			movies = results.split("\n");
		} 
		catch (IOException e) { //if error return
			return null;
		} 
		finally { //finishing off the remaining process by closing connection and close out reader
			if (urlConnection != null) { af
				urlConnection.disconnect();
			}
			if (reader != null) {
				try { //try catch here may mean that if the reader is still being use and can't be closed?
					reader.close();
				} 
				catch (IOException e) {
				}
			}
		}

		return movies;
	}


	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);

		boolean searching = true;

		while(searching) {					
			System.out.print("Enter a movie name to search for or type 'q' to quit: ");
			String searchTerm = sc.nextLine().trim();
			if(searchTerm.toLowerCase().equals("q")){
				searching = false;
			}
			else {
				String[] movies = downloadMovieData(searchTerm);
				for(String movie : movies) {
					System.out.println(movie);
				}
			}
		}
		sc.close();
	}
}
