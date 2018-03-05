import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A daily event list
 * 
 * @author qiaoliu
 * @version 1.0
 *
 */
public class DayEventMap implements Serializable {

	private Map<String, Event> events;

	/**
	 * Construct DayEventList.
	 */
	public DayEventMap() {
		events = new HashMap<>();
	}

	/**
	 * Add Event to the DayEventList.
	 * 
	 * @param e
	 *            the event
	 * @throws Exception
	 *             the exception if and only if there is conflict event.
	 */
	public void addEvent(Event aEvent) throws Exception {
		int startTime = aEvent.getStartTime();
		int endTime = aEvent.getEndTime();
		if (isConflict(startTime, endTime)) {
			throw new IllegalArgumentException("There is schedule during " + startTime + ":00 and "
					+ endTime + ":00." + "\n" + "Please reschedule.");
		}
		while (startTime < endTime) {
			int tmp = startTime + 1;
			events.put(startTime + "-" + tmp, aEvent);
			startTime++;
		}
	}

	/**
	 * Get the events of the day.
	 * 
	 * @return the event of the day
	 */
	public Map<String, Event> getEvents() {
		return events;
	}

	/**
	 * Checks if there is an event conflict.
	 * @param startTime
	 * @param endTime
	 * @return true if conflict appears
	 */
	public boolean isConflict(int startTime, int endTime) {

		while (startTime < endTime) {
			int tmp = startTime + 1;
			if (events.containsKey(startTime + "-" + tmp))
				return true;
			startTime++;
		}
		return false;
	}

}
