/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.GUI;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.swingx.JXLabel;

/**
 *
 * @author Arild-Bærbar
 */
public class Modul {
    String name;
    JXLabel modulContent;
    int status;
    boolean delivered;
    
    public Modul(String name, JXLabel modulContent) {
        this.name = name;
        this.modulContent = modulContent;
        delivered = false;
               
    }
    
    public String getName() {
        return name;
    }
    
    public JXLabel getInnhold() {
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
    public static List makeModules(int numberOfModules)   {
        ArrayList<Modul> modules = new ArrayList<Modul>();
        int i = 1;
        while (i <= numberOfModules) {
            JXLabel content = new JXLabel("Her er innholdet i modul " + i + "   ");
            Modul modul = new Modul("Modul " + i + "     ", content);
            modules.add(modul);
            i++;
        }
        return modules;
    }
}
