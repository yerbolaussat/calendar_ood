import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An EventModel saving all scheduled Event objects.
 * 
 * @author qiaoliu
 *
 */
public class EventModel {
	private Map<String, DayEventMap> map;
	private Calendar cal;
	private ArrayList<ChangeListener> l;

	/**
	 * Construct EventModel saving all Events.
	 * @param aCal is a calendar for initialization
	 */
	public EventModel(Calendar aCal) {
		try {
			readFile();
		} catch (Exception e) {
			map = new HashMap<>();
		}
		cal = aCal;
		l = new ArrayList<>();

		//System.out.println("Month " + aCal.get(Calendar.YEAR));
	}

	/**
	 * Get calendar
	 * @return returns calendar
	 */
	public Calendar getCalendar() {
		return cal;
	}

	/**
	 * Get first day of the week
	 * @return the first day of the week
	 */
	public int getFirstDayOfWeek() {
		Calendar tmp = (Calendar) cal.clone();
		tmp.set(Calendar.DAY_OF_MONTH, 1);
		return tmp.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Get number of days in the month
	 * @return the number of days in month
	 */
	public int getNumberOfDays() {
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get number of weeks in the month
	 * @return the number of weeks in the month
	 */
	public int getNumberOfWeeks() {
		return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
	}
	
	/**
	 * Gets the year.
	 * @return
	 */
	public int getYearNumber() {
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Get number of specific month
	 * @return month number
	 */
	public int getMonthNumber() {
		return cal.get(Calendar.MONTH) + 1;
	}
	
	/**
	 * Get day number out of the year
	 * @return day number
	 */
	public int getDayNumber() {
		return cal.get(Calendar.DATE);
	}
	
	/**
	 * Get the day of the week
	 * @return the day of the week
	 */
	public String getDayOfWeek() {
		return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
	}
	


	/**
	 * Get the DayEventMap.
	 * 
	 * @param date
	 *            the date
	 * @return the DayEventList
	 */
	public DayEventMap getDailyEventMap() {
		String date = "" + getYearNumber() + parseToTwoInteger(getMonthNumber()) + parseToTwoInteger(getDayNumber());
		if (map.containsKey(date))
			return map.get(date);
		return new DayEventMap();
	}
	
	/**
	 * Get the DayEventMapp
	 * @param cal2 the date
	 * @return the DayEventList
	 */
	public DayEventMap getDailyEventMap(Calendar cal2) {
		String date = "" + cal2.get(Calendar.YEAR) + parseToTwoInteger((cal2.get(Calendar.MONTH) + 1)) + parseToTwoInteger((cal2.get(Calendar.DATE)));
		if (map.containsKey(date))
			return map.get(date);
		return new DayEventMap();
	}
	

	/**
	 * Add Event to the model.
	 * 
	 * @param date
	 *            the date
	 * @param e
	 *            the event
	 * @throws Exception
	 *             the exception while there is conflict.
	 */
	public void addEvent(Event e) throws Exception {
		String date = e.getDate();
		DayEventMap dayEvents;
		if (!map.containsKey(date)) {
			dayEvents = new DayEventMap();
			map.put(date, dayEvents);
		}
		dayEvents = map.get(date);
		dayEvents.addEvent(e);
		
		if(date.equals("" + getYearNumber() + parseToTwoInteger(getMonthNumber()) + parseToTwoInteger(getDayNumber()))) {
			for (ChangeListener cl : l) {
				cl.stateChanged(new ChangeEvent(this));
			}
		}
	}
	
	/**
	 * Converts integer of time into a string
	 * @param input integer of time
	 * @return string of integer representing time
	 */
	private String parseToTwoInteger(int input) {
		return input < 10 ? "0" + input : "" + input; 
	}
	

	/**
	 * Updates calendar
	 * @param aCal the date
	 */
	public void updateDate(Calendar aCal) {
		cal = aCal;
		for (ChangeListener cl : l) {
			cl.stateChanged(new ChangeEvent(this));
		}

	}

	/**
	 * Updates the view
	 * @param aL
	 */
	public void attach(ChangeListener aL) {
		l.add(aL);
	}

	/**
	 * Write events to file.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void writeFile() throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("event.txt"));
		out.writeObject(map);
		out.close();
	}

	/**
	 * Read events from file.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readFile() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("events.data"));
		map = (HashMap<String, DayEventMap>) in.readObject();
		in.close();
	}

	/**
	 * Checks if there is a time conflict
	 * @param days the date
	 * @param startTime the start time
	 * @param endTime the end time
	 * @return true if there is a conflict
	 */
	public boolean isConflict(ArrayList<String> days, int startTime, int endTime) {
		for (int i = 0; i < days.size(); i++) {
			if (map.containsKey(days.get(i))) {
				DayEventMap tmp = map.get(days.get(i));
				if (tmp.isConflict(startTime, endTime)) return true;
			}
		} 
		return false;
	}

	/**
	 * Gets all events
	 * @return all events
	 */
	public Object getAllEvents() {
		return map;
	}
	
}
