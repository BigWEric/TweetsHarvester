import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.lightcouch.CouchDbClient;

import twitter4j.conf.ConfigurationBuilder;
import com.google.gson.Gson;

import twitter4j.*;

public class Harvestor {
	private static TwitterStream twitterStream;
	private static StatusListener listener;
	private static CouchDbClient dbClient1 = new CouchDbClient("couchdb.properties");
	private static File Tweets_Melbourne = new File("./Tweets_MelbourneNE.txt");
	private static FileWriter fw_tweets;
	private static BufferedWriter bw_tweets;
	private static long count = 0 ;
	private static final long LIMIT = 1000000;
	
	public static void main(String[] args) {
		
		try
		{
			fw_tweets = new FileWriter(Tweets_Melbourne.getAbsoluteFile());
			bw_tweets = new BufferedWriter(fw_tweets);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true);
	    cb.setOAuthConsumerKey("JuDAs23ssLkzgGv7HAzA08Rfd");
        cb.setOAuthConsumerSecret("R4xrvBWIzU1Mqd7BU13hX4AiHmzRqNQIJUhXNXZZYH661TIX6h");
        cb.setOAuthAccessToken("808402454-1JJqi9TDziPasFusqXRoiFcYnvBOAyzcuCZMkZHT");
        cb.setOAuthAccessTokenSecret("lbZjgJI7S44A1HZdYE2p13PoAdHyqDAkCFbZfG0EBsTNH");

	    twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

	    listener = new StatusListener() {

	        @Override
	        public void onException(Exception arg0) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onDeletionNotice(StatusDeletionNotice arg0) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	            public void onScrubGeo(long arg0, long arg1) {
	            // TODO Auto-generated method stub

	        }

	        @Override
	        public void onStatus(Status status) {
	        	
	        	try 
	        	{
					String fetch = (new Gson()).toJson(status);
	        		bw_tweets.append(fetch);
					dbClient1.save(status);
					bw_tweets.append("\n");
					count++;
					if(count>=LIMIT)
					{
						bw_tweets.close();
						fw_tweets.close();
						System.exit(0); 
					}
				} 
	        	catch (IOException e) 
	        	{
					e.printStackTrace();
				}
	        	
	            User user = status.getUser();
	            String profileLocation = user.getLocation();
	            System.out.println(profileLocation);
	        }

	        @Override
	        public void onTrackLimitationNotice(int arg0) {
	            // TODO Auto-generated method stub
	            System.out.println("onTrackLimitationNotice" +"\n");

	        }

	        @Override
	        public void onStallWarning(StallWarning arg0) {
	            // TODO Auto-generated method stub
	            System.out.println("onStallWarning" +"\n");
	        }

	    };
	    
	    FilterQuery fq = new FilterQuery();
	    double lat = -37.8136;
	    double longitude = 144.9631;
	    double lat1 = lat;
	    double longitude1 = longitude;
	    double lat2 = lat + 0.5;
	    double longitude2 = longitude + 0.5;
	    twitterStream.addListener(listener);
	    double[][] bb= {{longitude1, lat1}, {longitude2, lat2}};
	    
	    fq.locations(bb);

	    twitterStream.filter(fq);  

	}
}
