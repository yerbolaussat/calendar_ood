import java.util.Calendar;
import java.util.Map;
import java.util.Set;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.*;


/**
 * Displays Month View and events when Month button is clicked
 * @author Team Numero Uno
 *
 */
public class MonthViewRender implements ViewRender {

	String[] arrayOfMonths = new String[] {"Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

	@Override
	public DefaultTableModel getTableModel(EventModel eventsData) {
		String[] columns = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		
		DefaultTableModel model = new DefaultTableModel(null, columns) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		int rows = eventsData.getNumberOfWeeks();
		model.setRowCount(eventsData.getNumberOfWeeks());
		
		
		Calendar cal = (Calendar) eventsData.getCalendar().clone();
		int dayOffset = cal.get(Calendar.DAY_OF_MONTH) - 1;
		cal.add(Calendar.DAY_OF_MONTH, (-1) * dayOffset);
		int weekOffset = cal.get(Calendar.DAY_OF_WEEK) - 1;
		cal.add(Calendar.DAY_OF_MONTH, (-1)*weekOffset);

		for (int i = 0; i < rows * 7; i++) {
			int row = i / 7;
			int col = i % 7;
			String value = "<html>"; 

			if (cal.get(Calendar.DAY_OF_MONTH) == 1) {
				value = "<html>" + arrayOfMonths[cal.get(Calendar.MONTH)] + " ";
			}

		 	value += cal.get(Calendar.DAY_OF_MONTH) + "";
			DayEventMap dayEventMap = eventsData.getDailyEventMap(cal);	
			if (dayEventMap != null) {
				Map<String, Event> dayEvents = dayEventMap.getEvents();
				Set<String> keySet = dayEvents.keySet();
				ArrayList<String> sortedKeys = new ArrayList<String>(keySet);
                
                Collections.sort(sortedKeys, new Comparator<String>() {
                    public int compare(String s1, String s2){    
                        return Integer.parseInt(s1.split("-")[0]) - Integer.parseInt(s2.split("-")[0]);
                    }  
                });

                String previousEvent = "";
                int previousEndTime = 0;
                String ampm = "";
                for (String str : sortedKeys) {
                    if (dayEvents.get(str).getContent() == previousEvent && Integer.parseInt(str.split("-")[0]) == previousEndTime) {
                        continue;
                    } else {
                    	previousEvent = dayEvents.get(str).getContent();
                    	previousEndTime = Integer.parseInt(str.split("-")[1]);
                        Event event = dayEvents.get(str);

                        int hourValue = Integer.parseInt(str.split("-")[0]);
                        if (hourValue < 12) {
                            ampm = "am";
                        } else if (hourValue > 12) {
                            hourValue = hourValue - 12;
                            ampm = "pm";
                        }

                        if (hourValue == 12) {
                            value += "<br>" + "Noon" + ": " + event.getContent();
                        } else if (hourValue == 0) {
                            value += "<br>" + "Midnight" + ": " + event.getContent();
                        } else {
                            value += "<br>" + hourValue + ampm + ": " + event.getContent();
                        }
                    }
                }

/*
				for (String str : sortedKeys) {
					Event event = dayEvents.get(str);
					value += "<br>" + str + ": " + event.getContent();
				}*/
			}

			model.setValueAt(value, row, col);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		return model;
	}


	@Override
	public void setTableFormate(JTable eventsTable){
		eventsTable.setRowHeight(100);

		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.LEFT);
		renderer.setVerticalAlignment(SwingConstants.NORTH);
		for (int i = 0; i<7; i++) { 
			eventsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
		//eventsTable.setCellSelectionEnabled(true);
		//eventsTable.setCellEditor(new ExtendedTableCellEditor());
    }
}
