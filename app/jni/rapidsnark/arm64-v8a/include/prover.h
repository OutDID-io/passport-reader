#ifndef PROVER_HPP
#define PROVER_HPP

#ifdef __cplusplus
extern "C" {
#endif

//Error codes returned by the functions.
#define PROVER_OK     0x0
#define PROVER_ERROR  0x1


/**
 * @return error code:
 *         PROVER_OK - in case of success.
 *         PROVER_ERROR - in case of an error.
 */
extern
int groth16_prover(const void *zkey_buffer,   unsigned long zkey_size,
               const void *wtns_buffer,   unsigned long wtns_size,
               char       *proof_buffer,  unsigned long proof_size,
               char       *public_buffer, unsigned long public_size,
               char       *error_msg,     unsigned long error_msg_maxsize);

#ifdef __cplusplus
}
#endif


#endif // PROVER_HPP
