/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototypes;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zteff1
 */
public class JTableRow {


    public static void main(String[] args){

        JFrame frame = new JFrame();
        JTable table = new JTable(); 

        Object[] columns = {"User","First Name","Last Name"};
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(columns);

        table.setModel(model);
        
        table.setBackground(Color.LIGHT_GRAY);
        table.setForeground(Color.black);
        Font font = new Font("",1,22);
        table.setFont(font);
        table.setRowHeight(30);

        JTextField textId = new JTextField();
        JTextField textFname = new JTextField();
        JTextField textLname = new JTextField();

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnUpdate = new JButton("Update");

        textId.setBounds(20, 220, 100, 25);
        textFname.setBounds(20, 250, 100, 25);
        textLname.setBounds(20, 280, 100, 25);

        btnAdd.setBounds(150, 220, 100, 25);
        btnUpdate.setBounds(150, 265, 100, 25);
        btnDelete.setBounds(150, 310, 100, 25);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(0, 0, 880, 200);

        frame.setLayout(null);
        
        frame.add(pane);
        
        frame.add(textId);
        frame.add(textFname);
        frame.add(textLname);
        
        frame.add(btnAdd);
        frame.add(btnDelete);
        frame.add(btnUpdate);
        
        Object[] row = new Object[4];
        
        btnAdd.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
             
                row[0] = textId.getText();
                row[1] = textFname.getText();
                row[2] = textLname.getText();
               
                model.addRow(row);
            }
        });

        
        btnDelete.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                int i = table.getSelectedRow();
                if(i >= 0){

model.removeRow(i);
                }
                else{
                    System.out.println("Delete Error");
                }
            }
        });

table.addMouseListener(new MouseAdapter(){
        
        @Override
        public void mouseClicked(MouseEvent e){

int i = table.getSelectedRow();
            
            textId.setText(model.getValueAt(i, 0).toString());
            textFname.setText(model.getValueAt(i, 1).toString());
            textLname.setText(model.getValueAt(i, 2).toString());
            
        }
        });

btnUpdate.addActionListener(new ActionListener(){
    
    @Override
            public void actionPerformed(ActionEvent e) {
int i = table.getSelectedRow();
                
                if(i >= 0) 
                {
                   model.setValueAt(textId.getText(), i, 0);
                   model.setValueAt(textFname.getText(), i, 1);
                   model.setValueAt(textLname.getText(), i, 2);
                }
                else{
                    System.out.println("Update Error");
                }
            }
        });

frame.setSize(900,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
    }
}
