/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import db.dbConnectorRemote;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import slitclient.EJBConnector;
import slitclient.Login;

public class EditUser {

    dbConnectorRemote dbConnector;

    public EditUser() {

        EJBConnector ejbConnector = EJBConnector.getInstance();

        this.dbConnector = ejbConnector.getEjbRemote();
        dbConnector.updateUsersHashMap();

        JFrame frame = new JFrame();

        frame.setSize(900, 470);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        setComponents(frame);

    }

    private void setComponents(JFrame FFrame) {
        FFrame.setLayout(null);

        String[] columns = {"User", "First Name", "Last Name", "Role", "Mail"};
        JTable table = new JTable();

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);

        table.setModel(model);

        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.black);
        Font font = new Font("", 1, 22);
        table.setFont(font);
        table.setRowHeight(30);
        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, 0, 880, 200);

        String[] roles = {"teacher", "student"};
        JComboBox roleCombo = new JComboBox(roles);
        JTextField textId = new JTextField();
        JTextField textFname = new JTextField();
        JTextField textLname = new JTextField();
        JTextField textMail = new JTextField();
        JPasswordField textPassword = new JPasswordField();

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");
        JButton btnCancel = new JButton("Cancel");

        roleCombo.setBounds(330, 220, 100, 25);
        textId.setBounds(330, 250, 100, 25);
        textFname.setBounds(330, 280, 100, 25);
        textLname.setBounds(330, 310, 100, 25);
        textMail.setBounds(330, 340, 100, 25);
        textPassword.setBounds(330, 370, 100, 25);

        btnAdd.setBounds(440, 220, 100, 25);
        btnUpdate.setBounds(440, 265, 100, 25);
        btnDelete.setBounds(440, 310, 100, 25);
        btnCancel.setBounds(440, 355, 100, 25);

        FFrame.add(pane);

        FFrame.add(roleCombo);
        FFrame.add(textId);
        FFrame.add(textFname);
        FFrame.add(textLname);
        FFrame.add(textMail);
        FFrame.add(textPassword);

        FFrame.add(btnAdd);
        FFrame.add(btnDelete);
        FFrame.add(btnUpdate);
        FFrame.add(btnCancel);

        Object[] row = new Object[5];

        int index = 0;
        for (Map.Entry<String, Map> entry : dbConnector.getAllUsersHashMap().entrySet()) {
            row[0] = entry.getValue().get("userName");
            row[1] = entry.getValue().get("fname");
            row[2] = entry.getValue().get("lname");
            row[3] = entry.getValue().get("userType");
            row[4] = entry.getValue().get("mail");
            model.addRow(row);
            index++;
        }

        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                row[0] = textId.getText();
                row[1] = textFname.getText();
                row[2] = textLname.getText();
                row[3] = roleCombo.getSelectedItem();
                row[4] = textMail.getText();

                model.addRow(row);
            }
        });

        btnDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                int i = table.getSelectedRow();
                if (i >= 0) {

                    model.removeRow(i);
                } else {
                    System.out.println("Delete Error");
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int i = table.getSelectedRow();

                textId.setText(model.getValueAt(i, 0).toString());
                textFname.setText(model.getValueAt(i, 1).toString());
                textLname.setText(model.getValueAt(i, 2).toString());
                roleCombo.setSelectedItem(model.getValueAt(i, 3).toString());
                textMail.setText(model.getValueAt(i, 4).toString());
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                new Login();
                FFrame.dispose();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = table.getSelectedRow();

                if (i >= 0) {
                    model.setValueAt(textId.getText(), i, 0);
                    model.setValueAt(textFname.getText(), i, 1);
                    model.setValueAt(textLname.getText(), i, 2);
                    model.setValueAt(roleCombo.getSelectedItem(), i, 3);
                    model.setValueAt(textMail.getText(), i, 4);
                } else {
                    System.out.println("Update Error");
                }
            }
        });

    }
}
