package edu.jabs.messenger.servidor;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Esta interfaz define los métodos que debe ofrecer el administrador de amigos
 */
public interface IAdministradorAmigos
{
	/**
	 * Conecta el administrador a la base de datos
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas realizando la
	 *             operación
	 * @throws Exception Se lanza esta excepción si hay problemas con los drivers
	 */
	void conectarABD( ) throws SQLException, Exception;

	/**
	 * Desconecta el administrador de la base de datos y la detiene
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas realizando la
	 *             operación
	 */
	void desconectarBD( ) throws SQLException;

	/**
	 * Crea las tablas necesarias para guardar la información de los usuarios y sus
	 * amigos. Si las tablas ya estaban creadas entonces no hace nada.
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas creando las
	 *             tablas
	 */
	void inicializarTablas( ) throws SQLException;

	/**
	 * Este método crea un nuevo usuario en la base de datos <br>
	 * <b>pre: </b>No hay ya un usuario con el mismo nombre en la base de datos
	 * 
	 * @param usuario El nombre del usuario - usuario != null && usuario != ""
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	void crearUsuario( String usuario ) throws SQLException;

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
	void agregarAmigo( String usuario, String amigo ) throws SQLException;

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
	void cambiarEstadoUsuario( String usuario, String estado, String ip, int puerto ) throws SQLException;

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
	Collection darAmigos( String usuario ) throws SQLException;

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
	Collection darPersonasConocen( String nombreUsuario ) throws SQLException;

	/**
	 * Este método consulta el estado actual de un usuario
	 * 
	 * @param nombre El nombre del usuario que se está consultando - nombre != null
	 * @return Retorna un objeto con la información del estado del usuario. Si el
	 *         usuario no existe, retorna null.
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	Usuario darEstadoUsuario( String nombre ) throws SQLException;

	/**
	 * Este método sirve para saber si un usuario existe ya en la base de datos
	 * 
	 * @param usuario El nombre del usuario buscado - usario != null
	 * @return Retorna true si el usuario aparece en la base de datos; retorna false
	 *         en caso contrario
	 * @throws SQLException Se lanza esta excepción si hay problemas en la
	 *             comunicación con la base de datos
	 */
	boolean existeUsuario( String usuario ) throws SQLException;

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
	boolean existeAmigo( String usuario, String amigo ) throws SQLException;
}