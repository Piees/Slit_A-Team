/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import javax.ejb.Remote;

/**
 * @author Arild Høyland
 * @author Viktor Setervang
 */
@Remote
public interface DBDeleterRemote {
    
    public Connection dbConnection();
    
    public String deleteDelivery(int idModul, String userName);

    public String deleteModul(int idModul);
}
