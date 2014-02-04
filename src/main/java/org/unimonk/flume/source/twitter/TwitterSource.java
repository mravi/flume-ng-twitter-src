/* 
 * Author : Magham Ravi Kiran.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
* may not use this file except in compliance with the License. You may
* obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0 
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
package org.unimonk.flume.source.twitter;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;

public class TwitterSource extends AbstractSource
                implements EventDrivenSource, Configurable{
    
    private static final Logger logger = 
            LoggerFactory.getLogger(TwitterSource.class); 
    
    private static final String NAME   = "Twitter Source__";
    private static AtomicInteger counter = new AtomicInteger();
    
    private TwitterStream twitterStream;
    private SourceCounter sourceCounter;
    
    /** Information necessary for accessing the Twitter API */
    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;
    private int batchSize = 1000;
    
    private String[] keywords = new String[1];

    @Override
    public void configure(Context context) {
        this.setName(NAME + counter.incrementAndGet());
        consumerKey = context.getString(ConfigConstants.CONSUMER_KEY_KEY);
        consumerSecret = context.getString(ConfigConstants.CONSUMER_SECRET_KEY);
        accessToken = context.getString(ConfigConstants.ACCESS_TOKEN_KEY);
        accessTokenSecret = context.getString(ConfigConstants.ACCESS_TOKEN_SECRET_KEY);
        String keywordString = context.getString(ConfigConstants.KEYWORDS_KEY, "");
        if(!Strings.isNullOrEmpty(keywordString)) {
            keywords[0] = keywordString;
        }
        logger.info("keywordString: '" + keywordString + "'");
        
        logger.info("Consumer Key:  {}", consumerKey);
        logger.info("Consumer Secret: {}" ,consumerSecret);
        logger.info("Access Token: {}", accessToken);
        logger.info("Access Token Secret:  {}" , accessTokenSecret);
        
        batchSize = context.getInteger(ConfigConstants.BATCH_SIZE_KEY, batchSize);
        this.sourceCounter = new SourceCounter(this.getName());
    }
    
    @Override
    public void start() {
  
      final ChannelProcessor channel = getChannelProcessor();
      
      StatusListener listener = new StatusListener() {
        @Override
        public void onStatus(Status status) {
       
          Tweet tweet = new Tweet ();
          tweet.setId(status.getId());
          tweet.setUserId(status.getUser().getId());
          tweet.setLatitude(status.getGeoLocation().getLatitude());
          tweet.setLongitude(status.getGeoLocation().getLongitude());
          tweet.setText(status.getText());
          tweet.setCreatedAt(new Timestamp(status.getCreatedAt().getTime()));
          
          Event event = EventBuilder.withBody(tweet.toString(), Charsets.UTF_8);
          sourceCounter.incrementAppendReceivedCount();
          try{
              channel.processEvent(event);
              sourceCounter.incrementEventAcceptedCount();
          } catch (ChannelException ex) {
              logger.error("In Twitter source {} : Unable to process event due to exception {}.",getName() ,ex);
             
            }
        }
        
        // This listener will ignore everything except for new tweets
        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        @Override
        public void onScrubGeo(long userId, long upToStatusId) {}
        @Override
        public void onException(Exception ex) {}
        @Override
        public void onStallWarning(StallWarning arg0) {
        }
      };
      
      logger.debug("Setting up Twitter sample stream using consumer key {} and" +
            " access token {}", new String[] { consumerKey, accessToken });
   
      twitterStream = new TwitterStreamFactory().getInstance();
      twitterStream.addListener(listener);
      twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
      AccessToken token = new AccessToken(accessToken, accessTokenSecret);
      twitterStream.setOAuthAccessToken(token);
      
      if (keywords.length == 0) {
        logger.debug("Starting up Twitter sampling...");
        twitterStream.sample();
      } else {
        logger.debug("Starting up Twitter filtering..."); 
        FilterQuery query = new FilterQuery()
          .track(keywords); 
        twitterStream.filter(query);
      }
      this.sourceCounter.start();
      super.start();
    }
    
    /**
     * Stops the Twitter stream.
     */
    @Override
    public void stop() {
      logger.debug("Shutting down Twitter stream...");
      twitterStream.shutdown();
      this.sourceCounter.stop();
      super.stop();
    }
    
    
}
