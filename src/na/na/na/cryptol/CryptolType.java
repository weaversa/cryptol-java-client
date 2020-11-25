package na.na.na.cryptol;

/*
  simple types
 
    Bit ~~> boolean --- BIT
    [n] ~~> BigInteger + bit size of n --- WORD
    Z n ~~> BigInteger + modulus of n --- RESIDUE
    Integer ~~> BigInteger + modulus of 0 --- RESIDUE
 
  recursive types
 
    [n_0]a ~~> Vector<CryptolValue> of length n_0 --- SEQUENCE
    (t_0, t_1, ... t_k) ~~> SortedMap<int><CryptolValue> from int 0..k to t_0, t_1, ... t_k respectively --- TUPLE
    {l_0 = t_0, l_1 = t_1, ... l_k = t_k} ~~> Map<String><CryptolValue> --- RECORD
 
   technically WORD is a SEQUENCE of BITs, but we make a special case since it is oft used
 */

public enum CryptolType {
    BIT,
    WORD,
    RESIDUE,
    SEQUENCE,
    TUPLE,
    RECORD
}
