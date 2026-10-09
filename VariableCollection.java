import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VariableCollection implements Cloneable{

    final private HashMap<Integer,String>index_name=new HashMap<>();
    final private HashMap<String ,String>name_value=new HashMap<>();

    final private ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock(false);

    final private static int
        MAX_VARIABLE_COUNT      =1<<16,
        MAX_VARIABLE_NAME_LENGTH=1<<16;

    static{
        if( MAX_VARIABLE_COUNT      <0||(1<<30)-1<MAX_VARIABLE_COUNT        ||
            MAX_VARIABLE_NAME_LENGTH<1|| 1<<16   <MAX_VARIABLE_NAME_LENGTH
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
        rwLock.writeLock().unlock();
    }

    private static void checkValidityOfVariableName(String variableName){
        int i=variableName.length();
        if(i==0||MAX_VARIABLE_NAME_LENGTH<i)throw new IllegalArgumentException();
        char c=variableName.charAt(0);
        if( (   !(  ('a'<=c&&c<='z')||
                    ('A'<=c&&c<='Z')
                )
            )
            &&c!='_'
            &&c!='$'
        )throw new IllegalArgumentException();
        while(i>1){
            c=variableName.charAt(--i);
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
            throw new RuntimeException();
        }
        int variableCountNew=name_value.size();
        Integer i=Integer.valueOf(variableCountNew);
        while(!index_name.get(i).equals(variableName))
            i=Integer.valueOf(i.intValue()-1);
        Integer k=Integer.valueOf(i.intValue()+1);
        while(k.intValue()<=variableCountNew){
            index_name.put(
                i,index_name.get(k)
            );
            k=Integer.valueOf((i=k).intValue()+1);
        }
        index_name.remove(i);
        rwLock.writeLock().unlock();
    }

    public boolean containsVariable        (String variableName){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().  lock();
        boolean t=name_value.containsKey(variableName);
        rwLock.readLock().unlock();
        return t;
    }
    public Integer  indexOfVariable_Integer(String variableName){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().lock();
        int l=

            //variableCount()
            index_name.size()

        ;
        for(Integer k=Integer.valueOf(0);
            k.intValue()<l;
            k=Integer.valueOf(k.intValue()+1)
        )if(index_name.get(k).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }
        rwLock.readLock().unlock();
        return Integer.valueOf(-1);
    }
    public int      indexOfVariable_int    (String variableName){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().lock();
        for(int k=0,l=

                //variableCount()
                index_name.size()

            ;
            k<l;
            ++k
        )if(index_name.get(Integer.valueOf(k)).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }
        rwLock.readLock().unlock();
        return-1;
    }
    public boolean containsValue           (String value       ){
        if(value==null)throw new NullPointerException();
        rwLock.readLock().  lock();
        boolean t=name_value.containsValue(value);
        rwLock.readLock().unlock();
        return t;
    }
    public Integer  indexOfValue_Integer   (String value       ){
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
    public int      indexOfValue_int       (String value       ){
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
        if(t==null)throw new RuntimeException();
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
            throw new RuntimeException();
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
            throw new RuntimeException();
        }
        rwLock.writeLock().unlock();
    }
    public String get(String variableName             ){
        rwLock.readLock().  lock();
        String t=name_value.get(variableName);
        rwLock.readLock().unlock();
        if(t==null)throw new RuntimeException();
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
            String n=
            l[--i]=index_name.get(Integer.valueOf(--k));
            l[--i]=name_value.get(n);
        }
        rwLock.readLock().unlock();
        return l;
    }
    private void recover     (String[] strings){
        int k=strings.length;
        rwLock.writeLock().  lock();

        //clear();
        index_name.clear();
        name_value.clear();

        while(k>0){
            String n=strings[--k];
            index_name.put(Integer.valueOf(k>>1),n);
            name_value.put(n,strings[--k]);
        }
        rwLock.writeLock().unlock();
    }
    public  void setto       (String...strings){

        /*rwLock.writeLock().lock();
        String[]backup=toStrings();

        //clear();
        index_name.clear();
        name_value.clear();

        try{for(int k=0;k<strings.length;k+=2)add(strings[k+1],strings[k]);
        }catch(Throwable e){
            recover(backup);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }*/
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<<1<k)
            throw new IllegalArgumentException();
        rwLock.writeLock().lock();
        String[]backup=toStrings();
        index_name.clear();
        name_value.clear();
        try{while(k>0){
                String n=strings[--k],v=strings[--k];
                if(v==null)throw new NullPointerException();
                checkValidityOfVariableName(n);
                if(name_value.putIfAbsent(n,v)!=null)
                    throw new IllegalArgumentException();
                index_name.put(Integer.valueOf(k>>1),n);
            }
        }catch(Throwable e){
            recover(backup);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }

    }
    public VariableCollection(String...strings){

        //setto(strings);
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<<1<k)
            throw new IllegalArgumentException();
        rwLock.writeLock().lock();
        try{while(k>0){
                String n=strings[--k],v=strings[--k];
                if(v==null)throw new NullPointerException();
                checkValidityOfVariableName(n);
                if(name_value.putIfAbsent(n,v)!=null)
                    throw new IllegalArgumentException();
                index_name.put(Integer.valueOf(k>>1),n);
            }
        }catch(Throwable e){

            //clear();
            index_name.clear();
            name_value.clear();

            throw e;
        }finally{
            rwLock.writeLock().unlock();
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

    /*private static void checkValidityOfStorage(String storage){
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
    }*/
    /*private static String storageTOmemory (String storage){
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
    }*/
    /*private static String  memoryTOstorage(String value  ){
        int l=value.length();
        StringBuilder storage=new StringBuilder((int)(1.06d*(double)l));
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
    }*/

    public String toString   (int nextLines){
        if(nextLines<0||16<nextLines)throw new IllegalArgumentException();
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
        int variableCount=variableCount();
        StringBuilder str=new StringBuilder(
            ls[0]+variableCount*(2+nextLine.length())+
            (int)(1.06d*(double)ls[1])
        );
        String n,v;
        char c;
        int l,k;
        while(nextLines<variableCount){
            str.append(n=index_name.get(Integer.valueOf(nextLines)));
            str.append("=\"");
            for(l=(v=name_value.get(n)).length(),k=0;k<l;++k)
                if('\n'==(c=v.charAt(k)))
                    str.append("\\n");
                else{
                    if('"'==c||'\\'==c)
                        str.append('\\');
                    str.append(c);
                }
            str.append(nextLine);
            ++nextLines;
        }
        rwLock.readLock().unlock();
        return str.toString();
    }
    @Override
    public String toString   (){
        return toString(1);
    }
    public byte[] toBytes    (){
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
            index_name.clear();
            name_value.clear();

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

        try{do{ while( ' '==(c=string.charAt(i))
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
                while('"'!=(c=string.charAt(++i)))
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
                        }
                        default ->v.append(c);
                    }
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                if(c!=';')throw new IllegalArgumentException();

                add(n,v.toString());

            }while(++i<l);
        }catch(Throwable e){
            recover(backup);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }
    }
    public void setto        (byte[] bytes ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
    public VariableCollection(String string){
        setto(string);
    }
    public VariableCollection(byte[] bytes ){

        //setto(bytes);
        setto(new String(bytes,StandardCharsets.UTF_8));

    }
}