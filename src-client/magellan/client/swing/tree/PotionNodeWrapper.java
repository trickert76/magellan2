/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package magellan.client.swing.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import magellan.library.Potion;

/**
 * DOCUMENT-ME
 * 
 * @author $Author: $
 * @version $Revision: 259 $
 */
public class PotionNodeWrapper implements CellObject, SupportsClipboard {
  private Potion potion = null;
  private String name = null;
  private String postfix = null;
  /*
   * We want Icons besides PotionNodes
   */
  protected List<String> icon;

  /**
   * Creates a new PotionNodeWrapper object.
   */
  public PotionNodeWrapper(Potion p) {
    this(p, null);
  }

  /**
   * Creates a new PotionNodeWrapper object.
   */
  public PotionNodeWrapper(Potion p, String postfix) {
    this(p, p.getName(), postfix);
  }

  /**
   * Creates a new PotionNodeWrapper object.
   * 
   * @param p
   * @param name
   * @param postfix
   */
  public PotionNodeWrapper(Potion p, String name, String postfix) {
    potion = p;
    this.name = name;
    this.postfix = postfix;
  }

  /**
   * @return The corresponding potion
   */
  public Potion getPotion() {
    return potion;
  }

  /**
   * @return potion name + postfix
   */
  @Override
  public String toString() {
    return postfix == null ? name : (name + postfix);
  }

  /**
   * @see magellan.client.swing.tree.CellObject#getIconNames()
   */
  public List<String> getIconNames() {
    if (icon == null) {
      icon = new ArrayList<String>(1);

      if (potion != null) {
        icon.add("items/" + potion.getName());
      }
    }

    return icon;
  }

  /**
   * @see magellan.client.swing.tree.CellObject#emphasized()
   */
  public boolean emphasized() {
    return false;
  }

  /**
   * @see magellan.client.swing.tree.CellObject#propertiesChanged()
   */
  public void propertiesChanged() {
    // no changeable properties
  }

  /**
   * @see magellan.client.swing.tree.SupportsClipboard#getClipboardValue()
   */
  public String getClipboardValue() {
    if (potion != null)
      return potion.getName();
    else
      return toString();
  }

  /**
   * @see magellan.client.swing.tree.CellObject#init(java.util.Properties,
   *      magellan.client.swing.tree.NodeWrapperDrawPolicy)
   */
  public NodeWrapperDrawPolicy init(Properties settings, NodeWrapperDrawPolicy adapter) {
    return null;
  }

  /**
   * @see magellan.client.swing.tree.CellObject#init(java.util.Properties, java.lang.String,
   *      magellan.client.swing.tree.NodeWrapperDrawPolicy)
   */
  public NodeWrapperDrawPolicy init(Properties settings, String prefix,
      NodeWrapperDrawPolicy adapter) {
    return null;
  }
}
