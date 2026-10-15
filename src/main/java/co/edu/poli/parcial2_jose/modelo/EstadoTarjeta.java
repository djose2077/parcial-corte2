
package co.edu.poli.parcial2_jose.modelo;

/**
 * Estados permitidos para controlar el uso de una tarjeta.
 */
public enum EstadoTarjeta {
    /** La tarjeta puede utilizarse. */
    ACTIVA,
    /** El uso de la tarjeta está temporalmente restringido. */
    BLOQUEADA,
    /** La tarjeta ha sido cancelada y no debe utilizarse. */
    CANCELADA
}
