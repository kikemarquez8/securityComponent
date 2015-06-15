package access;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Kike on 5/25/15.
 */
public class Executor {

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

    private String paramTypes(Object ... params){
        String returnable="";
        for (Object param : params) {
            returnable+=param.getClass().getSimpleName()+",";
        }
        return returnable.substring(0,returnable.length()-1);
    }
}
