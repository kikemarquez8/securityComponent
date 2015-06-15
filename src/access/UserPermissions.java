package access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Kike on 5/18/15.
 */
public class UserPermissions {
    protected HashMap<Integer, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>> profiles = new HashMap<>();

    protected HashMap<String,Integer[]> connected = new HashMap<>();
    protected HashMap<Integer, String> apps = new HashMap<>();

    protected void getApps(){
        DBPermissions db =new DBPermissions();
        this.apps=db.getApps();
        db.close();
    }
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
    protected Integer[] getPermissionsForUser(int profile){
        ArrayList<Integer> returnable = new ArrayList<>();
        int count=0;
        for(Integer apps : profiles.get(profile).keySet()){
            for(Integer objects : profiles.get(profile).get(apps).keySet()){
                returnable.addAll(profiles.get(profile).get(apps).get(objects).stream().collect(Collectors.toList()));
            }
        }
        return returnable.toArray(new Integer[returnable.size()]);
    }

    protected String generateKey(){
        Random bi= new Random();
        String returnable= "";
        for(int i =0;i<20;i++)
            returnable += bi.nextInt(10);
        return returnable;
    }

}
