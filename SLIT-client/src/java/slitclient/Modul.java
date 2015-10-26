/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slitclient;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jdesktop.swingx.JXLabel;

/**
 *
 * @author Arild
 */
public class Modul {
    String name;
    JPanel modulContent;
    int status;
    boolean delivered;
    
    public Modul(String name, JPanel modulContent) {
        this.name = name;
        this.modulContent = modulContent;
        delivered = false;
               
    }
    
    public String getName() {
        return name;
    }
    
    public JPanel getInnhold() {
        return modulContent;
    }
    
    public String getStatus()   {
        if(delivered != false)  {
            if(status == 0)  {
                return "Ikke vurdert";
            } else if(status == 1)  {
                return "Under vurdering";
            } else if(status == 2)  {
                return "Godkjent";
            } else {
                return "Ikke godkjent";
            }
        } else  {
            return "Ikke levert";
        }
        
    }
    public static ArrayList makeModules(int numberOfModules)   {
        ArrayList<Modul> modules = new ArrayList<Modul>();
        int i = 1;
        while (i <= numberOfModules) {
            
            Modul modul = new Modul("Modul " + i + "     ", makeContent(i));
            modules.add(modul);
            i++;
        }
        return modules;
    }
    
    public static JPanel makeContent(int i)    {
        JPanel content = new JPanel();
       // JTextField text = getModul(i);
       // content.add(text);
        
        return content;
    }
}
