    /*
    * To change this license header, choose License Headers in Project Properties.
    * To change this template file, choose Tools | Templates
    * and open the template in the editor.
    */
    package prototypes;
//
//    import java.awt.event.ActionEvent;
//    import java.awt.event.ActionListener;
//
//    import javax.swing.JButton;
//    import javax.swing.JComboBox;
//    import javax.swing.JFrame;
//    import javax.swing.JLabel;
//    import javax.swing.JOptionPane;
//    import javax.swing.JPasswordField;
//    import javax.swing.JTextField;
//    import db.dbConnectorRemote;
//    import java.util.ArrayList;
//    import org.jdesktop.swingx.JXPanel;
//    import org.jdesktop.swingx.JXTaskPane;
//    import org.jdesktop.swingx.JXTaskPaneContainer;
//    /**
//    *
//    * @author zteff1
//    */
//    public class EditUser {
//
//    public EditUser() {        
//    JFrame eframe = new JFrame("CreateUser");
//    eframe.setSize(600, 300);
//    eframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    showComponents(eframe);
//    eframe.setVisible(true); 
//    }
//
//    
//    private void showComponents(JFrame eframe) {
//        
//        
//        eframe.setLayout(null);
//	JLabel roleLabel = new JLabel("Pick User");
//	roleLabel.setBounds(10, 10, 80, 25);
//	eframe.add(roleLabel);
//        
//       
//        JComboBox idCombo = new JComboBox();
//	idCombo.setBounds(100, 10, 160, 25);
//	eframe.add(idCombo);
//    }
//    
//    private void fillcombo(){
//    try{
//        String userSql = "select userName from User";
//        EJBConnector ejbConnector = EJBConnector.getInstance();
//                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
//                
//        pst=dbConnector.prepareStatement(userSql);
//        rs=pst.executeQuery();
//        
//        while(rs.next()){
//            String name = rs.getString("userName");
//            idCombo.addItem(name)
//        }
//    }catch(Exception e){
//        JOptionPane.showMessageDialog(null, e);
//    }
//    }
//    
////    private void Filecombo(){
////    try{
////        String sql="selesct * from User";
////        
////        while(ps.next()){
////        EJBConnector ejbConnector = EJBConnector.getInstance();
////                dbConnectorRemote dbConnector = ejbConnector.getEjbRemote();
////                String name = dbConnector.getString("userName")
////                        }
////    
////    
////    }catch(Exeption e){
////        JOptionPane.showMessageDialog(null, e);
////        
////    }
////    }
//    
//    }

//////import javax.swing.*;
//////import java.sql.*;
//////
//////public class editUser extends JFrame{ 
//////    JComboBox jc = new JComboBox(); 
//////    JPanel panel = new JPanel(); 
//////    Connection con; 
//////    Statement st; 
//////    ResultSet rs; 
//////    public editUser() 
//////    { 
//////        this.setSize(400, 400);
//////        this.setLocationRelativeTo(null); 
//////        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
//////        try{ 
//////            con = DriverManager.getConnection("jdbc:mysql://peterhagane.net:3306/a_team","yngve","a_team");
//////            st = con.createStatement();
//////            String s = "select * from users"; 
//////            rs = st.executeQuery(s); 
//////            while(rs.next()) 
//////            { 
//////                jc.addItem(rs.getString(1)+" === "+rs.getString(2)); 
//////            } 
//////        }catch(Exception e){
//////            JOptionPane.showMessageDialog(null, "ERROR"); 
//////        }finally{ 
//////            try{ 
//////                st.close();
//////                rs.close();
//////                con.close(); 
//////            }catch(Exception e){
//////                JOptionPane.showMessageDialog(null, "ERROR CLOSE"); 
//////            } 
//////        } 
//////        panel.add(jc);
//////        this.getContentPane().add(panel); 
//////        this.setVisible(true);
//////    } 
//////    public static void main(String[] args){ 
//////        new editUser();
//////    } 
//////}

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

public class editUser {

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