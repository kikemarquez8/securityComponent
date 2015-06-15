package access;

import org.json.JSONObject;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;


/**
 * Created by Kike on 5/14/15.
 */
@WebService()
public class Security {
    private UserPermissions permissions = new UserPermissions();

    private void init() {
        fillPermissions();
    }

    @WebMethod
    public String login(String jsonValidate) {
        String[] usrpass = deserializeJSON(jsonValidate);
        DBPermissions db = new DBPermissions();
        Integer prof = db.authenticate(usrpass[0], usrpass[1]);
        String key= "ACCESS NOT GRANTED";

        if (prof > 0) {
            key = permissions.generateKey();
            permissions.connected.put(key, permissions.getPermissionsForUser(prof));
        }

        return key;
    }

    @WebMethod
    public Object executeMethod(String key, String obj, String method, Object ... params) {
        Executor runner = new Executor();
        Object returnable =null;
        Integer methodNum = runner.checkMethod(permissions.connected.get(key), obj, method,params);

        if (methodNum>0) {
            String app= permissions.getAppForMethod(methodNum);
            returnable = runner.execute(app,obj, method, params);
            System.out.print(returnable);
        } else {
            System.out.print(key+": has no access to this method\n");
        }
        return returnable;
    }

    public static void main(String[] args) {
        Security a = new Security();
        a.init();
//        String key = a.login("{user:" + '"' + "kikemarquez" + '"' + ",password:" + '"' + "123456" + '"' + "}");
//        a.executeMethod(key, "Basicas", "sumar", 1.2,2.3);
        String address = "http://localhost:9000/Security";
        Endpoint.publish(address, a);
    }

    private void fillPermissions() {
        DBPermissions db = new DBPermissions();
        permissions.profiles = db.getProfiles();
        permissions.getApps();
        db.close();
    }

    private String[] deserializeJSON(String json) {
        JSONObject a = new JSONObject(json);
        String[] returnable = new String[a.keySet().size()];
        returnable[0] = a.getString("user");
        returnable[1] = a.getString("password");

        return returnable;
    }

}
