
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * The Frame of Calendar GUI
 * 
 * @author qiaoliu
 * @version 1.0
 */
public class CalendarFrame extends JFrame {

	private static final String DAY = "day";
	private static final String WEEK = "week";
	private static final String MONTH = "month";
	private static final String AGENDA = "agenda";

	EventModel eventsData;

	Calendar cal;
	int firstDayOfWeek;
	int currentDayOfMonth;
	int numberOfDays;
	int numberOfWeeks;
	int year;
	int month;
	int day;
	String week;

	JLabel label;
	JTable table;
	JTable eventsTable;
	DefaultTableModel model;

	ViewRender eventsRender;

	/**
	 * Constructs GUI with event date in EventModel
	 * 
	 * @param eM
	 *            the eventModel
	 */
	CalendarFrame(EventModel eM) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("CS151Project");
		setSize(1200, 400);
		setVisible(true);
		setLayout(new GridLayout(1, 2));
		addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	ObjectOutputStream out = null;
				try {
					out = new ObjectOutputStream(new FileOutputStream("events.data"));
					out.writeObject(eventsData.getAllEvents());
					out.close();
				} catch (FileNotFoundException e1) {
					promptMsg(e1.getMessage(), "Message");
				} catch (IOException e1) {
					promptMsg(e1.getMessage(), "Message");
				}
		    }
		});

		eventsData = eM;
		eventsRender = new DayViewRender();

		eventsData.attach(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateData();
				updateTableView();
				updateEventsView(eventsRender);
			}
		});

		// previous day button
		JButton preDay = new JButton("<-");
		preDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DAY_OF_MONTH, -1);
				eventsData.updateDate(cal);
			}
		});

		// current day button
		JButton today = new JButton("Today");
		today.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal = new GregorianCalendar();
				eventsData.updateDate(cal);
			}
		});

		// next day button
		JButton nextDay = new JButton("->");
		nextDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.DAY_OF_MONTH, +1);
				eventsData.updateDate(cal);
			}
		});

		// Top1 left: panel1
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		panel1.add(preDay, BorderLayout.WEST);
		panel1.add(today, BorderLayout.CENTER);
		panel1.add(nextDay, BorderLayout.EAST);

		// previous month button
		JButton b1 = new JButton("<-");
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.MONTH, -1);
				eventsData.updateDate(cal);
			}
		});

		// next month button
		JButton b2 = new JButton("->");
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				cal.add(Calendar.MONTH, +1);
				eventsData.updateDate(cal);
			}
		});

		// Month year label
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);

		// Top2 left: panel2
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(b1, BorderLayout.WEST);
		panel2.add(label, BorderLayout.CENTER);
		panel2.add(b2, BorderLayout.EAST);

		JPanel topLeft = new JPanel();
		topLeft.setLayout(new GridLayout(2, 1));
		topLeft.add(panel1);
		topLeft.add(panel2);

		// Calendar model
		String[] columns = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		model = new DefaultTableModel(null, columns) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// Calendar table
		table = new JTable(model) {
			public TableCellRenderer getCellRenderer(int row, int column) {
				return new CellRenderer();
			}
		};
		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int rowCal = table.getSelectedRow();
				int columnCal = table.getSelectedColumn();
				int val = (int) model.getValueAt(rowCal, columnCal);
				cal.set(Calendar.DAY_OF_MONTH, val);
				eventsData.updateDate(cal);
			}
		});

		// middle left: pane1
		JScrollPane pane1 = new JScrollPane(table);

		// bottom left: create
		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createFrame();
			}
		});

		// left: paneLeft
		JPanel panelLeft = new JPanel();
		panelLeft.setLayout(new BorderLayout());
		panelLeft.add(topLeft, BorderLayout.NORTH);
		panelLeft.add(pane1, BorderLayout.CENTER);
		panelLeft.add(create, BorderLayout.SOUTH);

		// top right
		JButton dayView = new JButton("Day");
		dayView.addActionListener(eventsViewListener(DAY));
		JButton weekView = new JButton("Week");
		weekView.addActionListener(eventsViewListener(WEEK));
		JButton monthView = new JButton("Month");
		monthView.addActionListener(eventsViewListener(MONTH));
		JButton agendaView = new JButton("Agenda");
		agendaView.addActionListener(eventsViewListener(AGENDA));
		JButton inputFile = new JButton("From File");
		inputFile.addActionListener(loadFile());
		JPanel topRightPanel = new JPanel();
		topRightPanel.setLayout(new GridLayout(1, 5));
		topRightPanel.add(dayView);
		topRightPanel.add(weekView);
		topRightPanel.add(monthView);
		topRightPanel.add(agendaView);
		topRightPanel.add(inputFile);

		// Events table
		eventsTable = new JTable() {
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component result = super.prepareRenderer(renderer, row, column);
				if (result instanceof JComponent) {
					((JComponent) result).setBorder(new MatteBorder(1, 1, 0, 0, Color.BLACK));
				}
				return result;
			}
		};

		JScrollPane pane2 = new JScrollPane(eventsTable);

		JPanel panelRight = new JPanel();
		panelRight.setLayout(new BorderLayout());
		panelRight.add(topRightPanel, BorderLayout.NORTH);
		panelRight.add(pane2, BorderLayout.CENTER);

		add(panelLeft);
		add(panelRight);

		eventsRender = new DayViewRender();
		updateData();
		updateTableView();
		updateEventsView(eventsRender);

	}

	/**
	 * Actionlistener to load file when From File button is clicked
	 * @return actionListener
	 */
	private ActionListener loadFile() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				File file = null;
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("."));

				chooser.setFileFilter(new FileFilter() {
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
					}

					public String getDescription() {
						return ".txt File";
					}
				});

				int r = chooser.showOpenDialog(new JFrame());
				if (r == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
				}

				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = br.readLine()) != null) {
						// Math Class;2014;1;2;MWF;17;18;
						String[] inputStrs = line.split(";");
						String content = inputStrs[0].trim();
						ArrayList<String> days = parseDays(inputStrs[1].trim(), inputStrs[2].trim(),
								inputStrs[3].trim(), inputStrs[4].trim());
						String startTime = inputStrs[5].trim();
						String endTime = inputStrs[6].trim();
						if (eventsData.isConflict(days, Integer.parseInt(startTime), Integer.parseInt(endTime))) {
							throw new IllegalArgumentException("There is schedule during " + startTime + ":00 and "
									+ endTime + ":00." + "\n" + "Please reschedule.");
						} else {
							for (int i = 0; i < days.size(); i++) {
								eventsData.addEvent(new Event(days.get(i), startTime, endTime, content));
							}
						}
						updateEventsView(eventsRender);
					}
				} catch (FileNotFoundException e1) {
				    promptMsg(e1.getMessage(), "Error");
				} catch (IOException e1) {
				    promptMsg(e1.getMessage(), "Error");
				} catch (IllegalArgumentException e1) {
				    promptMsg(e1.getMessage(), "Error");
				} catch (Exception e1) {
				    promptMsg(e1.getMessage(), "Error");
				}
			}

			/**
			 * Method to parse through the text file to add events
			 * @param y the event
			 * @param startMonth starting month
			 * @param endMonth ending month
			 * @param weekDays weekdays where event occurs
			 * @return arraylist contaiing events
			 */
			private ArrayList<String> parseDays(String y, String startMonth, String endMonth, String weekDays) {
				ArrayList<String> result = new ArrayList<>();
				int year = Integer.parseInt(y);
				int startM = Integer.parseInt(startMonth) - 1;
				int endM = Integer.parseInt(endMonth) - 1;

				Map<String, Integer> weekMap = new HashMap<>();
				weekMap.put("S", 1);
				weekMap.put("M", 2);
				weekMap.put("T", 3);
				weekMap.put("W", 4);
				weekMap.put("H", 5);
				weekMap.put("F", 6);
				weekMap.put("A", 7);

				Set<Integer> set = new HashSet<>();
				for (int i = 0; i < weekDays.length(); i++) {
					set.add(weekMap.get(weekDays.substring(i, i + 1)));
				}

				Calendar cal = new GregorianCalendar(year, startM, 1);
				do {
					int day = cal.get(Calendar.DAY_OF_WEEK);
					if (set.contains(day)) {
						result.add("" + y + parseToTwoInteger((cal.get(Calendar.MONTH) + 1))
								+ parseToTwoInteger(cal.get(Calendar.DAY_OF_MONTH)));
					}
					cal.add(Calendar.DAY_OF_YEAR, 1);
				} while (cal.get(Calendar.MONTH) <= endM);

				return result;

			}
		};

	}

	/**
	 * Changed the view according to which button is clicked
	 * @param s the string of the button that is clicked
	 * @return actionListener
	 */
	public ActionListener eventsViewListener(final String s) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (s) {
				case "day":
					eventsRender = new DayViewRender();
					break;

				case "week":
					eventsRender = new WeekViewRender();
					break;

				case "month":
					eventsRender = new MonthViewRender();
					break;

				case "agenda":
					agendaFrame();
					break;

				default:
					break;
				}

				updateEventsView(eventsRender);

			}
		};
	}

	/**
	 * Updates the calendar and data after an action occurs.
	 */
	public void updateData() {
		cal = eventsData.getCalendar();
		firstDayOfWeek = eventsData.getFirstDayOfWeek();
		currentDayOfMonth = eventsData.getDayNumber();
		numberOfDays = eventsData.getNumberOfDays();
		numberOfWeeks = eventsData.getNumberOfWeeks();
		year = eventsData.getYearNumber();
		month = eventsData.getMonthNumber();
		day = eventsData.getDayNumber();
		week = eventsData.getDayOfWeek();
	}

	/**
	 * Updates the label to show current month
	 */
	public void updateTableView() {

		// label as "July 2016"
		label.setText(month + "/" + year);

		// reset the model
		model.setRowCount(0);
		model.setRowCount(numberOfWeeks);

		// set value in the DefaultTableModel
		int i = firstDayOfWeek - 1;
		for (int d = 1; d <= numberOfDays; d++) {
			int row = i / 7;
			int col = i % 7;
			model.setValueAt(d, row, col);
			if (d == day) {
				table.changeSelection(row, col, false, false);
			}
			i++;
		}
	}

	/**
	 * Updates the event view after an action occurs, such as an event being added
	 * @param eventsRender
	 */
	public void updateEventsView(ViewRender eventsRender) {
		DefaultTableModel eventsTableModel = eventsRender.getTableModel(eventsData);
		eventsTable.setModel(eventsTableModel);
		eventsRender.setTableFormate(eventsTable);
	}

	/**
	 * The Class to render cell
	 * 
	 * @author qiaoliu
	 * 
	 */
	public class CellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);

			if (isSelected)
				cell.setBackground(Color.BLUE);

			return cell;
		}
	}

	/**
	 * Creates a frame to create a new event
	 */
	private void createFrame() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new JFrame("Create Event");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				final JTextArea textArea = new JTextArea(15, 50);
				textArea.setText("Untitled Event");
				textArea.setWrapStyleWord(true);
				textArea.setEditable(true);
				textArea.setFont(Font.getFont(Font.SANS_SERIF));

				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(1, 4));
				final JTextField d = new JTextField("" + year + "/" + month + "/" + day);
				final JTextField startTime = new JTextField("1:00am");
				JTextField t = new JTextField("to");
				t.setEditable(false);
				final JTextField endTime = new JTextField("2:00am");
				JButton save = new JButton("Save");
				panel.add(d);
				panel.add(startTime);
				panel.add(t);
				panel.add(endTime);
				panel.add(save);

				save.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							eventsData.addEvent(new Event(YYYYMMDD(d.getText().trim()), startTime.getText(),
									endTime.getText(), textArea.getText()));
							updateEventsView(eventsRender);
						} catch (Exception ex) {
							if (ex instanceof IllegalArgumentException)
								promptMsg(ex.getMessage(), "Error");
							else promptMsg(ex.getMessage(), "Error");
						}
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				});

				frame.setSize(500, 100);
				frame.setLayout(new GridLayout(2, 1));
				frame.add(textArea);
				frame.add(panel);
				frame.setLocationByPlatform(true);
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Creates a new frame to enter input for Agenda View
	 */
	private void agendaFrame() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final JFrame frame = new JFrame("Please select a time period.");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(1, 4));
				//JTextField d = new JTextField("" + year + "/" + month + "/" + day);
				final JTextField startDate = new JTextField("1" + "/1" + "/2016");
				JTextField to = new JTextField("to");
				to.setEditable(false);
				final JTextField endDate = new JTextField("1" + "/2" + "/2016");
				JButton go = new JButton("Go");
				
				go.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						eventsRender = new AgendaViewRender();
						AgendaViewRender.setStartDate(startDate.getText());
						AgendaViewRender.setEndDate(endDate.getText());
						updateEventsView(eventsRender);
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					}
				});

				panel.add(startDate);
				panel.add(to);
				panel.add(endDate);
				panel.add(go);
				
				frame.setSize(500, 100);
				frame.setLayout(new GridLayout(2, 1));
				frame.add(panel);
				frame.setLocationByPlatform(true);
				frame.setVisible(true);

			}
		});
	}
	

	/**
	 * Formats date input to remove slashes
	 * @param str the date inputed
	 * @return the date without "/"
	 */
	private String YYYYMMDD(String str) {
		String[] s = str.split("/");
		return s[0] + parseToTwoInteger(Integer.parseInt(s[1].trim())) + parseToTwoInteger(Integer.parseInt(s[2].trim()));
	}
	
	/**
	 * Returns a string variable of integer input
	 * @param input
	 * @return
	 */
	private String parseToTwoInteger(int input) {
		return input < 10 ? "0" + input : "" + input;
	}
	
	/**
	 * Creates a pop up to display a message
	 * @param infoMessage message to be shown
	 * @param titleBar title of message
	 */
    public static void promptMsg(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Message: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }

}