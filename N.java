

final public class N implements Cloneable,Comparable<N>{

    long[]dgts;
    int deg;
    N(){}

    final static long BASE=32;
    public N(long v){
        (dgts=new long[63])[0]=v-BASE*(v/=BASE);
        while(0L<v)dgts[++deg]=v-BASE*(v/=BASE);
    }

    final static N
        ZERO=new N(),
        ONE =new N();
    static{
        ZERO.dgts=new long[]{0};
        ONE .dgts=new long[]{1};
        ZERO.deg=ONE.deg=0;
    }

    public N(N v){
        int l=(deg=v.deg)+1;
        System.arraycopy(v.dgts,0,dgts=new long[l],0,l);
    }
    @Override public N clone(){
        //return new N(this);
        N o=new N();
        int l=(o.deg=deg)+1;
        System.arraycopy(dgts,0,o.dgts=new long[l],0,l);
        return o;
    }

    public static boolean equiv(N x,N y){

        int p=x.deg;
        if(p!=y.deg)return false;
        do if(x.dgts[p]!=y.dgts[p])return false;while(0<=--p);
        return true;
        /*int p=0;
        do if(x.dgts[p]!=y.dgts[p])return false;while(++p<=x.deg&&p<=y.deg);
        return x.deg==y.deg;*/

    }
    @Override public boolean equals(Object o){
        return o instanceof N y?equiv(this,y):false;
    }
    public static byte    order(N x,N y){

        int p=x.deg;
        if(p    <y.deg)return-1;
        if(y.deg<p    )return 1;
        do{ if(x.dgts[p]<y.dgts[p])return-1;
            if(y.dgts[p]<x.dgts[p])return 1;
        }while(0<=--p);
        /*int p=0;
        long t=0L;
        do if(x.dgts[p]!=y.dgts[p])t=x.dgts[p]=y.dgts[p];while(++p<=x.deg&&p<=y.deg);
        if(x.deg<y.deg)return-1;
        if(y.deg<x.deg)return 1;
        if(t <0L)return-1;
        if(0L<t )return 1;*/

        return 0;
    }
    @Override public int compareTo(N y){
        return order(this,y);
    }

    public static N add    (N x,N y){
        int p=0;
        long t=0L;

        /*N z=new N();
        long[]dgts=z.dgts=new long[(x.deg<y.deg?y.deg:x.deg)+2];
        do                dgts[p]=(t+=x.dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=x.deg&&p<=y.deg);
        for(;p<=x.deg;++p)dgts[p]=(t+=x.dgts[p]          )-BASE*(t/=BASE);
        for(;p<=y.deg;++p)dgts[p]=(t+=          y.dgts[p])-BASE*(t/=BASE);
        //z.deg=(dgts[p]=t)!=0L?p:p-1;
        if(t!=0L)dgts[z.deg=p]=1L;else z.deg=p-1;*/

        N z;
        if(y.deg<x.deg){
            z=x;
            x=y;
            y=z;
        }
        long[]dgts=(z=new N()).dgts=new long[y.deg+2];
        do                dgts[p]=(t+=x.dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=x.deg);
        for(;p<=y.deg;++p)dgts[p]=(t+=          y.dgts[p])-BASE*(t/=BASE);
        //z.deg=(dgts[p]=t)!=0L?p:y.deg;
        if(t!=0L)dgts[z.deg=p]=1L;else z.deg=y.deg;

        return z;
    }
    public     void add    (    N y){
        int p=0;
        long t=0L;

        long[]dgtsNew=new long[(deg<y.deg?y.deg:deg)+2];
        do                dgtsNew[p]=(t+=dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=deg&&p<=y.deg);
        for(;p<=  deg;++p)dgtsNew[p]=(t+=dgts[p]          )-BASE*(t/=BASE);
        for(;p<=y.deg;++p)dgtsNew[p]=(t+=        y.dgts[p])-BASE*(t/=BASE);
        //deg=((dgts=dgtsNew)[p]=t)!=0L?p:p-1;
        if(t!=0L)dgtsNew[deg=p]=1L;else deg=p-1;dgts=dgtsNew;

         /*if(deg<y.deg){
            long[]dgtsNew=new long[y.deg+2];
            do                dgtsNew[p]=(t+=dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=deg);
            for(;p<=y.deg;++p)dgtsNew[p]=(t+=        y.dgts[p])-BASE*(t/=BASE);
            //deg=((dgts=dgtsNew)[p]=t)!=0L?p:y.deg;
            if(t!=0L)dgtsNew[deg=p]=1L;else deg=y.deg;dgts=dgtsNew;
            return;
        }if(dgts.length<deg+2){
            long[]dgtsNew=new long[deg+2];
            do              dgtsNew[p]=(t+=dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=y.deg);
            for(;p<=deg;++p)dgtsNew[p]=(t+=dgts[p]          )-BASE*(t/=BASE);
            //deg=((dgts=dgtsNew)[p]=t)!=0L?p:deg;
            if(t!=0L)dgtsNew[deg=p]=1L;dgts=dgtsNew;
            return;
        }do             dgts[p]=(t+=dgts[p]+y.dgts[p])-BASE*(t/=BASE);while(++p<=y.deg);
        for(;p<=deg;++p)dgts[p]=(t+=dgts[p]          )-BASE*(t/=BASE);
        //deg=(dgts[p]=t)!=0L?p:deg;
        if(t!=0L)dgts[deg=p]=1L;*/

    }
    final public static long MAX_DGT=BASE-1L;
    public     void add    (       ){
        int p=0;
        if(dgts.length<deg+2){

            long[]dgtsNew=new long[deg+2];
            do{ if(dgts[p]<MAX_DGT){
                    System.arraycopy(dgts,p,dgts=dgtsNew,p,deg+1-p);
                    ++dgts[p];
                    return;
                }dgts[p]=0L;
            }while(++p<=deg);
            (dgts=dgtsNew)[deg=p]=1L;
            return;
            //System.arraycopy(dgts,0,dgts=new long[deg+2],0,deg+1);

        }do{if(dgts[p]<MAX_DGT){
                ++dgts[p];
                return;
            }dgts[p]=0L;
        }while(++p<=deg);
        dgts[deg=p]=1L;
    }
    public     void addBase(       ){
        if(dgts.length==1){
            long t=dgts[0];
            (dgts=new long[2])[0]=t;
            dgts[deg=1]=1L;
            return;
        }
        int p=1;
        if(dgts.length<deg+2){

            long[]dgtsNew=new long[deg+2];
            do{ if(dgts[p]<MAX_DGT){
                    long t=dgts[0];
                    System.arraycopy(dgts,p,dgts=dgtsNew,p,deg+1-p);
                    ++dgts[p];
                    dgts[0]=t;
                    return;
                }dgtsNew[p]=0L;
            }while(++p<=deg);
            (dgts=dgtsNew)[deg=p]=1L;
            return;
            //System.arraycopy(dgts,0,dgts=new long[deg+2],0,deg+1);

        }do{if(dgts[p]<MAX_DGT){
                ++dgts[p];
                return;
            }dgts[p]=0L;
        }while(++p<=deg);
        dgts[deg=p]=1L;
    }

    public static N minus    (N x,N y){
        int p=0,deg=0;
        long t=0L;
        N z=new N();
        long[]dgts=z.dgts=new long[x.deg+1];
        do                if((dgts[p]=(t+=x.dgts[p]-y.dgts[p])-BASE*(t=(t+BASE)/BASE-1L))!=0L)deg=p;while(++p<=y.deg);
        for(;p<=x.deg;++p)if((dgts[p]=(t+=x.dgts[p]          )-BASE*(t=(t+BASE)/BASE-1L))!=0L)deg=p;
        z.deg=deg;
        return z;
    }
    public     void minus    (    N y){
        int p=0,degNew=0;
        long t=0L;
        do              if((dgts[p]=(t+=dgts[p]-y.dgts[p])-BASE*(t=(t+BASE)/BASE-1L))!=0L)degNew=p;while(++p<=y.deg);
        for(;p<=deg;++p)if((dgts[p]=(t+=dgts[p]          )-BASE*(t=(t+BASE)/BASE-1L))!=0L)degNew=p;
        deg=degNew;
    }
    public     void minus    (       ){
        int p=0;
        do{ if(0L<dgts[p]){
                if(0L==--dgts[p]&&p==deg&&0<deg)
                    --deg;
                return;
            }dgts[p]=MAX_DGT;
        }while(++p<=deg);
        throw new RuntimeException();
    }
    public     void minusBase(       ){
        int p=1;
        do{ if(0L<dgts[p]){
                if(0L==--dgts[p]&&p==deg&&0<deg)
                    --deg;
                return;
            }dgts[p]=MAX_DGT;
        }while(++p<=deg);
        throw new RuntimeException();
    }

    public     void multBase(       ){
        if(0<deg||0L<dgts[0])System.arraycopy(dgts,0,dgts=new long[deg+2],1,++deg);
    }
    public     void mult    ( long y){
        if(0L<y){
            int p=0;
            long t=0L;
            long[]dgtsNew=new long[deg+2];
            do dgtsNew[p]=(t+=dgts[p]*y)-BASE*(t/=BASE);while(++p<=deg);

            //deg=((dgts=dgtsNew)[p]=t)!=0L?p:p-1;
            if(t!=0L)dgtsNew[deg=p]=t;else deg=p-1;dgts=dgtsNew;

            return;
        }
        (dgts=new long[2])[deg=0]=0L;
    }
    public static N mult    (N x,N y){
        N z;
        if(y.deg<x.deg){
            z=x;
            x=y;
            y=z;
        }
        int Σdeg=x.deg+y.deg,p=0,deg=0,k;
        long[]dgts=(z=new N()).dgts=new long[Σdeg+2];
        long t=0L;
        for(;p<x.deg;++p){for(k=0      ;k<=p    ;++k)t+=y.dgts[k]*x.dgts[p-k];if((dgts[p]=t-BASE*(t/=BASE))!=0L)deg=p;}
        for(;p<y.deg;++p){for(k=p-x.deg;k<=p    ;++k)t+=y.dgts[k]*x.dgts[p-k];if((dgts[p]=t-BASE*(t/=BASE))!=0L)deg=p;}
        for(;p<=Σdeg;++p){for(k=p-x.deg;k<=y.deg;++k)t+=y.dgts[k]*x.dgts[p-k];if((dgts[p]=t-BASE*(t/=BASE))!=0L)deg=p;}
        //z.deg=(dgts[p]=t)!=0L?p:deg;
        if(t!=0L)dgts[z.deg=p]=t;else z.deg=deg;
        return z;
    }
    public     void mult    (    N y){
        N z=mult (this,y);
        dgts=z.dgts;
        deg =z.deg ;
    }

    public static N div(N m,N n){
        byte order=order(m,n);
        if(order==0)return new N(ONE);
        N q=new N(ZERO);
        if(order<0){
            q.deg=~q.deg;
            return q;
        }
        N Δq=new N(),nq=new N(ZERO),nΔq;
        int Δq_deg=m.deg-n.deg;
        long[]Δq_dgts=Δq.dgts=new long[Δq_deg+1];
        for(;0<=Δq_deg;--Δq_deg){
            Δq_dgts[Δq.deg=Δq_deg]=BASE;
            order=order(nq,m);
            while(1L<(Δq_dgts[Δq_deg]>>=1)){
                switch(order){
                     case-1 ->{
                         q.add  (       Δq );
                        nq.add  (mult(n,Δq));
                    }case 1 ->{
                         q.minus(       Δq );
                        nq.minus(mult(n,Δq));
                    }default->{
                        return q;
                    }
                }order=order(nq,m);
            }
            Δq_dgts[Δq_deg]=1L;
            nΔq=mult(n,Δq);
             while(order<0    ){
                 q.add  ( Δq);
                nq.add  (nΔq);
                order=order(nq,m);
            }while(0    <order){
                 q.minus( Δq);
                nq.minus(nΔq);
                order=order(nq,m);
            }
        }
        if(order!=0)q.deg=~q.deg;
        return q;
    }
    public static boolean divisibility(N q){
        if(q.deg<0){
            q.deg=~q.deg;
            return false;
        }
        return true;
    }
    public  boolean div(    N n){
        N z=div  (this,n);
        dgts=z.dgts;
        deg =z.deg ;
        return divisibility(this);
    }

    public static N factorial(N n){
        N m=new N(ONE),k=new N(ONE);
        while(order(k,n)<=0){
            m.mult(k);
            k.add();
        }return m;
    }

    /**
     * p is not 1 for any n. If n is not 0, p can not be 0
     * 
     * @param p
     * @param n
     * @return {@code max{k in N | n/(p^k) in Z}}
     */
    public static N v(N p,N n){
        if(equiv(p,ZERO))return new N(ZERO);
        N k=new N(ONE),
         pk=new N(p);
        while(divisibility(div(n,pk))){
             k.add();
            pk.mult(p);
        }k.minus();
        pk.div(p);
        return k;
    }

    long remainder(N n){
        byte order=order(this,n);
        if(order<0){
            long v=dgts[0],b=1L;
            for(int p=1;p<=deg;++p)v+=dgts[p]*(b*=BASE);
            return v;
        }
        if(order==0)return 0L;
        N t=div(this,n);
        if(divisibility(t))return 0L;
        long v=dgts[0],b=1L;
        for(int p=1;p<=deg;++p)v+=dgts[p]*(b*=BASE);
        return v;
    }
    final static N
        pow2$63=add(ONE,new N(Long   .MAX_VALUE)),
        pow2$31=add(ONE,new N(Integer.MAX_VALUE)),
        pow2$15=add(ONE,new N(Short  .MAX_VALUE)),
        pow2$7 =add(ONE,new N(Byte   .MAX_VALUE)),
        pow2$32=add(pow2$31,pow2$31);
    public long    longValue(){
        return(long )remainder(pow2$63);
    }
    public int      intValue(){
        return(int  )remainder(pow2$31);
    }
    public short  shortValue(){
        return(short)remainder(pow2$15);
    }
    public byte    byteValue(){
        return(byte )remainder(pow2$7);
    }
    public boolean boolValue(){
        return(dgts[0]&1L)==1L;
    }
    @Override public int hashCode(){
        return(int  )remainder(pow2$32);
    }

    @Override public String toString(){
        String str=Long.toString(dgts[0],32);
        for(int k=1;k<=deg;++k)str+=','+Long.toString(dgts[k],32);
        return str;
    }
    public N(String str){
        dgts=new long[str.length()];
        int begin=0,i=1;
        while(i<dgts.length)
            if(str.charAt(i)==','){
                dgts[deg]=Long.parseLong(str.substring(begin,i),32);
                begin=++i;
                ++deg;
            }else++i;
        dgts[deg]=Long.parseLong(str.substring(begin,i),32);
    }
}