package edu.jabs.messenger.servidor;

/**
 * Esta clase representa a un usuario del messenger. <br>
 * <b>inv: </b> <br>
 * estado = ONLINE o estado = OFFLINE <br>
 * estado = OFFLINE => direccionIp = null <br>
 * estado = ONLINE => direccionIp != null <br>
 * nombre != null y nombre != ""
 */
public class Usuario implements Comparable
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Indica que el usuario está conectado
	 */
	public static final int ONLINE = 1;

	/**
	 * Indica que el usuario está desconectado
	 */
	public static final int OFFLINE = 0;

	/**
	 * Indica que el usuario está conectado
	 */
	public static final String STR_ONLINE = "ONLINE";

	/**
	 * Indica que el usuario está desconectado
	 */
	public static final String STR_OFFLINE = "OFFLINE";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Estado del usuario
	 */
	private int estado;

	/**
	 * Nombre del usuario
	 */
	private String nombre;

	/**
	 * Si está conectado, dirección ip
	 */
	private String direccionIp;

	/**
	 * El puerto por el que el cliente espera las conexiones de otros clientes
	 */
	private int puerto;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Crea un nuevo usuario cuyo estado es OFFLINE
	 * 
	 * @param nombreUsuario Nombre del usuario - nombreUsuario != null &&
	 *            nombreUsuario != ""
	 */
	public Usuario( String nombreUsuario )
	{
		nombre = nombreUsuario;
		estado = OFFLINE;
		direccionIp = null;
		puerto = 0;

		verificarInvariante( );
	}

	/**
	 * Crea un nuevo usuario cuyo estado es ONLINE
	 * 
	 * @param nombreUsuario Nombre del usuario - nombreUsuario != null &&
	 *            nombreUsuario != ""
	 * @param ipUsuario Direccion ip actual del usuario - ipUsuario != null &&
	 *            ipUsuario != ""
	 * @param puertoUsuario El puerto a través del cual se debe realizar la conexión
	 *            con el usuario
	 */
	public Usuario( String nombreUsuario, String ipUsuario, int puertoUsuario )
	{
		nombre = nombreUsuario;
		estado = ONLINE;
		direccionIp = ipUsuario;
		puerto = puertoUsuario;

		verificarInvariante( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Retorna el estado del usuario
	 * 
	 * @return estado
	 */
	public int darEstado( )
	{
		return estado;
	}

	/**
	 * Retorna el nombre del usuario
	 * 
	 * @return nombre
	 */
	public String darNombre( )
	{
		return nombre;
	}

	/**
	 * Retorna la dirección ip actual del usuario
	 * 
	 * @return direccionIp
	 */
	public String darDireccionIp( )
	{
		return direccionIp;
	}

	/**
	 * Retorna el puerto a través del cual se debe conectar al usuario
	 * 
	 * @return puerto
	 */
	public int darPuerto( )
	{
		return puerto;
	}

	/**
	 * Cambia el estado del usuario a ONLINE
	 * 
	 * @param ipUsuario Direccion ip actual del usuario - ipUsuario != null &&
	 *            ipUsuario != ""
	 * @param puertoUsuario El puerto a través del cual se debe realizar la conexión
	 *            con el usuario
	 */
	public void cambiarEstado( String ipUsuario, int puertoUsuario )
	{
		estado = ONLINE;
		direccionIp = ipUsuario;
		puerto = puertoUsuario;

		verificarInvariante( );
	}

	/**
	 * Retorna una cadena que identifica al usuario
	 * 
	 * @return cadena
	 */
	public String toString( )
	{
		if ( estado == ONLINE )
		{
			return nombre;
		}
		else
		{
			return nombre + " (offline) ";
		}
	}

	/**
	 * Compara a este usuario con otro. El criterio para decidir si un usuario es
	 * menor que otro es el órden lexicográfico de sus nombres.
	 * 
	 * @param otro Es el usuario con el que se va a comparar - otro != null && otro
	 *            es un Usuario
	 * @return Retorna -1 si this < otro, 0 si this = otro y 1 si this > otro <br>
	 */
	public int compareTo( Object otro )
	{
		Usuario otroUsuario = (Usuario) otro;

		return darNombre( ).compareToIgnoreCase( otroUsuario.darNombre( ) );
	}

	/**
	 * Verifica el invariante de la clase <br>
	 * <b>inv: </b> <br>
	 * estado = ONLINE o estado = OFFLINE <br>
	 * estado = OFFLINE => direccionIp = null <br>
	 * estado = ONLINE => direccionIp != null <br>
	 * nombre != null y nombre != ""
	 */
	public void verificarInvariante( )
	{
		assert (estado == ONLINE || estado == OFFLINE) : "El estado es incorrecto";
		if ( estado == ONLINE )
			assert (direccionIp != null) : "Si está ONLINE debe tener una direccion ip";

		else if ( estado == OFFLINE )
			assert (direccionIp == null) : "Si está OFFLINE no debe tener una direccion ip";

		assert (nombre != null) : "El nombre del usuario no puede ser null";

		assert (!nombre.equals( "" )) : "El nombre del usuario no puede estar vacío";
	}

}