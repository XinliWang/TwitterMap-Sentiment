package appserver;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;

public class SNSchannel {
	public static String public_ip="http://";
	public static String port = ":8080";
	public static String topicName = "sentiment";
// 	public static String endpoint = public_ip + port + "/SNS_servlet";
	public static String endpoint = "";
	public static String topicArn = "";
	
	public static String createTopic(AmazonSNSClient snsClient, String topicName){
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topicName);
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		String Arn = createTopicResult.toString();
		String topicArn = Arn.split("TopicArn: ")[1];
		topicArn = topicArn.split("}")[0];
		System.out.println("Topic Arn: "+topicArn);
		//get request id for CreateTopicRequest from SNS metadata		
		System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
		return topicArn;
	}
	public static void subscribeEndpoint(AmazonSNSClient snsClient, String topicArn, String endpoint){
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "http", endpoint);
		SubscribeResult subscribereseult= snsClient.subscribe(subRequest);
		System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("subscribereseult - " + subscribereseult);
	}
	public static void sendMsg(AmazonSNSClient snsClient, String topicArn, String msg){
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		//print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}
	public static void deleteTopic(AmazonSNSClient snsClient, String topicArn){
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		//get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - " + snsClient.getCachedResponseMetadata(deleteTopicRequest));
		System.out.println("Successfully deleted topic!");
	}
	public static AmazonSNSClient newSNSClient(){
		AWSCredentials credentials = new ProfileCredentialsProvider("default").getCredentials();
		AmazonSNSClient snsClient = new AmazonSNSClient(credentials);		                           
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		return snsClient;
	}

}
