

final public class Cache{

    public  static Boolean Boolean_valueOf(boolean value){
        return value?Boolean.TRUE:Boolean.FALSE;
    }
    public  static Boolean Boolean_valueOf(byte    value){
        return Boolean_valueOf(value!=(byte) 0 );
    }
    public  static Boolean Boolean_valueOf(short   value){
        return Boolean_valueOf(value!=(short)0 );
    }
    public  static Boolean Boolean_valueOf(int     value){
        return Boolean_valueOf(value!=       0 );
    }
    public  static Boolean Boolean_valueOf(long    value){
        return Boolean_valueOf(value!=       0L);
    }
    public  static Boolean Boolean_valueOf(Object  value){
        return Boolean_valueOf(value!=     null);
    }

//==============================================================================

    final private static class IntegerNode{
        private IntegerNode left,right;
        private Integer value;
        private IntegerNode(){}
    }
    private static IntegerNode Integer_root;
    public  static void    Integer_malloc (){
        Integer_root=new IntegerNode();
    }
    private static void    Integer_free   (IntegerNode node){
        if(node==null)return;
        node.value=null;
        Integer_free(node.left );
        node.left =null;
        Integer_free(node.right);
        node.right=null;
    }
    public  static void    Integer_free   (){
        Integer_free(Integer_root);
        Integer_root=null;
    }
    public  static Integer Integer_valueOf(int value){
        IntegerNode ptr=Integer_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new IntegerNode():ptr.left :
            ptr.right==null?ptr.right=new IntegerNode():ptr.right;
        while(++k<32);
        return ptr.value==null?ptr.value=Integer.valueOf(value):ptr.value;
    }

//==============================================================================

    final private static class LongNode{
        private LongNode left,right;
        private Long value;
        private LongNode(){}
    }
    private static LongNode Long_root;
    public  static void Long_malloc (){
        Long_root=new LongNode();
    }
    private static void Long_free   (LongNode node){
        if(node==null)return;
        node.value=null;
        Long_free(node.left );
        node.left =null;
        Long_free(node.right);
        node.right=null;
    }
    public  static void Long_free   (){
        Long_free(Long_root);
        Long_root=null;
    }
    public  static Long Long_valueOf(long value){
        if(-128L<=value||value<=127L)return Long.valueOf(value);
        LongNode ptr=Long_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new LongNode():ptr.left :
            ptr.right==null?ptr.right=new LongNode():ptr.right;
        while(++k<64);
        return ptr.value==null?ptr.value=new Long(value):ptr.value;
    }

//==============================================================================

    public  static Byte Byte_valueOf(byte value){
        return Byte.valueOf(value);
    }

//==============================================================================

    final private static class ShortNode{
        private ShortNode left,right;
        private Short value;
        private ShortNode(){}
    }
    private static ShortNode Short_root;
    public  static void  Short_malloc (){
        Short_root=new ShortNode();
    }
    private static void  Short_free   (ShortNode node){
        if(node==null)return;
        node.value=null;
        Short_free(node.left );
        node.left =null;
        Short_free(node.right);
        node.right=null;
    }
    public  static void  Short_free   (){
        Short_free(Short_root);
        Short_root=null;
    }
    public  static Short Short_valueOf(short value){
        if((short)-128<=value||value<=(short)127)return Short.valueOf(value);
        ShortNode ptr=Short_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new ShortNode():ptr.left :
            ptr.right==null?ptr.right=new ShortNode():ptr.right;
        while(++k<16);
        return ptr.value==null?ptr.value=new Short(value):ptr.value;
    }

//==============================================================================








//==============================================================================

    public  static void malloc(){
        Short_malloc  ();
        Integer_malloc();
        Long_malloc   ();
    }
    public  static void free  (){
        Short_free  ();
        Integer_free();
        Long_free   ();
    }
    private Cache(){}
}