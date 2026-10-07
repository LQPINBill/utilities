import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final public class VariableCollection implements Cloneable{

    final private ConcurrentHashMap<Integer,String>index_name=new ConcurrentHashMap<>();
    final private ConcurrentHashMap<String ,String>name_value=new ConcurrentHashMap<>();

    private int variableCount=0;
    final private static int MAX_VARIABLE_COUNT=256;
    final private static int MAX_VARIABLE_NAME_LENGTH=256;

    final private ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock(false);

    /**
     * create a new VC with no variable
     */
    public VariableCollection(){}

    public int variableCount(){
        rwLock.readLock().  lock();
        int t=variableCount;
        rwLock.readLock().unlock();
        return t;
    }
    public boolean isEmpty(){
        //return variableCount()==0;
        rwLock.readLock().  lock();
        boolean t=variableCount==0;
        rwLock.readLock().unlock();
        return t;
    }
    public void clear(){
        rwLock.writeLock().  lock();
        index_name.clear();
        name_value.clear();
        variableCount=0;
        rwLock.writeLock().unlock();
    }

    /**
     * does nothing iff{@code variableName}is a valid variable name, else throw
     * {@code NullPointerException}or{@code IllegalArgumentException}
     * 
     * a String is a valid variable name, iff the String is not{@code null}and
     * does not equal to the empty String{@code ""}, and its every char is a
     * digit(0 to 9), letter(a to z, A to Z), '_' or '$', and begins with a non-
     * digit char, and the length of String is no greater than the maximum 
     * variable name length allowed
     * 
     * @param variableName the variable name to check
     * @throws NullPointerException iff{@code variableName==null}
     * @throws IllegalArgumentException iff{@code variableName}is not a valid
     * variable name
     */
    private static void checkValidityOfVariableName(String variableName){
        int i=variableName.length();
        if(i==0||MAX_VARIABLE_NAME_LENGTH<i)throw new IllegalArgumentException();
        char c=variableName.charAt(0);
        if( (!Character.isLetter(c))
            &&'_'!=c
            &&'$'!=c
        )throw new IllegalArgumentException();
        while(i>1)
            if( (!Character.isLetterOrDigit(c=variableName.charAt(--i)))
                &&'_'!=c
                &&'$'!=c
            )throw new IllegalArgumentException();
    }
    /**
     * declare a new variable at the tail of this VC with name
     * {@code variableName}and default initial value{@code ""}
     * 
     * @param variableName the name of the new variable to declare and add
     * @throws NullPointerException iff{@code variableName==null}
     * @throws IllegalArgumentException iff{@code variableName}is not a valid
     * variable name
     * @throws RuntimeException iff this VC already contained a variable with
     * name{@code variableName}, or this VC's variable count is the maximum 
     * variable count allowed
     */
    public void add   (String variableName){
        checkValidityOfVariableName(variableName);
        rwLock.writeLock().  lock();
        try{
            if(variableCount==MAX_VARIABLE_COUNT)
                throw new RuntimeException();
            if(name_value.putIfAbsent(variableName,"")!=null)
                throw new RuntimeException();
        }catch(Throwable e){
            rwLock.writeLock().unlock();
            throw e;
        }
        index_name.put(Cache.getInteger(variableCount),variableName);
        ++variableCount;
        rwLock.writeLock().unlock();
    }
    /**
     * remove the variable of this VC with name{@code variableName}
     * 
     * @param variableName the name of the variable to remove
     * @throws NullPointerException iff{@code variableName==null}
     * @throws RuntimeException iff this VC does not contain a variable with
     * name{@code variableName}
     */
    public void remove(String variableName){
        rwLock.writeLock().  lock();
        try{
            if(name_value.remove(variableName)==null)
                throw new RuntimeException();
        }catch(Throwable e){
            rwLock.writeLock().unlock();
            throw e;
        }
        Integer i=Cache.getInteger(--variableCount);
        while(!index_name.get(i).equals(variableName))
            i=Cache.getInteger(i.intValue()-1);
        Integer k=Cache.getInteger(i.intValue()+1);
        while(k.intValue()<=variableCount){
            index_name.put(
                i,index_name.get(k)
            );
            k=Cache.getInteger((i=k).intValue()+1);
        }
        index_name.remove(i);
        rwLock.writeLock().unlock();
    }

    /**
     * @param variableName the name of the variable to find
     * @return the index in this VC of the variable with name
     * {@code variableName}, or{@code -1}if such variable does not exist
     * @throws NullPointerException iff{@code variableName==null}
     */
    public int      indexOfVariable(String variableName){
        if(variableName==null)throw new NullPointerException();
        rwLock.readLock().  lock();

        int k=variableCount;
        while(k>0)
            if(index_name.get(Cache.getInteger(--k)).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }
        /*for(int k=0;k<variableCount;++k)
            if(index_name.get(Cache.getInteger(k)).equals(variableName)){
                rwLock.readLock().unlock();
                return k;
            }*/

        rwLock.readLock().unlock();
        return-1;
    }
    /**
     * @param variableName the name of the variable to find
     * @return {@code true}iff this VC contains a variable with name
     * {@code variableName}
     * @throws NullPointerException iff{@code variableName==null}
     */
    public boolean containsVariable(String variableName){
        boolean t;
        rwLock.readLock().lock();
        try{
            t=name_value.get(variableName)!=null;
        }finally{
            rwLock.readLock().unlock();
        }
        return t;
    }
    /**
     * @param value the value of the variable to find
     * @return the index in this VC of the first occurence of a variable with
     * value{@code value}, or{@code -1}if such variable does not exist
     * @throws NullPointerException iff{@code value==null}
     */
    public int      indexOfValue   (String value       ){
        if(value==null)throw new NullPointerException();
        rwLock.readLock().  lock();
        for(int k=0;k<variableCount;++k)
            if(name_value.get(index_name.get(Cache.getInteger(k))).equals(value)){
                rwLock.readLock().unlock();
                return k;
            }
        rwLock.readLock().unlock();
        return-1;
    }
    /**
     * @param value the value of the variable to find
     * @return {@code true}iff this VC contains a variable with value
     * {@code value}
     * @throws NullPointerException iff{@code value==null}
     */
    public boolean containsValue   (String value       ){
        boolean t;
        rwLock.readLock().lock();
        try{
            t=name_value.containsValue(value);
        }finally{
            rwLock.readLock().unlock();
        }
        return t;
    }

    /**
     * @param index the index in this VC of the variable to find
     * @return the name of the variable with index{@code index}in this VC
     * @throws NullPointerException iff{@code index==null}
     * @throws IndexOutOfBoundsException iff there exists no variable with index
     * {@code index}in this VC
     */
    public String variableAt(Integer index){
        String t;
        rwLock.readLock().lock();
        try{
            if((t=index_name.get(index))==null)
                throw new IndexOutOfBoundsException(
                    "index "+index.intValue()+" is not in interval [0,"+variableCount+')'
                );
        }finally{
            rwLock.readLock().unlock();
        }
        return t;
    }
    /**
     * @param index the index in this VC of the variable to find
     * @return the name of the variable with index{@code index}in this VC
     * @throws IndexOutOfBoundsException iff there exists no variable with index
     * {@code index}in this VC
     */
    public String variableAt(int     index){
        return variableAt(Cache.getInteger(index));
    }
    /**
     * @param index the index in this VC of the variable to find
     * @return the value of the variable with index{@code index}in this VC
     * @throws NullPointerException iff{@code index==null}
     * @throws IndexOutOfBoundsException iff there exists no variable with index
     * {@code index}in this VC
     */
    public String    valueAt(Integer index){
        String t;
        rwLock.readLock().lock();
        try{
            t=name_value.get(variableAt(index));
        }finally{
            rwLock.readLock().unlock();
        }
        return t;
    }
    /**
     * @param index the index in this VC of the variable to find
     * @return the value of the variable with index{@code index}in this VC
     * @throws IndexOutOfBoundsException iff there exists no variable with index
     * {@code index}in this VC
     */
    public String    valueAt(int     index){
        return valueAt(Cache.getInteger(index));
    }

    /**
     * set the variable with name{@code variableName}of this VC to{@code value}
     * @param variableName the name of the variable to set
     * @param value the value set to the variable to set
     * @throws NullPointerException iff{@code variableName==null||value==null}
     * @throws RuntimeException iff this VC does not contain a variable with
     * name{@code variableName}
     */
    public void   set(String variableName,String value){
        rwLock.writeLock().lock();
        try{
            if(name_value.replace(variableName,value)==null)
                throw new RuntimeException();
        }finally{
            rwLock.writeLock().unlock();
        }
    }
    /**
     * @param variableName the name of the variable to read
     * @return the value of the variable to read
     * @throws NullPointerException iff{@code variableName==null}
     * @throws RuntimeException iff this VC does not contain a variable with
     * name{@code variableName}
     */
    public String get(String variableName             ){
        String t;
        rwLock.readLock().lock();
        try{
            if((t=name_value.get(variableName))==null)throw new RuntimeException();
        }finally{
            rwLock.readLock().unlock();
        }
        return t;
    }

    public String[]toStrings(){
        int i=0;
        String n;
        rwLock.readLock().  lock();
        String[]l=new String[variableCount*2];
        for(int k=0;k<variableCount;++k){
            l[i]=n=index_name.get(Cache.getInteger(k));
            l[++i]=name_value.get(n);
            ++i;
        }
        rwLock.readLock().unlock();
        return l;
    }
    private void recover(String[]recovery){
        int k=0;
        String n;
        rwLock.writeLock().  lock();
        index_name.clear();
        name_value.clear();
        variableCount=0;
        while(k<recovery.length){
            index_name.put(Cache.getInteger(variableCount),n=recovery[k]);
            name_value.put(n,recovery[++k]);
            ++k;
            ++variableCount;
        }
        rwLock.writeLock().unlock();
    }
    public void setto        (String[]strings){
        int k=0;
        String n;
        rwLock.writeLock().lock();
        String[]recovery=toStrings();
        clear();
        try{
            while(k<strings.length){
                add(n=strings[k]);
                name_value.put(n,strings[++k]);
                ++k;
            }
        }catch(Throwable e){
            recover(recovery);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }
    }
    public VariableCollection(String[]strings){
        //setto(strings);
        int k=0;
        String n;
        try{
            while(k<strings.length){
                add(n=strings[k]);
                name_value.put(n,strings[++k]);
                ++k;
            }
        }catch(Throwable e){
            clear();
            throw e;
        }
    }

    public String[]structure(){
        rwLock.readLock().  lock();
        int k=variableCount;
        String[]l=new String[variableCount];
        while(k>0){
            --k;
            l[k]=index_name.get(Cache.getInteger(k));
        }
        rwLock.readLock().unlock();
        return l;
    }
    public String[]cartesian(){
        rwLock.readLock().  lock();
        int k=variableCount;
        String[]l=new String[variableCount];
        while(k>0){
            --k;
            l[k]=name_value.get(
                index_name.get(Cache.getInteger(k))
            );
        }
        rwLock.readLock().unlock();
        return l;
    }
    public void setto        (String[]structure,String[]cartesian){
        if(structure.length!=cartesian.length)throw new RuntimeException();
        String n;
        rwLock.writeLock().lock();
        String[]recovery=toStrings();
        clear();
        try{
            for(int k=0;k<structure.length;++k){
                add(n=structure[k]);
                name_value.put(n,cartesian[k]);
            }
        }catch(Throwable e){
            recover(recovery);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }
    }
    public VariableCollection(String[]structure,String[]cartesian){
        //setto(structure,cartesian);
        if(structure.length!=cartesian.length)throw new RuntimeException();
        String n;
        try{
            for(int k=0;k<structure.length;++k){
                add(n=structure[k]);
                name_value.put(n,cartesian[k]);
            }
        }catch(Throwable e){
            clear();
            throw e;
        }
    }

    @Override public int hashCode(){
        int h=0,k=0;
        String n;
        rwLock.readLock().  lock();
        while(k<variableCount){
            h+=(n=index_name.get(Cache.getInteger(k))).hashCode();
            h*=(++k)*name_value.get(n).hashCode();
        }
        rwLock.readLock().unlock();
        return h;
    }

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

    private String toString(int nextLines){
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
        StringBuilder str=new StringBuilder(
            ls[0]+variableCount*(2+nextLine.length())+
            (int)(1.06d*(double)ls[1])
        );
        String n,v;
        char c;
        int l,k;
        while(nextLines<variableCount){
            str.append(n=index_name.get(Cache.getInteger(nextLines)));
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
    public String toString(){
        return toString(1);
    }
    public byte[] toBytes (){
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
            clear();
            return;
        }
        if(c!=';')throw new IllegalArgumentException();
        if(++i!=l)string=string.substring(0,l=i);
        int begin=i=0;
        String name;
        StringBuilder value;
        rwLock.writeLock().lock();
        String[]recovery=toStrings();
        clear();
        try{do{ while( ' '==(c=string.charAt(i))
                    ||'\n'== c
                )++i;
                if( (!Character.isLetter(c))
                    &&'_'!=c
                    &&'$'!=c
                )throw new IllegalArgumentException();
                begin=i;
                c=string.charAt(++i);
                while(  c!=' '&&
                        c!='='&&
                        c!='\n'
                ){  if( (!Character.isLetterOrDigit(c))
                        &&'_'!=c
                        &&'$'!=c
                    )throw new IllegalArgumentException();
                    c=string.charAt(++i);
                }
                if(MAX_VARIABLE_NAME_LENGTH<i-begin)throw new IllegalArgumentException();
                name=string.substring(begin,i);
                while(c==' '
                    ||c=='\n'
                )c=string.charAt(++i);
                if(c!='=')throw new IllegalArgumentException();
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                if(c!='"')throw new IllegalArgumentException();
                value=new StringBuilder(64);
                while('"'!=(c=string.charAt(++i))){
                    switch(c){
                        case'\n'->throw new IllegalArgumentException();
                        case'\\'->{
                            if(++i==l)throw new IllegalArgumentException();
                            value.append(
                                switch(c=string.charAt(i)){
                                    case '"'->  c ;
                                    case 'n'->'\n';
                                    case'\\'->  c ;
                                    default ->throw new IllegalArgumentException();
                                }
                            );
                        }
                        default ->value.append(c);
                    }
                }
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                if(c!=';')throw new IllegalArgumentException();

                if(variableCount==MAX_VARIABLE_COUNT)
                    throw new RuntimeException();
                if(name_value.putIfAbsent(name,"")!=null)
                    throw new RuntimeException();
                index_name.put(Cache.getInteger(variableCount),name);
                ++variableCount;

                name_value.put(name,value.toString());
            }while(++i<l);
        }catch(Throwable e){
            recover(recovery);
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
        setto(bytes);
    }
}