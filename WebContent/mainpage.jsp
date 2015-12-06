<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="js/Chart.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script> 
<script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
<script src="//code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<style>
	    html, body, #map-canvas {
	      height: 100%;
	      margin: 0px;
	      padding: 0px;
				font-family: 'Raleway', sans-serif;
				z-index:1;
	    }
  	
</style>
<script>
var map, pointarray, heatmap;
var datapoints = [];
var ctx, myPie, myRadar, lnctx, myLine;
var ext_neg=0, neg=0, mid=0, pos=0, ext_pos=0, count=0, total=0;
/*Java code to fetch data from DB*/
<% 
	java.sql.Connection con;
	java.sql.Statement s;
	java.sql.ResultSet rs;
	java.sql.PreparedStatement pst;

	con=null;
	s=null;
	pst=null;
	rs=null;

	String url= "";
	/* "jdbc:mysql://DB_host:port/DB_name"+"user=DB_user_name&password=DB_PW"; */
	try{

		Class.forName("com.mysql.jdbc.Driver");
		con = java.sql.DriverManager.getConnection(url);

		}catch(ClassNotFoundException cnfex){
			cnfex.printStackTrace();

		}
		String sql = "select i.lati, i.longi, t.sent from info i"
		    +" left join twit_sent t on t.twid = i.twid ";
		try{
		s = con.createStatement();
		rs = s.executeQuery(sql);

		while( rs.next() ){		 
		%>
		  var lat = <%=rs.getString("lati")%>;
		  var lng = <%=rs.getString("longi")%>;
		  var sent = <%=rs.getString("sent")%>;
		  sent = Number(sent);
		  var latlng = new google.maps.LatLng(lat,lng);
		  datapoints.push(latlng);
		  count++; total+=sent;
		  if (sent<-0.5){
			  ext_neg++;
		  }
		  else if(sent<0){
			  neg++;
		  }
		  else if(sent==0){
			  mid++;
		  }
		  else if(sent<0.5){
			  pos++;
		  }
		  else{
			  ext_pos++;
		  }
		<%		
		}
	}catch(Exception e)
		{e.printStackTrace();}
	finally{
		if(rs!=null) rs.close();
		if(s!=null) s.close();
		if(con!=null) con.close();
	}
%>

var pieData = [
				{
					value: ext_neg,
					color:"#282828",
					highlight: "#3d434d",
					label: "Extremely Negative"
				},
				{
					value: neg,
					color: "#686868",
					highlight: "#3d5578",
					label: "Negative"
				},
				{
					value: mid,
					color: "#1d65cd",
					highlight: "#7598ca",
					label: "Neutral"
				},
				{
					value: pos,
					color: "#A80000",
					highlight: "#85bff0",
					label: "Positive"
				},
				{
					value: ext_pos,
					color: "#E00000",
					highlight: "#90d3fa",
					label: "Extremely Positive"
				}

			];

var radarChartData = {
		labels: ["Extremely Negative", "Negative", "Neutral", "Positive", "Extremely Positvie"],
		datasets: [
			{
				label: "My dataset",
				fillColor: "rgba(151,187,205,0.2)",
				strokeColor: "rgba(151,187,205,1)",
				pointColor: "rgba(151,187,205,1)",
				pointStrokeColor: "#fff",
				pointHighlightFill: "#fff",
				pointHighlightStroke: "rgba(151,187,205,1)",
				data: [ext_neg,neg,mid,pos,ext_pos]
			}
		]
	}; 

var lineChartData = {
		labels : [""],
		datasets : [
			{
				label: "My dataset",
				fillColor : "rgba(151,187,205,0.2)",
				strokeColor : "rgba(151,187,205,1)",
				pointColor : "rgba(151,187,205,1)",
				pointStrokeColor : "#fff",
				pointHighlightFill : "#fff",
				pointHighlightStroke : "rgba(151,187,205,1)",
				data : [total/count]
			}
		]
	}

	/*function polling1() {
		timer = setInterval(function() {
			<% 
			try{

				Class.forName("com.mysql.jdbc.Driver");
				con = java.sql.DriverManager.getConnection(url);

				}catch(ClassNotFoundException cnfex){
					cnfex.printStackTrace();

				}
				String sql1 = "select i.lati, i.longi, t.sent from info i"
				    +" left join twit_sent t on t.twid = i.twid ";
				try{
				s = con.createStatement();
				rs = s.executeQuery(sql1);
				%>
				var ext_neg=0, neg=0, mid=0, pos=0, ext_pos=0, count=0, total=0;
				<%
				while( rs.next() ){		 
				%>
				  
				  var lat = <%=rs.getString("lati")%>;
				  var lng = <%=rs.getString("longi")%>;
				  var sent = <%=rs.getString("sent")%>;
				  sent = Number(sent);
				  var latlng = new google.maps.LatLng(lat,lng);
				  datapoints.push(latlng);
				  count++; total+=sent;
				  if (sent<-0.5){
					  ext_neg++;
				  }
				  else if(sent<0){
					  neg++;
				  }
				  else if(sent==0){
					  mid++;
				  }
				  else if(sent<0.5){
					  pos++;
				  }
				  else{
					  ext_pos++;
				  }
				<%		
				}
			}catch(Exception e)
				{e.printStackTrace();}
			finally{
				if(rs!=null) rs.close();
				if(s!=null) s.close();
				if(con!=null) con.close();
			}
		%>	
		myPie.segments[0].value = ext_neg;
		myPie.segments[1].value = neg;
		myPie.segments[2].value = mid;
		myPie.segments[3].value = pos;
		myPie.segments[4].value = ext_pos;
		myPie.update();
		myLine.addData([total/count], "");
		if (total>1000){
			total = 0;
			count = 0;
		}
		//initializeMap();
		}, 30000);
		
	}*/
	
	
function polling() {
    setTimeout(function () {
        $.ajax({
            type: 'GET',
            dataType: 'json',
            url: 'SNS_servlet',
            success: function (data) {
                if(data.lat!=""&&data.lng!=""){
                	var latlng = new google.maps.LatLng(data.lat,data.lng);
                	pointArray.push(latlng);
                }
                if(data.sentiment!=""){
                	var sentiment = Number(data.sentiment);
                	count++; total+=sentiment;
                	if (sentiment<-0.5){
          			  	ext_neg++;
          		  	}
          		  	else if(sentiment<0){
          			  	neg++;
          		  	}
          		  	else if(sentiment==0){
          			  	mid++;
          		  	}
          		  	else if(sentiment<0.5){
          			  	pos++;
          		  	}
          		  	else{
          			  	ext_pos++;
          		  	}
                	myPie.segments[0].value = ext_neg;
            		myPie.segments[1].value = neg;
            		myPie.segments[2].value = mid;
            		myPie.segments[3].value = pos;
            		myPie.segments[4].value = ext_pos;
            		myPie.update();
            		myRadar.datasets[0].points[0].value = ext_neg;
            		myRadar.datasets[0].points[1].value = neg;
            		myRadar.datasets[0].points[2].value = mid;
            		myRadar.datasets[0].points[3].value = pos;
            		myRadar.datasets[0].points[4].value = ext_pos;
            		myRadar.update(); 
            		myLine.addData([total/count], "");
            		if (total>1000){
            			total = 0;
            			count = 0;
            		}
                }
            },
            complete: polling
        });
    }, 1000);
}

function initializeMap() {
	  var mapOptions = {
	    zoom: 2,
	    center: new google.maps.LatLng(46, 0),
	    mapTypeId: google.maps.MapTypeId.ROADMAP,
	    noClear:false
	  };

	  map = new google.maps.Map(document.getElementById('map-canvas'),
	      mapOptions);

	  pointArray = new google.maps.MVCArray(datapoints);

	  heatmap = new google.maps.visualization.HeatmapLayer({
	    data: pointArray
	  });

	  heatmap.setMap(map);
}

$( document ).ready(function() {
	initializeMap();
	ctx = document.getElementById("chart-area").getContext("2d");
	myPie = new Chart(ctx).Pie(pieData);
	/* myRadar = new Chart(document.getElementById("radar").getContext("2d")).Radar(radarChartData, {
		responsive: true
	}); */
	lnctx = document.getElementById("lineChart").getContext("2d");
	myLine = new Chart(lnctx).Line(lineChartData, {
		responsive: true
	});
	polling1();
});
</script>
</head>
<body>
<div id="map-canvas" style="margin-top:-50px;"></div>
<div style="float:left">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
</div>
<!-- <div style="float:left;">
	<canvas id="radar" height="320" width="320"/>
</div> -->
<div style="float:left">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
</div>
<div style="float:left;">
	<h2>Sentiment Analysis</h2>
	<canvas id="chart-area" width="260" height="260"/>
</div>
<div style="float:left">
<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>
</div>
<div style="float:left;">
	<canvas id="lineChart" height="250" width="740"></canvas>
</div>
</body>
</html>