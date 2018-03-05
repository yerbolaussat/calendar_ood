import java.io.Serializable;

/**
 * An Event with event information
 * 
 * @author qiaoliu
 * @version 1.0
 */
public class Event implements Serializable{

	private String date;
	private int startTime;
	private int endTime;
	private String content;

	/**
	 * Construct an event.
	 * 
	 * @param d
	 *            the date event scheduled in the format of YYYYMMDD
	 * @param s
	 *            the event start time in the format of 24 hours 0~23
	 * @param e
	 *            the event end time in the format of 24 hours 0~23
	 * @param c
	 *            the event content
	 */
	public Event(String d, String s, String e, String c) {
		date = d;
		startTime = convertToInt(s);
		endTime = convertToInt(e);
		content = c;
	}

	/**
	 * Get the date YYYYMMDD
	 * 
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Get the start time in the format of 24. 0~ 23
	 * 
	 * @return the start time
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * Get the end time.
	 * 
	 * @return the end time in the format of 24. 0~ 23
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * Get the content.
	 * 
	 * @return the event content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Converts a string into an integer for military time
	 * @param str time to be converted
	 * @return integer representation of time
	 * ex: "03:00am" -> 3; "03:00pm" -> 15
	 */
	private static int convertToInt(String str) {
		int time = 0;
		str = str.trim().replace("\\s++", "");

		if (str.equals("12:00am")) {
			str = "0:00am";
		}
		str = str.replace("am", "");
		if (str.contains("pm")) {
			time += 12;
			str = str.replace("pm", "");
		}
		String[] s = str.split(":");
		time += Integer.parseInt(s[0]);
		// if (s[1].equals("30")) time++;
		if (time == 24) {
			time = 12;
		}
		return time;
	}
}
