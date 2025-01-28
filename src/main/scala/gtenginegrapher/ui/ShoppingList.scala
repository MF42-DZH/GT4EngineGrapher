package gtenginegrapher.ui

import java.awt.{Graphics2D, Image, RenderingHints}
import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import javax.swing.{ImageIcon, JDialog, JFrame, JTree}
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree._

import gtenginegrapher.schema._
import gtenginegrapher.Main

class ShoppingList(
  owner: JFrame,
  upgrades: Map[String, Seq[(String, Upgrade)]],
  engineRowId: Int,
  name: SimpleName,
) extends JDialog(owner, s"Shopping List for ${name.name}") {
  private val top = new DefaultMutableTreeNode(
    s"Parts for ${name.name} (RowId: $engineRowId) - Total Cost: Cr. ${upgrades.values
        .flatMap(_.map(_._2))
        .foldLeft(0)(_ + _.price)}",
  )

  for ((category, namedUpgrades) <- upgrades) {
    val categoryNode = new DefaultMutableTreeNode(category)
    for ((name, upgrade) <- namedUpgrades) {
      categoryNode.add(
        new DefaultMutableTreeNode(
          s"$name - ${upgrade.toString} (Cr. ${upgrade.price}; RowId: ${upgrade.rowId})",
        ),
      )
    }

    top.add(categoryNode)
  }

  private val tree = new JTree(top)
  (0 until tree.getRowCount).reverse.foreach(tree.expandRow)

  tree.setUI(new BasicTreeUI() {
    override def shouldPaintExpandControl(
      path: TreePath,
      row: Int,
      isExpanded: Boolean,
      hasBeenExpanded: Boolean,
      isLeaf: Boolean,
    ): Boolean = false
  })

  {
    val renderer = new DefaultTreeCellRenderer()
    renderer.setClosedIcon(ShoppingList.basketIcon)
    renderer.setOpenIcon(ShoppingList.basketIcon)
    renderer.setLeafIcon(ShoppingList.partIcon)

    tree.setCellRenderer(renderer)
  }

  this.setContentPane(tree)
}

object ShoppingList {
  private def resizeImage(src: Image, targetWidth: Int, targetHeight: Int): Image = {
    val resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics: Graphics2D = resized.createGraphics()

    graphics.setRenderingHint(
      RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BICUBIC,
    )
    graphics.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON,
    )
    graphics.drawImage(src, 0, 0, targetWidth, targetHeight, null)
    graphics.dispose()

    resized
  }

  private[ShoppingList] lazy val basketIcon: ImageIcon = new ImageIcon(
    resizeImage(
      ImageIO.read(Main.getClass.getResourceAsStream("/basket-icon.png")),
      targetWidth  = 16,
      targetHeight = 16,
    ),
  )

  private[ShoppingList] lazy val partIcon: ImageIcon = new ImageIcon(
    resizeImage(
      ImageIO.read(Main.getClass.getResourceAsStream("/setting-icon.png")),
      targetWidth  = 16,
      targetHeight = 16,
    ),
  )
}
