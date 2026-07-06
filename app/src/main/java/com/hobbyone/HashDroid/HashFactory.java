/* HashFactory.java --
   版权所有 (C) 2001, 2002, 2003, 2006 Free Software Foundation, Inc.

   此文件是 GNU Classpath 的一部分。

   GNU Classpath 是自由软件；您可以根据自由软件基金会发布的 GNU 通用公共许可证的条款重新分发和/或修改它；无论是许可证的第 2 版，还是（根据您的选择）任何更高版本。

   GNU Classpath 的分发是希望它有用，但没有任何担保；甚至没有适销性或特定用途适用性的隐含担保。有关更多详细信息，请参阅 GNU 通用公共许可证。

   您应该已经随 GNU Classpath 一起收到了 GNU 通用公共许可证的副本；如果没有，请写信给自由软件基金会，地址：51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA

   将此库静态或动态地与其他模块链接是基于此库的联合工作。因此，GNU 通用公共许可证的条款和条件涵盖整个组合。

   作为特殊例外，此库的版权持有人允许您将此库与独立模块链接以生成可执行文件，无论这些独立模块的许可条款如何，并根据您选择的条款复制和分发生成的可执行文件，前提是您还满足每个链接的独立模块的许可条款和条件。独立模块是不是从此库派生或基于此库的模块。如果您修改此库，您可以将此例外扩展到您的库版本，但您没有义务这样做。如果您不希望这样做，请从您的版本中删除此例外声明。*/

package com.hobbyone.HashDroid;

/**
 * <p>
 * 一个用于实例化消息摘要算法实例的 <i>工厂</i>。
 * </p>
 */
public class HashFactory {

    // 常量与变量
    // -------------------------------------------------------------------------

    // 构造方法
    // -------------------------------------------------------------------------

    /**
     * 为强制实现 <i>单例</i> 模式的简单构造方法。
     */
    private HashFactory() {
        super();
    }

    // 类方法
    // -------------------------------------------------------------------------

    /**
     * <p>
     * 根据哈希算法的名称返回其实例。
     * </p>
     *
     * @param name 哈希算法的名称。
     * @return 哈希算法的实例，如果未找到则返回 null。
     * @throws InternalError 如果实现未通过其自检。
     */
    public static IMessageDigest getInstance(String name) {
        if (name == null) {
            return null;
        }

        name = name.trim();
        IMessageDigest result = null;
        if (name.equalsIgnoreCase("haval"))
            result = new Haval();
        else if (name.equalsIgnoreCase("md2"))
            result = new MD2();
        else if (name.equalsIgnoreCase("md4"))
            result = new MD4();
        else if (name.equalsIgnoreCase("md5"))
            result = new MD5();
        else if (name.equalsIgnoreCase("ripemd-128"))
            result = new RipeMD128();
        else if (name.equalsIgnoreCase("ripemd-160"))
            result = new RipeMD160();
        else if (name.equalsIgnoreCase("sha-1"))
            result = new Sha160();
        else if (name.equalsIgnoreCase("sha-256"))
            result = new Sha256();
        else if (name.equalsIgnoreCase("sha-384"))
            result = new Sha384();
        else if (name.equalsIgnoreCase("sha-512"))
            result = new Sha512();
        else if (name.equalsIgnoreCase("tiger"))
            result = new Tiger();
        else if (name.equalsIgnoreCase("whirlpool"))
            result = new Whirlpool();

        return result;
    }
}
