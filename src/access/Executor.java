package access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by Kike on 5/25/15.
 */
public class Executor {

    /*
        profileMethods: array de enteros con numero de metodos.
        object: nombre del objeto
        method: nombre del metodo
        params: parametros que recibe
        El metodo checkMethod recibe un array de enteros que son los metodos con los cuales un perfil particular tiene accesso.
        Va a base de datos con la metadata de objeto metodo y parametros y verifica que el metodo este dentro del array.
        En cuyo caso retorna el numero del metodo.
        En caso contrario retorna -1.

     */
    public Integer checkMethod(Integer[] profileMethods,String object, String method,Object ... params){
        DBPermissions met = new DBPermissions();
        Integer check= met.methodForName(method, object, this.paramTypes(params));
        met.close();
        for (Integer i : profileMethods) {
            if(i==check)
                return i;
        }
        return -1;
    }

    /*
        app: nombre de la aplicacion dentro del servicio.
        object: nombre del objeto.
        method:nombre del metodo.
        params: parametros a pasar
        el metodo execute utiliza la API Reflection de java para ejecutar el metodo solo utilizando su metadata.
        y retorna un objeto con lo que retorne la llamada al metodo (null en caso de no retornar nada).
        En caso de que no exista la clase, el metodo, o no pueda accederse a el, ocurrira una excepcion
     */
    public Object execute(String app,String object, String method, Object ... params) {
        Class c;
        Method m;
        Object o,returnable;
        try {
            c = Class.forName("access."+app + "." + object);
            o = c.newInstance();

            if(params.length>=1) {
                Class[] argTypes = new Class[params.length];
                int count=0;
                for (Object param : params) {
                    argTypes[count++] = param.getClass();
                }
                m = c.getMethod(method, argTypes);
                returnable = m.invoke(o,params);
            }else{
                m=c.getMethod(method);
                returnable = m.invoke(o);
            }
            return returnable;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException| InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    el metodo paramTypes convierte los parametros a pasar a la app en un string del tipo : "TipoParam1,TipoParam2,TipoParamN"
    Este metodo se utiliza para la correlacion de los tipos de datos ingresados con el metodo en base de datos. (como esta definido en el modelo de datos).
     */

    private String paramTypes(Object ... params){
        String returnable="";
        for (Object param : params) {
            returnable+=param.getClass().getSimpleName()+",";
        }
        return returnable.substring(0,returnable.length()-1);
    }
}
