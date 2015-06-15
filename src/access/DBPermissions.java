package access;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kike on 5/14/15.
 */
public class DBPermissions {
    Connection con;
    Statement statement;

    /*
        dir: direccion de la base de datos a conectar
        username: nombre de usuario de la base de datos a conectar
        pass: password para ingresar a base de datos.
     */
    public void connect(String dir, String username, String pass){
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection(dir,username,pass);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /*
        sql: query a realizar.
        el metodo realiza la consulta pasada como string y retorna un ResultSet con los datos.
     */
    public ResultSet open(String sql) {
        try {
            statement= con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.executeQuery(sql);
            return statement.getResultSet();
        }
        catch(Exception e){
            e.printStackTrace ();
            return null;
        }
    }
    /*
        sql: query a realizar
        hace operaciones de borrado y actualizacion de la base de datos.
     */
    public void exeSql(String sql) {
        try{
            statement=con.createStatement();
            statement.executeQuery(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        cierra la conexion.
     */
    public void close(){
        try{
            statement.close();
            con.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
        rs: ResultSet con la consulta realizada.
        llena una estructura del tipo profiles (UserPermissions).
     */
    private HashMap<Integer, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>> permissionSet(ResultSet rs){
        HashMap<Integer, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>> perms = new HashMap<>();
        HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> tempapp = new HashMap<>();
        HashMap<Integer, ArrayList<Integer>> tempobj = new HashMap<>();
        ArrayList<Integer> methodnum = new ArrayList<>();
        try {
            rs.last();
            int numrows = rs.getRow();
            //numrows is the total number of rows in the query
            rs.first();
            //profile num will be the number of the profile associated with a method
            Integer profiles, app,object,method;
            rs.beforeFirst();
            while(rs.next()) {
                profiles = rs.getInt(1);
                app = rs.getInt(4);
                object = rs.getInt(3);
                method = rs.getInt(2);
                if (perms.containsKey(profiles)) {
                    if (perms.get(profiles).containsKey(app)) {
                        if(perms.get(profiles).get(app).containsKey(object)){
                            perms.get(profiles).get(app).get(object).add(method);
                        }else{
                            methodnum = new ArrayList<Integer>();
                            methodnum.add(method);
                            perms.get(profiles).get(app).put(object,methodnum);
                        }
                    }else{
                        methodnum = new ArrayList<Integer>();
                        tempobj = new HashMap<Integer,ArrayList<Integer>>();
                        methodnum.add(method);
                        tempobj.put(object, methodnum);
                        perms.get(profiles).put(app, tempobj);
                    }
                }else{
                    methodnum = new ArrayList<Integer>();
                    tempobj = new HashMap<Integer,ArrayList<Integer>>();
                    tempapp = new HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>();
                    methodnum.add(method);
                    tempobj.put(object,methodnum);
                    tempapp.put(app,tempobj);
                    perms.put(profiles,tempapp);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return perms;
    }

    /*
        retorna la estructura de tipo profiles (UserPermissions). Esta funcion hace el query que obtiene el ResultSet a ser entregado
        a la funcion permissionSet.
     */
    protected HashMap<Integer, HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>> getProfiles(){
        this.connect("jdbc:postgresql://localhost:5432/securitycomp", "postgres","7413246");
        return this.permissionSet(this.open("SELECT id_perfil, permisos.id_metodo, objetos.id_objeto , objetos.id_aplicacion  FROM permisos JOIN metodos ON permisos.id_metodo=metodos.id_metodo JOIN objetos ON metodos.id_objeto= objetos.id_objeto JOIN aplicacion ON objetos.id_aplicacion = aplicacion.id_aplicacion ORDER BY permisos.id_perfil,objetos.id_aplicacion, objetos.id_objeto"));
    }

    /*
        usr: nombre de usuario
        pass: contraseña
        identifica a un usuario con su nombre de usuario y password, de existir retorna el perfil. Caso Contrario retorna -1.
     */
    protected Integer authenticate(String usr, String pass){
        this.connect("jdbc:postgresql://localhost:5432/securitycomp", "postgres","7413246");
        ResultSet rs = this.open("SELECT id_perfil FROM usuario JOIN usuario_perfil ON usuario.id_usuario=usuario_perfil.id_usuario WHERE nom_usuario='"+usr+"' AND pas_usuario='"+pass+"'");
        try {
            rs.next();
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }

    /*
        method: nombre del metodo
        object: nombre del objeto
        params: tipo de parametros separados por comas.
        identifica el numero del metodo a ejecutar
     */
    protected Integer methodForName(String method, String object,String params){
        this.connect("jdbc:postgresql://localhost:5432/securitycomp", "postgres","7413246");
        ResultSet rs = this.open("SELECT id_metodo FROM metodos JOIN objetos ON objetos.id_objeto = metodos.id_objeto WHERE class_name="+"'"+object+"'"+" and metodo_nom="+"'"+method+"' and typeparams='"+params+"'" );
        try{
            rs.next();
            return rs.getInt(1);
        }catch (SQLException e){
            return -1;
        }
    }
    /*
        obtiene las aplicaciones y las retorna en una estructura de tipos apps (UserPermissions).
     */
    protected HashMap<Integer, String> getApps(){
        this.connect("jdbc:postgresql://localhost:5432/securitycomp", "postgres","7413246");
        ResultSet rs = this.open("SELECT * FROM aplicacion");
        HashMap<Integer,String> apps= new HashMap<>();
        try{
            while(rs.next())
                apps.put(rs.getInt("id_aplicacion"),rs.getString("nom_app"));

        }catch (SQLException e){
            e.printStackTrace();
        }
        return apps;
    }



}
