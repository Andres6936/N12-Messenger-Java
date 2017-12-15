package edu.jabs.messenger.cliente;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TreeSet;

import edu.jabs.messenger.interfazCliente.IInterfazCliente;

/**
 * Esta es la clase principal del cliente para el Messenger Amigos. <br>
 * Esta clase es la que permite la comunicación del cliente con el servidor e
 * implementa las acciones que se deben realizar cuando se reciben mensajes del
 * servidor. <br>
 * Cuando se quiere establecer una conversación con un amigo, esta es la clase
 * que debe crear el objeto Conversacion. <br>
 * Esta clase tiene además la responsabilidad de mantener la colección de amigos
 * con su estado actualizado, tanto en la representación interna como en la
 * interfaz.
 */
public class ClienteAmigos implements IClienteMessenger
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * La colección con el estado de los amigos del usuario actual. Esta colección
	 * tiene objetos de tipo Usuario.
	 */
	private Collection amigos;

	/**
	 * La colección con las conversaciones que el cliente está llevando a cabo
	 * actualmente
	 */
	private Collection conversaciones;

	/**
	 * El nombre del usuario que está usando el cliente
	 */
	private String nombreUsuario;

	/**
	 * La dirección ip de la máquina local
	 */
	private String direccionLocal;

	/**
	 * El puerto en el cliente al cual deben conectarse los otros clientes que
	 * quieran establecer una conversación
	 */
	private int puertoLocal;

	/**
	 * La dirección donde se encuentra el servidor
	 */
	private String direccionServidor;

	/**
	 * El puerto en el servidor al cual debe conectarse el cliente
	 */
	private int puertoServidor;

	/**
	 * Es el socket a través del cual se envía y se recibe información del servidor
	 */
	private Socket socketServidor;

	/**
	 * Es el stream usuado para leer los mensajes enviados por el servidor
	 */
	private BufferedReader inServidor;

	/**
	 * Es el stream usado para enviar mensajes al servidor
	 */
	private PrintWriter outServidor;

	/**
	 * Esta es una referencia a la clase principal de la interfaz, a que el cliente
	 * debe notificarle los cambios que haya en la lista de amigos.
	 */
	private IInterfazCliente ventanaPrincipal;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el nuevo cliente
	 * 
	 * @param ventana La ventana principal de la aplicación - ventana != null
	 * @param archivoConfiguracion El archivo que contiene la configuración para el
	 *            cliente - archivoConfiguracion != null
	 * @throws Exception Se lanza esta excepción si hay problemas con el archivo de
	 *             configuración
	 */
	public ClienteAmigos( IInterfazCliente ventana, String archivoConfiguracion ) throws Exception
	{
		amigos = new TreeSet( );
		conversaciones = new LinkedList( );
		ventanaPrincipal = ventana;

		cargarConfiguracion( archivoConfiguracion );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Carga la configuración a partir de un archivo de propiedades
	 * 
	 * @param archivo El archivo de propiedades que contiene la configuración que
	 *            requiere el cliente - archivo != null y el archivo debe contener
	 *            las propiedades "servidor.direccion", "servidor.puerto" y
	 *            "cliente.puerto". - archivo != null
	 * @throws Exception Se lanza esta excepción si hay problemas cargando el
	 *             archivo de propiedades
	 */
	private void cargarConfiguracion( String archivo ) throws Exception
	{
		FileInputStream fis = new FileInputStream( archivo );
		Properties configuracion = new Properties( );
		configuracion.load( fis );
		fis.close( );

		direccionServidor = configuracion.getProperty( "servidor.direccion" );
		puertoServidor = Integer.parseInt( configuracion.getProperty( "servidor.puerto" ) );
		puertoLocal = Integer.parseInt( configuracion.getProperty( "cliente.puerto" ) );
	}

	/**
	 * Indica si el cliente está conectado al servidor
	 * 
	 * @return Retorna true si el cliente está conectado
	 */
	public boolean estaConectado( )
	{
		return socketServidor != null;
	}

	/**
	 * Retorna el nombre del usuario conectado
	 * 
	 * @return nombreUsuario
	 */
	public String darNombreUsuario( )
	{
		return nombreUsuario;
	}

	/**
	 * Conecta el cliente al servidor y deja la aplicación lista para enviar y
	 * recibir mensajes. <br>
	 * Después de conectarse al servidor se envía la información del usuario (nombre
	 * y dirección ip) y se crea un hilo especial para leer los mensajes
	 * provenientes del servidor usando una instancia de la clase
	 * ThreadRecibirMensajesServidor.
	 * 
	 * @param usuario El nombre del usuario local que se va a conectar al servidor -
	 *            usuario != null && usuario != ""
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación.
	 */
	public void conectar( String usuario ) throws IOException
	{
		amigos.clear( );
		nombreUsuario = usuario;

		// Establecer la conexión al servidor
		socketServidor = new Socket( direccionServidor, puertoServidor );
		outServidor = new PrintWriter( socketServidor.getOutputStream( ), true );
		inServidor = new BufferedReader( new InputStreamReader( socketServidor.getInputStream( ) ) );

		// Enviar el mensaje con la información del usuario
		direccionLocal = InetAddress.getLocalHost( ).getHostAddress( );
		String mensaje = IClienteMessenger.LOGIN + ":" + nombreUsuario + ";" + direccionLocal + ";" + puertoLocal;
		outServidor.println( mensaje );

		// Crear el hilo que se encarga de recibir los mensajes del servidor
		ThreadRecibirMensajesServidor hiloServidor = new ThreadRecibirMensajesServidor( this, inServidor );
		hiloServidor.start( );
	}

	/**
	 * Envía un mensaje de LOGOUT al servidor. Este a su vez deberá cerrar la
	 * conexión con el cliente y enviar un mensaje de desconexión (DESCONEXION) para
	 * que, cuando sea recibido por el ThreadRecibirMensajesServidor, se inicie la
	 * desconexión del lado del cliente.
	 */
	public void enviarDesconexion( )
	{
		if ( outServidor != null )
			outServidor.println( LOGOUT + ":" + nombreUsuario );
	}

	/**
	 * Cierra todas las conversaciones que están abiertas actualmente, limpia la
	 * lista de amigos y desconecta al cliente del servidor. <br>
	 * <b>pre: </b>No hay nadie escribiendo ni leyendo del socket que comunica al
	 * cliente con el servidor.
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas desconectando al
	 *             cliente
	 */
	public void desconectar( ) throws IOException
	{
		if ( socketServidor != null )
		{
			inServidor.close( );
			inServidor = null;

			outServidor.close( );
			outServidor = null;

			socketServidor.close( );
			socketServidor = null;

			for ( Iterator iter = conversaciones.iterator( ); iter.hasNext( ); )
			{
				Conversacion c = (Conversacion) iter.next( );
				c.terminar( );
			}

			amigos.clear( );
			ventanaPrincipal.actualizarAmigos( amigos );
			ventanaPrincipal.actualizarEstadoInterfaz( );
			ventanaPrincipal.terminarAplicacion( );
		}
	}

	/**
	 * Agrega un amigo al usuario, enviando un mensaje al servidor
	 * 
	 * @param nombreAmigo El nombre del nuevo amigo - amigo != null
	 */
	public void agregarAmigo( String nombreAmigo )
	{
		if ( outServidor != null )
			outServidor.println( AGREGAR_AMIGO + ":" + nombreAmigo );
	}

	/**
	 * Actualiza el estado de un amigo. Si no se encontraba en la lista de amigos,
	 * entonces es agregado.<br>
	 * Se debe actualizar también la interfaz.
	 * 
	 * @param amigo Los datos del amigo - amigo != null
	 */
	public void actualizarEstado( Usuario amigo )
	{
		for ( Iterator iter = amigos.iterator( ); iter.hasNext( ); )
		{
			Usuario a = (Usuario) iter.next( );
			if ( a.darNombre( ).equals( amigo.darNombre( ) ) )
			{
				iter.remove( );
			}
		}
		amigos.add( amigo );

		// Actualiza la interfaz
		ventanaPrincipal.actualizarAmigos( amigos );
	}

	/**
	 * Este método es usado cuando el usuario local va a iniciar una conversación
	 * con un amigo. <br>
	 * El proceso de crear una conversación se ejecuta en tres etapas principales.
	 * <br>
	 * 1. Se envía al servidor un mensaje solicitando que se le envíe un mensaje al
	 * otro cliente para que entre a hacer parte de la conversación. <br>
	 * 2. Se crea un objeto Conversacion que se encargará de recibir y manejar la
	 * conexión del cliente remoto. <br>
	 * 3. Se retorna el objeto conversación creado para que se abra la ventana
	 * correspondiente en la interfaz.
	 * 
	 * @param amigo El nombre del amigo con el que se va a realizar la conversación
	 *            - amigo != null && amigo hace parte de la lista de amigos
	 * @return Retorna el objeto conversación creado
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	public Conversacion crearConversacionLocal( String amigo ) throws IOException
	{
		String mensaje = CONVERSACION + ":" + amigo;
		outServidor.println( mensaje );

		Conversacion conv = new Conversacion( this, puertoLocal, amigo );
		conversaciones.add( conv );

		return conv;
	}

	/**
	 * Este método es usado cuando se recibe una notificación que indica que se debe
	 * participar en una conversación con un amigo. <br>
	 * El proceso para crear la conversación es el siguiente. <br>
	 * 1. Se crear un objeto Conversacion que se encargará de recibir y manejar la
	 * conexión del cliente remoto. <br>
	 * 2. El objeto Conversacion recién creado establece una comunicación con el
	 * otro cliente. <br>
	 * 3. Se le notifica a la interfaz que se creó una Conversación para que se cree
	 * la ventana correspondiente en la interfaz.
	 * 
	 * @param nombreAmigo El nombre del amigo con el que se va a realizar la
	 *            conversación - nombreAmigo != null
	 * @param ipAmigo La dirección del amigo con el que se va a realizar la
	 *            conversación - ipAmigo != null && ipAmigo != ""
	 * @param puertoAmigo El puerto del amigo al que se debe conectar el cliente
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	public void conectarAConversacion( String nombreAmigo, String ipAmigo, int puertoAmigo ) throws IOException
	{
		Usuario amigo = new Usuario( nombreAmigo, ipAmigo, puertoAmigo );

		Conversacion conv = new Conversacion( this, amigo );
		conversaciones.add( conv );

		ventanaPrincipal.crearVentanaConversacion( conv );
	}

	/**
	 * Elimina de la colección de conversaciones la conversación indicada
	 * 
	 * @param conv La conversación que se va a eliminar - conv != null
	 */
	public void eliminarConversacion( Conversacion conv )
	{
		conversaciones.remove( conv );
	}

	// -----------------------------------------------------------------
	// Puntos de Extensión
	// -----------------------------------------------------------------

	/**
	 * Método para la extensión 1
	 * 
	 * @return respuesta1
	 */
	public String metodo1( )
	{
		return "Respuesta 1";
	}

	/**
	 * Método para la extensión2
	 * 
	 * @return respuesta2
	 */
	public String metodo2( )
	{
		return "Respuesta 2";
	}

}