package com.fifteen.events;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;

import com.fifteen.auth.login.LoginPage;
import com.fifteen.database.User;
import com.fifteen.events.local.EventLocal;
import com.fifteen.events.local.localDb;
import com.fifteen.events.local.localDbMethod;
import com.fifteen.events.local.exportImport.FileTypeFilter;
import com.fifteen.events.local.exportImport.exportTxt;
import com.fifteen.settings.EventSettings;
import com.fifteen.events.sync.localDatabaseFile;
import com.fifteen.events.sync.reAuthenticatePage;
import com.fifteen.profile.ProfilePage;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import org.apache.commons.io.FileUtils;

/**
 * Class responsible for the main GUI of our scheduler.
 * Uses a table to display a Calendar model and shows upcoming events
 * Links to other GUIs and functionalities.
 *
 * @author Tim
 */

public class CalendarView extends JFrame {

  private JTable tableCalendar;
  private JPanel panel1;
  private JScrollPane spanel1;
  private JButton nextMonth;
  private JButton prevMonth;
  private JLabel monthCalendar;
  private JLabel yearJlabel;
  private JButton addEvent;
  private JList upcomEvents;
  private JPanel panel2;
  private JLabel upcomingEventLable;
  private JButton syncButton;
  private DefaultTableModel modelCalendar;
  private DefaultListModel modelList;
  private JFrame frame;
  private final int day, month, year;
  private int currentMonth, currentYear;


  // MenuBar @author Jorge
  private JMenuBar e_menuBar;
  private JMenu menu;
  private JMenuItem menu1, menu2, menu3, menu4;
  private JMenu sview;
  private JMenuItem sview1, sview2, sview3;
  private JMenu export;
  private JMenuItem txt;
  private JMenu database;
  private JMenuItem dimport, dexport;
  private JMenu about;
  private JMenuItem about1;

  public CalendarView(User user) {

    // Create frame @Tim Görß 1252200
    frame = new JFrame("Calendar View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(true);
    frame.setPreferredSize(new Dimension(1920, 1080));

    e_menuBar = new JMenuBar();

    // create menus
    menu = new JMenu("Menu");
    sview = new JMenu("Switch View");
    export = new JMenu("Export");
    database = new JMenu("Database");
    about = new JMenu("About");

    // create menuitems
    menu1 = new JMenuItem("Profile");
    menu2 = new JMenuItem("Settings");
    menu3 = new JMenuItem("Log Out");
    menu4 = new JMenuItem("Exit");

    sview1 = new JMenuItem("Daily");
    sview2 = new JMenuItem("Weekly");
    sview3 = new JMenuItem("Monthly");

    txt = new JMenuItem(".txt");

    dimport = new JMenuItem("Import Database");
    dexport = new JMenuItem("Export Database");

    about1 = new JMenuItem("Documentation");

    // add menu items to menu
    menu.add(menu1);
    menu.add(menu2);
    menu.add(menu3);
    menu.add(menu4);

    sview.add(sview1);
    sview.add(sview2);
    sview.add(sview3);

    export.add(txt);

    database.add(dimport);
    database.add(dexport);

    about.add(about1);

    // add menu to menu bar
    e_menuBar.add(menu);
    e_menuBar.add(sview);
    e_menuBar.add(export);
    e_menuBar.add(database);
    e_menuBar.add(about);

    // add menubar to frame
    frame.setJMenuBar(e_menuBar);

    // Add panel @Tim Görß 1252200
    frame.add(panel1);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    if (!user.getEmail().equals("Guest")) {
      try {
        localDatabaseFile.localDatabase(user);
      } catch (IOException | SQLException e2) {
        e2.printStackTrace();
      }
    } else {
      localDb.initializeLocalDatabase();
    }

    localDb.loadSqliteDriver();

    try {
      localDb.createLocalConncetion();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    /**
     * class to export the local database to the directory that the user has
     * selected. Under the hood, the class while copy the local.db file in the
     * current directory and rename it to the destination directory.
     *
     * @author Triet Huynh
     */
    class exportLocalDatabase implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileTypeFilter(".db", "SQLite File"));

        int respond = chooser.showSaveDialog(null);

        if (respond == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          String path = file.getAbsolutePath();
          path = path + ".db";

          try {
            FileUtils.copyFile(new File("local.db"), new File(path));
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }

      }
    }
    dexport.addActionListener(new exportLocalDatabase());

    class importLocalDatabase implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileTypeFilter(".db", "SQLite File"));

        int respond = chooser.showOpenDialog(null);

        if (respond == JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          String path = file.getAbsolutePath();

          try {
            FileUtils.copyFile(new File(path), new File("local.db"), StandardCopyOption.REPLACE_EXISTING);
          } catch (IOException e1) {
            e1.printStackTrace();
          }

          updateCalendar(currentMonth, currentYear);
          JOptionPane.showMessageDialog(frame, "Database imported successfully! \n Please restart the application",
                  "Success",
                  JOptionPane.INFORMATION_MESSAGE);

        }
      }
    }
    dimport.addActionListener(new importLocalDatabase());

    class openProfile implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        new ProfilePage(user);
      }
    }
    menu1.addActionListener(new openProfile());

    class openSettings implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        new EventSettings();
      }
    }
    menu2.addActionListener(new openSettings());

    class logOut implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (!user.getEmail().equals("Guest")) {
          try {
            localDatabaseFile.uploadLocalDatabase(user);
          } catch (IOException | SQLException e1) {
            e1.printStackTrace();
          }
        }
        new LoginPage();
        frame.dispose();
      }
    }
    menu3.addActionListener(new logOut());

    class eventExit implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (!user.getEmail().equals("Guest")) {
          try {
            localDatabaseFile.uploadLocalDatabase(user);
          } catch (IOException | SQLException e1) {
            e1.printStackTrace();
          }
        }
        localDb.closeLocalConnection();
        System.exit(0);
      }
    }
    menu4.addActionListener(new eventExit());

    class addExportAction implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        // new AddContact();
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileFilter(new FileTypeFilter(".txt", "Text File"));
        int respond = fileChooser.showSaveDialog(null);

        if (respond == JFileChooser.APPROVE_OPTION) {
          try {
            String content = localDbMethod.getExportContent();
            File file = fileChooser.getSelectedFile();
            exportTxt.writeEvents(file, content);
          } catch (IOException e1) {
            e1.printStackTrace();
          } catch (SQLException e2) {
            e2.printStackTrace();
          }
        }

      }
    }
    txt.addActionListener(new addExportAction());

    // Create Calendar object and get current day, month and year @Tim Görß 1252200
    GregorianCalendar cal = new GregorianCalendar();
    day = cal.get(GregorianCalendar.DAY_OF_MONTH);
    month = cal.get(GregorianCalendar.MONTH);
    year = cal.get(GregorianCalendar.YEAR);
    currentMonth = month;
    currentYear = year;

    // Initialize JList @Tim Görß 1252200
    modelList = new DefaultListModel();
    upcomEvents.setModel(modelList);

    // Create TableModel and add it to Table @Tim Görß 1252200
    modelCalendar = new DefaultTableModel() {
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    tableCalendar.setModel(modelCalendar);

    spanel1.add(tableCalendar);
    spanel1.setViewportView(tableCalendar);

    // Add Column consisting of weekdays @Tim Görß 1252200
    String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    for (int i = 0; i < 7; i++) {
      modelCalendar.addColumn(weekdays[i]);
    }
    tableCalendar.setRowHeight(100);
    modelCalendar.setRowCount(6);

    // Update Calendar @Tim Görß 1252200
    updateCalendar(month, year);

    addEvent.addActionListener(new ActionListener() {
      /**
       * Invoked when an action occurs.
       *
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        new AddEvents(user, day, month, year, CalendarView.this);
        updateCalendar(currentMonth, currentYear);
      }
    });

    // Clicking event cell opens ShowEventFrame @Tim Görß 1252200
    spanel1.addMouseListener(new MouseAdapter() {
    });
    tableCalendar.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        JTable tblCalender = (JTable) e.getSource();
        int column = tblCalender.getSelectedColumn();
        int row = tblCalender.getSelectedRow();

        if (modelCalendar.getValueAt(row, column) != null) {
          int day = (Integer) modelCalendar.getValueAt(row, column);

          new ShowEvents(user, day, currentMonth, currentYear, CalendarView.this);
        }

      }
    });

    syncButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (user.getEmail().equals("Guest")) {
          new reAuthenticatePage();

        } else {
          try {
            localDatabaseFile.uploadLocalDatabase(user);
          } catch (IOException | SQLException e1) {
            e1.printStackTrace();
          }
          JOptionPane.showMessageDialog(frame, "Database synced successfully!", "Success",
                  JOptionPane.INFORMATION_MESSAGE);
        }
      }

    });
      // Forward one month @Tim Görß 1252200
      nextMonth.addActionListener(e -> {
          if (currentMonth == 11) {
              currentMonth = 0;
              currentYear += 1;
          } else {
              currentMonth += 1;
          }
          updateCalendar(currentMonth, currentYear);

      });

      // Backward one month @Tim Görß 1252200
      prevMonth.addActionListener(e -> {

          if (currentMonth == 0) {
              currentMonth = 11;
              currentYear -= 1;
          } else {
              currentMonth -= 1;
          }

          updateCalendar(currentMonth, currentYear);

      });
  }

  /**
   * Function to draw the table and fill the cells with values.
   * Also updates the upcoming events list
   *
   * @param month - month used for the calendar
   * @param year  - year used for the calendar
   * @author Tim Görß 1252200
   */
  public void updateCalendar(int month, int year) {

    // Get Events @Tim Görß 1252200
    ArrayList<EventLocal> eventMonths = new ArrayList<>();
    try {
      eventMonths = localDbMethod.buildEventLocal(month, year);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Update current month label @Tim Görß 1252200
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    monthCalendar.setText(months[month]);
    yearJlabel.setText(String.valueOf(currentYear));

    // Clear table and insert null value @Tim Görß 1252200
    for (int i = 0; i < 6; i++) {
      for (int j = 0; j < 7; j++) {
        modelCalendar.setValueAt(null, i, j);
      }
    }

    // Clear list @Tim Görß 1252200
    modelList.removeAllElements();

    // Create Calendar and get the number of days in the month and the weekday of the first day @Tim Görß 1252200
    int startDay, numberDays;

    GregorianCalendar cal = new GregorianCalendar(year, month, 1);
    numberDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
    startDay = cal.get(GregorianCalendar.DAY_OF_WEEK);

    // Draw calendar @Tim Görß 1252200
    for (int i = 1; i <= numberDays; i++) {
        int column = ((i + startDay) - 2) % 7;
        int row = (i + startDay - 2) / 7;
      modelCalendar.setValueAt(i, row, column);
    }

    // Add upcoming events in month @Tim Görß 1252200
    if (!eventMonths.isEmpty()) {
      for (int i = 0; i < eventMonths.size(); i++) {
        modelList.addElement(
                eventMonths.get(i).getEventName() + " " + eventMonths.get(i).getDayOfEvent().get(GregorianCalendar.DATE)
                        + " " + eventMonths.get(i).getPriority());
        modelList.addElement(" ");
      }
    }
    // Render Table, set cell colour @Tim Görß 1252200
    tableCalendar.setDefaultRenderer(Object.class, new tableCalendarRenderer());

  }

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
    nextMonth = new JButton();
    nextMonth.setText(">>");
    panel1.add(nextMonth, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    monthCalendar = new JLabel();
    monthCalendar.setAlignmentX(0.0f);
    monthCalendar.setHorizontalAlignment(0);
    monthCalendar.setText("months");
    panel1.add(monthCalendar, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(327, 17), null, 0, false));
    prevMonth = new JButton();
    prevMonth.setText("<<");
    panel1.add(prevMonth, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    spanel1 = new JScrollPane();
    panel1.add(spanel1, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(327, 428), null, 0, false));
    tableCalendar = new JTable();
    spanel1.setViewportView(tableCalendar);
    yearJlabel = new JLabel();
    yearJlabel.setHorizontalAlignment(0);
    yearJlabel.setHorizontalTextPosition(0);
    yearJlabel.setText("year");
    panel1.add(yearJlabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel1.add(spacer1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    panel2 = new JPanel();
    panel2.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel2.setBackground(new Color(-10855075));
    panel2.setDoubleBuffered(false);
    panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel2.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel2.add(spacer3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    upcomEvents = new JList();
    panel2.add(upcomEvents, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    upcomingEventLable = new JLabel();
    upcomingEventLable.setBackground(new Color(-1));
    Font upcomingEventLableFont = this.$$$getFont$$$("JetBrains Mono", Font.PLAIN, 12, upcomingEventLable.getFont());
    if (upcomingEventLableFont != null) upcomingEventLable.setFont(upcomingEventLableFont);
    upcomingEventLable.setForeground(new Color(-1));
    upcomingEventLable.setText("Upcoming Events");
    panel2.add(upcomingEventLable, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    addEvent = new JButton();
    addEvent.setHorizontalAlignment(0);
    addEvent.setHorizontalTextPosition(0);
    addEvent.setLabel("New Event");
    addEvent.setMaximumSize(new Dimension(100, 15));
    addEvent.setMinimumSize(new Dimension(16, 10));
    addEvent.setText("New Event");
    panel2.add(addEvent, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    syncButton = new JButton();
    syncButton.setText("Sync");
    panel1.add(syncButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
    if (currentFont == null) return null;
    String resultName;
    if (fontName == null) {
      resultName = currentFont.getName();
    } else {
      Font testFont = new Font(fontName, Font.PLAIN, 10);
      if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
        resultName = fontName;
      } else {
        resultName = currentFont.getName();
      }
    }
    Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
    Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
    return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return panel1;
  }
  /**
   * Renderer used for all cells in the table.
   * Changes the color of the cells based on the priority
   * of events attached to it
   * @author Tim Görß 1252200
   */
  public class tableCalendarRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused,
                                                   int row, int column) {
      super.getTableCellRendererComponent(table, value, selected, focused, row, column);

      // Variables used to highlight cells based on highest priority @Tim Görß 1252200
      int priorityOfDay;
      int temp;

      // Get Events @Tim Görß 1252200
      ArrayList<EventLocal> eventMonths = new ArrayList<>();

      try {
        eventMonths = localDbMethod.buildEventLocal(currentMonth, currentYear);
      } catch (SQLException e) {
        e.printStackTrace();
      }

      // Default white background @Tim Görß 1252200
      setBackground(new Color(255, 255, 255));

      // Changes cell background based on priority @Tim Görß 1252200
      if (value != null) {

        priorityOfDay = 0;

        for (int i = 0; i < eventMonths.size(); i++) {

          if (Integer.parseInt(value.toString()) == eventMonths.get(i).getDayOfEvent().get(GregorianCalendar.DATE)) {

              switch (eventMonths.get(i).getPriority()) {
                  case "high" -> {
                      temp = 3;
                      if (temp > priorityOfDay) {

                          priorityOfDay = temp;
                          setBackground(new Color(236, 9, 9));
                      }
                  }
                  case "medium" -> {
                      temp = 2;
                      if (temp > priorityOfDay) {

                          priorityOfDay = temp;
                          setBackground(new Color(208, 196, 65));
                      }
                  }
                  case "low" -> {
                      temp = 1;
                      if (temp > priorityOfDay) {

                          priorityOfDay = temp;
                          setBackground(new Color(119, 206, 13));
                      }
                  }
              }

          }
        }
      }
      setForeground(Color.black);
      return this;
    }

  }

}
