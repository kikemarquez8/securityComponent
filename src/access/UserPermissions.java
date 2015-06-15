package access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Kike on 5/18/15.
 */
public class UserPermissions {
    /*
        las estructuras profiles, conneted y apps responden a las necesidades de la aplicacion y pueden verse asi.
        profiles: para identificar cada perfil con un set de apps, a su vez con sus objetos a su vez con sus metodos.
            ->numprofile:
                ->numapp:
                    ->numobject: {numMethods}

        apps: para identificar el numero de los metodos con su nombre real.
            ->methodNum:methodName

        connected: para identificar usuarios conectados con su key y los metodos a los cuales tienen acceso.
            ->userKey:[numMethods]
     */
    protected HashMap<Integer, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>> profiles = new HashMap<>();
    protected HashMap<String,Integer[]> connected = new HashMap<>();
    protected HashMap<Integer, String> apps = new HashMap<>();


    /*
        llena la estructura apps.
     */
    protected void getApps(){
        DBPermissions db =new DBPermissions();
        this.apps=db.getApps();
        db.close();
    }

    /*
        method: numero de metodo a consultar.
        busca en la estructura profiles la aplicacion a la cual hace referencia el metodo pasado.
        en caso de no encontrarse retorna string vacio.
     */

    protected String getAppForMethod(Integer method){
        for (Integer app : apps.keySet()) {
            for (Integer profile : profiles.keySet()) {
                if(profiles.get(profile).containsKey(app))
                    for (Integer object : profiles.get(profile).get(app).keySet()) {
                        if(profiles.get(profile).get(app).get(object).contains(method)) return apps.get(app);
                    }
            }
        }
        return "";
    }

    /*
        profile: numero de perfil del usuario
        el metodo busca los metodos para los cuales tiene permiso el perfil pasado.
     */
    protected Integer[] getPermissionsForUser(int profile){
        ArrayList<Integer> returnable = new ArrayList<>();
        for(Integer apps : profiles.get(profile).keySet()){
            for(Integer objects : profiles.get(profile).get(apps).keySet()){
                returnable.addAll(profiles.get(profile).get(apps).get(objects).stream().collect(Collectors.toList()));
            }
        }
        return returnable.toArray(new Integer[returnable.size()]);
    }
    /*
        retorna un string conteniendo un numero aleatorio de 20 caracteres.
     */
    protected String generateKey(){
        Random bi= new Random();
        String returnable= "";
        for(int i =0;i<20;i++)
            returnable += bi.nextInt(10);
        return returnable;
    }

}
