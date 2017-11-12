package entities.db;

import entities.auth.PrivelegedUser;

/**
 * Created by aliss on 11/11/2017.
 */
public interface LoginInfoDataSource {
    public boolean checkCredentials(PrivelegedUser p);

}
