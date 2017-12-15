package edu.jabs.messenger.servidor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Esta es la clase que se encarga de manejar la información sobre los usuarios
 * y sus amigos en la base de datos
 */
public class AdministradorAmigos implements IAdministradorAmigos
{

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Conexión a la base de datos
	 */
	private Connection conexion;

	/**
	 * Conjunto de propiedades que contienen la configuración de la aplicación
	 */
	private Properties configuracion;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el administrador de amigos y lo deja listo para conectarse a la
	 * base de datos
	 * 
	 * @param propiedades Las propiedades para la configuración del administrador -
	 *            Debe tener las propiedades "admin.db.path", "admin.db.driver",
	 *            "admin.db.url" y "admin.db.shutdown"
	 */
	public AdministradorAmigos( Properties propiedades )
	{
		configuracion = propiedades;

		// Establecer la ruta donde va a estar la base de datos.
		// Derby utiliza la propiedad del sistema derby.system.home para saber donde
		// están los datos
		File directorioData = new File( configuracion.getProperty( "admin.db.path" ) );
		System.setProperty( "derby.system.home", directorioData.getAbsolutePath( ) );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Conecta el administrador a la base de datos
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas realizando la
	 *             operación
	 * @throws Exception Se lanza esta excepción si hay problemas con los drivers
	 */
	public void conectarABD( ) throws SQLException, Exception
	{
		String driver = configuracion.getProperty( "admin.db.driver" );
		Class.forName( driver ).newInstance( );

		String url = configuracion.getProperty( "admin.db.url" );
		conexion = DriverManager.getConnection( url );
	}

	/**
	 * Desconecta el administrador de la base de datos y la detiene
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas realizando la
	 *             operación
	 */
	public void desconectarBD( ) throws SQLException
	{
		conexion.close( );
		String shutdownURL = configuracion.getProperty( "admin.db.shutdown" );
		try
		{
			DriverManager.getConnection( shutdownURL );
		}
		catch ( SQLException e )
		{
			// Al bajar la base de datos se produce siempre una excepción
		}
	}

	/**
	 * Crea las tablas necesarias para guardar la información de los usuarios y sus
	 * amigos. Si las tablas ya estaban creadas entonces no hace nada.
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas creando las
	 *             tablas
	 */
	public void inicializarTablas( ) throws SQLException
	{
		Statement s = conexion.createStatement( );

		// Crear la tabla de usuarios
		boolean crearTabla = false;
		try
		{
			// Verificar si ya existe la tabla usuarios
			s.executeQuery( "SELECT * FROM usuarios WHERE 1=2" );
		}
		catch ( SQLException se )
		{
			// La excepción se lanza si la tabla usuarios no existe
			crearTabla = true;
		}

		// Se crea una nueva tabla vacía
		if ( crearTabla )
		{
			s.execute(
					"CREATE TABLE usuarios (nombre varchar(32), estado varchar(15), ip varchar(15), puerto int , PRIMARY KEY (nombre))" );
		}

		// Crear la tabla de amigos
		crearTabla = false;
		try
		{
			// Verificar si ya existe la tabla amigos
			s.executeQuery( "SELECT * FROM amigos WHERE 1=2" );
		}
		catch ( SQLException se )
		{
			// La excepción se lanza si la tabla amigos no existe
			crearTabla = true;
		}

		// Se crea una nueva tabla vacía
		if ( crearTabla )
		{
			s.execute(
					"CREATE TABLE amigos (nombreUsuario varchar(32), nombreAmigo varchar(32), PRIMARY KEY (nombreUsuario, nombreAmigo))" );
		}

		s.close( );
	}

	/**
	 * Este método crea un nuevo usuario en la base de datos <br>
	 * <b>pre: </b>No hay ya un usuario con el mismo nombre en la base de datos
	 * 
	 * @param usuario El nombre del usuario - usuario != null && usuario != ""
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public void crearUsuario( String usuario ) throws SQLException
	{
		Statement st = conexion.createStatement( );

		String insert = "INSERT INTO usuarios (nombre, estado, ip, puerto) VALUES ('" + usuario + "', '"
				+ Usuario.STR_OFFLINE + "','', 0)";
		st.execute( insert );

		st.close( );
	}

	/**
	 * Agrega un amigo a un usuario <br>
	 * <b>pre: </b>El amigo indicado no hace parte todavía de los amigos del usuario
	 * 
	 * @param usuario El nombre del usuario al que se le va a agregar un amigo -
	 *            usuario != null && usuario != ""
	 * @param amigo El nombre del usuario que desde ahora va a ser amigo del usuario
	 *            - amigo != null && amigo != ""
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public void agregarAmigo( String usuario, String amigo ) throws SQLException
	{
		Statement st = conexion.createStatement( );

		String insert = "INSERT INTO amigos (nombreUsuario, nombreAmigo) VALUES ('" + usuario + "','" + amigo + "')";
		st.execute( insert );

		st.close( );
	}

	/**
	 * Este método cambia el estado de un usuario en la base de datos <br>
	 * <b>pre: </b>El usuario existe en la base de datos
	 * 
	 * @param usuario El nombre del usuario al que se va a cambiar el estado -
	 *            usuario != null && usuario != ""
	 * @param estado El nuevo estado del usuario - estado != null && estado != ""
	 * @param ip La dirección ip del usuario - ip != null
	 * @param puerto El puerto usado para las conexiones con el usuario
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public void cambiarEstadoUsuario( String usuario, String estado, String ip, int puerto ) throws SQLException
	{
		String sql = "UPDATE usuarios SET estado = '" + estado + "', ip = '" + ip + "', puerto = " + puerto
				+ " WHERE nombre ='" + usuario + "'";

		Statement st = conexion.createStatement( );
		st.executeUpdate( sql );
		st.close( );
	}

	/**
	 * Consulta en la base de datos cuales son los amigos de un usuario y los
	 * retorna ordenados por nombre
	 * 
	 * @param usuario El nombre del usuario del que se quieren los amigos - usuario
	 *            != null && usuario != ""
	 * @return Retorna una colección de objetos de tipo Usuario
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public Collection darAmigos( String usuario ) throws SQLException
	{
		Collection amigos = new LinkedList( );

		String sql = "SELECT nombreAmigo FROM amigos WHERE nombreUsuario = '" + usuario + "' ORDER BY nombreAmigo";

		Statement st = conexion.createStatement( );
		ResultSet resultado = st.executeQuery( sql );

		while ( resultado.next( ) )
		{
			String nombreAmigo = resultado.getString( 1 );
			Usuario amigo = darEstadoUsuario( nombreAmigo );
			amigos.add( amigo );
		}

		resultado.close( );
		st.close( );

		return amigos;
	}

	/**
	 * Consulta en la base de datos el estado de las personas que conocen a un
	 * usuario (el usuario es su amigo aunque ellos no necesariamente no son sus
	 * amigos) y los retorna ordenados por nombre.
	 * 
	 * @param nombreUsuario El nombre del usuario que las personas deben conocer -
	 *            nombreUsuario != null && nombreUsuario != ""
	 * @return Retorna una colección de objetos de tipo Usuario
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public Collection darPersonasConocen( String nombreUsuario ) throws SQLException
	{
		Collection conocen = new LinkedList( );

		String sql = "SELECT nombreUsuario FROM amigos WHERE nombreAmigo = '" + nombreUsuario
				+ "' ORDER BY nombreUsuario";

		Statement st = conexion.createStatement( );
		ResultSet resultado = st.executeQuery( sql );

		while ( resultado.next( ) )
		{
			String nombreConoce = resultado.getString( 1 );
			Usuario usuarioConoce = darEstadoUsuario( nombreConoce );
			conocen.add( usuarioConoce );
		}

		resultado.close( );
		st.close( );

		return conocen;
	}

	/**
	 * Este método consulta el estado actual de un usuario
	 * 
	 * @param nombre El nombre del usuario que se está consultando - nombre != null
	 * @return Retorna un objeto con la información del estado del usuario. Si el
	 *         usuario no existe, retorna null.
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public Usuario darEstadoUsuario( String nombre ) throws SQLException
	{
		Usuario amigo = null;
		String sql = "SELECT estado, ip, puerto FROM usuarios WHERE nombre = '" + nombre + "'";

		Statement st = conexion.createStatement( );
		ResultSet resultado = st.executeQuery( sql );

		if ( resultado.next( ) )
		{
			String estado = resultado.getString( 1 );
			if ( estado.equals( Usuario.STR_ONLINE ) )
			{
				String ip = resultado.getString( 2 );
				int puerto = resultado.getInt( 3 );
				amigo = new Usuario( nombre, ip, puerto );
			}
			else
				amigo = new Usuario( nombre );
		}

		resultado.close( );
		st.close( );

		return amigo;
	}

	/**
	 * Este método sirve para saber si un usuario existe ya en la base de datos
	 * 
	 * @param usuario El nombre del usuario buscado - usario != null
	 * @return Retorna true si el usuario aparece en la base de datos; retorna false
	 *         en caso contrario
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public boolean existeUsuario( String usuario ) throws SQLException
	{
		boolean encontreJugador = false;
		String sql = "SELECT nombre FROM usuarios WHERE nombre ='" + usuario + "'";

		Statement st = conexion.createStatement( );
		ResultSet resultado = st.executeQuery( sql );

		if ( resultado.next( ) ) // Se encontró el jugador
		{
			encontreJugador = true;
		}

		resultado.close( );
		st.close( );

		return encontreJugador;
	}

	/**
	 * Este método sirve para saber si un usuario ya es amigo de otro
	 * 
	 * @param usuario El nombre del usuario para el que se van a revisar los amigos-
	 *            usario != null
	 * @param amigo El nombre del amigo que se va a buscar dentro de los contactos
	 *            de usuario - amigo != null
	 * @return Retorna true si los usuarios dados ya son amigos
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	public boolean existeAmigo( String usuario, String amigo ) throws SQLException
	{
		boolean encontreAmigo = false;
		String sql = "SELECT nombreUsuario FROM amigos WHERE nombreUsuario ='" + usuario + "' AND nombreAmigo ='"
				+ amigo + "'";

		Statement st = conexion.createStatement( );
		ResultSet resultado = st.executeQuery( sql );

		if ( resultado.next( ) ) // Se encontró el amigo
		{
			encontreAmigo = true;
		}

		resultado.close( );
		st.close( );

		return encontreAmigo;
	}

}