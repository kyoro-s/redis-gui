package jp.dwango.redis.client.ui.swing

import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.tree.{TreeNode, DefaultMutableTreeNode, DefaultTreeModel}
import jp.dwango.redis.client._
import scala.swing._
import javax.swing.JTree

class RedisPanel extends MigPanel with TreeSelectionListener {

  val uiConfig = ConfigProvider.uiConfig

  val clients = new RedisClients(ConfigProvider.redisClientConfig)

  val tree = new Tree(new DefaultTreeModel(new DefaultMutableTreeNode("root")))
  tree.addTreeSelectionListener(this)

  val dataTable = new ColumnAdjustableTable()

  private val typeLabel = new Label("")

  val tablePanel = new MigPanel {
    add(new Label("データタイプ："))
    add(typeLabel, "wrap")
    add(new ScrollPane(dataTable), "span, w 100%, h 100%, growx")
  }

  private val searchField = new TextField("*", 30)
  private val searchButton = new Button("Search")
  searchButton.action = Action("search") {
    // 該当キーの一覧を取得し、ツリーを生成
    val keyword = searchField.text
    val keys = clients.keys(keyword)
    tree.generateTree(keyword, keys)
  }

  private def init() = {
    add(new Label("キーワード"), "split 3")
    add(searchField)
    add(searchButton, "wrap")
    add(new SplitPane(Orientation.Vertical) {
      leftComponent = new ScrollPane(tree)
      rightComponent = tablePanel
      resizeWeight = uiConfig.splitWeight
    }, "w 100%, h 100%")
  }
  init()

  /**
   * treeのノードが選択された時の処理
   *
   * @param event
   */
  def valueChanged(event: TreeSelectionEvent) {
    event.getSource match {
      case t: JTree =>
        t.getLastSelectedPathComponent match {
          case node: DefaultMutableTreeNode =>
            showValuesInTable(node)
          case _ =>
        }
      case _ =>
    }
  }

  private def showValuesInTable(node: DefaultMutableTreeNode) {
    val dataConverters = ConfigProvider.dataConverterConfigs.map(DataConverterFactory.create(_))

    getKeysFromNode(node).map {
      key =>
        if (key.keyType != NoneType) {
          val rows = ResultTableRowFactory.getRows(key, clients)(dataConverters)

          dataTable.setHeader(Seq("host", "key", "field", "value", "score"))
          dataTable.setData(rows)
          typeLabel.text = key.keyType.stringValue
        } else {
          //リーフ以外のノード
          val keys = (1 to node.getChildCount).flatMap {
            index =>
              getLeafKeysFromNode(node.getChildAt(index - 1))
          }
          val rows = keys.flatMap {
            key =>
              if (key.keyType != NoneType) {
                ResultTableRowFactory.getRows(key, clients)(dataConverters)
              } else {
                None
              }
          }
          dataTable.setHeader(Seq("host", "key", "field", "value", "score"))
          dataTable.setData(rows)
          if (keys.map(_.keyType).distinct.length == 1) {
            typeLabel.text = keys.head.keyType.stringValue
          }
        }
    }
  }

  private def getKeysFromNode(node: TreeNode): Option[KeyWithHostName] = {
    val n = node.asInstanceOf[DefaultMutableTreeNode]
    n.getUserObject() match {
      case key: KeyWithHostName => Some(key)
      case _ => None
    }
  }

  private def getLeafKeysFromNode(node: TreeNode): Option[KeyWithHostName] = {
    getKeysFromNode(node).filter(_.keyType != NoneType)
  }
}
