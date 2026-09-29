
public class BitArray64{
    final public static long MAX_BITS=64L*Integer.MAX_VALUE;
    private long[]segments;
    private long bits;
    public BitArray64(long bits){
        if(bits<0L|MAX_BITS<bits)throw new IllegalArgumentException();
        segments=new long[(int)(((this.bits=bits)+63L)/64L)];
    }
    public long bits(){
        return bits;
    }

    final private static long[]bases=new long[64];
    static{
        long basis=1L;
        for(int k=0;k<64;++k){
            bases[k]=basis;
            basis=basis<<1;
        }
    }
    public boolean get(long index){
        if(index<0L|bits<=index)throw new IndexOutOfBoundsException();
        long s=index/64L;
        return(segments[(int)s]&bases[(int)(index-64L*s)])!=0L;
    }
    public void    set(long index,boolean bit){
        if(index<0L|bits<=index)throw new IndexOutOfBoundsException();
        int s=(int)(index/64L);
        long f=bases[(int)(index-64L*(long)s)];
        segments[s]=(segments[s]&~f)|(bit?f:0L);
    }

    final private static long identity=bases[0]&bases[1];
    /*final private static long[]covers=new long[64];
    static{
        long cover=0L;
        for(int k=0;k<64;++k){
            covers[k]=cover|=bases[k];
        }
    }*/
    public void realloc(long bits){
        if(bits<0L|MAX_BITS<bits)throw new IllegalArgumentException();
        if(bits<=this.bits){
            this.bits=bits;return;
        }
        int segments_required=(int)((bits+63L)/64L);
        int ptr=(int)(this.bits/64L);
        int r=(int)(this.bits-64L*(long)ptr);
        if(segments_required<=segments.length){
            long f=identity;
            while(r!=0)f|=bases[--r];
            segments[ptr]&=f;
            while(++ptr!=segments_required)segments[ptr]=0L;
            this.bits=bits;return;
        }
        long[]segments_new=new long[segments_required];
        if(r!=0){
            long f=identity;
            do f|=bases[--r];while(r!=0);
            segments_new[ptr]=segments[ptr]&f;
        }
        while(ptr>0){
            --ptr;
            segments_new[ptr]=segments[ptr];
        }
        this.bits=bits;segments=segments_new;
    }

    public void trimToBits(){
        int q=(int)((bits+63L)/64L);
        if(q!=segments.length){
            long[]segments_new=new long[q];
            while(q>0){
                --q;
                segments_new[q]=segments[q];
            }
            segments=segments_new;
        }
    }

    @Override
    public String toString(){
        return toString((byte)0);
    }
    public String toString(byte s){
        if(s<0|63<s)throw new IllegalArgumentException();
        long begin=Integer.MAX_VALUE*(long)s;
        long end=begin+Integer.MAX_VALUE;
        if(bits<end)end=bits;
        if(begin<end){
            StringBuilder str=new StringBuilder();
            while(begin<end)str.append(get(begin++)?'1':'0');
            return str.toString();
        }
        return null;
    }
}
