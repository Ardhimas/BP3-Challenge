//BP3 Challenge Completed by Ardhimas Kamdani

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.json.simple.*;
import org.json.simple.parser.*;

public class bp3challenge{

	public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(new FileReader("task.json"));

			JSONArray arr = (JSONArray)obj;

			//HashMap containing dates and open and close count for that date
			HashMap<Date,int[]> dateMap = new HashMap<Date,int[]>();
			//Put open and close counts into dateMap
			for (int i = 0; i < arr.size(); i++) {
				Object rawDate = ((JSONObject)arr.get(i)).get("createDate").toString();
				String strDate = rawDate.toString();
				Date date = df.parse(strDate);

				//If key is new, add it
				if(!dateMap.containsKey(date)){
					dateMap.put(date, getOpenCloseCount(arr,date));
				}else{
					//otherwise, combine values
					int[] oldCount = dateMap.get(date);
					int[] newCount = getOpenCloseCount(arr,date);
					int[] combineCount = new int[2];
					combineCount[0] = oldCount[0] + newCount[0];
					combineCount[1] = oldCount[1] + newCount[1];
					dateMap.put(date, combineCount);
				}
			}

			//Given date, get current open and closed tasks count
			String testDate = "2015-02-14T18:24:15Z";
			Date test = df.parse(testDate);
			int[] currCount = getOpenCloseDate(dateMap, test);
			System.out.println("On the day of " + test + " there are " + currCount[0] + " open tasks and " + currCount[1]
				 + " closed tasks.");

			//Given start and end date, return how many tasks were opened and closed;
			String strBeginDate = "2014-11-09T21:39:19Z";
			Date begin = df.parse(strBeginDate);
			String strEndDate = "2015-02-24T18:39:13Z";
			Date end = df.parse(strEndDate);
			int[] rangeCount = getOpenCloseRange(dateMap,begin,end);
			System.out.println("Between " + begin + " and " + end + " there were " + rangeCount[0] + 
				" tasks opened and " + rangeCount[1] + " tasks closed.");

			String instanceId = "680";
			//Given instance ID, return name of most recent task
			System.out.println("Name of most recent task of instanceId " + instanceId + ": " + mostRecent(arr,instanceId));
			//Given instance ID, return task count
			System.out.println("Task count for instanceId " + instanceId + ": " + taskCount(arr,instanceId));

		} catch (Exception e) {
			System.out.println("X");
			e.printStackTrace();
		}
	}			

	public static int[] getOpenCloseCount(JSONArray arr, Date date) throws Exception{
		Date beginTime = new Date(Long.MIN_VALUE);
		//Int array where first value is open tasks, and second is closed tasks
		int[] tasks = new int[2];
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		int openCount = 0;
		int countCount = 0;
			
		for (int i = 0; i < arr.size(); i++) {
			Object openDate = ((JSONObject)arr.get(i)).get("createDate");
			if(openDate != null){
				String openDateString = openDate.toString();
				Date dfoDate = df.parse(openDateString);
				tasks[0]++;
			}
			Object closeDate = ((JSONObject)arr.get(i)).get("closeDate");
			if(closeDate != null){
				String closeDateString = closeDate.toString();
				Date dfcDate = df.parse(closeDateString);
				tasks[1]++;
			}
		}
		
		return tasks;
	}

	public static int[] getOpenCloseRange(HashMap<Date,int[]> dateMap, Date begin, Date end){
		int[] count = new int[2];
		for (Map.Entry<Date,int[]> entry : dateMap.entrySet()) {
			if(entry.getKey().compareTo(begin) >= 0 && entry.getKey().compareTo(end) < 0){
				count[0] += entry.getValue()[0];
				count[1] += entry.getValue()[1];
			}
		}
		return count;
	}

	public static int[] getOpenCloseDate(HashMap<Date,int[]> dateMap, Date date){
		Date initDate = new Date(Long.MIN_VALUE);
		int[] count = getOpenCloseRange(dateMap, initDate, date);
		//Modify opened numbers to current number
		count[0] -= count[1];
		return count;
	}

	public static int taskCount(JSONArray arr, String instanceId){
		int count = 0;
		for (int i = 0; i < arr.size(); i++) {
			String id = ((JSONObject)arr.get(i)).get("instanceId").toString();
			if(instanceId.equals(id)){
				count++;
			}
		}
		return count;
	}

	public static String mostRecent(JSONArray arr, String instanceId) throws Exception{
		String mostRecentId = "";
		String name = "";
		Date date = new Date(Long.MIN_VALUE);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		for (int i = 0; i < arr.size(); i++) {
			String id = ((JSONObject)arr.get(i)).get("instanceId").toString();
			if(instanceId.equals(id)){
				Object openDate = ((JSONObject)arr.get(i)).get("createDate");
				String openDateString = openDate.toString();
				Date dfDate = df.parse(openDateString);
				if(dfDate.compareTo(date) >= 1){
					date = dfDate;
					Object objName = ((JSONObject)arr.get(i)).get("name");
					name = objName.toString();
				}
			}
		}
		return name;
	}
}
