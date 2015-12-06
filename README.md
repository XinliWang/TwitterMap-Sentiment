# TwitterMap-Sentiment
●	Use the Amazon SQS service to create a processing queue for the Tweets that are delivered by the Twitter Streaming API.           
●	Use Amazon SNS service to update the status processing on each tweet so the UI can refresh.           
●	Integrate a third party cloud service API into the Tweet processing flow.              
             
For this project I develop an application that:           
●	Reads a stream of tweets from the Twitter Live API (Code provided). Note: you might follow a specific topic on the API or get the complete stream              
●	Records the tweet ID, time, and other relevant elements into a DB (SQL)               
●	After the tweet is recorded in the DB send a message to the Queue for Asynchronous processing on the text of the tweet           
●	Presents the Tweet in a map that is being updated in Near Real Time (Consider evaluating WebSockets, or Server Side Events for your implementation)              
●	The map clusters tweets as to show where is people tweeting the most, according to the sample tweets you get from the streaming API.   
●	Define a worker pool that will pick up messages from the queue to process. These workers should each run on a separate pool thread.    
●	Make a call to the sentiment API off your preference (e.g. Alchemy). This can return a positive or negative sentiment evaluation for the text of the submitted Tweet.             
●	As soon as the tweet is processed send a notification -using SNS- to an HTTP endpoint that will update the UI with the new information about the Tweet.             
●	Using this information your application should display the Tweet clusters and the overall sentiment.  

![architecture](https://cloud.githubusercontent.com/assets/10342877/11604772/d893224a-9abe-11e5-94e5-83a6881cac21.png)  

###Function Description:         
1.For this application, I need to use URL to call the TwitterFetchServlet to start to crawl the tweets.                      
2.When crawl the tweets and insert them into Amazon RDS(SQL), we send this tweets into SQS.  Then we will use a thread pool to process data by calling Alchemy API to acquire sentiment and insert it into database.                      
3.After that, push it to Amazon SNS so that Web Server could use it for visualization.The server subscribed to a SNS topic as an endpoint so as to get real-time publishment fron the topic, and push those content to the client ends.The servlet based on the message type it received from the SNS topic to extract the real messages, which are of the type "Notification". And it stacks those messages to be sent.         
4.At the client end, we use AJAX to poll the message every 10 seconds, and clan that stack at the servlet. The message is in JSON format, which includes location information and analyzed sentiment information for each tweet.             
5.In mainpage.jsp, the data already in DB will be fetched out and painted on Google Heatmap. We will use ajax call per 1 minute so that new points would be added to the map. And sentiment data would be processed and visualized use Chart.js.                  
6.There are three forms of the data visualization--the radar, the pie, and the line chart, where the former two show the ratio of each range of sentiment(extremely negative, negative, netral, positive, extremely positive); while the last one dynamically shows the trend of the overall sentiment which is the current average of the sentiments scores received.            
7.For the AWS service of SQS, we create it manually and get its URL in our project.  For the SNS, we create is first, then it will show PendingConfirmation status.  I run SNSchannel.java using the below code to confirm the pending status and start to use it.               

```
public static void main(String args[]){
		for(int i=0; i<100; i++){
			float lat = 30+(float) Math.random()*10;
			float lng = -100-(float) Math.random()*30;
			float sentiment = (float) Math.random()*2-1;
			String tosend = "{\"lat\":\""+lat+"\",\"lng\":\""+lng+"\",\"sentiment\":\""+sentiment+"\"}";
			sendMsg(newSNSClient(),topicArn,tosend);
		}
	}

```

![twitter map](https://cloud.githubusercontent.com/assets/10342877/11604753/832321a2-9abe-11e5-8583-0dfbe40e1c1c.png)
![twitter sentiment and trend](https://cloud.githubusercontent.com/assets/10342877/11604758/91cd54de-9abe-11e5-83cb-a8e26e0feeb8.png)


