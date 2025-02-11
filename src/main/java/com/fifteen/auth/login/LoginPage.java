package com.fifteen.auth.login;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

import com.fifteen.auth.admin.AdminPage;
import com.fifteen.auth.security.PasswordHasher;
import com.fifteen.auth.security.UserAuthenticator;
import com.fifteen.auth.signUp.SignUpPage;
import com.fifteen.database.DBMethod;
import com.fifteen.database.User;
import com.fifteen.database.UserDao;
import com.fifteen.database.UserDaoImp;
import com.fifteen.events.CalendarView;

/**
 * Added in email pattern checking to see if email is in correct format (the format is must have @
 * sign, after dot must have at least 2 letters), also check if email entered exists on the 
 * database. If the entered email existed and the password is correct then switch to the main 
 * event page.
 * normal account: email: t@g.com | password: t
 * admin account: email: f@g.com | password: f
 * @author Ante Maric 1273904, Triet Huynh
 */

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class LoginPage extends JFrame {
  private JTextField email;
  private JPanel loginPanel;
  private JPasswordField password;
  private JButton login;
  private JButton signUp;
  private JLabel emailLabel;
  private JLabel passwordLabel;
  private JCheckBox showPassword;
  private JButton offlineButton;
  private JFrame frame;

  public LoginPage() {
    frame = new JFrame("Login Frame");
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(300, 250));
    frame.setResizable(false);

    // adding the panel
    frame.add(loginPanel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    /**
     * When the user clicks the login button, the program checks if the email and
     * password are correct. If they are correct, the program switches to the main
     * event page.
     *
     * @author Ante Maric 1273904, Triet Huynh
     */
    login.addActionListener(e -> {
      String enteredEmail = email.getText();
      String enteredPassword = new String(password.getPassword());
      boolean allFieldsCorrect = false;

      DBMethod.createConnection();

      UserAuthenticator.checkFieldEmpty(passwordLabel, enteredPassword, "Please enter your password");

      allFieldsCorrect = UserAuthenticator.checkEmailFormat(emailLabel,
          enteredEmail);

      if (allFieldsCorrect) {
        allFieldsCorrect = UserAuthenticator.authenticateEmailField(emailLabel,
            enteredEmail, "");
      } else
        return;

      allFieldsCorrect = UserAuthenticator.checkFieldEmpty(passwordLabel, enteredPassword,
          "Please enter your password");

      if (allFieldsCorrect) {
        allFieldsCorrect = UserAuthenticator.authenticatePasswordField(
            passwordLabel, enteredEmail, enteredPassword);
      } else
        return;

      if (allFieldsCorrect == true) {
        UserDao userHandler = new UserDaoImp();
        User loginUser = userHandler.createUserFromLogin(enteredEmail);

        String hashedPassword = PasswordHasher.sha2(enteredPassword);
        try {
          DBMethod.fillInUserInfoFromUserEmail(loginUser, hashedPassword);
          if (loginUser.getIsAdmin() == 0) {

            new CalendarView(loginUser);
          } else {
            new AdminPage();
          }
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
        frame.dispose();
        DBMethod.closeConnection();
      }
    });

    /**
     * Button to toggle between showing and hiding the password.
     *
     * @author Triet Huynh
     */
    showPassword.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (showPassword.isSelected()) {
          password.setEchoChar((char) 0);
        } else {
          // unicode character bullet
          password.setEchoChar('\u2022');
        }
      }
    });

    /**
     * Button to switch to the sign up page.
     *
     * @author Ante Maric 1273904, Triet Huynh
     */
    signUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        DBMethod.closeConnection();
        new SignUpPage();
        frame.dispose();
      }
    });
    offlineButton.addActionListener(new ActionListener() {
      /**
       * When the user clicks the offline button, the program switches to the main
       * page and the user is login as a guest. This is use when there is no internet
       * connection
       * 
       * @author Ante Maric 1273904, Triet Huynh
       * @param e the event to be processed
       */
      @Override
      public void actionPerformed(ActionEvent e) {
        UserDao userHandler = new UserDaoImp();
        User offlineUser = userHandler.createUserFromLogin("Guest");

        new CalendarView(offlineUser);

        frame.dispose();

        DBMethod.closeConnection();
      }
    });
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
    loginPanel = new JPanel();
    loginPanel.setLayout(new GridLayoutManager(11, 10, new Insets(0, 0, 0, 0), -1, -1));
    email = new JTextField();
    email.setText("");
    loginPanel.add(email,
        new GridConstraints(2, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null,
            0, false));
    password = new JPasswordField();
    loginPanel.add(password,
        new GridConstraints(5, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null,
            0, false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayoutManager(1, 6, new Insets(0, 0, 0, 0), -1, -1));
    loginPanel.add(panel1,
        new GridConstraints(9, 1, 1, 8, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    login = new JButton();
    login.setText("Login");
    panel1.add(login,
        new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer1 = new Spacer();
    panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    signUp = new JButton();
    signUp.setText("Sign Up");
    panel1.add(signUp,
        new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer2 = new Spacer();
    panel1.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final Spacer spacer3 = new Spacer();
    panel1.add(spacer3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final Spacer spacer4 = new Spacer();
    loginPanel.add(spacer4, new GridConstraints(3, 4, 1, 5, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final Spacer spacer5 = new Spacer();
    loginPanel.add(spacer5, new GridConstraints(0, 1, 1, 8, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final Spacer spacer6 = new Spacer();
    loginPanel.add(spacer6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Email");
    loginPanel.add(label1, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label2 = new JLabel();
    label2.setText("Password");
    loginPanel.add(label2, new GridConstraints(4, 1, 1, 8, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer7 = new Spacer();
    loginPanel.add(spacer7, new GridConstraints(10, 2, 1, 7, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    final Spacer spacer8 = new Spacer();
    loginPanel.add(spacer8, new GridConstraints(7, 9, 1, 1, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    final Spacer spacer9 = new Spacer();
    loginPanel.add(spacer9, new GridConstraints(6, 7, 1, 2, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    passwordLabel = new JLabel();
    passwordLabel.setText("");
    loginPanel.add(passwordLabel,
        new GridConstraints(6, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    emailLabel = new JLabel();
    emailLabel.setText("");
    loginPanel.add(emailLabel, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    showPassword = new JCheckBox();
    showPassword.setText("Show password");
    loginPanel.add(showPassword,
        new GridConstraints(7, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final Spacer spacer10 = new Spacer();
    loginPanel.add(spacer10, new GridConstraints(8, 1, 1, 2, GridConstraints.ANCHOR_CENTER,
        GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    offlineButton = new JButton();
    offlineButton.setHorizontalTextPosition(0);
    offlineButton.setText("Offline Mode");
    loginPanel.add(offlineButton,
        new GridConstraints(10, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return loginPanel;
  }

}
