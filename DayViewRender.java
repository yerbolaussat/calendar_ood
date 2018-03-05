import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Displays Day view after Day button is clicked
 * @author Team Numero Uno
 *
 */
public class DayViewRender implements ViewRender {

	@Override
	public DefaultTableModel getTableModel(EventModel eventsData) {

		String[] title = new String[2];
		title[0] = eventsData.getCalendar().getTimeZone().getDisplayName();
		title[1] = eventsData.getDayOfWeek() + " " + eventsData.getMonthNumber() + "/" + eventsData.getDayNumber();

		DefaultTableModel tableModel = new DefaultTableModel(null, title);
		tableModel.setRowCount(0);
		tableModel.setRowCount(24);

		DayEventMap dayEventMap = eventsData.getDailyEventMap();

		if (dayEventMap != null) {
			Map<String, Event> dayEvents = dayEventMap.getEvents();

			for (int j = 0; j < 24; j++) {
				String time = (j <= 12) ? j + ":00am" : (j - 12) + ":00pm";
				if (j == 0) {
					time = "Mignight";
				} else if (j == 12) {
					time = "Noon";
				}

				tableModel.setValueAt(time, j, 0);
				String key = j + "-" + (j + 1);
				if (dayEvents.containsKey(key)) {
					tableModel.setValueAt(dayEvents.get(key).getContent(), j, 1);
				} else {
					tableModel.setValueAt("", j, 1);
				}
			}
		}
		return tableModel;
	}

	@Override
	public void setTableFormate(JTable eventsTable){
    	eventsTable.getColumnModel().getColumn(0).sizeWidthToFit();
    	eventsTable.getColumnModel().getColumn(1).setPreferredWidth(400);
    	eventsTable.setRowHeight(20);
    }

}
