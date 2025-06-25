package test.com.luotao.数据结构与算法.mytree;

import java.util.*;

/**
 * @Classname MyTree
 * @Description
## 树?
> 子树：结点>1时，其余的结点分为的互不相交的集合
> 度：一个结点拥有的子树数量叫结点的度
> 树的高度：根结点的高度
> 结点的高度：结点到叶子结点的最长路径
> 结点的深度：根结点到该结点的边个数
> 结点的层数: 结点的深度+1
> 叶子：度为0的结点
> 孩子结点：结点的子树的根
> 双亲：和孩子结点对应
> 兄弟：同一个双亲结点
> 森林：N哥互不相交的树
 * @Version 1.0.0
 * @Date 2025/5/15 14:34
 * @Author LuoTao
 */
public class MyTree {
}

/**
## 二叉树(binary tree)?
> - 每个结点最多只有两颗子树
> - 第N层上最多有2^(n-1)个结点（1 2 4 8...），最多有2^n-1个结点数量
> - 满二叉树：除叶子结点外，每个结点都有左右两个子节点


## 为什么需要区分一个完全二叉树出来？
        A
      /   \
     B     C
   /  \   /
  D    E F
数组索引： [0] [1] [2] [3] [4] [5]
对应节点：  A   B   C   D   E   F
关系：
- A 的左孩子是 B（索引 0 → 1）
- A 的右孩子是 C（索引 0 → 2）
- B 的左孩子是 D（索引 1 → 3）
- B 的右孩子是 E（索引 1 → 4）
- C 的左孩子是 F（索引 2 → 5）

> - 完全二叉树：除最后一层外，其余层的结点个数必须达到最大，并且最后一层结点都连续靠左排列
> - 完全二叉树是满二叉树里面的一个子集
> - 数组的性能是高效的，可以用数组来存储完全二叉树。

前序遍历（根→左→右）- 递归 + 迭代
中序遍历（左→根→右）- 递归 + 迭代
后序遍历（左→右→根）- 递归 + 迭代
层序遍历（逐层访问）- 队列实现
* @Author: LuoTao
* @Date: 2025-05-15 14:58:45
**/
class BinaryTree{
    public static void main(String[] args) {
        testAllTraversals();
    }
    /**
    * 二叉树节点定义
    * 这是二叉树的基本组成单元，每个结点包括：
     * 1.数据域：存储结点的值
     * 2.左指针：指向左子树
     * 3.右指针：指向右子树
    * @author: LuoTao
    * 2025-05-23 08:26:38
    **/
    static class TreeNode{
        int val;        // 数据域：存储结点的值
        TreeNode left;  // 左指针：指向左子树
        TreeNode right; // 右指针：指向右子树

        // 构造叶子节点（无子节点）
        TreeNode(int val) {
            this.val = val;
            this.left = null;
            this.right = null;
        }

        // 构造非叶子节点（有左右子节点）
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    // ===========================================
    // 深度优先遍历 - 递归实现（推荐使用，简洁易懂）
    // ===========================================

    /**
     * 前序遍历 - 递归实现
     * 访问顺序：根节点 → 左子树 → 右子树
     *
     * 应用场景：
     * 1. 复制二叉树：先复制根节点，再复制左右子树
     * 2. 打印树的结构：可以清晰看到树的层次关系
     * 3. 序列化二叉树：将树转换为字符串保存
     *
     * 时间复杂度：O(n) - 每个节点访问一次
     * 空间复杂度：O(h) - h为树的高度，最坏情况O(n)
     */
    public static void preorderRecursive(TreeNode root, List<Integer> result) {
        // 基准条件：如果当前节点为空，直接返回
        if (root == null) {
            return;
        }

        // 1. 访问根节点：将当前节点的值加入结果
        result.add(root.val);
        System.out.print(root.val + " ");  // 可视化输出

        // 2. 递归遍历左子树：处理所有左子树节点
        preorderRecursive(root.left, result);

        // 3. 递归遍历右子树：处理所有右子树节点
        preorderRecursive(root.right, result);
    }

    /**
     * 中序遍历 - 递归实现
     * 访问顺序：左子树 → 根节点 → 右子树
     *
     * 应用场景：
     * 1. 二叉搜索树：中序遍历可以得到升序序列
     * 2. 表达式树：中序遍历可以还原中缀表达式
     * 3. 验证二叉搜索树：检查遍历结果是否为升序
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(h)
     */
    public static void inorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        // 1. 递归遍历左子树：先处理所有左子树节点
        inorderRecursive(root.left, result);

        // 2. 访问根节点：在左右子树之间访问根节点
        result.add(root.val);
        System.out.print(root.val + " ");

        // 3. 递归遍历右子树：最后处理所有右子树节点
        inorderRecursive(root.right, result);
    }

    /**
     * 后序遍历 - 递归实现
     * 访问顺序：左子树 → 右子树 → 根节点
     *
     * 应用场景：
     * 1. 删除二叉树：必须先删除子节点，再删除父节点
     * 2. 计算目录大小：先计算子目录大小，再计算当前目录
     * 3. 表达式求值：后缀表达式求值
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(h)
     */
    public static void postorderRecursive(TreeNode root, List<Integer> result) {
        if (root == null) {
            return;
        }

        // 1. 递归遍历左子树：先处理左子树
        postorderRecursive(root.left, result);

        // 2. 递归遍历右子树：再处理右子树
        postorderRecursive(root.right, result);

        // 3. 访问根节点：最后处理根节点
        result.add(root.val);
        System.out.print(root.val + " ");
    }

    // ===========================================
    // 深度优先遍历 - 迭代实现（使用栈模拟递归）
    // ===========================================

    /**
     * 前序遍历 - 迭代实现
     * 使用栈来模拟递归过程
     *
     * 核心思想：
     * 1. 栈的特性是后进先出(LIFO)
     * 2. 由于前序遍历是 根→左→右，为了保证左子树先于右子树被访问
     * 3. 我们需要先将右子树压入栈，再将左子树压入栈
     *
     * 优势：避免递归调用，在极深的树中不会栈溢出
     */
    public static List<Integer> preorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        // 使用栈来模拟递归调用栈
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);  // 首先将根节点压入栈

        System.out.print("前序遍历(迭代): ");
        while (!stack.isEmpty()) {
            // 弹出栈顶节点并访问
            TreeNode node = stack.pop();
            result.add(node.val);
            System.out.print(node.val + " ");

            // 关键：先压入右子树，再压入左子树
            // 这样左子树会先被弹出（栈的LIFO特性）
            if (node.right != null) {
                stack.push(node.right);  // 右子树后访问，所以先压入
            }
            if (node.left != null) {
                stack.push(node.left);   // 左子树先访问，所以后压入
            }
        }
        System.out.println();
        return result;
    }

    /**
     * 中序遍历 - 迭代实现
     *
     * 核心思想：
     * 1. 不断向左走到最深处，将路径上的所有节点压入栈
     * 2. 当无法继续向左时，弹出栈顶节点并访问
     * 3. 然后转向该节点的右子树，重复上述过程
     *
     * 这个过程正好符合 左→根→右 的访问顺序
     */
    public static List<Integer> inorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        System.out.print("中序遍历(迭代): ");
        while (current != null || !stack.isEmpty()) {
            // 阶段1：一直向左走，将路径上的节点都压入栈
            while (current != null) {
                stack.push(current);
                current = current.left;  // 继续向左
            }

            // 阶段2：弹出栈顶节点并访问（这是最左的未访问节点）
            current = stack.pop();
            result.add(current.val);
            System.out.print(current.val + " ");

            // 阶段3：转向右子树
            current = current.right;
        }
        System.out.println();
        return result;
    }

    /**
     * 后序遍历 - 迭代实现（方法一：双栈法）
     *
     * 核心思想：
     * 1. 后序遍历顺序是：左→右→根
     * 2. 我们可以先实现：根→右→左（前序遍历的镜像）
     * 3. 然后将结果反转，就得到了：左→右→根
     *
     * 实现步骤：
     * 1. 使用栈1按照 根→右→左 的顺序遍历
     * 2. 将访问的节点压入栈2
     * 3. 最后依次弹出栈2的元素，得到后序遍历结果
     */
    public static List<Integer> postorderIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack1 = new Stack<>();  // 用于遍历
        Stack<TreeNode> stack2 = new Stack<>();  // 用于存储结果

        stack1.push(root);

        // 阶段1：按照 根→右→左 的顺序遍历，结果存入stack2
        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);  // 将访问的节点压入结果栈

            // 注意：这里先压入左子树，再压入右子树
            // 这样右子树会先被弹出，形成 根→右→左 的顺序
            if (node.left != null) {
                stack1.push(node.left);
            }
            if (node.right != null) {
                stack1.push(node.right);
            }
        }

        // 阶段2：弹出stack2中的所有元素，得到后序遍历结果
        System.out.print("后序遍历(迭代): ");
        while (!stack2.isEmpty()) {
            TreeNode node = stack2.pop();
            result.add(node.val);
            System.out.print(node.val + " ");
        }
        System.out.println();
        return result;
    }

    // ===========================================
    // 广度优先遍历 - 层序遍历
    // ===========================================

    /**
     * 层序遍历 - 队列实现
     * 访问顺序：从上到下，从左到右，一层一层访问
     *
     * 应用场景：
     * 1. 寻找最短路径：在无权图中寻找最短路径
     * 2. 逐层处理：比如打印每一层的节点
     * 3. 判断是否为完全二叉树
     * 4. 序列化和反序列化二叉树
     *
     * 核心思想：
     * 1. 使用队列的先进先出(FIFO)特性
     * 2. 先访问当前节点，再将其子节点加入队列
     * 3. 这样可以保证同一层的节点先被访问，下一层的节点后被访问
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(w) - w为树的最大宽度
     */
    public static List<Integer> levelOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        // 使用队列来实现层序遍历
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);  // 将根节点加入队列

        System.out.print("层序遍历: ");
        while (!queue.isEmpty()) {
            // 弹出队首节点并访问
            TreeNode node = queue.poll();
            result.add(node.val);
            System.out.print(node.val + " ");

            // 将子节点按从左到右的顺序加入队列
            if (node.left != null) {
                queue.offer(node.left);   // 左子节点先入队
            }
            if (node.right != null) {
                queue.offer(node.right);  // 右子节点后入队
            }
        }
        System.out.println();
        return result;
    }

    /**
     * 分层的层序遍历 - 返回每一层的节点
     *
     * 应用场景：
     * 1. 打印二叉树的层次结构
     * 2. 寻找每一层的最大值/最小值
     * 3. 判断二叉树是否对称
     *
     * 核心思想：
     * 1. 在每一轮循环开始时，记录当前队列的大小
     * 2. 这个大小就是当前层的节点个数
     * 3. 只处理这么多个节点，就完成了一层的遍历
     */
    public static List<List<Integer>> levelOrderByLevel(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        System.out.println("分层的层序遍历:");
        int level = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();  // 当前层的节点个数
            List<Integer> currentLevel = new ArrayList<>();

            System.out.print("第" + level + "层: ");

            // 处理当前层的所有节点
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                System.out.print(node.val + " ");

                // 将下一层的节点加入队列
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            result.add(currentLevel);
            System.out.println();
            level++;
        }
        return result;
    }

    // ===========================================
    // 工具方法
    // ===========================================

    /**
     * 创建测试用的二叉树
     * 构建如下结构的二叉树：
     *       1
     *      / \
     *     2   3
     *    / \   \
     *   4   5   6
     *
     * 各种遍历的预期结果：
     * - 前序遍历：1 2 4 5 3 6
     * - 中序遍历：4 2 5 1 3 6
     * - 后序遍历：4 5 2 6 3 1
     * - 层序遍历：1 2 3 4 5 6
     */
    public static TreeNode createTestTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.right = new TreeNode(6);
        return root;
    }

    /**
     * 打印二叉树的结构（简单的可视化）
     * 这个方法可以帮助你直观地看到二叉树的结构
     */
    public static void printTree(TreeNode root, String prefix, boolean isLeft) {
        if (root == null) {
            return;
        }

        System.out.println(prefix + (isLeft ? "├── " : "└── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLeft ? "│   " : "    "), true);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "├── null");
            }

            if (root.right != null) {
                printTree(root.right, prefix + (isLeft ? "│   " : "    "), false);
            } else {
                System.out.println(prefix + (isLeft ? "│   " : "    ") + "└── null");
            }
        }
    }

    // ===========================================
    // 测试方法
    // ===========================================

    /**
     * 二叉树遍历最佳实践演示
     */
    public static void testAllTraversals() {
        System.out.println("=== 二叉树遍历最佳实践演示 ===\n");

        // 1. 创建测试用的二叉树
        TreeNode root = createTestTree();

        // 2. 打印二叉树结构
        System.out.println("二叉树结构:");
        printTree(root, "", false);
        System.out.println();

        // 3. 深度优先遍历 - 递归实现
        System.out.println("=== 深度优先遍历（递归实现）===");

        List<Integer> preorderResult = new ArrayList<>();
        System.out.print("前序遍历(递归): ");
        preorderRecursive(root, preorderResult);
        System.out.println();

        List<Integer> inorderResult = new ArrayList<>();
        System.out.print("中序遍历(递归): ");
        inorderRecursive(root, inorderResult);
        System.out.println();

        List<Integer> postorderResult = new ArrayList<>();
        System.out.print("后序遍历(递归): ");
        postorderRecursive(root, postorderResult);
        System.out.println();
        System.out.println();

        // 4. 深度优先遍历 - 迭代实现
        System.out.println("=== 深度优先遍历（迭代实现）===");
        preorderIterative(root);
        inorderIterative(root);
        postorderIterative(root);
        System.out.println();

        // 5. 广度优先遍历
        System.out.println("=== 广度优先遍历 ===");
        levelOrder(root);
        levelOrderByLevel(root);

        // 6. 总结说明
        System.out.println("\n=== 总结 ===");
        System.out.println("1. 递归实现：代码简洁，易于理解，是面试中的首选方案");
        System.out.println("2. 迭代实现：避免递归调用栈，适用于极深的树");
        System.out.println("3. 层序遍历：使用队列实现，适用于逐层处理的场景");
        System.out.println("4. 时间复杂度：所有遍历方法都是O(n)");
        System.out.println("5. 空间复杂度：递归O(h)，迭代O(h)或O(w)，h为高度，w为宽度");
    }


}

/**
* ## 二叉搜索树
对于任意一个节点，它的左子树中所有节点的值都小于该节点的值。
对于任意一个节点，它的右子树中所有节点的值都大于该节点的值。
它的左右子树也分别是二叉搜索树
* @author: LuoTao
* 2025-05-25 15:16:51
**/
class BinarySearchTree{
}