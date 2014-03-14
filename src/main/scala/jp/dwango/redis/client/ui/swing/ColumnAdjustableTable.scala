package jp.dwango.redis.client.ui.swing

import javax.swing.table.{DefaultTableModel, TableColumn}
import jp.dwango.redis.client.{ResultTableRow, KeyType}
import scala.swing.{Component, Table}

class ColumnAdjustableTable() extends Table {

  override def model = peer.getModel.asInstanceOf[DefaultTableModel]

  autoResizeMode = Table.AutoResizeMode.Off

  def setHeader(dataType: KeyType) = {}

  def setHeader(header: Seq[String] = Seq.empty) = {
    peer.setModel(new DefaultTableModel)
    header.foreach(model.addColumn(_))
  }

  override protected def rendererComponent(isSelected: Boolean, focused: Boolean, row: Int, column: Int): Component = {
    val r = super.rendererComponent(isSelected, focused, row, column)
    val d = model.getValueAt(row, column).toString
    r.tooltip = if (isBinary(d)) BinaryDataUtil.toBinaryString(d) else d
    r
  }

  /**
   * リーダブルなCharであるか
   * @return
   */
  private def extendedCode: (Char) => Boolean = (c: Char) => (c < 32 || c >= 127)

  /**
   * バリューの中にバイナリデータがあるか
   * @param value
   * @return
   */
  private def isBinary(value: String): Boolean = {
    value.filter(extendedCode).length > 0
  }

  def setData(rows: Seq[ResultTableRow]) = {
    model.setRowCount(0)
    if (rows.length > 0) {
      rows.map {
        row =>
          model.addRow(row.toArray)
      }
      adjustColumns()
    }
    model.fireTableDataChanged()
  }

  private def adjustColumns() {
    (0 to peer.getColumnCount - 1) foreach {
      col =>
        adjustColumn(col)
    }
  }

  private def adjustColumn(column: Int) {
    val tableColumn = peer.getColumnModel().getColumn(column)

    val columnHeaderWidth = getColumnHeaderWidth(column, tableColumn)
    val columnDataWidth = getColumnDataWidth(column)
    val preferredWidth = Math.max(columnHeaderWidth, columnDataWidth)
    peer.getColumnModel.getColumn(column).setPreferredWidth(preferredWidth)
  }

  private def getColumnDataWidth(column: Int): Int = {
    (0 to peer.getRowCount - 1).map {
      row =>
        getCellDataWidth(row, column)
    }.max
  }

  private def getColumnHeaderWidth(column: Int, tableColumn: TableColumn): Int = {
    val value = tableColumn.getHeaderValue()
    val renderer = if (tableColumn.getHeaderRenderer() == null) {
      peer.getTableHeader().getDefaultRenderer()
    } else {
      tableColumn.getHeaderRenderer()
    }

    val c = renderer.getTableCellRendererComponent(peer, value, false, false, -1, column)
    c.getPreferredSize().width
  }

  /**
   * Get the preferred width for the specified cell
   */
  private def getCellDataWidth(row: Int, column: Int): Int = {
    //  Invoke the renderer for the cell to calculate the preferred width
    val cellRenderer = peer.getCellRenderer(row, column)
    val c = peer.prepareRenderer(cellRenderer, row, column)
    c.getPreferredSize().width + peer.getIntercellSpacing().width
  }
}

object BinaryDataUtil {
  /**
   * 16進数でバイナリデータを表示する
   * @param value
   * @return
   */
  def toBinaryString(value: String): String = {
    value.map(char => "%02X".format(char.toLong)).mkString(" ")
  }
}