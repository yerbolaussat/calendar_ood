import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.table.*;

/**
 * Displays Agenda View and events when Agenda button is clicked
 * @author Team Numero Uno
 *
 */
public class AgendaViewRender implements ViewRender {
    String[] arrayOfMonths = new String[] {"Jan", "Feb", "March", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    // [Month], [Day], [Year];
    public static int[] startDate;
    public static int[] endDate;


	@Override
	public DefaultTableModel getTableModel(EventModel eventsData) {
        String[] columns = { "Day", "List of events" };
        
        DefaultTableModel tableModel = new DefaultTableModel(null, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Calendar startCal  = new GregorianCalendar();
        startCal.set(GregorianCalendar.YEAR, startDate[2]);
        startCal.set(GregorianCalendar.MONTH, (startDate[0]-1));
        startCal.set(GregorianCalendar.DATE, startDate[1]);


        Calendar endCal  = new GregorianCalendar();
        endCal.set(GregorianCalendar.YEAR, endDate[2]);
        endCal.set(GregorianCalendar.MONTH, (endDate[0]-1));
        endCal.set(GregorianCalendar.DATE, (endDate[1]+1));

        int n = 1;

        while (!sameDate(startCal, endCal)) {
            String value = "<html>"; 
            DayEventMap dayEventMap = eventsData.getDailyEventMap(startCal); 

            if (dayEventMap.getEvents().size() != 0) {
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
                            value += "Noon" + ": " + event.getContent() + "<br>";
                        } else if (hourValue == 0) {
                            value += "Midnight" + ": " + event.getContent() + "<br>";
                        } else {
                            value += "" + hourValue + ampm + ": " + event.getContent() + "<br>";
                        }
                    }
                }
/*
                for (String str : sortedKeys) {
                        Event event = dayEvents.get(str);
                        value += str + ": " + event.getContent() + "<br>";
                }
*/
                tableModel.setRowCount(n);
                tableModel.setValueAt(arrayOfMonths[startCal.get(Calendar.MONTH)] + " " + startCal.get(Calendar.DAY_OF_MONTH) + " " + startCal.get(Calendar.YEAR), n-1, 0);            
                tableModel.setValueAt(value, n-1, 1);            
                n++;
            }

            startCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return tableModel;
	}


    @Override
    public void setTableFormate(JTable eventsTable){
        eventsTable.setRowHeight(100);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        renderer.setVerticalAlignment(SwingConstants.NORTH);
        for (int i = 0; i<2; i++) { 
            eventsTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        eventsTable.setShowGrid(true);
        eventsTable.setGridColor(Color.GRAY);

        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(500);
    }
    /**
     * Sets the start date of event
     * @param start the start date
     */
    public static void setStartDate(String start) {
        startDate = new int[3];
        
        for (int i = 0; i < 3; i++) {
            startDate[i] = Integer.parseInt(start.split("/")[i]);
        } 
    }

    /**
     * Sets the end date of event
     * @param end the end date
     */
    public static void setEndDate(String end) {
        endDate = new int[3];
        for (int i = 0; i < 3; i++) {
            endDate[i] = Integer.parseInt(end.split("/")[i]);
        } 
    }

    /**
     * Checks if two events are on the same day
     * @param cal1 first event
     * @param cal2 second event
     * @return true if events are on the same date
     */
    private boolean sameDate(Calendar cal1, Calendar cal2) {
        if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            return true;
        }
        return false;
    }
}
