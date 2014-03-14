package jp.dwango.redis.client.ui.swing

import scala.swing.{Component, LayoutContainer, Panel}
import net.miginfocom.swing.MigLayout

class MigPanel(
    val layoutConstraints: String = "",
    val columnConstraints: String = "",
    val rowConstraints: String = "")
  extends Panel with LayoutContainer {

  type Constraints = String

  override lazy val peer = {
    val mig = new MigLayout(
      layoutConstraints,
      columnConstraints,
      rowConstraints)
    new javax.swing.JPanel(mig) with SuperMixin
  }

  def add(comp: Component, constraints: String): Unit = peer.add(comp.peer, constraints)

  def add(comp: Component): Unit = add(comp, "")

  protected def constraintsFor(comp: Component): Constraints =
    layoutManager.getComponentConstraints(comp.peer).asInstanceOf[String]

  protected def areValid(constraints: Constraints): (Boolean, String) = (true, "")

  private def layoutManager = peer.getLayout.asInstanceOf[MigLayout]
}