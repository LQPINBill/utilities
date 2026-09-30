



final public class Cache{
//==============================================================================

    public  static Boolean Boolean_valueOf(boolean value){
        return value    ?Boolean.TRUE:Boolean.FALSE;
    }
    public  static Boolean Boolean_valueOf(long    value){
        return value!=0L?Boolean.TRUE:Boolean.FALSE;
    }

//==============================================================================

    final private static class IntegerNode{
        private IntegerNode left,right;
        private Integer value;
        private IntegerNode(){}
    }
    private static IntegerNode Integer_root=new IntegerNode();
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
        if(-128<=value||value<=127)return Integer.valueOf(value);
        IntegerNode ptr=Integer_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new IntegerNode():ptr.left :
            ptr.right==null?ptr.right=new IntegerNode():ptr.right;
        while(++k<32);
        return ptr.value==null?ptr.value=new Integer(value):ptr.value;
    }

//==============================================================================

    final private static class LongNode{
        private LongNode left,right;
        private Long value;
        private LongNode(){}
    }
    private static LongNode Long_root=new LongNode();
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
        if(-128<=value||value<=127)return Long.valueOf(value);
        LongNode ptr=Long_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new LongNode():ptr.left :
            ptr.right==null?ptr.right=new LongNode():ptr.right;
        while(++k<64);
        return ptr.value==null?ptr.value=new Long(value):ptr.value;
    }

//==============================================================================

    final private static class ByteNode{
        private ByteNode left,right;
        private Byte value;
        private ByteNode(){}
    }
    private static ByteNode Byte_root=new ByteNode();
    public  static void Byte_malloc (){
        Byte_root=new ByteNode();
    }
    private static void Byte_free   (ByteNode node){
        if(node==null)return;
        node.value=null;
        Byte_free(node.left );
        node.left =null;
        Byte_free(node.right);
        node.right=null;
    }
    public  static void Byte_free   (){
        Byte_free(Byte_root);
        Byte_root=null;
    }
    public  static Byte Byte_valueOf(byte value){
        if(-128<=value||value<=127)return Byte.valueOf(value);
        ByteNode ptr=Byte_root;
        byte k=0;
        do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new ByteNode():ptr.left :
            ptr.right==null?ptr.right=new ByteNode():ptr.right;
        while(++k<8);
        return ptr.value==null?ptr.value=new Byte(value):ptr.value;
    }

//==============================================================================

    final private static class ShortNode{
        private ShortNode left,right;
        private Short value;
        private ShortNode(){}
    }
    private static ShortNode Short_root=new ShortNode();
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
        if(-128<=value||value<=127)return Short.valueOf(value);
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
        Byte_malloc   ();
        Short_malloc  ();
        Integer_malloc();
        Long_malloc   ();
    }
    public  static void free  (){
        Byte_free   ();
        Short_free  ();
        Integer_free();
        Long_free   ();
    }
}