package jp.dwango.redis.client.ui.swing

import javax.swing.event.TreeSelectionListener
import javax.swing.JTree
import javax.swing.tree.{DefaultTreeModel, DefaultMutableTreeNode}
import jp.dwango.redis.client.{NoneType, KeyWithHostName, ConfigProvider}
import scala.swing.Component
import scala.collection.JavaConversions._

class Tree(val model: DefaultTreeModel) extends Component {

  val Divider = ConfigProvider.redisDataConfig.divider

  override lazy val peer: JTree = new JTree(model) with SuperMixin
  protected def scrollablePeer = peer

  def addTreeSelectionListener(tsl: TreeSelectionListener) = peer.addTreeSelectionListener(tsl)

  /**
   * Keyの配列からツリーの構造を生成する
   *
   * @param keyword
   * @param keys
   * @return
   */
   def generateTree(keyword: String, keys: Seq[KeyWithHostName]): Unit =  {
    val root = new DefaultMutableTreeNode(keyword)
    val newKeyword = if (keyword.indexOf("*") >= 0) "" else keyword

    keys.map { key =>
      var currentNode = root
      var currentPath = newKeyword
      val str = key.key.substring(currentPath.length)

      val segments = str.split(Divider)
      var i = 0
      segments.foreach { segment =>
        currentPath = if (i == 0) (currentPath + segment) else (currentPath + Divider + segment)
        val existNode = getChildByName(currentNode, currentPath)
        currentNode = existNode.getOrElse{
          val newNode = new DefaultMutableTreeNode(KeyWithHostName(key.host, currentPath, key.keyType))
          currentNode.add(newNode)
          newNode
        }
        i = i + 1
      }
    }
    compactTree(root)
    countLeaves(root)
    peer.setModel(new DefaultTreeModel(root))
  }

  private def getChildByName(node: DefaultMutableTreeNode, name: String): Option[DefaultMutableTreeNode] = {
    val nodes = node.children().map { case n: DefaultMutableTreeNode => n }.toSeq
    nodes.flatMap { n =>
      n.getUserObject match {
        case key: KeyWithHostName if key.key.equalsIgnoreCase(name) =>
          Some(n)
        case _ => None
      }
    }.toSeq.headOption
  }

  /**
   * ツリーの中に、直下にリーフがないNODEを削除する
   * @param parent
   */
  private def compactTree(parent: DefaultMutableTreeNode): Unit = {
    parent.children().foreach {
      child =>
        val node = child.asInstanceOf[DefaultMutableTreeNode]
        mergeNodes(node, parent)
    }
  }

  /**
   * 直下にリーフがないNODEを親ノードと併合する
   * @param node
   * @param parent
   */
  private def mergeNodes(node: DefaultMutableTreeNode, parent: DefaultMutableTreeNode): Unit = {
    if (node.getChildCount() == 1) {
      val child = node.getFirstChild().asInstanceOf[DefaultMutableTreeNode]
      val index = parent.getIndex(node)
      parent.remove(node)
      parent.insert(child, index)
      mergeNodes(child, parent)
    } else {
      compactTree(node)
    }
  }

  /**
   * 自分直下のリーフである子ノードの数を計算して、ノードのUserObjectに保存する
   *
   * @param node
   * @return
   */
  def countLeaves(node: DefaultMutableTreeNode): Int = {
    if (!node.isLeaf()) {
      var childLeavesCount = 0
      node.children().foreach { child =>
        val childNode = child.asInstanceOf[DefaultMutableTreeNode]
        childLeavesCount = childLeavesCount + countLeaves(childNode)
      }
      val leafCount = node.getLeafCount - childLeavesCount
      node.getUserObject() match {
        case key: KeyWithHostName =>
          node.setUserObject(key.copy(keyType = NoneType, leafNum = leafCount))
          leafCount
        case _ =>
          0
      }
    } else {
      0
    }
  }
}
