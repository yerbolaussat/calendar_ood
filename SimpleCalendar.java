import java.util.GregorianCalendar;

import javax.swing.JFrame;
/**
 * Main Method to run SimpleCalendar
 * @author Team Numero Uno
 *
 */
public class SimpleCalendar {
	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		EventModel model = new EventModel(new GregorianCalendar());
		model.addEvent(new Event("20160808", "4:00", "5:00", "classes"));
        model.addEvent(new Event("20160808", "7:00", "9:00", "classes"));

		CalendarFrame sc = new CalendarFrame(model);
	}
}
