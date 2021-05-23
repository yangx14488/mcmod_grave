package net.atcat.nanzhi.grave.obj;

public class bitmap {
  private int SIZE ;
  private int LENGTH ;
  private byte[] MAP ;
  public bitmap ( int length ) {
    this.LENGTH = length - 1 ;
    this.SIZE = ( this.LENGTH >> 3 ) + 1 ; // 所需比特长度
    MAP = new byte[ this.SIZE ] ;
  } ;
  private static boolean _get ( int a, int i ) {
    return ( ( a >> ( i & 7 ) ) & 1 ) == 1 ;
  } ;
  public boolean get ( int index ) {
    return index >= 0 && index <= this.LENGTH && _get( this.MAP[ index >> 3 ], index );
  } ;
  public bitmap set ( int index, boolean bit ) {
    if ( index >= 0 && index <= this.LENGTH && _get( this.MAP[ index >> 3 ], index ) != bit ) {
      this.MAP[ index >> 3 ] ^= ( 1 << ( index &7 ) ) ;
    }
    return this ;
  } ;
}
