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


    /*
    jsonValidate: json con datos de ingreso.
    El metodo loginJSON  deber recibir un String en formato JSON.
    Este se encargara  de Acceder a Base de datos y validar los datos del usuario ingresado.
    de ser correcto el ingreso se retornara un string con la llave del usuario.
    de lo contrario se retornara null
     */
    @WebMethod
    public String loginJSON(String jsonValidate) {
        String[] usrpass = deserializeJSON(jsonValidate);
        DBPermissions db = new DBPermissions();
        Integer prof = db.authenticate(usrpass[0], usrpass[1]);
        String key= null;

        if (prof > 0) {
            key = permissions.generateKey();
            permissions.connected.put(key, permissions.getPermissionsForUser(prof));
        }

        return key;
    }


    /*
    key: llave entregada al usuario
    obj: nombre objeto
    method: nombre metodo
    params: parametros correspondientes
    executeMethod recibe la llave del usuario (metodo login) el nombre del objeto el nombre del metodo y los parametros que recibe.
    verifica si el usuario con el cual se ingreso cuenta con permiso para ejecutar ese metodo.
    de contar con permiso ejecuta el metodo y en caso de que el metodo cuente con un objeto de retorno lo retorna en caso contrario retorna null.
    de no contar con permiso hace un print de la llave que trato de ingresar al metodo y retorna null
     */

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
            System.out.print(key+": has no access to this method:"+method+"\n");
        }
        return returnable;
    }

    public static void main(String[] args) {
        Security a = new Security();
        a.init();
        String key = a.loginJSON("{user:" + '"' + "kikemarquez" + '"' + ",password:" + '"' + "123456" + '"' + "}");
        Object b = a.executeMethod(key, "Basicas", "sumarString", "2.3",2.3);
        String address = "http://localhost:9000/Security";
        Endpoint.publish(address, a);
    }

    /*
    el metodo fillPermissions se encarga de llenar la estructura UserPermissions haciendo uso de la clase DBPermissions que consulta
      la base de datos para llenar la estructura. Ademas ejecuta el metodo getApps ( DBPermisssions). Es llamada al inicio del servicio.
     */
    private void fillPermissions() {
        DBPermissions db = new DBPermissions();
        permissions.profiles = db.getProfiles();
        permissions.getApps();
        db.close();
    }
    /*
    json: json a deserializar
    el metodo deserializeJSON recibe un JSON y lo deserializa de ser valido (debe contar con llaves user y password).
    retorna un Array Strings con 2 valores. en 1 posicion el usuario y 2da posicion la contraseña
     */

    private String[] deserializeJSON(String json) {
        JSONObject a = new JSONObject(json);
        String[] returnable = new String[a.keySet().size()];
        returnable[0] = a.getString("user");
        returnable[1] = a.getString("password");

        return returnable;
    }

}
