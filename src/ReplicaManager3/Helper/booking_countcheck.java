package ReplicaManager3.Helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class booking_countcheck {
	public static int check_appointment_counts(String code,String a) {
		
		if(code == "MTL") {
			Pattern pattern1 = Pattern.compile("SHE");
			Pattern pattern2 = Pattern.compile("QUE");
	        Matcher matcher = pattern1.matcher(a);
	        Matcher matcher1 = pattern2.matcher(a);
	        int count = 0;
	        int temp= 0 ; 
	        while (matcher.find() || matcher1.find())
	        	count++;
	            temp = temp + count;
	       return temp;
		}
		else if(code == "QUE") {
			Pattern pattern1 = Pattern.compile("MTL");
			Pattern pattern2 = Pattern.compile("SHE");
	        Matcher matcher = pattern1.matcher(a);
	        Matcher matcher1 = pattern2.matcher(a);
	        int count = 0;
	        int temp= 0 ; 
	        while (matcher.find() || matcher1.find())
	        	count++;
	            temp = temp + count;
			
	       return temp;
		}
		
		else if(code == "SHE") {
			Pattern pattern1 = Pattern.compile("MTL");
			Pattern pattern2 = Pattern.compile("QUE");
	        Matcher matcher = pattern1.matcher(a);
	        Matcher matcher1 = pattern2.matcher(a);
	        int count = 0;
	        int temp= 0 ; 
	        while (matcher.find() || matcher1.find())
	        	count++;
	            temp = temp + count;
	        
	        return temp;
			
		}
		else {
			return 5;
		}
	}
}
