package na.na.na;


/*
  simple types
 
    Bit ~~> boolean
    [n] ~~> BigInteger + bitlength of n
    Z n ~~> BigInteger + modulus of n
    Integer ~~> BigInteger + modulus of n
 
  complex types
 
    [n_0]a ~~> Vector<CryptolDatum> of length n_0
    (t_0, t_1, ... t_k) ~~> SortedMap<Integer><CryptolDatum> from int 0..k to t_0, t_1, ... t_k respectively
    {l_0 = t_0, l_1 = t_1, ... l_k = t_k} ~~> Map<String>
 
 */

public enum CryptolType {
    BIT,
    WORD,
    RESIDUE,
    VECTOR,
    TUPLE,
    RECORD
}
