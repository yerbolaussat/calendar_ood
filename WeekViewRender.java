import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Displays Week View and events when Week button is clicked
 * @author Team Numero Uno
 *
 */
public class WeekViewRender implements ViewRender {


	@Override
	public DefaultTableModel getTableModel(EventModel eventsData) {

		DefaultTableModel tableModel = new DefaultTableModel(null, new String[]{"Time"});
		tableModel.setRowCount(24);
		tableModel.setColumnCount(1);

		Calendar cal = (Calendar) eventsData.getCalendar().clone();
		int offset = cal.get(Calendar.DAY_OF_WEEK);

		cal.add(Calendar.DAY_OF_MONTH, (-1) * offset);

		String[] keys = new String[24];
		for (int j = 0; j < 24; j++) {
			String time = (j <= 12) ? j + ":00am" : (j - 12) + ":00pm";
			if (j == 0) {
				time = "Mignight";
			} else if (j == 12) {
				time = "Noon";
			}
			tableModel.setValueAt(time, j, 0);
			keys[j] = j + "-" + (j + 1);
		}

		for (int i = 0; i < 7; i++) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			DayEventMap dayEventMap = eventsData.getDailyEventMap(cal);
			String[] value = new String[24];
			if (dayEventMap != null) {
				Map<String, Event> dayEvents = dayEventMap.getEvents();
				for (int j = 0; j < 24; j++) {
					if (dayEvents.containsKey(keys[j])) {
						value[j] = dayEvents.get(keys[j]).getContent();
					} else {
						value[j] = "";
					}
				}
			}
			String title = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US).substring(0, 3) + " "
					+ (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE);
			tableModel.addColumn(title, value);
		}
		return tableModel;
	}
	

	@Override
	public void setTableFormate(JTable eventsTable){
		eventsTable.setRowHeight(20);
    }
}
