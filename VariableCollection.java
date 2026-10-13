import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final public class VariableCollection{

    final private HashMap<Integer,String>index_name=new HashMap<>();
    final private HashMap<String ,String>name_value=new HashMap<>();

    final private ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock(false);

    final private static int
        MAX_VARIABLE_COUNT      =1<<31,
        MAX_VARIABLE_NAME_LENGTH=(1<<30);
    static{
        if( MAX_VARIABLE_NAME_LENGTH<1||
            MAX_VARIABLE_COUNT      <0||(1<<30)-1<MAX_VARIABLE_COUNT
        )throw new IllegalArgumentException();
    }

    public VariableCollection(){}

    public int variableCount(){
        rwLock.readLock().  lock();

        int t=index_name.size();
        //int t=name_value.size();

        rwLock.readLock().unlock();
        return t;
    }
    public boolean isEmpty(){
        rwLock.readLock().  lock();

        boolean t=index_name.isEmpty();
        //boolena t=name_value.isEmpty();

        rwLock.readLock().unlock();
        return t;
    }
    public void clear(){
        rwLock.writeLock().  lock();

        index_name.clear();
        name_value.clear();
        /*name_value.clear();
        index_name.clear();*/

        rwLock.writeLock().unlock();
    }

    public static void checkValidityOfVariableName(String variableName){
        int k=variableName.length();
        if(k==0||MAX_VARIABLE_NAME_LENGTH<k)throw new IllegalArgumentException();
        char c=variableName.charAt(0);
        if( (   !(  ('a'<=c&&c<='z')||
                    ('A'<=c&&c<='Z')
                )
            )
            &&c!='_'
            &&c!='$'
        )throw new IllegalArgumentException();
        while(k>1){
            c=variableName.charAt(--k);
            if( (   !(  ('a'<=c&&c<='z')||
                        ('A'<=c&&c<='Z')||
                        ('0'<=c&&c<='9')
                    )
                )
                &&c!='_'
                &&c!='$'
            )throw new IllegalArgumentException();
        }
    }
    public void add   (String variableName,String value){
        if(value==null)throw new NullPointerException();
        checkValidityOfVariableName(variableName);
        rwLock.writeLock().lock();

        //int variableCountOld=variableCount();
        int variableCountOld=index_name.size();

        if( variableCountOld==MAX_VARIABLE_COUNT||
            name_value.putIfAbsent(variableName,value)!=null
        ){  rwLock.writeLock().unlock();
            throw new RuntimeException();
        }
        index_name.put(Integer.valueOf(variableCountOld),variableName);
        rwLock.writeLock().unlock();
    }
    public void add   (String variableName             ){

        //add(variableName,"");
        checkValidityOfVariableName(variableName);
        rwLock.writeLock().lock();
        int variableCountOld=index_name.size();
        if( variableCountOld==MAX_VARIABLE_COUNT||
            name_value.putIfAbsent(variableName,"")!=null
        ){  rwLock.writeLock().unlock();
            throw new RuntimeException();
        }
        index_name.put(Integer.valueOf(variableCountOld),variableName);
        rwLock.writeLock().unlock();

    }
    public void remove(String variableName             ){
        rwLock.writeLock().lock();
        if(name_value.remove(variableName)==null){
            rwLock.writeLock().unlock();
            if(variableName==null)throw new NullPointerException();
            throw new RuntimeException();
        }
        int variableCountNew=name_value.size();
        Integer i=Integer.valueOf(variableCountNew);
        while(!index_name.get(i).equals(variableName))
            i=Integer.valueOf(i.intValue()-1);
        Integer k=Integer.valueOf(i.intValue()+1);
        while(i.intValue()<variableCountNew){
            index_name.put(i,index_name.get(k));
            k=Integer.valueOf((i=k).intValue()+1);
        }
        index_name.remove(i);
        rwLock.writeLock().unlock();
    }

    public boolean       containsVariable(String variableName ){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().  lock();
        boolean t=name_value.containsKey(variableName);
        rwLock.readLock().unlock();
        return t;
    }
    public Integer IntegerIndexOfVariable(String variableName ){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().lock();

        /*int l=

            //variableCount()
            index_name.size()

        ;
        for(Integer k=Integer.valueOf(0);
            k.intValue()<l;
            k=Integer.valueOf(k.intValue()+1)
        )if(index_name.get(k).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }*/
        for(Integer k=Integer.valueOf(

                //variableCount()
                index_name.size()

            -1);
            k.intValue()>=0;
            k=Integer.valueOf(k.intValue()-1)
        )if(index_name.get(k).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }

        rwLock.readLock().unlock();
        return Integer.valueOf(-1);
    }
    public int         intIndexOfVariable(String variableName ){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().lock();

        /*for(int k=0,l=

                //variableCount()
                index_name.size()

            ;
            k<l;
            ++k
        )if(index_name.get(Integer.valueOf(k)).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }*/
        for(int k=

                //variableCount()
                index_name.size()

            ;
            k>0;
        )if(index_name.get(--k).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }

        rwLock.readLock().unlock();
        return-1;
    }
    public boolean       containsValue   (String         value){
        if(value==null)throw new NullPointerException();
        rwLock.readLock().  lock();
        boolean t=name_value.containsValue(value);
        rwLock.readLock().unlock();
        return t;
    }
    public Integer IntegerIndexOfValue   (String         value){
        if(value==null)throw new NullPointerException();
        rwLock.readLock().lock();
        int l=

            //variableCount()
            index_name.size()

        ;
        for(Integer k=Integer.valueOf(0);
            k.intValue()<l;
            k=Integer.valueOf(k.intValue()+1)
        )if(name_value.get(index_name.get(k)).equals(value)){
                rwLock.readLock().unlock();
                return k;
            }
        rwLock.readLock().unlock();
        return Integer.valueOf(-1);
    }
    public int         intIndexOfValue   (String         value){
        if(value==null)throw new NullPointerException();
        rwLock.readLock().lock();
        for(int k=0,l=

                //variableCount()
                index_name.size()

            ;
            k<l;
            ++k
        )if(name_value.get(index_name.get(Integer.valueOf(k))).equals(value)){
                rwLock.readLock().unlock();
                return k;
            }
        rwLock.readLock().unlock();
        return-1;
    }

    public String variableAt(Integer index){
        rwLock.readLock().  lock();
        String t=index_name.get(index);
        rwLock.readLock().unlock();
        if(t==null){
            if(index==null)throw new NullPointerException();
            throw new IndexOutOfBoundsException();
        }
        return t;
    }
    public String variableAt(int     index){

        //return variableAt(Integer.valueOf(index));
        rwLock.readLock().  lock();
        String t=index_name.get(Integer.valueOf(index));
        rwLock.readLock().unlock();
        if(t==null)throw new IndexOutOfBoundsException();
        return t;

    }
    public String    valueAt(Integer index){
        rwLock.readLock().lock();

        /*try{return name_value.get(variableAt(index));
        }finally{rwLock.readLock().unlock();}*/
        String t=index_name.get(index);
        if(t==null){
            rwLock.readLock().unlock();
            if(index==null)throw new NullPointerException();
            throw new IndexOutOfBoundsException();
        }
        t=name_value.get(t);
        rwLock.readLock().unlock();
        return t;

    }
    public String    valueAt(int     index){

        //return valueAt(Integer.valueOf(index));
        rwLock.readLock().lock();
        String t=index_name.get(Integer.valueOf(index));
        if(t==null){
            rwLock.readLock().unlock();
            throw new IndexOutOfBoundsException();
        }
        t=name_value.get(t);
        rwLock.readLock().unlock();
        return t;

    }

    public void   set(String variableName,String value){
        if(value==null)throw new NullPointerException();
        rwLock.writeLock().lock();
        if(name_value.replace(variableName,value)==null){
            rwLock.writeLock().unlock();
            if(variableName==null)throw new NullPointerException();
            throw new RuntimeException();
        }
        rwLock.writeLock().unlock();
    }
    public String get(String variableName             ){
        rwLock.readLock().  lock();
        String t=name_value.get(variableName);
        rwLock.readLock().unlock();
        if(t==null){
            if(variableName==null)throw new NullPointerException();
            throw new RuntimeException();
        }
        return t;
    }

    public String[]toStrings (){
        rwLock.readLock().  lock();
        int k=

            //variableCount()
            index_name.size()

        ,i=k<<1;
        String[]l=new String[i];
        while(k>0){
            String
                n=index_name.get(Integer.valueOf(--k)),
                v=name_value.get(n);
            l[--i]=v;
            l[--i]=n;
        }
        rwLock.readLock().unlock();
        return l;
    }
    private void recover     (String[] backup ){
        int k=backup.length;
        rwLock.writeLock().  lock();

        //clear();
        index_name.clear();
        name_value.clear();

        while(k>0){
            String
                v=backup[--k],
                n=backup[--k];

            index_name.put(Integer.valueOf(k>>1),n);
            name_value.put(n,v);
            /*name_value.put(n,v);
            index_name.put(Integer.valueOf(k>>1),n);*/

        }
        rwLock.writeLock().unlock();
    }
    public  void setto       (String...strings){

        /*rwLock.writeLock().lock();
        String[]backup=toStrings();
        clear();
        try{for(int k=0;k!=strings.length;k+=2)add(strings[k],strings[k+1]);
        }catch(Throwable e){
            recover(backup);
            throw new IllegalArgumentException();
        }finally{rwLock.writeLock().unlock();}*/
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<k>>1)throw new IllegalArgumentException();
        rwLock.writeLock().lock();
        String[]backup=toStrings();
        index_name.clear();
        name_value.clear();
        try{while(k!=0){
                String
                    v=strings[--k],
                    n=strings[--k];
                if(v==null)throw new NullPointerException();
                checkValidityOfVariableName(n);
                if(name_value.putIfAbsent(n,v)!=null)
                    throw new RuntimeException();
                index_name.put(Integer.valueOf(k>>1),n);
            }
        }catch(Throwable e){
            recover(backup);
            throw new IllegalArgumentException();
        }finally{rwLock.writeLock().unlock();}

    }
    public VariableCollection(String...strings){

        //setto(strings);
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<k>>1)throw new IllegalArgumentException();
        try{while(k!=0){
                String
                    v=strings[--k],
                    n=strings[--k];
                if(v==null)throw new NullPointerException();
                checkValidityOfVariableName(n);
                if(name_value.putIfAbsent(n,v)!=null)
                    throw new RuntimeException();
                index_name.put(Integer.valueOf(k>>1),n);
            }
        }catch(Throwable e){

            //clear();
            index_name.clear();
            name_value.clear();

            throw new IllegalArgumentException();
        }

    }

    /*public boolean equals(VariableCollection y){
        if(y==null)return false;
        String[]
            X=  toStrings(),
            Y=y.toStrings();

        return true;
    }*/
    /*public boolean equals(Object obj){
        return obj instanceof VariableCollection y?equals(y):false;
    }*/
    /*@Override public int hashCode(){
        int h=0,k=0;
        String n;
        rwLock.readLock().  lock();
        while(k<variableCount){
            h+=(n=index_name.get(Integer.valueOf(k))).hashCode();
            h*=(++k)*name_value.get(n).hashCode();
        }
        rwLock.readLock().unlock();
        return h;
    }*/

    public static void checkValidityOfStorage(String storage){
        for(int l=storage.length(),k=0;k<l;++k)
            switch(storage.charAt(k)){
                case '"'->throw new IllegalArgumentException();
                case'\n'->throw new IllegalArgumentException();
                case'\\'->{
                    if(++k==l)throw new IllegalArgumentException();
                    switch(storage.charAt(k)){
                        case '"'->{}
                        case 'n'->{}
                        case'\\'->{}
                        default ->throw new IllegalArgumentException();
                    }
                }
            }
    }
    public static String storageTOmemory (String storage){
        char[]value=new char[storage.length()];
        char c;
        int i=0;
        for(int k=0;k<value.length;++k,++i)
            switch(c=storage.charAt(k)){
                case '"'->throw new IllegalArgumentException();
                case'\n'->throw new IllegalArgumentException();
                case'\\'->{
                    if(++k==value.length)throw new IllegalArgumentException();
                    value[i]=switch(c=storage.charAt(k)){
                        case '"'->  c ;
                        case 'n'->'\n';
                        case'\\'->  c ;
                        default ->throw new IllegalArgumentException();
                    };
                }
                default ->value[i]=c;
            }
        return new String(value,0,i);
    }
    public static String  memoryTOstorage(String value  ){
        int l=value.length(),t=(int)(1.06d*(double)l);
        StringBuilder storage=new StringBuilder(t<0?1<<28:t);
        char c;
        for(int k=0;k<l;++k)
            if('\n'==(c=value.charAt(k)))
                storage.append("\\n");
            else{
                if('"'==c||'\\'==c)
                    storage.append('\\');
                storage.append(c);
            }
        return storage.toString();
    }

    private String toString  (int nextLines){
        if(nextLines<0)throw new IllegalArgumentException();
        String nextLine="\";";
        while(nextLines>0){
            --nextLines;
            nextLine+='\n';
        }
        int[]ls=new int[2];
        rwLock.readLock().  lock();
        name_value.forEach((n,v)->{
            ls[0]+=n.length();
            ls[1]+=v.length();
        });
        int variableCount=variableCount(),
            t=  ls[0]+variableCount*(2+nextLine.length())+
                (int)(1.06d*(double)ls[1]);
        StringBuilder str=new StringBuilder(t<0?1<<28:t);
        /* 
         * 
         * 前面保证了nextLines一定从非负的值开始递减，因此nextLines在这个方法刚开始的while循环结束后一定是0，之后没有写入过，
         * 因此它此时一定是0，可以直接用作索引
         */
        while(nextLines<variableCount){
            String
                n=index_name.get(Integer.valueOf(nextLines)),
                v=name_value.get(n);
            str.append(n);
            str.append("=\"");
            for(int k=0,l=v.length();k<l;++k){
                char c=v.charAt(k);
                if('\n'==c)
                    str.append("\\n");
                else{
                    if('"'==c||'\\'==c)
                        str.append('\\');
                    str.append(c);
                }
            }
            str.append(nextLine);
            ++nextLines;
        }
        rwLock.readLock().unlock();
        return str.toString();
    }
    @Override
    public  String toString  (){
        return toString(1);
    }
    public  byte[] toBytes   (){
        return toString(0).getBytes(StandardCharsets.UTF_8);
    }
    public void setto        (String string){
        int l=string.length(),i=l;
        boolean allEmpty=true;
        char c='J';
        while(allEmpty&&i>0)
            allEmpty=' '==(c=string.charAt(--i))
                ||  '\n'== c;
        if(allEmpty){

            //clear();
            rwLock.writeLock().  lock();
            index_name.clear();
            name_value.clear();
            rwLock.writeLock().unlock();

            return;
        }
        if(c!=';')throw new IllegalArgumentException();
        if(++i!=l)string=string.substring(0,l=i);
        i=0;
        rwLock.writeLock().lock();
        String[]backup=toStrings();

        //clear();
        index_name.clear();
        name_value.clear();

        int variableCount=0;

        try{do{ if(variableCount==MAX_VARIABLE_COUNT)throw new IllegalArgumentException();
                while( ' '==(c=string.charAt(i))
                    ||'\n'== c
                )++i;
                if( (   !(  ('a'<=c&&c<='z')||
                            ('A'<=c&&c<='Z')
                        )
                    )
                    &&c!='_'
                    &&c!='$'
                )throw new IllegalArgumentException();
                int begin=i;
                c=string.charAt(++i);
                while(  c!=' '&&
                        c!='='&&
                        c!='\n'
                ){  if( (   !(  ('a'<=c&&c<='z')||
                                ('A'<=c&&c<='Z')||
                                ('0'<=c&&c<='9')
                            )
                        )
                        &&c!='_'
                        &&c!='$'
                    )throw new IllegalArgumentException();
                    c=string.charAt(++i);
                }
                if(MAX_VARIABLE_NAME_LENGTH<i-begin)throw new IllegalArgumentException();
                String n=string.substring(begin,i);
                while(c==' '
                    ||c=='\n'
                )c=string.charAt(++i);
                if(c!='=')throw new IllegalArgumentException();
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                if(c!='"')throw new IllegalArgumentException();
                StringBuilder v=new StringBuilder(64);
                ++i;
                while('"'!=(c=string.charAt(i)))
                    switch(c){
                        case'\n'->throw new IllegalArgumentException();
                        case'\\'->{
                            if(++i==l)throw new IllegalArgumentException();
                            v.append(
                                switch(c=string.charAt(i)){
                                    case '"'->  c ;
                                    case 'n'->'\n';
                                    case'\\'->  c ;
                                    default ->throw new IllegalArgumentException();
                                }
                            );
                            ++i;
                        }
                        default ->{
                            if(++i==l)throw new IllegalArgumentException();
                            v.append(c);
                        }
                    }
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                if(c!=';')throw new IllegalArgumentException();

                //add(n,v.toString());
                if(name_value.putIfAbsent(n,v.toString())!=null)
                    throw new RuntimeException();
                index_name.put(Integer.valueOf(variableCount),n);
                ++variableCount;

            }while(++i<l);
        }catch(Throwable e){
            recover(backup);
            throw new IllegalArgumentException();
        }finally{rwLock.writeLock().unlock();}
    }
    public void setto        (byte[] bytes ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
    public VariableCollection(String string){
        setto(string);
    }
    public VariableCollection(byte[] bytes ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
}