/* BaseHash.java --
   版权所有 (C) 2001, 2002, 2006 Free Software Foundation, Inc.

   此文件是 GNU Classpath 的一部分。

   GNU Classpath 是自由软件；您可以根据自由软件基金会发布的 GNU 通用公共许可证的条款重新分发和/或修改它；无论是许可证的第 2 版，还是（根据您的选择）任何更高版本。

   GNU Classpath 的分发是希望它有用，但没有任何担保；甚至没有适销性或特定用途适用性的隐含担保。有关更多详细信息，请参阅 GNU 通用公共许可证。

   您应该已经随 GNU Classpath 一起收到了 GNU 通用公共许可证的副本；如果没有，请写信给自由软件基金会，地址：51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

   将此库静态或动态地与其他模块链接是基于此库的联合工作。因此，GNU 通用公共许可证的条款和条件涵盖整个组合。

   作为特殊例外，此库的版权持有人允许您将此库与独立模块链接以生成可执行文件，无论这些独立模块的许可条款如何，并根据您选择的条款复制和分发生成的可执行文件，前提是您还满足每个链接的独立模块的许可条款和条件。独立模块是不是从此库派生或基于此库的模块。如果您修改此库，您可以将此例外扩展到您的库版本，但您没有义务这样做。如果您不希望这样做，请从您的版本中删除此例外声明。*/

package com.hobbyone.HashDroid;

/**
 * <p>
 * 一个用于促进哈希实现的基础抽象类。
 * </p>
 */
public abstract class BaseHash implements IMessageDigest {

    // 常量与变量
    // -------------------------------------------------------------------------

    /**
     * 哈希的规范名称前缀。
     */
    protected String name;

    /**
     * 哈希（输出）大小，以字节为单位。
     */
    protected int hashSize;

    /**
     * 哈希（内部）块大小，以字节为单位。
     */
    protected int blockSize;

    /**
     * 迄今为止已处理的字节数。
     */
    protected long count;

    /**
     * 临时输入缓冲区。
     */
    protected byte[] buffer;

    // 构造方法
    // -------------------------------------------------------------------------

    /**
     * <p>
     * 供具体子类使用的简单构造方法。
     * </p>
     *
     * @param name      此实例的规范名称前缀。
     * @param hashSize  输出的块大小，以字节为单位。
     * @param blockSize 内部转换的块大小，以字节为单位。
     */
    protected BaseHash(String name, int hashSize, int blockSize) {
        super();

        this.name = name;
        this.hashSize = hashSize;
        this.blockSize = blockSize;
        this.buffer = new byte[blockSize];

        resetContext();
    }

    // 类方法
    // -------------------------------------------------------------------------

    // 实例方法
    // -------------------------------------------------------------------------

    // IMessageDigest 接口实现 ------------------------------------------------

    public String name() {
        return name;
    }

    public int hashSize() {
        return hashSize;
    }

    public int blockSize() {
        return blockSize;
    }

    public void update(byte b) {
        // 计算尚未哈希的字节数；即缓冲区中存在的字节数
        int i = (int) (count % blockSize);
        count++;
        buffer[i] = b;
        if (i == (blockSize - 1)) {
            transform(buffer, 0);
        }
    }

    public void update(byte[] b) {
        update(b, 0, b.length);
    }

    public void update(byte[] b, int offset, int len) {
        int n = (int) (count % blockSize);
        count += len;
        int partLen = blockSize - n;
        int i = 0;

        if (len >= partLen) {
            System.arraycopy(b, offset, buffer, n, partLen);
            transform(buffer, 0);
            for (i = partLen; i + blockSize - 1 < len; i += blockSize) {
                transform(b, offset + i);
            }
            n = 0;
        }

        if (i < len) {
            System.arraycopy(b, offset + i, buffer, n, len - i);
        }
    }

    public byte[] digest() {
        byte[] tail = padBuffer(); // 填充缓冲区中的剩余字节
        update(tail, 0, tail.length); // 消息的最后一次转换
        byte[] result = getResult(); // 从上下文中生成结果

        reset(); // 重置此实例以供将来重用

        return result;
    }

    public void reset() { // 重置此实例以供将来重用
        count = 0L;
        for (int i = 0; i < blockSize; ) {
            buffer[i++] = 0;
        }

        resetContext();
    }

    // 由具体子类实现的方法 ------------------------------------------------

    public abstract Object clone();

    public abstract boolean selfTest();

    /**
     * <p>
     * 返回在完成哈希操作之前用作填充的字节数组。
     * </p>
     *
     * @return 在完成哈希操作之前填充缓冲区中剩余字节的字节。
     */
    protected abstract byte[] padBuffer();

    /**
     * <p>
     * 从当前上下文的内容构造结果。
     * </p>
     *
     * @return 完成的哈希操作的输出。
     */
    protected abstract byte[] getResult();

    /**
     * 重置实例以供将来重用。
     */
    protected abstract void resetContext();

    /**
     * <p>
     * 块摘要转换本身。
     * </p>
     *
     * @param in     要摘要的 <i>blockSize</i> 长的块，作为字节数组。
     * @param offset 输入缓冲区中要摘要的数据所在的索引。
     */
    protected abstract void transform(byte[] in, int offset);
}
