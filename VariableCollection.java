import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VariableCollection{

    /* 
     * 
     * name_value指定变量名和变量值的关联，但这是无序的，因此还需用index_name指定变量的顺序。两个散列表应作成具有下表结构的表：
     * ·-------------------·-----------------------------------·---------------------------------------------------·
     * |      indexs       |               names               |                      values                       |
     * ·-------------------·-----------------------------------·---------------------------------------------------·
     * |0                  |index_name.get(0)                  |name_value.get(index_name.get(0))                  |
     * |.                  |        .                          |                .                                  |
     * |.                  |        .                          |                .                                  |
     * |.                  |        .                          |                .                                  |
     * |index_name.size()-1|index_name.get(index_name.size()-1)|name_value.get(index_name.get(index_name.size()-1))|
     * ·-------------------·-----------------------------------·---------------------------------------------------·
     * 且表不含null，index_name是双射（此处认为空集是合法的定义域），name_value和index_name的.size()相等，且
     * index_name的每个value都是name_value的key。令这两个散列表满足这样的性质使得它易于直接使用直觉理解和维护。为防止AI直接
     * 读取文件的字节流时不使用视觉无法识别上表，以下是这个图表的文字形式：一个长度可变的表，对于任意[0,长度)上的整数k，可以使用k作为索引访问两个字
     * 符串，index_name.get(k)是第k个变量的名称，name_value.get(index_name.get(k))是第k个变量的值，若
     * 已知表中有变量的名称是String name, 则name_value.get(name)返回具有这个名称的变量的值
     * 
     * 我意图依下述方法设计该工具：每个方法可以被证明满足以下性质：若调用前的状态合法，则调用后的状态一定也合法，反之若方法结束前抛出异常，则在异常被抛出
     * 前状态应该被恢复到调用前的状态。又因为根据前面的定义，VC空状态合法，对空VC有限次应用任何方法后，VC依然合法。若所有的VC皆由空VC有限次应用
     * 这些方法得到，则每个VC总是合法。更激进的，大部分方法可以表示为有限次一些基础方法的复合。这样一来，代码会变的极一致、可读且易于维护，且该类作为工
     * 具，它的行为会具有极高的确定性，这是可预测的，且它可以由任何一个理解它的工作方式的人完全掌握，而结合前面给出的表，理解它是极容易的
     */
    final private HashMap<Integer,String>index_name=new HashMap<>();
    final private HashMap<String ,String>name_value=new HashMap<>();

    /* 
     * 
     * 用于多线程阻塞其它线程，防止数据竞争。为了使得读者可以区分可预料到的和不可预料到的被抛出的异常，我将尽可能的手动管理锁，只在一些特殊情况下使用
     * try-catch,try-finally或try-catch-finally自动控制锁
     * 
     * 关于抛出的异常的类型，我遵循以下原则：代码无需抛出异常的情况下的工作效率，优先于抛出的异常的类型的正确性。例如，考虑以下两个代码片段：
     * 
     * (A)
     *  public String get(String variableName){
     *      if(variableName==null)
     *          throw new NullPointerException();
     *      rwLock.readLock().  lock();
     *      String t=name_value.get(variableName);
     *      rwLock.readLock().unlock();
     *      if(t==null)
     *          throw new RuntimeException();
     *      return t;
     *  }
     * 
     * (B)
     *  public String get(String variableName){
     *      rwLock.readLock().  lock();
     *      String t=name_value.get(variableName);
     *      rwLock.readLock().unlock();
     *      if(t==null)
     *          throw new RuntimeException();
     *      return t;
     *  }
     * 
     * 其中(A)是一个方法的初始状态，它可以完成这个方法应该完成的操作。将(A)优化后可以得到(B)。两者区别在于(A)判断了参数是否为null，并基于
     * 此决定是否抛出NPE，而(B)没有这个操作，原因是这个操作是多余的，我接下来将利用HashMap的特性以及我在上一段注释中做出的规定证之。首先考虑
     * 简单的情况，若参数不是null，则(A)一定不会抛出NPE，之后的操作和(B)完全一致，而在另一种情况下，即参数是null的情况，我在上一段注释规
     * 定了两个散列表的key和value皆不是null，而HashMap本身是允许null作为key或value的，因此在(B)中的
     * name_value.get(variableName)必然返回null，这个返回值被赋给String t，之后又根据t是否是null决定是否抛出
     * RuntimeException，显然参数是null的情况下这个RuntimeException必然被抛出。这样一来(B)和(A)就在所有情况下都
     * 做了相同的操作：若参数是null，不在散列表中写入任何值，抛出异常，反之参数不是null就执行某一个相同的操作。在这个原则下，我倾向于使用(B)，
     * 因为在不会抛出异常的情况下，(B)总是比(A)严格少一个分支，这就是说“代码无需抛出异常的情况下的工作效率，优先于抛出的异常的类型的正确性”，后者
     * 是指，当参数是null时，(A)必然抛出NPE，而(B)必然抛出RuntimeException，类型不正确是可接受的，因为存在比“抛出的异常的类
     * 型的正确性”更优先的事。更具体的报错原因可以作为字符串参数传给异常构造函数，因此异常类型不正确实际上是可解决的问题。
     * 
     * 抛出的异常的类型还遵循以下原则：抛出NPE，当且仅当抛出异常是因为参数为null时；抛出IllegalArgumentException，当且仅当
     * 只有参数非法时；抛出RuntimeException，当且仅当无法确定具体的原因或存在多个原因，或参数对内存的当前状态非法时。其中
     * IllegalArgumentException的情况和RuntimeException的一种情况可能有歧义，更具体来说，抛出前者是因为仅通过检查
     * 参数即可确定是否会抛出异常，而后者的“参数对内存的当前状态非法”是指，是否抛出异常取决于参数之外的信息。例如，我可能意图在一个VC声明新变量，若变
     * 量名非法，则可能抛出IllegalArgumentException，因为仅通过检查变量名即可知道这是非法的，而若这个VC的变量数量已经达到了允许
     * 的最大的变量数量，或该名称已经被占用，则抛出的可能是RuntimeException，因为还需要根据这个VC的状态（index_name和
     * name_value的key-value对），结合参数才可以判断声明变量的操作是否合法
     */
    final private ReentrantReadWriteLock rwLock=new ReentrantReadWriteLock(false);
    final private long creationTime=System.currentTimeMillis();

    final private static int
        MAX_VARIABLE_COUNT      =1<<8,
        MAX_VARIABLE_NAME_LENGTH=1<<8;
    static{
        if( MAX_VARIABLE_COUNT      <0||(1<<30)-1<MAX_VARIABLE_COUNT        ||
            MAX_VARIABLE_NAME_LENGTH<1||(1<<31)-1<MAX_VARIABLE_NAME_LENGTH
        )throw new IllegalArgumentException();
    }

    public VariableCollection(){}

    /* 
     * 
     * 以下三个方法中，HashMap.size(), HashMap.isEmpty(), HashMap.clear()皆不应抛出异常，可手动管理锁。
     * 在两个散列表作成想要的表的前提下，对于int variableCount()和boolean isEmpty()，由于index_name和
     * name_value的长度总是相等，以index_name为准和以name_value为准无区别。对于void clear()，调用散列表的顺序改
     * 变无区别
     */
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

    /**
     * <p>does nothing iff{@code variableName}is a valid variable name, else
     * throw{@code NullPointerException}or{@code IllegalArgumentException}</p>
     * 
     * <p>当参数不是合法的变量名时抛出{@code NullPointerException}或
     * {@code IllegalArgumentException}，反之什么都不做</p>
     * 
     * <p>a String is a valid variable name iff it is not{@code null}and it
     * satisfies the following conditions: (1)contains atleast one char; (2)its
     * first char is a letter (a to z, A to Z), '_' or '$', and the rest of the
     * chars are letter (a to z, A to Z), digit (0 to 9), '_' or '$'; (3)its
     * length is no greater than{@code MAX_VARIABLE_NAME_LENGTH}</p>
     * 
     * <p>一个字符串是一个合法的变量名，当且仅当它不等于{@code null}且满足以下命题时：(1)存在至少一个char; (2)以英文字母、下划
     * 线或美元符号开始，其余的每个char都是英文字母、阿拉伯数字、下划线或美元符号; (3)长度不超过
     * {@code MAX_VARIABLE_NAME_LENGTH}</p>
     * 
     * <p>does not share data, except{@code variableName}. However it is very
     * unlikely</p>
     * 
     * <p>不共享数据，除非{@code variableName}是共享的，虽然这极少见</p>
     * 
     * @param variableName the variable name to check（要检查合法性的变量名）
     * @throws NullPointerException iff{@code variableName==null}
     * @throws IllegalArgumentException iff{@code variableName}is not
     * {@code null}and is not a valid variable name
     */
    private static void checkValidityOfVariableName(String variableName){

        // 若参数是null，这会自动抛出NPE
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

        /* 
         * 
         * 由于变量是逐个声明的，若一直成功声明新变量，一定存在一个时刻的变量数量是最大值，若数量已经是最大值，则无法继续添加，因为使用了短路或，
         * pufIfAbsent会被跳过，直接抛出RuntimeException。反之，则还可以添加新变量，执行pufIfAbsent。此时
         * variableName必不是null，因为若它是null，则异常应该在之前的
         * checkValidityOfVariableName(variableName)中抛出，因此检查变量名合法性之后variableName
         * 必不是null，而检查变量合法性之后variableName没有被写入，因此它此时一定不是null。若该名称已经被占用，则由于
         * pufIfAbsent只会添加新key-value对，不会修改已有的对，则putIfAbsent不会改变这个VC的状态，然后返回key关联
         * 的value，而散列表保证了不含null值，因此返回值一定不是null，此时应抛出RuntimeException。反之名称未被占用，
         * putIfAbsent等价于put，即添加新的key-value对，关联variableName和value，然后返回null（因为散列表
         * 原本不含variableName作为key）。于是，pufIfAbsent是否是null可以被用于判断声明是否合法，若返回值不是null，
         * 则说明变量名已被占用，声明是非法的，应抛出RuntimeException，反之则是合法的，变量名和变量值被成功关联、添加
         */
        if( variableCountOld==MAX_VARIABLE_COUNT||
            name_value.putIfAbsent(variableName,value)!=null
        ){  rwLock.writeLock().unlock();
            throw new RuntimeException();
        }

        /* 
         * 
         * 若程序执行到该语句时，说明声明变量一定合法，因为若不合法，应在if(true)分支中抛出RuntimeException。由前面的描述可以
         * 知道，name_value已经建立了要声明的变量名和变量值的关联，此处应建立索引和变量（名）的关联。由于方法开始前，两个散列表是一致的，此
         * 处直接put会创建新的key-value对，不会覆盖原key的所对的值，因为一致性确保了方法被调用前的表长度不是index_nanme的索
         * 引
         */
        index_name.put(Integer.valueOf(variableCountOld),variableName);
        rwLock.writeLock().unlock();
    }
    public void add   (String variableName             ){

        // 由于value是空字符串，这一定不是null，在add(String,String)中无需判空。直接复制代码再删去判空的分支即可
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

        /* 
         * 
         * 若参数是null，则由于散列表每个key和value都不是null，remove什么都不做然后返回null，此时应抛出NPE，反之参数不是
         * null，若这个VC不存在这个名称的变量，则由于散列表中找不到这个key，remove什么都不做然后返回null，此时应抛出
         * RuntimeException，反之存在这个名称的变量，则移除这个key（和所对的value），然后返回这个value，散列表合法性保证
         * 了这个返回值一定不是null，移除成功
         */
        if(name_value.remove(variableName)==null){
            rwLock.writeLock().unlock();
            if(variableName==null)throw new NullPointerException();
            throw new RuntimeException();
        }

        /* 
         * 
         * 执行到此处，表明移除变量成功，具体来说，移除了变量名-变量值的关联，下面要修改索引-变量的关联。具体来说，由于这个方法开始前的
         * index_name和name_value是一致的，此时一定可以在index_name中找到一个索引-变量（名）的关联（key-value
         * 对），使得它储存的变量名是参数，现在这个变量（名）被移除了，这个位置之后的每个变量名的索引都应该向前错一位，即让它们的索引变成当前索引减去
         * 1的结果，最后最大的索引会空余出来，需把这个空余的对移除以保证index_name和name_value一致
         * 
         * 由于声明和移除变量是动态的，若一个变量需要被移除，它更有可能是在这个VC中使用add临时添加的，因此它更可能有更靠后的索引，因此我线性找索
         * 引可以从末尾开始遍历，这不会引起任何问题，因为index_name是双射
         * 
         * 需注意，由于执行到此处时移除一定是成功的，移除前这个VC必然存在至少一个变量，而此时name_value更新了，index_name仍未更
         * 新，所以name_value.size()一定至少是0，index_name.size()一定等于name_value.size()+1，
         * 而从index_name末尾到头遍历时，应从索引index_name.size()-1开始，显然这等于name_value.size()，
         * 这也是移除变量后新的散列表应有的长度
         */
        int variableCountNew=name_value.size();
        Integer i=Integer.valueOf(variableCountNew);
        while(!index_name.get(i).equals(variableName))
            i=Integer.valueOf(i.intValue()-1);
        /* 
         * 
         * 需注意，对于以上循环，由于index_name长度至少是1，且一定存在一个key-value对的value是参数，因此遍历时一定不会越界，
         * 且while循环结束后，索引i在index_name中一定指向参数
         */

        /* 
         * 
         * 此时已经找到了移除的变量（名）的索引i，这个位置是空出来的，接下来要做的是控制另一个指针k=i+1指向i的下一个位置，取下一个变量（名），
         * 让空出来的i指向下一个变量名，然后i和k一起右移，重复以上过程，直到k不再指向变量名时停止
         * 
         * 此时i指向被删除的变量，这个位置之后的每个变量的索引都应该减去1，这等同于从i当前位置开始，向右（i自加1）遍历，过程中让一个指针k总是等
         * 于i+1，用于获取i（原本）所对的变量的下一个变量的名称（索引为k），然后让i=k-1指向这个变量名，然后两个指针向右移动一个单位，重复以
         * 上操作，直到k不再指向一个变量名，这时候i=k-1恰好是该方法开始前最大的索引，也就是末尾，它现在是空余的，直接删除index_name中
         * 的这个key-value对即可。其中“k不再指向一个变量名”是指，k从i+1开始递增，直到k<index_name.size()不成立，要
         * 求的继续循环的条件是k<index_name.size()，从前一段的推导过程可以知道
         * index_name.size()=name_size()-1，代入上式可以得到k<name_size()-1，即
         * k+1<name_value.size()，其中k+1恰好是i，因此直接使用i<name_value.size()即可，其中
         * name_size()是更新后散列表应有的长度
         */
        Integer k=Integer.valueOf(i.intValue()+1);
        while(i.intValue()<variableCountNew){
            index_name.put(i,index_name.get(k));
            k=Integer.valueOf((i=k).intValue()+1);
        }
        index_name.remove(i);
        rwLock.writeLock().unlock();
    }

    /* 
     * 
     * HashMap允许key或value是null，所以containsKey和containsValue传参前需要确保参数不是null，当参数是
     * null时抛出NPE。在indexOf中，我意图用variableName.equals和value.equals以自动在variableName
     * 和value为null时抛出NPE，但当表长度为0时循环体不执行，不会调用equals方法，所以最优雅的方法依然是手动判断参数是否是null，然后
     * 再遍历。由于散列表保证合法，区间上的每个index一定可以得到不是null的字符串，然后用这个不是null的字符串和参数比较。indexOf通常返
     * 回最小的满足性质的索引，因此根据变量名查找时可以从末尾到头遍历，原因是变量名只有可能不出现或恰好只出现一次，若不存在，则遍历不会找到任何一个符合条
     * 件的索引，若存在，则两个方向遍历一定会返回相同的索引。之所以选择使用从末尾到头的顺序，是因为当需要查找变量的位置时，极有可能这个变量是新添加的，因
     * 此它更可能接近表的末尾，从末尾开始遍历会比从头开始遍历快（前面remove方法的第一个while循环从末尾开始遍历，也是相同的原因）
     */
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
        /* 
         * 
         * 若index是null，由于index_name不含null，index_name.get(index)会返回null，反之index不是
         * null，索引可能越界或不越界，越界时由于找不到这个key，所以会返回null。因此，只有当t不是null时，t才是索引为i的变量的名称，
         * 反之就抛出异常
         */
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
        /* 
         * 
         * 代码全部来自variableAt(Integer)，但这里的index一定不是null，抛出异常只可能是数组越界导致的，所以可以把复制过来
         * 的代码的RuntimeException换成IndexOutOfBoundsException
         */
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
        /* 
         * 
         * 若variableName是null，由于name_value不存在null key，而且调用的是replace方法，这个方法什么都不做然
         * 后返回null，这应该抛出异常，反之variableName不是null，可能存在或不存在名称为variableName的变量，若不存在，
         * 由于找不到key，它什么都不做直接返回null，这应该抛出异常，反之存在，variableName会映射到新的value
         */
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
        /* 
         * 
         * 这里t是null只有可能是两个原因：参数是null，找不到null key所以返回null，或者参数不是null，但是找不到这个变量名的变
         * 量，所以返回null。这两种情况都应该抛出异常
         */
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
    private void recover(String[]backup){
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
        }
        rwLock.writeLock().unlock();
    }
    public void setto        (String...strings){

        /*rwLock.writeLock().lock();
        String[]backup=toStrings();
        clear();
        try{for(int k=0;k!=strings.length;k+=2)add(strings[k],strings[k+1]);
        }catch(Throwable e){
            recover(backup);
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }*/
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<k>>1)throw new IllegalArgumentException();
        rwLock.writeLock().lock();
        String[]backup=toStrings();
        index_name.clear();
        name_value.clear();
        try{while(k>0){
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
            throw e;
        }finally{
            rwLock.writeLock().unlock();
        }

    }
    public VariableCollection(String...strings){

        //setto(strings);
        int k=strings.length;
        if((k&1)==1||MAX_VARIABLE_COUNT<k>>1)throw new IllegalArgumentException();
        index_name.clear();
        name_value.clear();
        try{while(k>0){
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

            throw e;
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
        int variableCount=variableCount();
        StringBuilder str=new StringBuilder(
            ls[0]+variableCount*(2+nextLine.length())+
            (int)(1.06d*(double)ls[1])
        );
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
    public String toString   (){
        return toString(1);
    }
    public byte[] toBytes    (){
        return toString(0).getBytes(StandardCharsets.UTF_8);
    }
    public void setto        (String string){
        // 如果没有语句的话，它就应该只包含空符号，这表示VC没有包含任何变量，只调用clear()即可。反之就备份当前状态，然后清空再逐个语句读取，
        // 添加变量和赋值（用add(String,String)），把这放在try里，当抛出异常时跳转到catch，然后用备份回滚
        int l=string.length(),i=l; // 如果string是null，这个语句会自动抛出NPE
        boolean allEmpty=true;
        char c='J';
        while(allEmpty&&i>0)
            allEmpty=' '==(c=string.charAt(--i))
                ||  '\n'== c;
        // i是0的话，string就是空字符串，那么while里的语句完全不会被执行，allEmpty依然是true，所以空字符串也算作是没有语句
        if(allEmpty){

            //clear();
            rwLock.writeLock().  lock();
            index_name.clear();
            name_value.clear();
            rwLock.writeLock().unlock();

            return;
        }
        // 前面找非空字符是从末尾遍历的。如果找到了至少一个非空字符，并且执行到这里，就说明allEmpty一定是false，那么c一定被赋值过，而且
        // c是最后一个非空字符，只有当这个字符是';'的时候，string的语法才是正确的。保证最后一个语句必然以';'结尾非常重要，因为后面循环解
        // 字符串和检查语法都要用到这个事实
        if(c!=';')throw new IllegalArgumentException();
        // 当i不是最后一个char的时候，取substring，让最后一个语句结束后（';'之后）没有任何符号。这是为了检验是否有下一个语句，具体来
        // 说，语句正式开始前可能有一堆空字符，这个时候需要不断让指针右移，直到找到第一个非空字符。如果最后一个语句后面还有空字符的话，循环体会不断右
        // 移指针，结果它就会越界。但是如果我保证了';'就是最后一个字符的话，我就可以在找到了';'之后，让索引自加1，这之后，如果索引是字符串的长
        // 度的话，就代表指针已经越界了，然后结束循环。
        if(++i!=l)string=string.substring(0,l=i);
        i=0;
        rwLock.writeLock().lock();
        String[]backup=toStrings();

        //clear();
        index_name.clear();
        name_value.clear();

        // 这是优化用的。我在遍历字符串的时候，顺便把变量名和值的合法性也检验了，如果直接调用add方法，add方法会再检查一次变量名合法性，所以我直
        // 接把add的代码抄了过来，然后用局部变量variableCount来表示，已经添加了多少个变量
        int variableCount=0;

        try{do{ if(variableCount==MAX_VARIABLE_COUNT)throw new IllegalArgumentException();
                while( ' '==(c=string.charAt(i))
                    ||'\n'== c
                )++i;
                // 这个时候第i指向第一个非空字符，字符被储存在c里，这个字符是变量名的第一个字符
                if( (   !(  ('a'<=c&&c<='z')||
                            ('A'<=c&&c<='Z')
                        )
                    )
                    &&c!='_'
                    &&c!='$'
                )throw new IllegalArgumentException();
                int begin=i;
                // 记住变量名第一个字符的索引，然后i一直右移，直到遇到第一个空符号或者赋值符号（等于号）
                // 这不可能越界，因为前面保证了，字符串一定以';'结尾
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
                    // 这些连续出现的非空符号应该都是变量名的一部分，遍历的同时直接检验变量名合法性
                    // 这不会越界，因为前面保证了字符串一定以';'结尾，如果读取到';'，这就会被视为变量名的一部分，但这个符号不允许出
                    // 现在变量名里，会手动抛出异常
                    c=string.charAt(++i);
                }
                // 这个时候i指向变量名最后一个字符的下一个字符
                if(MAX_VARIABLE_NAME_LENGTH<i-begin)throw new IllegalArgumentException();
                String n=string.substring(begin,i);
                // 变量名后面可能直接接赋值符号，或者一些空字符，然后有赋值符号。下面的循环会去找第一个非空符号
                // 这不会越界，因为字符串以';'结尾
                while(c==' '
                    ||c=='\n'
                )c=string.charAt(++i);
                // 变量名之后应该接赋值符号，如果不是的话，语法就是错误的
                if(c!='=')throw new IllegalArgumentException();
                // 找到等于号之后，右移，然后找下一个非空符号
                // 由于同样的原因，这不会越界
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                // 赋值号之后，应该接引号，表示引号之后的内容是值，如果不是引号的话，语法就是错误的
                if(c!='"')throw new IllegalArgumentException();
                // 长度大于64的情况很少，因为VC对象主要是给数据库使用的。就算不够用它也会自动扩容。过大的数据可能不会放在数据库里，而是放
                // 在磁盘上不属于数据库的地方，然后在数据库里储存它们的路径，充当指针
                StringBuilder v=new StringBuilder(64);

                // 重复，直到找到下一个引号，表示值的部分结束
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
                            // 如果这一个符号不是引号、不是转义符，也不是换行符的话，按照预期它应该是值的一部分，但是它也有可能是
                            // ';'，也就是说程序可能因为一直找不到引号，导致指针一直往右移动然后越界，所以这里右移指针时应判断
                            // 下一个位置有没有越界，越界表明语句语法不正确，抛IllegalArgumentException
                            if(++i==l)throw new IllegalArgumentException();
                            v.append(c);
                        }
                    }

                // 这个时候，i指向引号，右移然后找第一个非空符号
                do c=string.charAt(++i);while(
                    c==' '||
                    c=='\n'
                );
                // 语句必须用';'表示结束
                if(c!=';')throw new IllegalArgumentException();

                //add(n,v.toString());
                if(name_value.putIfAbsent(n,v.toString())!=null)
                    throw new RuntimeException();
                index_name.put(Integer.valueOf(variableCount),n);
                ++variableCount;

                // 这个时候指针指向';'，往右移一次，如果依然小于字符串的长度，就说明后面还有语句要读
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
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
}