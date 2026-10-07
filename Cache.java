import java.util.concurrent.locks.ReentrantReadWriteLock;

final public class Cache{
    private Cache(){}
    final private static ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock(false);
    public static Byte    getByte   (byte    value){return Byte.valueOf(value);}
    public static Boolean getBoolean(boolean value){return value           ?Boolean.TRUE:Boolean.FALSE;}
    public static Boolean getBoolean(byte    value){return value!=(byte )0 ?Boolean.TRUE:Boolean.FALSE;}
    public static Boolean getBoolean(short   value){return value!=(short)0 ?Boolean.TRUE:Boolean.FALSE;}
    public static Boolean getBoolean(int     value){return value!=       0 ?Boolean.TRUE:Boolean.FALSE;}
    public static Boolean getBoolean(long    value){return value!=       0L?Boolean.TRUE:Boolean.FALSE;}
    public static Boolean getBoolean(Object  value){return value!=     null?Boolean.TRUE:Boolean.FALSE;}
//==============================================================================
    final private static class IntegerNode{
        private IntegerNode left,right;
        private Integer value;
        private IntegerNode(){}
    }
    private static IntegerNode rootInteger;
    public  static void mallocIntegers(){
        rwLock.writeLock().  lock();
        if(rootInteger==null)rootInteger=new IntegerNode();
        rwLock.writeLock().unlock();
    }
    private static void   freeIntegers(IntegerNode node){
        if(node==null)return;
        node.value=null;
        freeIntegers(node.left );
        node.left =null;
        freeIntegers(node.right);
        node.right=null;
    }
    public  static void   freeIntegers(){
        rwLock.writeLock().  lock();
        freeIntegers(rootInteger);
        rootInteger=null;
        rwLock.writeLock().unlock();
    }
    public  static Integer getInteger (int value){
        rwLock.writeLock().  lock();
        IntegerNode ptr=rootInteger;
        byte k=0;

        /*do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new IntegerNode():ptr.left :
            ptr.right==null?ptr.right=new IntegerNode():ptr.right;
        while(++k<32);*/

        boolean allKnownRoute;
        do ptr=(value&(1<<k++))==0?
            (allKnownRoute=ptr.left !=null)?ptr.left :(ptr.left =new IntegerNode()):
            (allKnownRoute=ptr.right!=null)?ptr.right:(ptr.right=new IntegerNode());
        while(k<32&&allKnownRoute);
        while(k<32)
            ptr=(value&(1<<k++))==0?
                (ptr.left =new IntegerNode()):
                (ptr.right=new IntegerNode());

        Integer ret=ptr.value==null?ptr.value=Integer.valueOf(value):ptr.value;
        rwLock.writeLock().unlock();
        return ret;
    }
    public  static void  cacheIntegers(int min,int max){
        if(min<=max){
            if(max==Integer.MAX_VALUE)
                getInteger(max--);
            while(min<=max)
                getInteger(min++);
        }
    }
//==============================================================================
    final private static class LongNode{
        private LongNode left,right;
        private Long value;
        private LongNode(){}
    }
    private static LongNode rootLong;
    public  static void mallocLongs(){
        rwLock.writeLock().  lock();
        if(rootLong==null)rootLong=new LongNode();
        rwLock.writeLock().unlock();
    }
    private static void   freeLongs(LongNode node){
        if(node==null)return;
        node.value=null;
        freeLongs(node.left );
        node.left =null;
        freeLongs(node.right);
        node.right=null;
    }
    public  static void   freeLongs(){
        rwLock.writeLock().  lock();
        freeLongs(rootLong);
        rootLong=null;
        rwLock.writeLock().unlock();
    }
    public  static Long    getLong (long value){
        if(-128L<=value&&value<=127L)return Long.valueOf(value);
        rwLock.writeLock().  lock();
        LongNode ptr=rootLong;
        byte k=0;

        /*do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new LongNode():ptr.left :
            ptr.right==null?ptr.right=new LongNode():ptr.right;
        while(++k<64);*/

        boolean allKnownRoute;
        do ptr=(value&(1<<k++))==0?
            (allKnownRoute=ptr.left !=null)?ptr.left :(ptr.left =new LongNode()):
            (allKnownRoute=ptr.right!=null)?ptr.right:(ptr.right=new LongNode());
        while(k<64&&allKnownRoute);
        while(k<64)
            ptr=(value&(1<<k++))==0?
                (ptr.left =new LongNode()):
                (ptr.right=new LongNode());

        Long ret=ptr.value==null?ptr.value=new Long(value):ptr.value;
        rwLock.writeLock().unlock();
        return ret;
    }
    public  static void  cacheLongs(long min,long max){
        if(min<=max){
            if(max==Long.MAX_VALUE)
                getLong(max--);
            while(min<=max)
                getLong(min++);
        }
    }
//==============================================================================
    final private static class ShortNode{
        private ShortNode left,right;
        private Short value;
        private ShortNode(){}
    }
    private static ShortNode rootShort;
    public  static void mallocShorts(){
        rwLock.writeLock().  lock();
        if(rootShort==null)rootShort=new ShortNode();
        rwLock.writeLock().unlock();
    }
    private static void   freeShorts(ShortNode node){
        if(node==null)return;
        node.value=null;
        freeShorts(node.left );
        node.left =null;
        freeShorts(node.right);
        node.right=null;
    }
    public  static void   freeShorts(){
        rwLock.writeLock().  lock();
        freeShorts(rootShort);
        rootShort=null;
        rwLock.writeLock().unlock();
    }
    public  static Short   getShort (short value){
        if((short)-128<=value&&value<=(short)127)return Short.valueOf(value);
        rwLock.writeLock().  lock();
        ShortNode ptr=rootShort;
        byte k=0;

        /*do ptr=(value&(1<<k))==0?
            ptr.left ==null?ptr.left =new ShortNode():ptr.left :
            ptr.right==null?ptr.right=new ShortNode():ptr.right;
        while(++k<16);*/

        boolean allKnownRoute;
        do ptr=(value&(1<<k++))==0?
            (allKnownRoute=ptr.left !=null)?ptr.left :(ptr.left =new ShortNode()):
            (allKnownRoute=ptr.right!=null)?ptr.right:(ptr.right=new ShortNode());
        while(k<16&&allKnownRoute);
        while(k<16)
            ptr=(value&(1<<k++))==0?
                (ptr.left =new ShortNode()):
                (ptr.right=new ShortNode());

        Short ret=ptr.value==null?ptr.value=new Short(value):ptr.value;
        rwLock.writeLock().unlock();
        return ret;
    }
    public  static void  cacheShorts(short min,short max){
        if(min<=max){
            if(max==Short.MAX_VALUE)
                getShort(max--);
            while(min<=max)
                getShort(min++);
        }
    }
//==============================================================================








//==============================================================================
    public static void malloc(){
        mallocShorts  ();
        mallocIntegers();
        mallocLongs   ();
    }
    public static void free  (){
        freeShorts  ();
        freeIntegers();
        freeLongs   ();
    }
}