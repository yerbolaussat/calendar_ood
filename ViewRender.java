import javax.swing.table.DefaultTableModel;
import javax.swing.*;


public interface ViewRender {
    /**
     * Method to load events and times
     * @param eventsData - event model that stores and manages the events
     */
	DefaultTableModel getTableModel(EventModel eventsData);
	
    /**
     * Formats the table to be visible in the view
     * @param eventsTable is a JTable in which the events are displayed
     */
    void setTableFormate(JTable eventsTable);
}
