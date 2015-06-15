
CREATE TABLE public.aplicacion (
                id_aplicacion INTEGER NOT NULL,
                nom_app VARCHAR(20) NOT NULL,
                CONSTRAINT id_aplicacion PRIMARY KEY (id_aplicacion)
);


CREATE TABLE public.objetos (
                id_objeto INTEGER NOT NULL,
                id_aplicacion INTEGER NOT NULL,
                class_name VARCHAR(20) NOT NULL,
                CONSTRAINT id_objeto PRIMARY KEY (id_objeto)
);


CREATE TABLE public.metodos (
                id_metodo INTEGER NOT NULL,
                id_objeto INTEGER NOT NULL,
                numparams SMALLINT NOT NULL,
                typeparams VARCHAR(20) NOT NULL,
                CONSTRAINT id_metodo PRIMARY KEY (id_metodo)
);
COMMENT ON COLUMN public.metodos.typeparams IS 'type of parameters separated by coma with the following syntax
Integer,String,Boolean,Double,Float,Byte,Char
for arrays 
IntegerA,StringA,BooleanA,DoubleA,FloatA,ByteA,CharA';


CREATE TABLE public.perfil (
                id_perfil INTEGER NOT NULL,
                descripcion VARCHAR(40) NOT NULL,
                CONSTRAINT id_perfil PRIMARY KEY (id_perfil)
);


CREATE TABLE public.permisos (
                id_permiso INTEGER NOT NULL,
                id_perfil INTEGER NOT NULL,
                id_metodo INTEGER NOT NULL,
                CONSTRAINT id_permiso PRIMARY KEY (id_permiso)
);


CREATE TABLE public.usuario (
                id_usuario INTEGER NOT NULL,
                nom_usuario VARCHAR NOT NULL,
                pas_usuario VARCHAR NOT NULL,
                prim_nom_usuario VARCHAR(20) NOT NULL,
                ape_usuario VARCHAR(20) NOT NULL,
                CONSTRAINT id_usuario PRIMARY KEY (id_usuario)
);


CREATE SEQUENCE public.usuario_perfil_id_usuario_perfil_seq;

CREATE TABLE public.usuario_perfil (
                id_usuario_perfil INTEGER NOT NULL DEFAULT nextval('public.usuario_perfil_id_usuario_perfil_seq'),
                id_usuario INTEGER NOT NULL,
                id_perfil INTEGER NOT NULL,
                CONSTRAINT id_usuario_perfil PRIMARY KEY (id_usuario_perfil)
);


ALTER SEQUENCE public.usuario_perfil_id_usuario_perfil_seq OWNED BY public.usuario_perfil.id_usuario_perfil;

ALTER TABLE public.objetos ADD CONSTRAINT aplicacion_objetos_fk
FOREIGN KEY (id_aplicacion)
REFERENCES public.aplicacion (id_aplicacion)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.metodos ADD CONSTRAINT objetos_metodos_fk
FOREIGN KEY (id_objeto)
REFERENCES public.objetos (id_objeto)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.permisos ADD CONSTRAINT metodos_permisos_fk
FOREIGN KEY (id_metodo)
REFERENCES public.metodos (id_metodo)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.usuario_perfil ADD CONSTRAINT perfil_usuario_perfil_fk
FOREIGN KEY (id_perfil)
REFERENCES public.perfil (id_perfil)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.permisos ADD CONSTRAINT perfil_permisos_fk
FOREIGN KEY (id_perfil)
REFERENCES public.perfil (id_perfil)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.usuario_perfil ADD CONSTRAINT usuario_usuario_perfil_fk
FOREIGN KEY (id_usuario)
REFERENCES public.usuario (id_usuario)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
