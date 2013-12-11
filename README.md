flume-ng-twitter-src
====================

This Flume-NG plugin primariy streams from Twitter and transforms each tweet into a tab separated(\t) Flume Event for further downstream consumption. 

Requirements:
 1) Flume 1.4 +

Configuration:


        agent.sources = twitter-source
        agent.channels = memoryChannel
 
        //configuration for channel
        agent.channels.memoryChannel.type=memory
        agent.channels.memoryChannel.transactionCapacity=100
        agent.channels.memoryChannel.byteCapacityBufferPercentage=20

        //configuration for source
        agent.sources.twitter-source.type = org.unimonk.flume.source.twitter.TwitterSource
		agent.sources.twitter-source.channels = memoryChannel
		agent.sources.twitter-source.consumerKey={consumer key}
		agent.sources.twitter-source.consumerSecret={consumer secrect>
		agent.sources.twitter-source.accessToken={access token}
		agent.sources.twitter-source.accessTokenSecret={access token secret}
		agent.sources.twitter-source.keywords=hadoop, big data, analytics
		agent.sources.twitter-source.interceptors = i1
		agent.sources.twitter-source.interceptors.i1.type = host
		agent.sources.twitter-source.interceptors.i1.hostHeader = fhost


		
You will need to provide your own authentication details for accessing the twitter streaming API.
 For more information, see twitter's streaming-apis and obtaining access tokens documentation.