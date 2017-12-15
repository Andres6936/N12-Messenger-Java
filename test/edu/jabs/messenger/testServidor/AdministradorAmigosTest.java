/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: AdministradorAmigosTest.java,v 1.2 2006/12/11 07:31:42 man-muno Exp $
 * Universidad de los Andes (Bogot� - Colombia)
 * Departamento de Ingenier�a de Sistemas y Computaci�n 
 * Licenciado bajo el esquema Academic Free License version 2.1 
 *
 * Proyecto Cupi2 (http://cupi2.uniandes.edu.co)
 * Ejercicio: n12_messengerAmigos
 * Autor: Mario S�nchez - 21-abr-2006
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package edu.jabs.messenger.testServidor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import edu.jabs.messenger.servidor.AdministradorAmigos;
import edu.jabs.messenger.servidor.Usuario;
import junit.framework.TestCase;

/**
 * Esta es la clase usada para verificar que los métodos de la clase
 * AdministradorAmigos están correctamente implementados
 */
public class AdministradorAmigosTest extends TestCase
{
    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
	 * Es la clase donde se harán las pruebas
	 */
    private AdministradorAmigos adminAmigos;

    /**
     * Es el conjunto de propiedades para configurar las pruebas
     */
    private Properties configuracion;

    /**
	 * Es la conexión usada para las pruebas
	 */
    private Connection conexionPruebas;

    // -----------------------------------------------------------------
	// Métodos
    // -----------------------------------------------------------------

    /**
     * Inicializa la base de datos y construye un nuevo AdministradorAmigos conectado a esta base de datos
     */
    private void setupEscenario1( )
    {
        adminAmigos = null;
        File directorioData = new File( "./test/data" );
        System.setProperty( "derby.system.home", directorioData.getAbsolutePath( ) );
        configuracion = new Properties( );
        configuracion.setProperty( "admin.db.url", "jdbc:derby:testAdmin;create=true" );
        configuracion.setProperty( "admin.test.url", "jdbc:derby:testAdmin" );
        configuracion.setProperty( "admin.db.driver", "org.apache.derby.jdbc.EmbeddedDriver" );
        configuracion.setProperty( "admin.db.shutdown", "jdbc:derby:;shutdown=true" );
        configuracion.setProperty( "admin.db.path", "./test/data" );

        // Conectar a la base de datos
        try
        {
            String driver = configuracion.getProperty( "admin.db.driver" );
            Class.forName( driver ).newInstance( );
            String url = configuracion.getProperty( "admin.db.url" );
            conexionPruebas = DriverManager.getConnection( url );
        }
        catch( Exception e )
        {
            fail( "Fall� la conexion a la base de datos: " + e.getMessage( ) );
        }

        try
        {
            // Crear las tablas si es necesario
            crearTablas( );
        }
        catch( SQLException e1 )
        {
            fail( "No se pudo crear las tablas" );
        }

        try
        {
            // Limpia todos los datos existentes e inserta datos iniciales para las pruebas
            inicializarTablas( );
        }
        catch( SQLException e2 )
        {
            fail( "No se pudo inicializar las tablas" );
        }

        // Construir el administrador
        adminAmigos = new AdministradorAmigos( configuracion );
        try
        {
            adminAmigos.conectarABD( );
        }
        catch( Exception e3 )
        {
            fail( "No se pudo conectar el administrador a la BD" );
        }
    }

    /**
     * Crea las tablas necesarias para el administrador de los usuarios y sus amigos
     * @throws SQLException Se lanza esta excepci�n si hay problemas creando las tablas
     */
    private void crearTablas( ) throws SQLException
    {
        Statement s = conexionPruebas.createStatement( );

        boolean crearTablaUsuarios = false;
        try
        {
            // Verificar si ya existe la tabla usuarios
            s.executeQuery( "SELECT * FROM usuarios" );
        }
        catch( SQLException se )
        {
            crearTablaUsuarios = true;
        }

        if( crearTablaUsuarios )
        {
            s.execute( "CREATE TABLE usuarios (nombre varchar(32), estado varchar(15), ip varchar(15), puerto int , PRIMARY KEY (nombre))" );
        }

        boolean crearTablaAmigos = false;
        try
        {
            // Verificar si ya existe la tabla amigos
            s.executeQuery( "SELECT * FROM amigos" );
        }
        catch( SQLException se )
        {
            crearTablaAmigos = true;
        }

        if( crearTablaAmigos )
        {
            s.execute( "CREATE TABLE amigos (nombreUsuario varchar(32), nombreAmigo varchar(32), PRIMARY KEY (nombreUsuario, nombreAmigo))" );
        }

        s.close( );
    }

    /**
     * Limpia las tablas e inserta valores iniciales para las pruebas
     * @throws SQLException Se lanza esta excepci�n si hay problemas inicializando las tablas
     */
    private void inicializarTablas( ) throws SQLException
    {
        Statement s = conexionPruebas.createStatement( );

        // Limpiar la tabla usuarios
        s.executeUpdate( "DELETE FROM usuarios" );

        // Limpiar la tabla amigos
        s.executeUpdate( "DELETE FROM amigos" );

        // Insertar los datos iniciales en la tabla de usuarios
        s.executeUpdate( "INSERT INTO usuarios (nombre, estado, ip, puerto) VALUES ('Alicia', 'ONLINE','192.168.0.1',9996)" );
        s.executeUpdate( "INSERT INTO usuarios (nombre, estado, ip, puerto) VALUES ('Belisario', 'ONLINE','192.168.0.2',9997)" );
        s.executeUpdate( "INSERT INTO usuarios (nombre, estado, ip, puerto) VALUES ('Carlos', 'OFFLINE','',0)" );
        s.executeUpdate( "INSERT INTO usuarios (nombre, estado, ip, puerto) VALUES ('David', 'ONLINE','192.168.0.3',9999)" );

        // Insertar los datos iniciales en la tabla de amigos
        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Alicia', 'Belisario')" );
        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Alicia', 'Carlos')" );
        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Alicia', 'David')" );

        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Belisario', 'Carlos')" );
        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Belisario', 'David')" );

        s.executeUpdate( "INSERT INTO amigos (nombreUsuario, nombreAmigo ) VALUES ('Carlos', 'David')" );
    }

    /**
     * Este m�todo, que se llama despu�s de cada prueba, se encarga de detener el administrador y desconectarlo de la base de datos
     * @throws Exception Se lanza esta excepci�n si hay problemas en la desconexi�n
     */
    protected void tearDown( ) throws Exception
    {
        // Desconectar el administrador de la base de datos
        try
        {
            if( adminAmigos != null )
            {
                adminAmigos.desconectarBD( );
            }
        }
        catch( Exception npe )
        {
            fail( "No se deber�a lanzar una excepci�n desconectando" );
        }
    }

    /**
     * Verifica el m�todo agregarAmigo
     */
    public void testAgregarAmigo( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            // Agregar el amigo
            adminAmigos.agregarAmigo( "David", "Carlos" );

            // Verificar que la informaci�n se haya insertado
            Collection amigos = adminAmigos.darAmigos( "David" );
            assertEquals( "El n�mero de amigos es incorrecto", 1, amigos.size( ) );
            Usuario amigo = ( Usuario )amigos.iterator( ).next( );
            assertEquals( "El nombre del amigo est� equivocado", "Carlos", amigo.darNombre( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo cambiarEstadoUsuario, modificando el estado de un usuario que actualmente est� conectado
     */
    public void testCambiarEstadoUsuario1( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            // Cambiar el estado de un usuario conectadp
            adminAmigos.cambiarEstadoUsuario( "Alicia", "OFFLINE", "", 0 );

            // Verificar que la informaci�n se haya actualizado
            Usuario alicia = adminAmigos.darEstadoUsuario( "Alicia" );
            assertEquals( "El nombre del usuario es incorrecto", "Alicia", alicia.darNombre( ) );
            assertEquals( "El estado del usuario es incorrecto", Usuario.OFFLINE, alicia.darEstado( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo cambiarEstadoUsuario, modificando el estado de un usuario que actualmente est� desconectado
     */
    public void testCambiarEstadoUsuario2( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            // Cambiar el estado de un usuario conectadp
            adminAmigos.cambiarEstadoUsuario( "Carlos", "ONLINE", "192.168.0.3", 9998 );

            // Verificar que la informaci�n se haya actualizado
            Usuario carlos = adminAmigos.darEstadoUsuario( "Carlos" );
            assertEquals( "El nombre del usuario es incorrecto", "Carlos", carlos.darNombre( ) );
            assertEquals( "El estado del usuario es incorrecto", Usuario.ONLINE, carlos.darEstado( ) );
            assertEquals( "El puerto del usuario es incorrecto", "192.168.0.3", carlos.darDireccionIp( ) );
            assertEquals( "El puerto del usuario es incorrecto", 9998, carlos.darPuerto( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo crearUsuario
     */
    public void testCrearUsuario( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            // Agregar el usuario
            adminAmigos.crearUsuario( "Enrique" );

            // Verificar que el usuario se haya creado
            Usuario enrique = adminAmigos.darEstadoUsuario( "Enrique" );
            assertEquals( "El nombre del usuario es incorrecto", "Enrique", enrique.darNombre( ) );
            assertEquals( "El estado del usuario es incorrecto", Usuario.OFFLINE, enrique.darEstado( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo darAmigos
     */
    public void testDarAmigos( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            Collection amigos = adminAmigos.darAmigos( "Alicia" );
            assertEquals( "El n�mero de amigos es incorrecto", 3, amigos.size( ) );

            Iterator iter = amigos.iterator( );

            Usuario amigo1 = ( Usuario )iter.next( );
            assertEquals( "El nombre del amigo est� equivocado", "Belisario", amigo1.darNombre( ) );

            Usuario amigo2 = ( Usuario )iter.next( );
            assertEquals( "El nombre del amigo est� equivocado", "Carlos", amigo2.darNombre( ) );

            Usuario amigo3 = ( Usuario )iter.next( );
            assertEquals( "El nombre del amigo est� equivocado", "David", amigo3.darNombre( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo darEstadoUsuario, con un usuario que actualmente est� conectado
     */
    public void testDarEstadoUsuario1( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            Usuario alicia = adminAmigos.darEstadoUsuario( "Alicia" );
            assertEquals( "El nombre del usuario es incorrecto", "Alicia", alicia.darNombre( ) );
            assertEquals( "El estado del usuario es incorrecto", Usuario.ONLINE, alicia.darEstado( ) );
            assertEquals( "El puerto del usuario es incorrecto", "192.168.0.1", alicia.darDireccionIp( ) );
            assertEquals( "El puerto del usuario es incorrecto", 9996, alicia.darPuerto( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo darEstadoUsuario, con un usuario que actualmente est� desconectado
     */
    public void testDarEstadoUsuario2( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            Usuario carlos = adminAmigos.darEstadoUsuario( "Carlos" );
            assertEquals( "El nombre del usuario es incorrecto", "Carlos", carlos.darNombre( ) );
            assertEquals( "El estado del usuario es incorrecto", Usuario.OFFLINE, carlos.darEstado( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo darPersonasConocen
     */
    public void testDarPersonasConocen( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            Collection amigos = adminAmigos.darPersonasConocen( "David" );
            assertEquals( "El n�mero de personas que conocen a David es incorrecto", 3, amigos.size( ) );

            Iterator iter = amigos.iterator( );

            Usuario amigo1 = ( Usuario )iter.next( );
            assertEquals( "El nombre del conocido est� equivocado", "Alicia", amigo1.darNombre( ) );

            Usuario amigo2 = ( Usuario )iter.next( );
            assertEquals( "El nombre del conocido est� equivocado", "Belisario", amigo2.darNombre( ) );

            Usuario amigo3 = ( Usuario )iter.next( );
            assertEquals( "El nombre del conocido est� equivocado", "Carlos", amigo3.darNombre( ) );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo existeAmigo, para un amigo que existe
     */
    public void testExisteAmigo1( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            boolean existeAmigo = adminAmigos.existeAmigo( "Alicia", "Belisario" );
            assertTrue( "El amigo si existe", existeAmigo );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo existeAmigo, para un amigo que NO existe
     */
    public void testExisteAmigo2( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            boolean existeAmigo = adminAmigos.existeAmigo( "Belisario", "Alicia" );
            assertFalse( "El amigo NO existe", existeAmigo );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo existeUsuario, para un usuario que existe
     */
    public void testExisteUsuario1( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            boolean existeUsuario = adminAmigos.existeUsuario( "Alicia" );
            assertTrue( "El usuario si existe", existeUsuario );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

    /**
     * Verifica el m�todo existeUsuario, para un usuario que NO existe
     */
    public void testExisteUsuario2( )
    {
        // Configuraci�n b�sica
        setupEscenario1( );

        try
        {
            boolean existeUsuario = adminAmigos.existeUsuario( "Enrique" );
            assertFalse( "El usuario NO existe", existeUsuario );
        }
        catch( Exception e )
        {
            fail( "No se deber�a lanzar una excepci�n" );
        }
    }

}
